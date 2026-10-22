# Queues and Deques Lab

Many programs read a long sequence of data, such as the lines of a log file or a series of measurements, and at every step need only the most recent part of it: the last 10 lines, the requests of the last minute, the temperatures of the last 30 days.
We call this recent part a **window**. When new data arrives, it enters the window at one end, and the oldest data leaves from the other end: the window is a queue.

In this lab you write classes that keep such windows, and run them on two real data sets:

- `data/nasa-1995-07-13.log`: the log of the web server of NASA's Kennedy Space Center on July 13, 1995, the day the Space Shuttle mission STS-70 launched. Each line is one request to the server: the computer that sent it (the **host**), the time, and the page requested.
- `data/centralpark.csv`: the high and low temperature of every day in Central Park, New York, since 1870.

Your code goes in the `windows/` folder. The comments in each file give the details, such as which exceptions to throw.
The programs in the top folder use your code: `Test` tests it, `Requests` and `Weather` run it on the data, and `Benchmark` times Part 3. They skip what you have not written yet.
Some tests only check speed: a solution with the wrong complexity fails after 5 seconds.

Use `ArrayDeque` for every queue and deque, through the `Queue` and `Deque` interfaces.

## Part 1: the last lines, the moving average

Your TA will guide you through this part.

**The last lines.** `Tail.tail(in, n)` reads all the lines of the `Scanner` `in`, and returns the last `n` of them, in order.
The file may be too large to fit in memory, so do not store all of it: keep only the last `n` lines read so far.
`Requests` uses it to print the end of the log.

**The moving average.** A moving average smooths a noisy series by replacing every value with the average of the last `k` values.
A `MovingAverage(k)` receives the values one at a time: `addNext(x)` adds `x` and returns the average of the last `k` values received, `x` included.
At the start, when fewer than `k` values have arrived, it returns the average of all of them.

```
k = 3:   addNext(1) returns 1,  addNext(2) returns 1.5,  addNext(6) returns 3,
         addNext(4) returns 4, the average of 2, 6, 4
```

`addNext` is `O(1)`, so it cannot add up the whole window at every call.
`Weather` uses it to find the coldest and the warmest 30 days of every decade.

## Part 2: requests per minute

A web server needs to know how many requests it received recently, and to refuse the hosts that send too many.
Both are questions about a window of time, such as the last 60 seconds.
In this part, times are in seconds, and they never decrease from one call to the next, as in the log.

**Counting.** A `HitCounter(window)` counts the events ("hits") of the last `window` seconds.
`hit(t)` records an event at time `t`. `count(t)` returns how many of the recorded events are in the window ending at `t`, that is, at times in `(t - window, t]`.

```
window 60:   hit(1), hit(1), hit(30)
             count(60) returns 3,  count(61) returns 1,  count(90) returns 0
```

Keep the times of the events in a queue. An event that has left the window is never counted again: remove it.
Both methods are `O(1)` amortized: one call may remove many events, but why is the total cost still `O(1)` per call?
`Requests` uses a `HitCounter` to find the busiest 60 seconds of every hour of the log. The launch was at 09:41.

**Limiting.** A rate limiter lets a host send at most `limit` requests in any `window` seconds, and refuses the rest.
A `RateLimiter(limit, window)` is the limiter of one host: `allow(t)` says whether a request at time `t` is accepted.
It is accepted if fewer than `limit` requests were accepted in the window ending at `t`. Accepted requests are recorded, refused ones are not.
Use a `HitCounter`.

```
limit 3, window 10:   allow(0), allow(1), allow(1) return true,  allow(2) returns false,
                      allow(10) returns true: the request at 0 has left the window
```

`Requests` gives each host its own limiter, with at most 20 requests in 60 seconds, and lists the hosts with the most refused requests.
Compare with `java Requests 100 3600`, which allows 100 requests per hour.

## Part 3: the maximum of every window

Back to the temperatures. During a heat wave every day is hot, so the hottest week of a decade is the 7 consecutive days whose coolest day is the warmest.
To find it we need the minimum of every window of 7 days. A running sum cannot give us that.

`Windows.max(a, k)` returns the maximum of every window of `k` consecutive elements of `a`. `min` does the same for the minimum.

```
a = [3, 1, 4, 1, 5], k = 3:   the windows are [3, 1, 4], [1, 4, 1], [4, 1, 5]
                              max returns [4, 4, 5]
```

**Naive.** Write `maxNaive` first: it computes the maximum of each window from scratch, in `O(N k)`.

**With a deque.** Once a larger element `y` enters the window after `x`, `x` can never be the maximum again: every later window that contains `x` also contains `y`.
So `max` keeps a deque with the indices of the elements that can still be the maximum, in the order in which they appear in `a`.
Their values decrease from first to last, and the first one is the maximum of the current window.
For each index `j`, from left to right:

1. remove from the back the indices whose value is `<= a[j]`;
2. add `j` at the back;
3. if the index at the front is no longer in the window, remove it;
4. if `j >= k - 1`, the window ending at `j` is complete, and its maximum is the value at the front.

Before writing the code, run it by hand on `a = [1, 3, 2, 5, 4, 1]`, `k = 3`.
What is the cost of `max`? A single step may remove many indices: why is the total still `O(N)`?

Then write `min`.

`Benchmark` compares the times of `maxNaive` and `max`, first growing `k`, then growing `N`.
`Weather` prints the hottest and the coldest week of every decade.

## Optional: stable weather

`longestStable(a, d)` returns `{i, j}`, the longest stretch `a[i], ..., a[j]` whose maximum and minimum differ by at most `d`, in `O(N)`.
Here the window has no fixed size: it grows on the right one element at a time, and shrinks from the left while it is too wide. Keep two deques, one for the maximum and one for the minimum.

`java Test optional` tests it. `Weather` prints the longest stretch of every decade with all the highs within 10 °F.

## What to submit

Upload the `windows` folder to Brightspace.
