/*  Part 1: the average of the last k values of a stream.
 */
package windows;

import java.util.ArrayDeque;
import java.util.Queue;

public class MovingAverage {
    // TODO Part 1: the fields
    private final int k;
    private final Queue<Double> last = new ArrayDeque<>();    // the last k values, oldest first
    private double sum;                                        // of the values in last

    // TODO Part 1
    // throws IllegalArgumentException if k < 1
    public MovingAverage(int k) {
        if (k < 1) throw new IllegalArgumentException("k < 1: " + k);
        this.k = k;
    }

    // TODO Part 1
    // adds x to the stream; returns the average of its last k values, x included
    // (of all of them, while there are fewer than k)
    // O(1)
    public double addNext(double x) {
        last.offer(x);
        sum += x;
        if (last.size() > k) sum -= last.poll();
        return sum / last.size();
    }
}
