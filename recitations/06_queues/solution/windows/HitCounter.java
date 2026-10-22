/*  Part 2: how many hits in the last `window` time units.
 *
 *  At time t, the window is (t - window, t]: with window 60, a hit at time 0
 *  is counted at times 0 to 59, and no longer at time 60.
 *  The times passed to hit and count never decrease.
 */
package windows;

import java.util.ArrayDeque;
import java.util.Queue;

public class HitCounter {
    // TODO Part 2: the fields
    private final long window;
    private final Queue<Long> hits = new ArrayDeque<>();      // the times of the hits in the window, oldest first

    // TODO Part 2
    // throws IllegalArgumentException if window < 1
    public HitCounter(long window) {
        if (window < 1) throw new IllegalArgumentException("window < 1: " + window);
        this.window = window;
    }

    // TODO Part 2
    // a hit at the given time
    // O(1) amortized; keeps only the hits that are still in the window
    public void hit(long time) {
        expire(time);
        hits.offer(time);
    }

    // TODO Part 2
    // the number of hits in (time - window, time]
    // O(1) amortized
    public int count(long time) {
        expire(time);
        return hits.size();
    }

    // removes the hits that are out of the window at the given time
    private void expire(long time) {
        while (!hits.isEmpty() && hits.peek() <= time - window) hits.poll();
    }
}
