/*
 * K queues sharing one fixed-size array
 * -------------------------------------
 * Implement k queues (numbered 0..k-1) using one array of fixed capacity N:
 * enqueue(q, e), dequeue(q), peek(q), isEmpty(q). All O(1). The structure is
 * full only when all N cells are in use, whichever queues they belong to.
 *
 * Naive: split the array into k blocks of N/k cells, one circular queue each.
 * Problem: queue 0 is full with N/k elements while all the others are empty.
 *
 * Solution: k linked queues where the "nodes" are array cells and the
 * "references" are indices into the array.
 *   data[j]:  the element in cell j.
 *   next[j]:  the cell after j in the same queue, -1 if none.
 *   front[q], back[q]: first and last cell of queue q, -1 if empty.
 *   free:     the unused cells, also chained through next[] as a stack.
 *             Initially 0 -> 1 -> ... -> N-1.
 *   enqueue(q, e): pop a cell from free, write e, link it after back[q].
 *   dequeue(q):    unlink front[q], push the cell back on free.
 * Exactly LinkedQueue, with new Node(...) replaced by "pop a free cell" and
 * the garbage collector replaced by "push the cell back on free".
 * This is a pool allocator.
 *
 * Extra memory: next[] (N ints) + front[], back[], size[] (3k ints).
 * Same idea for k stacks (then back[] is not needed).
 */

import java.util.*;

public class KQueues {

    static class SharedQueues<E> {
        private final E[] data;
        private final int[] next;       // next[j]: cell after j (in its queue or in free), -1 if none
        private final int[] front;      // front[q]: first cell of queue q, -1 if empty
        private final int[] back;       // back[q]: last cell of queue q, -1 if empty
        private final int[] size;
        private int free;               // first free cell, -1 if all cells are in use

        @SuppressWarnings("unchecked")
        SharedQueues(int k, int capacity) {
            data = (E[]) new Object[capacity];
            next = new int[capacity];
            for (int j = 0; j < capacity - 1; j++) next[j] = j + 1;
            next[capacity - 1] = -1;
            free = 0;
            front = new int[k];
            back = new int[k];
            Arrays.fill(front, -1);
            Arrays.fill(back, -1);
            size = new int[k];
        }

        public boolean isEmpty(int q) { return front[q] == -1; }
        public int size(int q) { return size[q]; }
        public boolean isFull() { return free == -1; }

        public void enqueue(int q, E e) {
            if (free == -1) throw new IllegalStateException("no free cells");
            int j = free;               // take a cell from the free list
            free = next[j];

            data[j] = e;
            next[j] = -1;
            if (back[q] == -1) front[q] = j;    // queue q was empty
            else next[back[q]] = j;
            back[q] = j;
            size[q]++;
        }

        public E dequeue(int q) {
            if (front[q] == -1) throw new NoSuchElementException("empty queue " + q);
            int j = front[q];
            E e = data[j];
            front[q] = next[j];
            if (front[q] == -1) back[q] = -1;   // removed the only element
            size[q]--;

            data[j] = null;             // give the cell back to the free list
            next[j] = free;
            free = j;
            return e;
        }

        public E peek(int q) {
            if (front[q] == -1) throw new NoSuchElementException("empty queue " + q);
            return data[front[q]];
        }

        // the array as it is
        public String internals() {
            return "data=" + Arrays.toString(data) + " next=" + Arrays.toString(next)
                    + " front=" + Arrays.toString(front) + " back=" + Arrays.toString(back)
                    + " free=" + free;
        }
    }

    public static void main(String[] args) {
        SharedQueues<String> s = new SharedQueues<>(3, 6);
        s.enqueue(0, "a0");
        s.enqueue(1, "b0");
        s.enqueue(0, "a1");
        s.enqueue(2, "c0");
        s.enqueue(1, "b1");
        System.out.println(s.internals());

        System.out.println(s.dequeue(0) + " " + s.dequeue(1));
        System.out.println(s.internals());

        // queue 2 can use every free cell
        s.enqueue(2, "c1");
        s.enqueue(2, "c2");
        s.enqueue(2, "c3");
        System.out.println(s.internals() + " full=" + s.isFull());

        for (int q = 0; q < 3; q++) {
            StringBuilder sb = new StringBuilder("queue " + q + ": ");
            while (!s.isEmpty(q)) sb.append(s.dequeue(q)).append(" ");
            System.out.println(sb);
        }
    }
}
