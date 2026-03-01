package org.example;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Thread-safe LRU cache with per-entry TTL support.
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │ Interview talking points │
 * │ │
 * │ 1. Lazy cleanup on GET – stale entry evicted before returning null. │
 * │ 2. Lazy cleanup on PUT – min-heap (PriorityQueue by expiryMs) lets │
 * │ us instantly find the earliest deadline and drain ALL expired nodes │
 * │ in O(k log k), regardless of their LRU position. │
 * │ 3. Background cleanup – same heap drain; O(k log k) vs old O(n) │
 * │ full-map scan. O(1) when nothing has expired yet. │
 * │ 4. Lazy heap deletion – nodes evicted by LRU or remove() stay in │
 * │ the heap as "stale" entries and are skipped via identity check. │
 * │ 5. Coarse synchronized – simple and correct; production would use │
 * │ striped/segment locks or a lock-free structure. │
 * │ 6. Absolute expiry-ms – insertion-time + ttl; O(1) per-node check.│
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * @param <K> key type
 * @param <V> value type
 */
public class LRUCacheWithTTL<K, V> implements Cache<K, V> {

  private static final Logger LOG = Logger.getLogger(LRUCacheWithTTL.class.getName());

  // ── core data structures ──────────────────────────────────────────────────
  private final int capacity;
  private final Map<K, Node<K, V>> map; // O(1) lookup by key
  private final DoublyLinkedList<K, V> dll; // MRU ←→ LRU ordered list

  // ── expiry heap ───────────────────────────────────────────────────────────
  /**
   * Min-heap ordered by {@code expiryMs}: the node with the soonest deadline
   * sits at the top.
   *
   * <p>
   * Only entries that carry a real TTL ({@code expiryMs != -1}) are ever
   * added here.
   *
   * <p>
   * <b>Why PriorityQueue over sequential cleanup?</b>
   * 
   * <pre>
   *   peek()  → O(1)        Know instantly whether anything has expired.
   *   poll()  → O(log n)    Remove the minimum (earliest-expiring entry).
   *   offer() → O(log n)    Add a new TTL entry.
   *
   *   Old sequential tail-walk  → O(k) but only drains from the LRU end;
   *                               interior expired nodes are missed.
   *   Old background O(n) scan  → visits every live entry even if none expired.
   *
   *   New heap-based approach   → O(k log k) total; finds ALL expired nodes
   *                               regardless of their LRU position; O(1) when
   *                               nothing has expired.
   * </pre>
   *
   * <p>
   * <b>Stale-entry handling (lazy deletion):</b> when a node is evicted by
   * LRU capacity pressure or an explicit {@link #remove} call, its heap entry
   * is NOT removed eagerly (Java's PriorityQueue has no O(1) arbitrary-remove).
   * Instead we check {@code map.get(node.key) == node} before evicting — if the
   * check fails the heap entry is simply discarded. This is the standard "lazy
   * heap deletion" pattern used in Dijkstra's shortest-path algorithm.
   */
  private final PriorityQueue<Node<K, V>> expiryHeap = new PriorityQueue<>(Comparator.comparingLong(n -> n.expiryMs));

  // ── background sweeper ────────────────────────────────────────────────────
  private final ScheduledExecutorService cleaner;

  // ── constructors ──────────────────────────────────────────────────────────

  /**
   * Build a cache with background sweep every 30 seconds.
   *
   * @param capacity maximum live entries (positive)
   */
  public LRUCacheWithTTL(int capacity) {
    this(capacity, 30, TimeUnit.SECONDS);
  }

