/*  The array-based stack from lecture, plus clear and an iterator. Do not modify this file. */
package stacks;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayStack<E> implements MyStack<E> {
    private E[] data;
    private int size;       // elements in data[0..size-1], top at size-1

    @SuppressWarnings("unchecked")
    public ArrayStack() { data = (E[]) new Object[4]; }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public void push(E e) {
        if (size == data.length)
            data = Arrays.copyOf(data, 2 * data.length);
        data[size] = e;
        size++;
    }

    public E pop() {
        if (size == 0) throw new NoSuchElementException("empty stack");
        size--;
        E e = data[size];
        data[size] = null;
        return e;
    }

    public E peek() {
        if (size == 0) throw new NoSuchElementException("empty stack");
        return data[size - 1];
    }

    public void clear() {
        Arrays.fill(data, 0, size, null);
        size = 0;
    }

    private class Itr implements Iterator<E> {
        private int next = size - 1;    // index of the next element to return

        public boolean hasNext() { return next >= 0; }

        public E next() {
            if (next < 0) throw new NoSuchElementException();
            E e = data[next];
            next--;
            return e;
        }
    }

    // top to bottom
    public Iterator<E> iterator() { return new Itr(); }

    // bottom to top: the top is the rightmost element
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(data[i]);
            if (i < size - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
