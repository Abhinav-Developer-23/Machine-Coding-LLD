package org.example;

/** Minimal doubly-linked list for LRU ordering. */
class DoublyLinkedList<K, V> {

  private final Node<K, V> head;
  private final Node<K, V> tail;

  DoublyLinkedList() {
    head = new Node<>(null, null);
    tail = new Node<>(null, null);
    head.next = tail;
    tail.prev = head;
  }

  void addFirst(Node<K, V> node) {
    node.next = head.next;
    node.prev = head;
    head.next.prev = node;
    head.next = node;
  }

  void remove(Node<K, V> node) {
    node.prev.next = node.next;
    node.next.prev = node.prev;
  }

  void moveToFront(Node<K, V> node) {
    remove(node);
    addFirst(node);
  }

  Node<K, V> removeLast() {
    if (tail.prev == head) {
      return null;
    }
    Node<K, V> lru = tail.prev;
    remove(lru);
    return lru;
  }
}
