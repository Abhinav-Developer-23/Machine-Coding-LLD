package org.example;

import java.util.HashMap;
import java.util.Map;

public class FifoCache<K, V> implements Cache<K, V> {
  private final int capacity;
  private final Map<K, Node<K, V>> map;
  private final DoublyLinkedList<K, V> dll;

  public FifoCache(int capacity) {
    this.capacity = capacity;
    this.map = new HashMap<>();
    this.dll = new DoublyLinkedList<>();
  }

  @Override
  public synchronized V get(K key) {
    Node<K, V> node = map.get(key);
    return node == null ? null : node.value;
  }

  @Override
  public synchronized void put(K key, V value) {
    if (map.containsKey(key)) {
      map.get(key).value = value;
      return;
    }

    if (map.size() == capacity) {
      Node<K, V> oldest = dll.removeLast();
      if (oldest != null) {
        map.remove(oldest.key);
      }
    }

    Node<K, V> newNode = new Node<>(key, value);
    dll.addFirst(newNode);
    map.put(key, newNode);
  }

  @Override
  public synchronized void remove(K key) {
    Node<K, V> node = map.get(key);
    if (node == null) return;
    dll.remove(node);
    map.remove(key);
  }
}
