import java.util.Iterator;
import java.util.NoSuchElementException;

// Part 3 (optional)
public class SentinelDLinkedList<E> implements MyList<E> {
    private static class Node<E> {
        E data;
        Node<E> next;
        Node<E> prev;

        Node(E data) { this.data = data; }
    }

    // circular: sentinel.next is the first node, sentinel.prev the last one
    private final Node<E> sentinel = new Node<>(null);
    private int size;

    public SentinelDLinkedList() {
        // TODO: the empty list
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    private void checkIndex(int pos) {
        if (pos < 0 || pos >= size)
            throw new IndexOutOfBoundsException("pos " + pos + ", size " + size);
    }

    // the node at pos: the sentinel for pos == size
    // assumes 0 <= pos <= size, walks from the nearer end
    private Node<E> nodeAt(int pos) {
        throw new UnsupportedOperationException("TODO");
    }

    // inserts e just before the node after
    private void insertBefore(Node<E> after, E e) {
        throw new UnsupportedOperationException("TODO");
    }

    // removes n, which is not the sentinel, and returns its element
    private E unlink(Node<E> n) {
        throw new UnsupportedOperationException("TODO");
    }

    public E get(int pos) {
        throw new UnsupportedOperationException("TODO");
    }

    public E set(int pos, E e) {
        throw new UnsupportedOperationException("TODO");
    }

    public void add(E e) {
        throw new UnsupportedOperationException("TODO");
    }

    public void addFirst(E e) {
        throw new UnsupportedOperationException("TODO");
    }

    public void add(int pos, E e) {
        throw new UnsupportedOperationException("TODO");
    }

    public E remove(int pos) {
        throw new UnsupportedOperationException("TODO");
    }

    public E removeFirst() {
        throw new UnsupportedOperationException("TODO");
    }

    // removes and returns the last element, in O(1)
    // empty list: NoSuchElementException
    public E removeLast() {
        throw new UnsupportedOperationException("TODO");
    }

    public void clear() {
        throw new UnsupportedOperationException("TODO");
    }

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

    // from the last element to the first
    private class DescendingItr implements Iterator<E> {
        // TODO: fields

        public boolean hasNext() {
            throw new UnsupportedOperationException("TODO");
        }

        public E next() {
            throw new UnsupportedOperationException("TODO");
        }
    }

    public Iterator<E> iterator() { return new Itr(); }
    public Iterator<E> descendingIterator() { return new DescendingItr(); }

    // true if o is a MyList with equal elements in the same order
    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("TODO");
    }

    // one pass, without size, get or nodeAt
    public E middle() {
        throw new UnsupportedOperationException("TODO");
    }

    // O(N), changing the links: no new nodes
    public void reverse() {
        throw new UnsupportedOperationException("TODO");
    }

    // ==== already written: do not modify ====

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<E> current = sentinel.next; current != sentinel; current = current.next) {
            sb.append(current.data);
            if (current.next != sentinel) sb.append(", ");
        }
        return sb.append("]").toString();
    }

    public void checkInvariants() {
        if (sentinel.data != null)
            throw new IllegalStateException("the sentinel holds an element: " + sentinel.data);
        Node<E> n = sentinel;
        int count = -1;             // position of n, -1 for the sentinel
        do {
            String where = count == -1 ? "the sentinel" : "the node at position " + count;
            if (n.next == null || n.prev == null)
                throw new IllegalStateException("a null link in " + where + ": in a circular list every next and prev is a node");
            if (n.next.prev != n)
                throw new IllegalStateException("n.next.prev != n for n = " + where);
            n = n.next;
            count++;
            if (count > size)
                throw new IllegalStateException("more than size = " + size + " nodes, or a cycle that skips the sentinel");
        } while (n != sentinel);
        if (count != size)
            throw new IllegalStateException("size is " + size + " but there are " + count + " nodes");
    }
}
