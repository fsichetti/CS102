/*  Part 3: the maximum and the minimum of every window of k consecutive elements.
 *
 *  a = [3, 1, 4, 1, 5], k = 3:   windows [3, 1, 4], [1, 4, 1], [4, 1, 5]
 *                                max     [4, 4, 5]
 *                                min     [1, 1, 1]
 */
package windows;

import java.util.ArrayDeque;
import java.util.Deque;

public class Windows {

    // For maxNaive, max and min:
    //   result[i] is the maximum (minimum) of a[i], ..., a[i + k - 1], for i from 0 to a.length - k
    //   throws IllegalArgumentException unless 1 <= k <= a.length
    //   a is unchanged

    // TODO Part 3
    // O(N k): for every window, look at all of its elements
    public static double[] maxNaive(double[] a, int k) {
        checkWindow(a, k);
        double[] result = new double[a.length - k + 1];
        for (int i = 0; i < result.length; i++) {
            double m = a[i];
            for (int j = i + 1; j < i + k; j++) m = Math.max(m, a[j]);
            result[i] = m;
        }
        return result;
    }

    // TODO Part 3
    // O(N), with a deque of indices
    public static double[] max(double[] a, int k) {
        checkWindow(a, k);
        double[] result = new double[a.length - k + 1];
        Deque<Integer> candidates = new ArrayDeque<>();     // indices; their values decrease from first to last
        for (int j = 0; j < a.length; j++) {
            while (!candidates.isEmpty() && a[candidates.peekLast()] <= a[j]) candidates.pollLast();
            candidates.offerLast(j);
            if (candidates.peekFirst() <= j - k) candidates.pollFirst();      // out of the window
            if (j >= k - 1) result[j - k + 1] = a[candidates.peekFirst()];
        }
        return result;
    }

    // TODO Part 3
    // O(N), with a deque of indices
    public static double[] min(double[] a, int k) {
        checkWindow(a, k);
        double[] result = new double[a.length - k + 1];
        Deque<Integer> candidates = new ArrayDeque<>();     // indices; their values increase from first to last
        for (int j = 0; j < a.length; j++) {
            while (!candidates.isEmpty() && a[candidates.peekLast()] >= a[j]) candidates.pollLast();
            candidates.offerLast(j);
            if (candidates.peekFirst() <= j - k) candidates.pollFirst();
            if (j >= k - 1) result[j - k + 1] = a[candidates.peekFirst()];
        }
        return result;
    }

    // TODO optional
    // the longest stretch a[i], ..., a[j] whose maximum and minimum differ by at most d, as {i, j}
    // the first one, if there are several
    // throws IllegalArgumentException if a is empty or d < 0
    // O(N)
    public static int[] longestStable(double[] a, double d) {
        if (a.length == 0) throw new IllegalArgumentException("empty array");
        if (d < 0) throw new IllegalArgumentException("d < 0: " + d);
        Deque<Integer> maxs = new ArrayDeque<>();   // as in max, for the stretch a[i..j]
        Deque<Integer> mins = new ArrayDeque<>();   // as in min
        int bestI = 0, bestJ = 0;
        int i = 0;
        for (int j = 0; j < a.length; j++) {
            while (!maxs.isEmpty() && a[maxs.peekLast()] <= a[j]) maxs.pollLast();
            maxs.offerLast(j);
            while (!mins.isEmpty() && a[mins.peekLast()] >= a[j]) mins.pollLast();
            mins.offerLast(j);
            // shrink from the left; j stays in both deques, so they are never empty
            while (a[maxs.peekFirst()] - a[mins.peekFirst()] > d) {
                i++;
                if (maxs.peekFirst() < i) maxs.pollFirst();
                if (mins.peekFirst() < i) mins.pollFirst();
            }
            if (j - i > bestJ - bestI) {
                bestI = i;
                bestJ = j;
            }
        }
        return new int[] {bestI, bestJ};
    }

    // ==== given ====

    private static void checkWindow(double[] a, int k) {
        if (k < 1 || k > a.length)
            throw new IllegalArgumentException("window of " + k + " in an array of " + a.length);
    }
}
