package org.example;

/** Doubly-linked list node used internally by the LRU cache. */
class Node<K, V> {

  K key;
  V value;
  long expiryMs; // -1 = no expiry

  Node<K, V> prev;
  Node<K, V> next;

  Node(K key, V value) {
    this(key, value, -1L);
  }

  Node(K key, V value, long expiryMs) {
    this.key = key;
    this.value = value;
    this.expiryMs = expiryMs;
  }

  boolean isExpired(long nowMs) {
    return expiryMs != -1L && nowMs > expiryMs;
  }
}
