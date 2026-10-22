/*  Part 2: at most `limit` requests in any window of `window` time units.
 *
 *  The times passed to allow never decrease.
 */
package windows;

public class RateLimiter {
    // TODO Part 2: the fields

    // TODO Part 2
    // throws IllegalArgumentException if limit < 1 or window < 1
    public RateLimiter(int limit, long window) {
    }

    // TODO Part 2
    // a request at the given time: allowed if fewer than limit requests were allowed in (time - window, time]
    // an allowed request is recorded, a rejected one is not
    // O(1) amortized
    public boolean allow(long time) {
        throw new UnsupportedOperationException("RateLimiter.allow");
    }
}
