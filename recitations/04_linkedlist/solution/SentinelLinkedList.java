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
        Node<E> current = sentinel;
        for (int i = 0; i < pos; i++)
            current = current.next;
        return current;
    }

    public E get(int pos) {
        checkIndex(pos);
        return nodeBefore(pos).next.data;
    }

    public E set(int pos, E e) {
        checkIndex(pos);
        Node<E> n = nodeBefore(pos).next;
        E old = n.data;
        n.data = e;
        return old;
    }

    public void add(E e) {
        Node<E> n = new Node<>(e);
        tail.next = n;
        tail = n;
        size++;
    }

    public void add(int pos, E e) {
        if (pos < 0 || pos > size)
            throw new IndexOutOfBoundsException("pos " + pos + ", size " + size);
        Node<E> before = nodeBefore(pos);
        Node<E> n = new Node<>(e);
        n.next = before.next;
        before.next = n;
        if (n.next == null) tail = n;
        size++;
    }

    public void addFirst(E e) {
        add(0, e);
    }

    public E remove(int pos) {
        checkIndex(pos);
        Node<E> before = nodeBefore(pos);
        Node<E> removed = before.next;
        before.next = removed.next;
        if (before.next == null) tail = before;
        size--;
        return removed.data;
    }

    public E removeFirst() {
        if (size == 0) throw new NoSuchElementException("empty list");
        return remove(0);
    }

    public void clear() {
        sentinel.next = null;
        tail = sentinel;
        size = 0;
    }

    // ==== Part 2 ====

    public int indexOf(E e) {
        int pos = 0;
        for (Node<E> current = sentinel.next; current != null; current = current.next) {
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
        private Node<E> beforeLast = sentinel;      // node before lastReturned

        public boolean hasNext() { return current != null; }

        public E next() {
            if (current == null) throw new NoSuchElementException();
            if (lastReturned != null) beforeLast = lastReturned;
            lastReturned = current;
            current = current.next;
            return lastReturned.data;
        }

        public void remove() {
            if (lastReturned == null) throw new IllegalStateException();
            beforeLast.next = current;
            if (lastReturned == tail) tail = beforeLast;
            lastReturned = null;
            size--;
        }
    }

    public Iterator<E> iterator() { return new Itr(); }

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
        if (sentinel.next == null) throw new NoSuchElementException("empty list");
        Node<E> slow = sentinel.next, fast = sentinel.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow.data;
    }

    public void reverse() {
        Node<E> first = sentinel.next;
        Node<E> previous = null, current = first;
        while (current != null) {
            Node<E> following = current.next;
            current.next = previous;
            previous = current;
            current = following;
        }
        sentinel.next = previous;
        if (first != null) tail = first;
    }

    // ==== already written ====

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
