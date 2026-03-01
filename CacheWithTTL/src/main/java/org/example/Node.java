package org.example;

/**
 * Doubly-linked list node used internally by the LRU cache.
 *
 * <p>{@code expiryMs == -1} means the entry has no TTL (never expires).
 * <p>{@code expiryMs  >  0} is the absolute epoch-millisecond timestamp
 *    after which the entry is considered stale.
 */
class Node<K, V> {

  K    key;
  V    value;
  long expiryMs;   // -1 = no expiry

  Node<K, V> prev;
  Node<K, V> next;

  /** Sentinel / no-TTL constructor. */
  Node(K key, V value) {
    this(key, value, -1L);
  }

  Node(K key, V value, long expiryMs) {
    this.key      = key;
    this.value    = value;
    this.expiryMs = expiryMs;
  }

  /** @return {@code true} if a TTL was set and it has already elapsed. */
  boolean isExpired() {
    return expiryMs != -1L && System.currentTimeMillis() > expiryMs;
  }
}
