/*  The list ADT from lecture, with elements of any type E.
 *  SentinelLinkedList and SentinelDLinkedList both implement it.
 *  Do not modify this file.
 */
public interface MyList<E> extends Iterable<E> {

    // ==== Part 1 ====

    // appends e at the end
    void add(E e);

    // inserts e at pos, elements pos..size-1 move up by one
    // valid: 0 <= pos <= size
    void add(int pos, E e);

    // inserts e at the front
    void addFirst(E e);

    // removes and returns the element at pos, elements pos+1..size-1 move down by one
    // valid: 0 <= pos < size
    E remove(int pos);

    // removes and returns the first element
    // empty list: NoSuchElementException
    E removeFirst();

    // valid: 0 <= pos < size
    E get(int pos);

    // replaces the element at pos, returns the old one
    // valid: 0 <= pos < size
    E set(int pos, E e);

    int size();
    boolean isEmpty();
    void clear();

    // ==== Part 2 ====

    // position of the first element equal to e, or -1
    int indexOf(E e);

    boolean contains(E e);

    // the element at position size / 2
    // empty list: NoSuchElementException
    E middle();

    // reverses the order of the elements
    void reverse();

    // also from Iterable<E>: iterator()

    // ==== already written ====

    // throws IllegalStateException if the links or the fields are inconsistent
    void checkInvariants();
}
