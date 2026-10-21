package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

// A deque on a circular array. Every operation is O(1), amortized for the ones that resize.
public class MyArrayDeque<E> implements MyDeque<E>, MyStack<E>, MyQueue<E> {

    public static final int INITIAL_CAPACITY = 8;

    // The elements are data[front], data[front + 1], ..., size of them, wrapping around the end of the array.
    // Unused cells are null.
    private E[] data;
    private int front;
    private int size;

    @SuppressWarnings("unchecked")
    public MyArrayDeque() {
        data = (E[]) new Object[INITIAL_CAPACITY];
    }

    // Given: the length of the array, for the tests.
    public int capacity() { return data.length; }

    @Override
    public int size() { return size; }

    @Override
    public boolean isEmpty() { return size == 0; }

    // index in data of element i, counting from the front
    private int index(int i) { return (front + i) % data.length; }

    // Copies the elements to a new array of the given capacity, the first one at index 0.
    @SuppressWarnings("unchecked")
    private void resize(int capacity) {
        E[] other = (E[]) new Object[capacity];
        for (int i = 0; i < size; i++) other[i] = data[index(i)];
        data = other;
        front = 0;
    }

    // After a removal: half the capacity once only a quarter of it is used.
    private void shrinkIfSparse() {
        if (data.length > INITIAL_CAPACITY && size <= data.length / 4) resize(data.length / 2);
    }

    private void checkNotEmpty() {
        if (size == 0) throw new NoSuchElementException("empty deque");
    }

    @Override
    public void addFirst(E e) {
        if (size == data.length) resize(2 * data.length);
        front = Math.floorMod(front - 1, data.length);
        data[front] = e;
        size++;
    }

    @Override
    public void addLast(E e) {
        if (size == data.length) resize(2 * data.length);
        data[index(size)] = e;
        size++;
    }

    @Override
    public E removeFirst() {
        checkNotEmpty();
        E e = data[front];
        data[front] = null;
        front = index(1);
        size--;
        shrinkIfSparse();
        return e;
    }

    @Override
    public E removeLast() {
        checkNotEmpty();
        int last = index(size - 1);
        E e = data[last];
        data[last] = null;
        size--;
        shrinkIfSparse();
        return e;
    }

    @Override
    public E peekFirst() {
        checkNotEmpty();
        return data[front];
    }

    @Override
    public E peekLast() {
        checkNotEmpty();
        return data[index(size - 1)];
    }

    @Override
    public Iterator<E> iterator() { return new DequeIterator(); }

    private class DequeIterator implements Iterator<E> {
        private int i = 0;      // position of the next element, counting from the front

        @Override
        public boolean hasNext() { return i < size; }

        @Override
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            E e = data[index(i)];
            i++;
            return e;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Given: MyStack and MyQueue, on top of the deque operations. The top of the stack and the front
    // of the queue are both the first element. size, isEmpty and iterator serve all three interfaces.

    @Override
    public void push(E e) { addFirst(e); }

    @Override
    public E pop() { return removeFirst(); }

    @Override
    public void enqueue(E e) { addLast(e); }

    @Override
    public E dequeue() { return removeFirst(); }

    // The top of the stack, and the front of the queue.
    @Override
    public E peek() { return peekFirst(); }

    // Given: "[a, b, c]", first to last.
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (E e : this) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(e);
        }
        return sb.append("]").toString();
    }

}
