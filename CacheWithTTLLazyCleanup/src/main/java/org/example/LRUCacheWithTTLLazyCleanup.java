package org.example;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Thread-safe LRU cache with per-entry TTL.
 *
 * This variant does not use any scheduler/background thread.
 * Expired entries are removed lazily during get/put/size calls.
 */
public class LRUCacheWithTTLLazyCleanup<K, V> implements Cache<K, V> {

  private final int capacity;
  private final Map<K, Node<K, V>> map;
  private final DoublyLinkedList<K, V> dll;
  private final PriorityQueue<Node<K, V>> expiryHeap =
      new PriorityQueue<>(Comparator.comparingLong(n -> n.expiryMs));

  public LRUCacheWithTTLLazyCleanup(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("capacity must be > 0");
    }
    this.capacity = capacity;
    this.map = new HashMap<>(capacity * 2);
    this.dll = new DoublyLinkedList<>();
  }

  @Override
  public synchronized V get(K key) {
    lazyCleanup();

    Node<K, V> node = map.get(key);
    if (node == null) {
      return null;
    }

    long now = System.currentTimeMillis();
    if (node.isExpired(now)) {
      evict(node);
      return null;
    }

    dll.moveToFront(node);
    return node.value;
  }

  @Override
  public synchronized void put(K key, V value) {
    put(key, value, -1L);
  }

  @Override
  public synchronized void put(K key, V value, long ttlMs) {
    lazyCleanup();

    long expiryMs = (ttlMs > 0) ? System.currentTimeMillis() + ttlMs : -1L;
    Node<K, V> existing = map.get(key);

    if (existing != null) {
      existing.value = value;
      existing.expiryMs = expiryMs;
      if (expiryMs != -1L) {
        expiryHeap.offer(existing);
      }
      dll.moveToFront(existing);
      return;
    }

    if (map.size() >= capacity) {
      Node<K, V> lru = dll.removeLast();
      if (lru != null) {
        map.remove(lru.key);
      }
    }

    Node<K, V> newNode = new Node<>(key, value, expiryMs);
    dll.addFirst(newNode);
    map.put(key, newNode);
    if (expiryMs != -1L) {
      expiryHeap.offer(newNode);
    }
  }

  @Override
  public synchronized void remove(K key) {
    Node<K, V> node = map.get(key);
    if (node != null) {
      evict(node);
    }
  }

  @Override
  public synchronized int size() {
    lazyCleanup();
    return map.size();
  }

  private void lazyCleanup() {
    long now = System.currentTimeMillis();
    while (true) {
      Node<K, V> earliest = expiryHeap.peek();
      if (earliest == null || earliest.expiryMs > now) {
        break;
      }

      expiryHeap.poll();
      if (map.get(earliest.key) != earliest) {
        continue;
      }

      if (earliest.isExpired(now)) {
        evict(earliest);
      }
    }
  }

  private void evict(Node<K, V> node) {
    dll.remove(node);
    map.remove(node.key);
  }
}
