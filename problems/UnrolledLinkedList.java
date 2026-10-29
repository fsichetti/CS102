/*
 * Unrolled linked list
 * --------------------
 * A singly linked list of nodes, each holding an array of up to B elements
 * (and a count). A list operation on index i first finds the node holding i,
 * then works inside that node's array.
 *
 *   get(i):       walk the nodes, subtracting their counts, then items[offset].
 *   insert(i, x): find the node, shift its array right. If the node is full,
 *                 first split it: keep B/2 elements, move the rest to a new
 *                 node right after it.
 *   delete(i):    find the node, shift its array left. If the node falls
 *                 below B/2 and has a next node m:
 *                   - both fit in one node: merge m into it (remove m);
 *                   - otherwise: take the first element of m.
 *                 A last node left empty is removed.
 *
 * Invariant: every node except the last holds between B/2 and B elements
 * (splits create two nodes of at least B/2; merge/take restore it on delete).
 * So there are at most 2n/B + 1 nodes.
 *
 * Cost of every operation: O(n/B) to reach the node + O(B) to shift inside it.
 * B = sqrt(n) balances the two terms: O(sqrt(n)).
 * Compare: array list get O(1), insert/delete O(n); linked list all O(n).
 * B = 1: a linked list. B = n: an array list.
 *
 * Keeping B close to sqrt(n) as n changes (B a power of 2, starts at 2):
 *   n reaches 4B^2:  B = 2B, merge the nodes in pairs (1+2, 3+4, ...).
 *                    Pair: B/2 + B/2 = new B/2 up to B + B = new B. OK.
 *   n below B^2/4:   B = B/2, split each node with more than B elements
 *                    in two halves. Half: between new B/2 and new B. OK.
 * Between two changes B^2/4 <= n < 4B^2, so sqrt(n)/2 < B <= 2 sqrt(n):
 * still O(sqrt(n)) per operation.
 * A change costs O(n) (every array is reallocated), but after a change n
 * must grow x4 or shrink /4 before the next one: at least 3n/4 operations in
 * between, O(1) amortized. Same argument as ArrayList's doubling.
 *
 * Memory: one node header and next reference per B elements instead of per
 * element. Neighbouring elements are adjacent in memory (cache friendly).
 */

import java.util.*;

public class UnrolledLinkedList {

    static class UnrolledList<E> {
        private static class Node<E> {
            E[] items;              // length B
            int count;              // items[0..count-1] in use
            Node<E> next;

            @SuppressWarnings("unchecked")
            Node(int capacity) { items = (E[]) new Object[capacity]; }
        }

        private int B = 2;          // node capacity, a power of 2
        private Node<E> head;       // null if empty
        private int size;

        public int size() { return size; }
        public int capacity() { return B; }

        private void checkIndex(int i) {
            if (i < 0 || i >= size)
                throw new IndexOutOfBoundsException("index " + i + ", size " + size);
        }

        // moves n.items[keep..count-1] to a new node right after n
        private void split(Node<E> n, int keep) {
            Node<E> m = new Node<>(B);
            for (int j = keep; j < n.count; j++) {
                m.items[j - keep] = n.items[j];
                n.items[j] = null;
            }
            m.count = n.count - keep;
            n.count = keep;
            m.next = n.next;
            n.next = m;
        }

        // appends the elements of n.next to n and removes n.next; they must fit
        private void mergeNext(Node<E> n) {
            Node<E> m = n.next;
            for (int j = 0; j < m.count; j++) n.items[n.count + j] = m.items[j];
            n.count += m.count;
            n.next = m.next;
        }

        private void grow() {
            B *= 2;
            for (Node<E> n = head; n != null; n = n.next) {
                n.items = Arrays.copyOf(n.items, B);
                if (n.next != null) mergeNext(n);
            }
        }

        private void shrink() {
            B /= 2;
            for (Node<E> n = head; n != null; n = n.next) {
                if (n.count > B) split(n, n.count / 2);     // the new node already has length B
                n.items = Arrays.copyOf(n.items, B);
            }
        }

        public E get(int i) {
            checkIndex(i);
            Node<E> n = head;
            while (i >= n.count) {  // i is now an offset inside n
                i -= n.count;
                n = n.next;
            }
            return n.items[i];
        }

