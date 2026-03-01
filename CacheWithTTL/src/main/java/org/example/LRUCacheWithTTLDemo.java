package org.example;

import java.util.concurrent.TimeUnit;

/**
 * Runnable demo for the TTL-based LRU cache.
 *
 * Scenarios covered:
 *  1. Basic LRU eviction (capacity enforcement)
 *  2. TTL expiry detected lazily on GET
 *  3. Lazy cleanup on PUT frees expired slots so capacity is not wasted
 *  4. Manual remove
 *  5. Background cleanup (triggered explicitly for demo purposes)
 */
public class LRUCacheWithTTLDemo {

  public static void main(String[] args) throws InterruptedException {

    System.out.println("════════════════════════════════════════");
    System.out.println("  LRU Cache with TTL — Interview Demo");
    System.out.println("════════════════════════════════════════\n");

    // ── 1. Basic LRU eviction ──────────────────────────────────────────────
    System.out.println("── Scenario 1: Basic LRU Eviction (capacity=3) ──");
    LRUCacheWithTTL<String, Integer> cache = new LRUCacheWithTTL<>(3);

    cache.put("a", 1);
    cache.put("b", 2);
    cache.put("c", 3);
    System.out.println("get(a) = " + cache.get("a")); // 1  → promotes a to MRU

    cache.put("d", 4); // capacity full; b is now LRU → evicted
    System.out.println("get(b) = " + cache.get("b")); // null  (evicted)
    System.out.println("get(c) = " + cache.get("c")); // 3
    System.out.println("get(d) = " + cache.get("d")); // 4

    // ── 2. TTL expiry — lazy cleanup on GET ───────────────────────────────
    System.out.println("\n── Scenario 2: TTL Expiry detected on GET ──");
    LRUCacheWithTTL<String, String> ttlCache =
        new LRUCacheWithTTL<>(5, 60, TimeUnit.SECONDS); // slow bg sweep for demo

    ttlCache.put("session:1", "user-alice", 200);  // expires in 200 ms
    ttlCache.put("session:2", "user-bob",   5000); // expires in 5 s
    ttlCache.put("permanent", "admin",      -1);   // no TTL

    System.out.println("Before expiry:");
    System.out.println("  session:1  = " + ttlCache.get("session:1")); // user-alice
    System.out.println("  session:2  = " + ttlCache.get("session:2")); // user-bob
    System.out.println("  permanent  = " + ttlCache.get("permanent")); // admin

    Thread.sleep(300); // let session:1 expire

    System.out.println("\nAfter 300 ms (session:1 should be expired):");
    System.out.println("  session:1  = " + ttlCache.get("session:1")); // null
    System.out.println("  session:2  = " + ttlCache.get("session:2")); // user-bob
    System.out.println("  permanent  = " + ttlCache.get("permanent")); // admin

    // ── 3. Lazy cleanup on PUT ────────────────────────────────────────────
    System.out.println("\n── Scenario 3: Lazy cleanup on PUT reclaims expired slots ──");
    LRUCacheWithTTL<Integer, String> putCache =
        new LRUCacheWithTTL<>(3, 60, TimeUnit.SECONDS);

    putCache.put(1, "one",   150); // expires in 150 ms
    putCache.put(2, "two",   150);
    putCache.put(3, "three", 150);
    System.out.println("size before expiry = " + putCache.size()); // 3

    Thread.sleep(200); // let all 3 expire

    // PUT triggers lazy cleanup — all 3 expired entries swept from tail,
    // so 4 fits without LRU-evicting any live entry
    putCache.put(4, "four");
    putCache.put(5, "five");
    putCache.put(6, "six");
    System.out.println("get(1) = " + putCache.get(1)); // null  (was expired & swept)
    System.out.println("get(4) = " + putCache.get(4)); // four
    System.out.println("get(5) = " + putCache.get(5)); // five
    System.out.println("get(6) = " + putCache.get(6)); // six

    // ── 4. Manual remove ──────────────────────────────────────────────────
    System.out.println("\n── Scenario 4: Manual remove ──");
    ttlCache.remove("permanent");
    System.out.println("After remove, permanent = " + ttlCache.get("permanent")); // null

    // ── 5. Background cleanup (triggered manually for demo) ───────────────
    System.out.println("\n── Scenario 5: Background cleanup ──");
    LRUCacheWithTTL<String, Integer> bgCache =
        new LRUCacheWithTTL<>(10, 60, TimeUnit.SECONDS);
    bgCache.put("x", 100, 50);  // expires in 50 ms
    bgCache.put("y", 200, 50);
    bgCache.put("z", 300);      // no TTL
    Thread.sleep(100);

    System.out.println("size before bg-cleanup = " + bgCache.size()); // 3 (stale in map)
    bgCache.backgroundCleanup(); // simulate the scheduled sweep
    System.out.println("size after  bg-cleanup = " + bgCache.size()); // 1  (only z alive)
    System.out.println("get(z) = " + bgCache.get("z")); // 300

    // ── Cleanup ───────────────────────────────────────────────────────────
    cache.shutdown();
    ttlCache.shutdown();
    putCache.shutdown();
    bgCache.shutdown();

    System.out.println("\n════════════════════════════════════════");
    System.out.println("  All scenarios passed ✓");
    System.out.println("════════════════════════════════════════");
  }
}
