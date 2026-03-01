package org.example;

/**
 * Minimal doubly-linked list used by the LRU cache.
 *
 * <ul>
 *   <li><b>head.next</b> → most-recently used (MRU) end</li>
 *   <li><b>tail.prev</b> → least-recently used (LRU) end</li>
 * </ul>
 *
 * Sentinel head and tail nodes simplify all edge cases (no null checks needed).
 */
class DoublyLinkedList<K, V> {

  private final Node<K, V> head; // dummy MRU sentinel
  private final Node<K, V> tail; // dummy LRU sentinel

  DoublyLinkedList() {
    head      = new Node<>(null, null);
    tail      = new Node<>(null, null);
    head.next = tail;
    tail.prev = head;
  }

  // ── public API ──────────────────────────────────────────────────────────────

  /** Insert {@code node} right after the head  →  MRU position. */
  void addFirst(Node<K, V> node) {
    node.next      = head.next;
    node.prev      = head;
    head.next.prev = node;
    head.next      = node;
  }

  /** Insert {@code node} right before the tail  →  LRU position. */
  void addLast(Node<K, V> node) {
    node.prev      = tail.prev;
    node.next      = tail;
    tail.prev.next = node;
    tail.prev      = node;
  }

  /** Unlink {@code node} from wherever it sits (does NOT clear its pointers). */
  void remove(Node<K, V> node) {
    node.prev.next = node.next;
    node.next.prev = node.prev;
  }

  /** Move an already-linked node to the MRU position. */
  void moveToFront(Node<K, V> node) {
    remove(node);
    addFirst(node);
  }

  /**
   * Remove and return the node at the LRU end (tail.prev).
   *
   * @return the evicted node, or {@code null} if the list is empty
   */
  Node<K, V> removeLast() {
    if (tail.prev == head) return null;
    Node<K, V> lru = tail.prev;
    remove(lru);
    return lru;
  }

  /** @return {@code true} if the list contains no real nodes. */
  boolean isEmpty() {
    return head.next == tail;
  }
}