        public void insert(int i, E x) {
            if (i < 0 || i > size)  // i == size is allowed
                throw new IndexOutOfBoundsException("index " + i + ", size " + size);
            if (head == null) head = new Node<>(B);

            Node<E> n = head;
            while (i > n.count) {   // stop at i == n.count: append at the end of n
                i -= n.count;
                n = n.next;
            }

            if (n.count == B) {     // full: split
                int h = B / 2;
                split(n, h);
                if (i > h) {        // x belongs in the new node
                    i -= h;
                    n = n.next;
                }
            }

            for (int j = n.count; j > i; j--) n.items[j] = n.items[j - 1];
            n.items[i] = x;
            n.count++;
            size++;

            if (size >= 4 * B * B) grow();
        }

        public E delete(int i) {
            checkIndex(i);
            Node<E> prev = null, n = head;
            while (i >= n.count) {
                i -= n.count;
                prev = n;
                n = n.next;
            }

            E x = n.items[i];
            for (int j = i; j < n.count - 1; j++) n.items[j] = n.items[j + 1];
            n.count--;
            n.items[n.count] = null;
            size--;

            if (n.next != null && n.count < B / 2) {
                Node<E> m = n.next;
                if (n.count + m.count <= B) {   // both fit: merge
                    mergeNext(n);
                } else {                        // take the first element of m
                    n.items[n.count] = m.items[0];
                    n.count++;
                    for (int j = 0; j < m.count - 1; j++) m.items[j] = m.items[j + 1];
                    m.count--;
                    m.items[m.count] = null;
                }
            } else if (n.count == 0) {          // n is the last node: remove it
                if (prev == null) head = null;
                else prev.next = null;
            }

            if (B > 2 && size < B * B / 4) shrink();
            return x;
        }

        // throws if the structure is inconsistent
        void check() {
            int total = 0;
            for (Node<E> n = head; n != null; n = n.next) {
                if (n.items.length != B || n.count > B || n.count == 0
                        || (n.next != null && n.count < B / 2))
                    throw new AssertionError("node with " + n.count + " elements, B = " + B);
                total += n.count;
            }
            if (total != size) throw new AssertionError("size");
            if (size >= 4 * B * B || (B > 2 && size < B * B / 4))
                throw new AssertionError("B = " + B + " for n = " + size);
        }

        // nodes separated by |
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (Node<E> n = head; n != null; n = n.next) {
                for (int j = 0; j < n.count; j++) {
                    sb.append(n.items[j]);
                    if (j < n.count - 1) sb.append(" ");
                }
                if (n.next != null) sb.append(" | ");
            }
            return sb.append("]").toString();
        }
    }

    public static void main(String[] args) {
        UnrolledList<Integer> u = new UnrolledList<>();
        for (int i = 0; i < 17; i++) {
            u.insert(u.size(), i);
            System.out.println("B=" + u.capacity() + " " + u);     // B: 2 -> 4 at n = 16
        }
        u.insert(0, 100);
        System.out.println("B=" + u.capacity() + " " + u);
        while (u.size() > 2) {
            u.delete(1);
            System.out.println("B=" + u.capacity() + " " + u);     // B: 4 -> 2 at n = 3
        }

        // random operations, compared with an ArrayList: grow, then shrink
        Random rnd = new Random(102);
        UnrolledList<Integer> a = new UnrolledList<>();
        List<Integer> b = new ArrayList<>();
        for (int phase = 0; phase < 2; phase++) {
            double pInsert = (phase == 0) ? 0.7 : 0.3;
            for (int step = 0; step < 100_000; step++) {
                if (rnd.nextDouble() < pInsert || b.isEmpty()) {
                    int i = rnd.nextInt(b.size() + 1), x = rnd.nextInt(1000);
                    a.insert(i, x);
                    b.add(i, x);
                } else {
                    int i = rnd.nextInt(b.size());
                    if (!a.delete(i).equals(b.remove(i))) throw new AssertionError("delete " + i);
                }
                int i = rnd.nextInt(b.size() + 1);
                if (i < b.size() && !a.get(i).equals(b.get(i))) throw new AssertionError("get " + i);
                a.check();
            }
            System.out.println("phase " + phase + " passed, size " + a.size() + ", B " + a.capacity());
        }
    }
}
