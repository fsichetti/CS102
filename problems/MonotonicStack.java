/*
 * Monotonic stack: stock span
 * ---------------------------
 * Given an array price[] of daily stock prices, the span of day i is the
 * number of consecutive days up to day i (including day i) with a price
 * less than or equal to price[i]. Return the span of every day.
 *
 *   [100, 80, 90, 120]          -> [1, 1, 2, 4]
 *   [10, 4, 5, 90, 120, 80]     -> [1, 1, 2, 4, 5, 1]
 *
 * Naive: for each i, walk back while price <= price[i]. O(n^2) worst case
 * (increasing prices).
 *
 * Monotonic stack: span[i] = i - p, where p is the closest earlier day with
 * price[p] > price[i] (p = -1 if none).
 * Keep a stack of indices of candidate days for p. At day i:
 *   pop every index with price <= price[i]: for day i they are not p, and for
 *   any later day j, day i is closer and at least as high, so they will never
 *   be p again;
 *   the top (if any) is p;
 *   push i.
 * Invariant: prices of the indices in the stack are strictly decreasing from
 * bottom to top (hence "monotonic").
 * Cost: each index is pushed once and popped at most once: O(n) total,
 * even though a single day can pop many indices. Extra space O(n).
 *
 * Same pattern: next greater element, daily temperatures,
 * largest rectangle in a histogram.
 */

import java.util.*;

public class MonotonicStack {

    static int[] spanNaive(int[] price) {
        int n = price.length;
        int[] span = new int[n];
        for (int i = 0; i < n; i++) {
            int k = 1;
            while (i - k >= 0 && price[i - k] <= price[i]) k++;
            span[i] = k;
        }
        return span;
    }

    static int[] spanStack(int[] price) {
        int n = price.length;
        int[] span = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();     // indices, used as a stack
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && price[stack.peek()] <= price[i])
                stack.pop();
            span[i] = stack.isEmpty() ? i + 1 : i - stack.peek();
            stack.push(i);
        }
        return span;
    }

    public static void main(String[] args) {
        int[][] tests = {
            {100, 80, 90, 120},
            {10, 4, 5, 90, 120, 80},
            {100, 80, 60, 70, 60, 75, 85},
        };
        for (int[] t : tests)
            System.out.println(Arrays.toString(t) + " -> " + Arrays.toString(spanStack(t))
                    + " " + Arrays.equals(spanStack(t), spanNaive(t)));

        // increasing prices: the naive version is quadratic
        System.out.printf("%10s %10s %10s%n", "n", "naive ms", "stack ms");
        for (int n = 10_000; n <= 80_000; n *= 2) {
            int[] p = new int[n];
            for (int i = 0; i < n; i++) p[i] = i;
            long t0 = System.nanoTime();
            spanNaive(p);
            long t1 = System.nanoTime();
            spanStack(p);
            long t2 = System.nanoTime();
            System.out.printf("%,10d %10d %10d%n", n, (t1 - t0) / 1_000_000, (t2 - t1) / 1_000_000);
        }
    }
}