  /**
   * Build a cache with a custom background sweep interval.
   *
   * @param capacity      maximum live entries (positive)
   * @param sweepInterval how often the background sweeper runs
   * @param unit          time unit for {@code sweepInterval}
   */
  public LRUCacheWithTTL(int capacity, long sweepInterval, TimeUnit unit) {
    if (capacity <= 0)
      throw new IllegalArgumentException("capacity must be > 0");
    this.capacity = capacity;
    this.map = new HashMap<>(capacity * 2);
    this.dll = new DoublyLinkedList<>();

    // Daemon so it won't block JVM shutdown
    cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
      Thread t = new Thread(r, "lru-ttl-cleaner");
      t.setDaemon(true);
      return t;
    });
    cleaner.scheduleAtFixedRate(this::backgroundCleanup,
        sweepInterval, sweepInterval, unit);
  }

  // ── Cache interface ───────────────────────────────────────────────────────

  /**
   * Retrieve a value by key.
   *
   * <p>
   * <b>Lazy cleanup:</b> if the node is expired it is evicted and
   * {@code null} is returned as if the key were absent.
   *
   * @return the live value, or {@code null} if absent / expired
   */
  @Override
  public synchronized V get(K key) {
    Node<K, V> node = map.get(key);
    if (node == null)
      return null;

    // ── lazy expiry check on GET ─────────────────────────────────────────
    if (node.isExpired()) {
      evict(node);
      LOG.fine(() -> "[GET] lazy-evicted expired key=" + key);
      return null;
    }

    dll.moveToFront(node); // promote to MRU
    return node.value;
  }

  /**
   * Insert or update with no TTL (entry lives until LRU-evicted by capacity).
   */
  @Override
  public synchronized void put(K key, V value) {
    put(key, value, -1L);
  }

  /**
   * Insert or update with a TTL.
   *
   * <p>
   * <b>Lazy cleanup:</b> before inserting a new key, peeks the min-heap to
   * drain every entry whose deadline has already passed. O(k log k) for k
   * expired entries; O(1) when nothing has expired yet.
   *
   * @param ttlMs positive ms → entry expires after this duration;
   *              ≤ 0 → no expiry
   */
  @Override
  public synchronized void put(K key, V value, long ttlMs) {
    long expiryMs = (ttlMs > 0) ? System.currentTimeMillis() + ttlMs : -1L;

    // ── update existing entry ────────────────────────────────────────────
    if (map.containsKey(key)) {
      Node<K, V> existing = map.get(key);
      existing.value = value;
      existing.expiryMs = expiryMs;
      // Push the updated node into the heap if it now carries a TTL.
      // The previous heap entry (if any) becomes stale and will be skipped.
      if (expiryMs != -1L)
        expiryHeap.offer(existing);
      dll.moveToFront(existing);
      return;
    }

    // ── lazy cleanup BEFORE capacity check ──────────────────────────────
    // Drain the min-heap of all entries that have already expired.
    lazyCleanupOnPut();

    // ── capacity enforcement ─────────────────────────────────────────────
    if (map.size() >= capacity) {
      Node<K, V> lru = dll.removeLast();
      if (lru != null) {
        map.remove(lru.key);
        // lru's heap entry (if any) is now stale → will be skipped when reached.
        LOG.fine(() -> "[PUT] LRU capacity-evicted key=" + lru.key);
      }
    }

    // ── insert new node at MRU end ───────────────────────────────────────
    Node<K, V> newNode = new Node<>(key, value, expiryMs);
    dll.addFirst(newNode);
    map.put(key, newNode);

    // Only TTL entries go into the heap; no-TTL entries never expire.
    if (expiryMs != -1L)
      expiryHeap.offer(newNode);
  }

  /** Explicitly remove a key (no-op if absent). */
  @Override
  public synchronized void remove(K key) {
    Node<K, V> node = map.get(key);
    if (node != null)
      evict(node);
    // node's heap entry (if any) is now stale → will be skipped when reached.
  }

  /**
   * Current number of entries in the underlying map (includes any not-yet-swept
   * expired entries — call {@link #backgroundCleanup()} if you need an exact
   * live count).
   */
  @Override
  public synchronized int size() {
    return map.size();
  }

  // ── cleanup helpers ───────────────────────────────────────────────────────

  /**
   * <b>Lazy cleanup triggered by PUT.</b>
   *
   * <p>
   * Peeks the min-heap and drains every entry whose deadline has passed.
   * Stops as soon as the heap top is either absent or not yet expired.
   *
   * <p>
   * <b>Complexity:</b> O(k log k) for k expired entries.
   * Best case O(1) when the earliest deadline has not yet elapsed.
   *
   * <p>
   * <b>Old vs new:</b>
   * 
   * <pre>
   *   Old (sequential tail-walk):
   *     - O(k) evictions, but only inspects nodes at the LRU end.
   *     - A node with an early deadline but high recent-use rank was invisible.
   *   New (heap drain):
   *     - O(k log k) total; finds ALL expired nodes regardless of LRU rank.
   *     - O(1) when nothing is expired (just a peek with no eviction).
   * </pre>
   */
  private void lazyCleanupOnPut() {
    long now = System.currentTimeMillis();
    while (true) {
      Node<K, V> earliest = expiryHeap.peek();
      if (earliest == null || earliest.expiryMs > now)
        break; // nothing expired

      expiryHeap.poll(); // O(log n) removal of the minimum

      // Lazy deletion: skip stale heap entries — node may have already been
      // evicted by LRU capacity pressure or an explicit remove() call.
      if (map.get(earliest.key) != earliest)
        continue;

      evict(earliest);
      LOG.fine(() -> "[PUT-CLEANUP] lazy-evicted expired key=" + earliest.key);
    }
  }

  /**
   * <b>Background cleanup.</b>
   *
   * <p>
   * Drains the min-heap of <em>all</em> entries whose deadline has passed.
   * Runs on the daemon cleaner thread but acquires the cache lock for atomicity.
   *
   * <p>
   * <b>Old vs new:</b>
   * 
   * <pre>
   *   Old: O(n) full map scan — visits every entry even when none are expired.
   *   New: O(k log k) heap drain — only touches the k expired entries;
   *        exits after a single O(1) peek when nothing has expired.
   * </pre>
   */
  void backgroundCleanup() {
    synchronized (this) {
      int swept = 0;
      long now = System.currentTimeMillis();
      while (true) {
        Node<K, V> earliest = expiryHeap.peek();
        if (earliest == null || earliest.expiryMs > now)
          break;

        expiryHeap.poll();

        // Same stale-entry check as lazyCleanupOnPut.
        if (map.get(earliest.key) != earliest)
          continue;

        evict(earliest);
        swept++;
      }
      if (swept > 0) {
        final int count = swept;
        LOG.info(() -> "[BG-CLEANUP] Swept " + count + " expired entries.");
      }
    }
  }

  // ── private utilities ─────────────────────────────────────────────────────

  /** Remove {@code node} from both DLL and map. Caller must hold the lock. */
  private void evict(Node<K, V> node) {
    dll.remove(node);
    map.remove(node.key);
  }

  /**
   * Shut down the background cleaner thread gracefully.
   * Call this when the cache is no longer needed to prevent thread leaks.
   */
  public void shutdown() {
    cleaner.shutdown();
    try {
      if (!cleaner.awaitTermination(5, TimeUnit.SECONDS)) {
        cleaner.shutdownNow();
      }
    } catch (InterruptedException e) {
      cleaner.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
