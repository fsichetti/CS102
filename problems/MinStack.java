/*
 * MinStack
 * --------
 * Given a working stack (push, pop, peek, isEmpty, size, all O(1)), write a
 * MinStack with the same operations plus min(): the smallest element currently
 * in the stack. Every operation O(1) worst case.
 *
 * Idea: a second stack `mins`, same size as `data`.
 *   push(e): data.push(e); mins.push(smaller of e and mins.peek()).
 *   pop():   mins.pop(); return data.pop().
 *   min():   mins.peek().
 * Invariant: mins.peek() is the minimum of everything in data, and, more
 * generally, the i-th element of mins is the minimum of the bottom i elements
 * of data. So after a pop the new mins.peek() is again correct.
 * O(1) worst case if the underlying stack is (a linked stack yes; ArrayDeque,
 * like ArrayStack, only amortized). Extra space O(N).
 * MaxStack: same thing, "larger" instead of "smaller".
 *
 * Queue from two stacks
 * ---------------------
 * Implement a queue (enqueue, dequeue, peek) using only two stacks.
 *   in:  enqueue pushes here.
 *   out: dequeue/peek pop from here. If out is empty, first move everything
 *        from in to out (pop/push one by one): this reverses the order, so the
 *        oldest element ends up on top of out.
 * Cost: one dequeue can be O(N), but each element is pushed and popped at most
 * twice overall (once in `in`, once in `out`): O(1) amortized.
 * Bad sequence for a naive version (moving back to `in` after every dequeue):
 * alternating enqueue/dequeue gives O(N) each.
 *
 * MinQueue from two MinStacks
 * ---------------------------
 * Same construction, with `in` and `out` both MinStacks.
 *   min() = smaller of in.min() and out.min() (skipping an empty one).
 * The transfer from in to out goes through MinStack.push, so out's mins are
 * rebuilt correctly for the reversed order. All operations O(1) amortized.
 * (Used for the "sliding window minimum" problem.)
 */

import java.util.*;

public class MinStack {

    // the stack ADT from class
    interface MyStack<E> {
        void push(E e);

        // throw NoSuchElementException if the stack is empty
        E pop();
        E peek();

        boolean isEmpty();
        int size();
    }

    // ==== MinStack ====

    static class MyMinStack<E extends Comparable<E>> implements MyStack<E> {
        private final Deque<E> data = new ArrayDeque<>();
        private final Deque<E> mins = new ArrayDeque<>();      // same size as data

        public int size() { return data.size(); }
        public boolean isEmpty() { return data.isEmpty(); }

        public void push(E e) {
            data.push(e);
            if (mins.isEmpty() || e.compareTo(mins.peek()) < 0) mins.push(e);
            else mins.push(mins.peek());
        }

        public E pop() {
            mins.pop();         // throws if empty
            return data.pop();
        }

        // getFirst, not peek: ArrayDeque.peek returns null if empty
        public E peek() { return data.getFirst(); }

        public E min() { return mins.getFirst(); }
    }

    // ==== Queue from two stacks ====

    // the queue ADT from class
    interface MyQueue<E> {
        void enqueue(E e);

        // throw NoSuchElementException if the queue is empty
        E dequeue();
        E peek();

        boolean isEmpty();
        int size();
    }

    static class TwoStackQueue<E> implements MyQueue<E> {
        private final Deque<E> in = new ArrayDeque<>();     // used as stacks: newest on top
        private final Deque<E> out = new ArrayDeque<>();    // oldest on top

        public int size() { return in.size() + out.size(); }
        public boolean isEmpty() { return in.isEmpty() && out.isEmpty(); }

        public void enqueue(E e) { in.push(e); }

        // move everything only when out is empty: otherwise the order breaks
        private void refill() {
            if (out.isEmpty())
                while (!in.isEmpty()) out.push(in.pop());
        }

        public E dequeue() {
            refill();
            if (out.isEmpty()) throw new NoSuchElementException("empty queue");
            return out.pop();
        }

        public E peek() {
            refill();
            if (out.isEmpty()) throw new NoSuchElementException("empty queue");
            return out.peek();
        }
    }

    // ==== MinQueue from two MinStacks ====

    static class MinQueue<E extends Comparable<E>> implements MyQueue<E> {
        private final MyMinStack<E> in = new MyMinStack<>();
        private final MyMinStack<E> out = new MyMinStack<>();

        public int size() { return in.size() + out.size(); }
        public boolean isEmpty() { return in.isEmpty() && out.isEmpty(); }

        public void enqueue(E e) { in.push(e); }

        private void refill() {
            if (out.isEmpty())
                while (!in.isEmpty()) out.push(in.pop());
        }

        public E dequeue() {
            refill();
            if (out.isEmpty()) throw new NoSuchElementException("empty queue");
            return out.pop();
        }

        public E peek() {
            refill();
            if (out.isEmpty()) throw new NoSuchElementException("empty queue");
            return out.peek();
        }

        public E min() {
            if (isEmpty()) throw new NoSuchElementException("empty queue");
            if (in.isEmpty()) return out.min();
            if (out.isEmpty()) return in.min();
            E a = in.min(), b = out.min();
            return a.compareTo(b) <= 0 ? a : b;
        }
    }

    // ==== Demo ====

    public static void main(String[] args) {
        MyMinStack<Integer> s = new MyMinStack<>();
        for (int x : new int[]{5, 3, 7, 3, 1, 8}) {
            s.push(x);
            System.out.print("push " + x + " min=" + s.min() + " | ");
        }
        System.out.println();
        while (s.size() > 1) {
            int x = s.pop();
            System.out.print("pop " + x + " min=" + s.min() + " | ");
        }
        System.out.println();

        MyQueue<String> q = new TwoStackQueue<>();
        q.enqueue("a");
        q.enqueue("b");
        q.enqueue("c");
        System.out.print(q.dequeue() + " ");
        q.enqueue("d");
        while (!q.isEmpty()) System.out.print(q.dequeue() + " ");
        System.out.println();

        // sliding window minimum, window of 3
        int[] a = {4, 2, 12, 3, 8, 5, 1, 7};
        int w = 3;
        MinQueue<Integer> mq = new MinQueue<>();
        StringBuilder sb = new StringBuilder("window mins: ");
        for (int i = 0; i < a.length; i++) {
            mq.enqueue(a[i]);
            if (mq.size() > w) mq.dequeue();
            if (mq.size() == w) sb.append(mq.min()).append(" ");
        }
        System.out.println(sb);    // expected: 2 2 3 3 1 1
    }
}
