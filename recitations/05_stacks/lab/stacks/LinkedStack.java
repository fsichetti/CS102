/*  The linked stack from lecture, plus clear and an iterator. Do not modify this file. */
package stacks;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedStack<E> implements MyStack<E> {
    private static class Node<E> {
        E data;
        Node<E> next;       // the node below this one

        Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<E> top;    // null if empty
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return top == null; }

    public void push(E e) {
        top = new Node<>(e, top);
        size++;
    }

    public E pop() {
        if (top == null) throw new NoSuchElementException("empty stack");
        E e = top.data;
        top = top.next;
        size--;
        return e;
    }

    public E peek() {
        if (top == null) throw new NoSuchElementException("empty stack");
        return top.data;
    }

    public void clear() {
        top = null;
        size = 0;
    }

    private class Itr implements Iterator<E> {
        private Node<E> current = top;  // node holding the next element to return

        public boolean hasNext() { return current != null; }

        public E next() {
            if (current == null) throw new NoSuchElementException();
            E e = current.data;
            current = current.next;
            return e;
        }
    }

    // top to bottom
    public Iterator<E> iterator() { return new Itr(); }

    // top to bottom
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("top: ");
        for (Node<E> n = top; n != null; n = n.next)
            sb.append(n.data).append(" ");
        return sb.toString();
    }
}
