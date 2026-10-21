package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

// A deque on a circular array. Every operation is O(1), amortized for the ones that resize.
// Full: double the capacity before adding. A quarter full after a removal: halve it, never below INITIAL_CAPACITY.
public class MyArrayDeque<E> implements MyDeque<E>, MyStack<E>, MyQueue<E> {

    public static final int INITIAL_CAPACITY = 8;

    // The elements are data[front], data[front + 1], ..., size of them, wrapping around the end of the array.
    // Unused cells are null.
    private E[] data;
    private int front;
    private int size;

    public MyArrayDeque() {
        //TODO: an empty array of INITIAL_CAPACITY cells
    }

    // Given: the length of the array, for the tests.
    public int capacity() { return data.length; }

    @Override
    public int size() {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean isEmpty() {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void addFirst(E e) {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void addLast(E e) {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public E removeFirst() {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public E removeLast() {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public E peekFirst() {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public E peekLast() {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    // First to last. An inner class, as for MyArrayListDouble.
    @Override
    public Iterator<E> iterator() {
        //TODO
        throw new UnsupportedOperationException("TODO");
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
