/*
 * ForkStack
 * ---------
 * Given LinkedStack<E> (chain of nodes with data and next, field top;
 * push, pop, peek, isEmpty, size, all O(1)), add fork(): returns a new,
 * fully independent LinkedStack with the same elements in the same order.
 * Afterward, no operation on one stack affects the other, as if it were a
 * complete copy. fork() must be O(1), regardless of the size.
 *
 * Key observation: once push creates a node, its data and next never change.
 *   push: creates a new node pointing at the current top, reassigns this.top.
 *   pop:  reassigns this.top to top.next, does not touch the node.
 * Nodes are immutable, so a chain of nodes can be shared.
 *
 * fork(): new stack with the same top reference and the same size. O(1).
 *
 * Why it is correct: after a fork both tops point into the same chain.
 * A push on either stack only creates new nodes above its own top. A pop only
 * moves its own top. Neither ever modifies an existing node, so each stack
 * still sees exactly the elements it saw before. The two stacks form a tree
 * of nodes (each stack is a path from its top to the bottom).
 *
 * Making data and next final lets the compiler enforce the invariant.
 * Any operation that modifies a node (e.g. set(i, e), or a "remove from the
 * middle") would break it.
 *
 * Does not work for:
 *   - ArrayStack: sharing the array, a push on one overwrites a cell the
 *     other may still use.
 *   - LinkedQueue: enqueue modifies back.next of an existing node. An O(1)
 *     "ForkQueue" needs a persistent data structure, beyond this course.
 *
 * Memory: a node is freed only when no stack reaches it anymore (garbage
 * collector).
 */

import java.util.*;

public class ForkStack {

    static class LinkedStack<E> {
        private static class Node<E> {
            final E data;
            final Node<E> next;     // final: nodes never change after creation

            Node(E data, Node<E> next) {
                this.data = data;
                this.next = next;
            }
        }

        private Node<E> top;
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

        // O(1): share the chain, copy only the two fields
        public LinkedStack<E> fork() {
            LinkedStack<E> copy = new LinkedStack<>();
            copy.top = top;
            copy.size = size;
            return copy;
        }

        // top to bottom
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("top: ");
            for (Node<E> n = top; n != null; n = n.next)
                sb.append(n.data).append(" ");
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        LinkedStack<Integer> a = new LinkedStack<>();
        for (int i = 1; i <= 4; i++) a.push(i);

        LinkedStack<Integer> b = a.fork();
        b.pop();
        b.pop();
        b.push(20);
        a.push(5);

        System.out.println("a " + a + " size=" + a.size());   // top: 5 4 3 2 1
        System.out.println("b " + b + " size=" + b.size());   // top: 20 2 1

        LinkedStack<Integer> c = b.fork();
        while (!c.isEmpty()) c.pop();
        System.out.println("b " + b + " c " + c);              // b unchanged, c empty
    }
}
