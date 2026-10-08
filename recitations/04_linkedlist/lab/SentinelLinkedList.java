import java.util.Iterator;
import java.util.NoSuchElementException;

public class SentinelLinkedList<E> implements MyList<E> {
    private static class Node<E> {
        E data;
        Node<E> next;       // null: no next node

        Node(E data) { this.data = data; }
    }

    private final Node<E> sentinel = new Node<>(null);  // before the first node, holds no element
    private Node<E> tail = sentinel;                     // last node, the sentinel if the list is empty
    private int size;

    // ==== Part 1 ====

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    private void checkIndex(int pos) {
        if (pos < 0 || pos >= size)
            throw new IndexOutOfBoundsException("pos " + pos + ", size " + size);
    }

    // the node before position pos: the sentinel for pos == 0
    // assumes 0 <= pos <= size
    private Node<E> nodeBefore(int pos) {
        throw new UnsupportedOperationException("TODO");
    }

    public E get(int pos) {
        throw new UnsupportedOperationException("TODO");
    }

    public E set(int pos, E e) {
        throw new UnsupportedOperationException("TODO");
    }

    // O(1)
    public void add(E e) {
        throw new UnsupportedOperationException("TODO");
    }

    public void add(int pos, E e) {
        throw new UnsupportedOperationException("TODO");
    }

    // O(1)
    public void addFirst(E e) {
        throw new UnsupportedOperationException("TODO");
    }

    public E remove(int pos) {
        throw new UnsupportedOperationException("TODO");
    }

    // O(1)
    public E removeFirst() {
        throw new UnsupportedOperationException("TODO");
    }

    public void clear() {
        throw new UnsupportedOperationException("TODO");
    }

    // ==== Part 2 ====

    public int indexOf(E e) {
        throw new UnsupportedOperationException("TODO");
    }

    public boolean contains(E e) {
        throw new UnsupportedOperationException("TODO");
    }

    private class Itr implements Iterator<E> {
        // TODO: fields

        public boolean hasNext() {
            throw new UnsupportedOperationException("TODO");
        }

        public E next() {
            throw new UnsupportedOperationException("TODO");
        }

        // removes the element last returned by next(), in O(1)
        public void remove() {
            throw new UnsupportedOperationException("TODO");
        }
    }

    public Iterator<E> iterator() { return new Itr(); }

    // true if o is a MyList with equal elements in the same order
    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("TODO");
    }

    // one pass, without size, get or nodeBefore
    public E middle() {
        throw new UnsupportedOperationException("TODO");
    }

    // O(N), changing the next references: no new nodes
    public void reverse() {
        throw new UnsupportedOperationException("TODO");
    }

    // ==== already written: do not modify ====

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<E> current = sentinel.next; current != null; current = current.next) {
            sb.append(current.data);
            if (current.next != null) sb.append(", ");
        }
        return sb.append("]").toString();
    }

    public void checkInvariants() {
        if (sentinel.data != null)
            throw new IllegalStateException("the sentinel holds an element: " + sentinel.data);
        Node<E> last = sentinel;
        int count = 0;
        while (last.next != null) {
            last = last.next;
            count++;
            if (count > size)
                throw new IllegalStateException("more than size = " + size + " nodes, or a cycle");
        }
        if (count != size)
            throw new IllegalStateException("size is " + size + " but there are " + count + " nodes");
        if (tail != last)
            throw new IllegalStateException("tail is not the last node");
    }
}
