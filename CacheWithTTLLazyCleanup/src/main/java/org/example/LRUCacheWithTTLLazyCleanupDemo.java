package org.example;

/**
 * Demo for the LRU cache with TTL and lazy cleanup only.
 */
public class LRUCacheWithTTLLazyCleanupDemo {

  public static void main(String[] args) throws InterruptedException {
    LRUCacheWithTTLLazyCleanup<String, String> cache = new LRUCacheWithTTLLazyCleanup<>(2);

    cache.put("a", "alpha", 150);
    cache.put("b", "beta", 5000);
    System.out.println("size before sleep = " + cache.size()); // 2

    Thread.sleep(220);
    // get() triggers lazy cleanup and removes expired "a"
    System.out.println("get(a) after expiry = " + cache.get("a")); // null

    cache.put("c", "gamma");
    System.out.println("get(b) = " + cache.get("b")); // beta
    System.out.println("get(c) = " + cache.get("c")); // gamma
    System.out.println("size final = " + cache.size()); // 2
  }
}
