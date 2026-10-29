/*
 * LRU cache
 * ---------
 * Setting: a slow source of data (disk, database, remote server) and a small
 * fast memory, the cache, holding at most C items. Keys are integers in
 * 0..K-1, with K much larger than C. Eviction policy: least recently used.
 *
 *   get(key):        if cached, return its value and mark it most recently
 *                    used. Otherwise a miss (here: return null).
 *   put(key, value): insert or update key, mark it most recently used. If the
 *                    cache now holds C+1 items, evict the least recently used.
 *
 * Example, C = 2: put(1), put(2), get(1), put(3) evicts 2 (1 was used more
 * recently). A following get(2) is a miss.
 *
 * Array-based list ordered by recency:
 *   find the key O(C), move it to the front O(C) (shift).
 *
 * Doubly linked list ordered by recency (head = most recent, tail = least
 * recent) plus an array node[0..K-1]: node[key] is the key's list node, or
 * null if the key is not cached.
 *   get/put: node[key] in O(1), unlink O(1), relink at the front O(1).
 *   eviction: remove the tail O(1), node[tail.key] = null.
 * The node stores the key too: on eviction we need to know which entry of
 * node[] to clear.
 * Doubly linked: unlinking a node from the middle needs its predecessor.
 * java.util.LinkedList does not work here: it does not expose its nodes, so
 * there is nothing to store in node[].
 * Memory: O(K) for the array, O(C) for the list. With K too large for an
 * array, a hash table replaces node[] (later in the course).
 */

import java.util.*;

public class LRUCache {

    static class Cache<V> {
        private static class Node<V> {
            final int key;
            V value;
            Node<V> prev;
            Node<V> next;

            Node(int key, V value) {
                this.key = key;
                this.value = value;
            }
        }

        private final int capacity;
        private final Node<V>[] node;   // node[key]: key's list node, null if not cached
        private Node<V> head;           // most recently used, null if empty
        private Node<V> tail;           // least recently used, null if empty
        private int size;

        @SuppressWarnings("unchecked")
        Cache(int capacity, int numKeys) {
            this.capacity = capacity;
            node = (Node<V>[]) new Node[numKeys];
        }

        public int size() { return size; }

        // remove n from the list, n must be in it
        private void unlink(Node<V> n) {
            if (n.prev == null) head = n.next;
            else n.prev.next = n.next;
            if (n.next == null) tail = n.prev;
            else n.next.prev = n.prev;
            n.prev = null;
            n.next = null;
        }

        private void addFront(Node<V> n) {
            n.next = head;
            if (head == null) tail = n;     // the list was empty
            else head.prev = n;
            head = n;
        }

        // null on a miss
        public V get(int key) {
            Node<V> n = node[key];
            if (n == null) return null;
            unlink(n);
            addFront(n);
            return n.value;
        }

        public void put(int key, V value) {
            Node<V> n = node[key];
            if (n != null) {                // already cached: update and move
                n.value = value;
                unlink(n);
                addFront(n);
            }
            else {
                n = new Node<>(key, value);
                node[key] = n;
                addFront(n);
                size++;
                if (size > capacity) {          // evict the least recently used
                    Node<V> lru = tail;
                    unlink(lru);
                    node[lru.key] = null;
                    size--;
                }
            }
        }

        // most to least recently used
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (Node<V> n = head; n != null; n = n.next) {
                sb.append(n.key).append("=").append(n.value);
                if (n.next != null) sb.append(", ");
            }
            return sb.append("]").toString();
        }
    }

    public static void main(String[] args) {
        Cache<String> c = new Cache<>(2, 100);
        c.put(1, "one");
        c.put(2, "two");
        System.out.println(c.get(1) + " " + c);     // one [1=one, 2=two]
        c.put(3, "three");                          // evicts 2
        System.out.println(c.get(2) + " " + c);     // null [3=three, 1=one]
        c.put(1, "ONE");                            // update, no eviction
        c.put(4, "four");                           // evicts 3
        System.out.println(c);                      // [4=four, 1=ONE]

        // a slow source simulated by a counter of misses
        Cache<Integer> sq = new Cache<>(3, 10);
        int misses = 0;
        for (int key : new int[]{1, 2, 3, 1, 4, 1, 2, 5, 1, 2}) {
            Integer v = sq.get(key);
            if (v == null) {
                misses++;
                sq.put(key, key * key);     // "fetch from the slow source"
            }
        }
        System.out.println("misses: " + misses + " " + sq);
    }
}
