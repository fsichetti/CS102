package deque;

import java.util.Iterator;

// Given, complete. The queue ADT from lecture, plus iteration.
public interface MyQueue<E> extends Iterable<E> {

    void enqueue(E e);

    // throw NoSuchElementException if the queue is empty
    E dequeue();
    E peek();

    boolean isEmpty();
    int size();

    // From the front to the back, without changing the queue.
    @Override
    Iterator<E> iterator();

}
