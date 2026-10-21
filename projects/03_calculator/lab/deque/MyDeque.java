package deque;

import java.util.Iterator;

// Given, complete. The deque ADT from lecture, plus iteration.
public interface MyDeque<E> extends Iterable<E> {

    void addFirst(E e);
    void addLast(E e);

    // throw NoSuchElementException if the deque is empty
    E removeFirst();
    E removeLast();
    E peekFirst();
    E peekLast();

    boolean isEmpty();
    int size();

    // From first to last, without changing the deque: a for-each loop over a deque uses it.
    // next() throws NoSuchElementException when there is nothing left.
    @Override
    Iterator<E> iterator();

}
