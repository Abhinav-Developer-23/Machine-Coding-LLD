package org.example;

/** Generic Cache interface. */
public interface Cache<K, V> {

  V get(K key);

  void put(K key, V value);

  void put(K key, V value, long ttlMs);

  void remove(K key);

  int size();
}
