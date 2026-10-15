/*  The stack ADT from lecture, plus clear and read-only iteration, from top to bottom.
 *  ArrayStack and LinkedStack both implement it.
 *  Do not modify this file.
 */
package stacks;

// for-each visits the elements from the top to the bottom, without changing the stack
// the iterator's remove is not supported: elements leave the stack only with pop
public interface MyStack<E> extends Iterable<E> {
    void push(E e);

    // throw NoSuchElementException if the stack is empty
    E pop();
    E peek();

    boolean isEmpty();
    int size();

    // removes every element
    void clear();
}
