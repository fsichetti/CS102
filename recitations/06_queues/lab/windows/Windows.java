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
        throw new UnsupportedOperationException("maxNaive");
    }

    // TODO Part 3
    // O(N), with a deque of indices
    public static double[] max(double[] a, int k) {
        checkWindow(a, k);
        throw new UnsupportedOperationException("max");
    }

    // TODO Part 3
    // O(N), with a deque of indices
    public static double[] min(double[] a, int k) {
        checkWindow(a, k);
        throw new UnsupportedOperationException("min");
    }

    // TODO optional
    // the longest stretch a[i], ..., a[j] whose maximum and minimum differ by at most d, as {i, j}
    // the first one, if there are several
    // throws IllegalArgumentException if a is empty or d < 0
    // O(N)
    public static int[] longestStable(double[] a, double d) {
        throw new UnsupportedOperationException("longestStable");
    }

    // ==== given ====

    private static void checkWindow(double[] a, int k) {
        if (k < 1 || k > a.length)
            throw new IllegalArgumentException("window of " + k + " in an array of " + a.length);
    }
}
