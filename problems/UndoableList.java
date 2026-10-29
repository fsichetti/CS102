/*
 * Doubly linked list with K undoable removes
 * ------------------------------------------
 * A doubly linked list with addFirst, addLast, remove(pos), plus undo():
 * restores the most recently removed element, in its old position. Up to the
 * K most recent removes can be undone, in reverse order. Any insert discards
 * the undo history. undo() O(1).
 *
 * Circular list with one sentinel node: sentinel.next is the first node,
 * sentinel.prev the last. Every node has a prev and a next (never null):
 * no special cases for the ends or the empty list.
 *
 * Idea: a removed node keeps its prev and next.
 *   remove x: x.prev.next = x.next; x.next.prev = x.prev;
 * changes the neighbours, not x itself. So x still knows where it was:
 *   undo x:   x.prev.next = x;      x.next.prev = x;
 * No search, O(1).
 *
 * History: a deque of references to the removed nodes, at most K.
 *   remove: addFirst(node); if more than K, removeLast() (forget the oldest).
 *   undo:   removeFirst(), relink.
 * A stack is not enough: the oldest entry is dropped from the other end.
 *
 * Why undos in reverse order work: undoing the last remove brings the list
 * back exactly to the state before it. By induction, when x is undone the
 * list is exactly as right after x was removed, so x.prev and x.next are
 * adjacent again.
 *
 * Why inserts empty the deque: list a b c, remove b (a <-> c, b still points
 * to a and c), insert x between a and c (a x c). Undo b: a.next = b and
 * c.prev = b, so x is lost from the list.
 * Removed nodes are never modified, so removes between them are fine.
 *
 * Costs: addFirst/addLast O(1) (+ clearing the deque, O(1) amortized: each
 * entry was added by one remove), remove(pos) O(n) to find the node + O(1),
 * undo O(1). Extra memory: O(K), the removed nodes are kept alive by the deque.
 */

import java.util.*;

public class UndoableList {

    static class UndoList<E> {
        private static class Node<E> {
            E data;
            Node<E> prev;
            Node<E> next;

            Node(E data) { this.data = data; }
        }

        private final int K;
        private final Node<E> sentinel = new Node<>(null);     // not an element
        private int size;
        private final Deque<Node<E>> removed = new ArrayDeque<>();     // most recent first

        UndoList(int K) {
            this.K = K;
            sentinel.next = sentinel;   // empty: the sentinel points to itself
            sentinel.prev = sentinel;
        }

        public int size() { return size; }
        public int undoable() { return removed.size(); }

        // insert a new node between p and p.next
        private void addAfter(Node<E> p, E e) {
            removed.clear();
            Node<E> n = new Node<>(e);
            n.prev = p;
            n.next = p.next;
            p.next.prev = n;
            p.next = n;
            size++;
        }

        public void addFirst(E e) { addAfter(sentinel, e); }
        public void addLast(E e) { addAfter(sentinel.prev, e); }

        public E remove(int pos) {
            if (pos < 0 || pos >= size)
                throw new IndexOutOfBoundsException("pos " + pos + ", size " + size);
            Node<E> n = sentinel.next;
            for (int i = 0; i < pos; i++) n = n.next;

            // unlink, but leave n.prev and n.next as they are
            n.prev.next = n.next;
            n.next.prev = n.prev;
            size--;

            removed.addFirst(n);
            if (removed.size() > K) removed.removeLast();
            return n.data;
        }

        public E undo() {
            if (removed.isEmpty()) throw new IllegalStateException("nothing to undo");
            Node<E> n = removed.removeFirst();
            n.prev.next = n;
            n.next.prev = n;
            size++;
            return n.data;
        }

        // first to last
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (Node<E> n = sentinel.next; n != sentinel; n = n.next) {
                sb.append(n.data);
                if (n.next != sentinel) sb.append(", ");
            }
            return sb.append("]").toString();
        }

        // last to first, to check the prev links
        public String backward() {
            StringBuilder sb = new StringBuilder("[");
            for (Node<E> n = sentinel.prev; n != sentinel; n = n.prev) {
                sb.append(n.data);
                if (n.prev != sentinel) sb.append(", ");
            }
            return sb.append("]").toString();
        }
    }

    public static void main(String[] args) {
        UndoList<String> l = new UndoList<>(3);
        for (String s : List.of("a", "b", "c", "d", "e", "f")) l.addLast(s);
        System.out.println(l);

        l.remove(0);
        l.remove(4);
        l.remove(1);
        l.remove(1);
        System.out.println(l + " undoable=" + l.undoable());   // [b, e], 3: the oldest is forgotten
        System.out.println("undo " + l.undo() + " " + l);
        System.out.println("undo " + l.undo() + " " + l);
        System.out.println("undo " + l.undo() + " " + l + " backward " + l.backward());
        try {
            l.undo();
        } catch (IllegalStateException e) {
            System.out.println(e);
        }

        l.remove(0);
        l.addFirst("z");            // discards the history
        System.out.println(l + " undoable=" + l.undoable());

        // random operations, compared with an ArrayList and a history of (pos, element)
        Random rnd = new Random(102);
        int K = 4;
        UndoList<Integer> a = new UndoList<>(K);
        List<Integer> b = new ArrayList<>();
        Deque<int[]> hist = new ArrayDeque<>();
        for (int step = 0; step < 100_000; step++) {
            int op = rnd.nextInt(5);
            if (op < 2 || b.isEmpty()) {
                int x = rnd.nextInt(1000);
                if (rnd.nextBoolean()) { a.addFirst(x); b.add(0, x); }
                else                   { a.addLast(x);  b.add(x); }
                hist.clear();
            } else if (op == 2 && !hist.isEmpty()) {
                int[] h = hist.removeFirst();
                a.undo();
                b.add(h[0], h[1]);
            } else {
                int pos = rnd.nextInt(b.size());
                hist.addFirst(new int[]{pos, b.get(pos)});
                if (hist.size() > K) hist.removeLast();
                a.remove(pos);
                b.remove(pos);
            }
            List<Integer> rev = new ArrayList<>(b);
            Collections.reverse(rev);
            if (!a.toString().equals(b.toString()) || !a.backward().equals(rev.toString()))
                throw new AssertionError("step " + step);
        }
        System.out.println("random test passed, size " + a.size());
    }
}
