import java.util.Iterator;
import java.util.NoSuchElementException;

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
        sentinel.next = sentinel;   // empty list: the sentinel links to itself
        sentinel.prev = sentinel;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    private void checkIndex(int pos) {
        if (pos < 0 || pos >= size)
            throw new IndexOutOfBoundsException("pos " + pos + ", size " + size);
    }

    // the node at pos: the sentinel for pos == size
    // assumes 0 <= pos <= size
    private Node<E> nodeAt(int pos) {
        Node<E> current;
        if (pos < size / 2) {
            current = sentinel.next;
            for (int i = 0; i < pos; i++) current = current.next;
        } else {
            current = sentinel;
            for (int i = size; i > pos; i--) current = current.prev;
        }
        return current;
    }

    // inserts e just before the node after
    private void insertBefore(Node<E> after, E e) {
        Node<E> n = new Node<>(e);
        n.prev = after.prev;
        n.next = after;
        after.prev.next = n;
        after.prev = n;
        size++;
    }

    // removes n, which is not the sentinel
    private E unlink(Node<E> n) {
        n.prev.next = n.next;
        n.next.prev = n.prev;
        size--;
        return n.data;
    }

    public E get(int pos) {
        checkIndex(pos);
        return nodeAt(pos).data;
    }

    public E set(int pos, E e) {
        checkIndex(pos);
        Node<E> n = nodeAt(pos);
        E old = n.data;
        n.data = e;
        return old;
    }

    public void add(E e) { insertBefore(sentinel, e); }

    public void addFirst(E e) { insertBefore(sentinel.next, e); }

    public void add(int pos, E e) {
        if (pos < 0 || pos > size)
            throw new IndexOutOfBoundsException("pos " + pos + ", size " + size);
        insertBefore(nodeAt(pos), e);
    }

    public E remove(int pos) {
        checkIndex(pos);
        return unlink(nodeAt(pos));
    }

    public E removeFirst() {
        if (size == 0) throw new NoSuchElementException("empty list");
        return unlink(sentinel.next);
    }

    public E removeLast() {
        if (size == 0) throw new NoSuchElementException("empty list");
        return unlink(sentinel.prev);
    }

    public void clear() {
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public int indexOf(E e) {
        int pos = 0;
        for (Node<E> current = sentinel.next; current != sentinel; current = current.next) {
            if (current.data.equals(e)) return pos;
            pos++;
        }
        return -1;
    }

    public boolean contains(E e) {
        return indexOf(e) != -1;
    }

    private class Itr implements Iterator<E> {
        private Node<E> current = sentinel.next;    // node holding the next element to return
        private Node<E> lastReturned;               // null: remove() not allowed

        public boolean hasNext() { return current != sentinel; }

        public E next() {
            if (current == sentinel) throw new NoSuchElementException();
            lastReturned = current;
            current = current.next;
            return lastReturned.data;
        }

        public void remove() {
            if (lastReturned == null) throw new IllegalStateException();
            unlink(lastReturned);
            lastReturned = null;
        }
    }

    private class DescendingItr implements Iterator<E> {
        private Node<E> current = sentinel.prev;

        public boolean hasNext() { return current != sentinel; }

        public E next() {
            if (current == sentinel) throw new NoSuchElementException();
            E e = current.data;
            current = current.prev;
            return e;
        }
    }

    public Iterator<E> iterator() { return new Itr(); }
    public Iterator<E> descendingIterator() { return new DescendingItr(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MyList)) return false;
        MyList<?> other = (MyList<?>) o;
        if (size != other.size()) return false;
        Iterator<?> it = other.iterator();
        for (E e : this)
            if (!e.equals(it.next())) return false;
        return true;
    }

    public E middle() {
        if (size == 0) throw new NoSuchElementException("empty list");
        Node<E> front = sentinel.next, back = sentinel.prev;
        while (front != back && front.prev != back) {   // stop when they meet or cross
            front = front.next;
            back = back.prev;
        }
        return front.data;
    }

    public void reverse() {
        Node<E> n = sentinel;
        do {                        // swap next and prev in every node, sentinel included
            Node<E> following = n.next;
            n.next = n.prev;
            n.prev = following;
            n = following;
        } while (n != sentinel);
    }

    // ==== already written ====

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
