package deque;

import java.util.Iterator;

// Given, complete. The stack ADT from lecture, plus iteration.
public interface MyStack<E> extends Iterable<E> {

    void push(E e);

    // throw NoSuchElementException if the stack is empty
    E pop();
    E peek();

    boolean isEmpty();
    int size();

    // From the top to the bottom, without changing the stack.
    @Override
    Iterator<E> iterator();

}
