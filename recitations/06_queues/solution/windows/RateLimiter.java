/*  Part 2: at most `limit` requests in any window of `window` time units.
 *
 *  The times passed to allow never decrease.
 */
package windows;

public class RateLimiter {
    // TODO Part 2: the fields
    private final int limit;
    private final HitCounter allowed;     // the requests allowed so far

    // TODO Part 2
    // throws IllegalArgumentException if limit < 1 or window < 1
    public RateLimiter(int limit, long window) {
        if (limit < 1) throw new IllegalArgumentException("limit < 1: " + limit);
        this.limit = limit;
        this.allowed = new HitCounter(window);
    }

    // TODO Part 2
    // a request at the given time: allowed if fewer than limit requests were allowed in (time - window, time]
    // an allowed request is recorded, a rejected one is not
    // O(1) amortized
    public boolean allow(long time) {
        if (allowed.count(time) >= limit) return false;
        allowed.hit(time);
        return true;
    }
}
