/*  Tests Parts 1 to 3.
 *      javac Test.java
 *      java Test             tests Parts 1 to 3
 *      java Test optional    also tests longestStable
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import windows.HitCounter;
import windows.MovingAverage;
import windows.RateLimiter;
import windows.Tail;
import windows.Windows;

public class Test {
    static volatile int failures = 0;

    // ==== helpers ====

    static void ok(String what) { System.out.println("OK    " + what); }

    static void fail(String what) {
        failures++;
        System.out.println("FAIL  " + what);
    }

    static void check(String what, Object expected, Object actual) {
        if (expected == null ? actual == null : expected.equals(actual)) ok(what);
        else fail(what + ": expected " + expected + ", got " + actual);
    }

    static void checkClose(String what, double expected, double actual) {
        if (Math.abs(expected - actual) <= 1e-9 * Math.max(1, Math.abs(expected))) ok(what);
        else fail(what + ": expected " + expected + ", got " + actual);
    }

    static void checkThrows(String what, Runnable r) {
        try {
            r.run();
            fail(what + ": expected IllegalArgumentException, nothing thrown");
        } catch (IllegalArgumentException e) {
            ok(what + ": throws IllegalArgumentException");
        } catch (UnsupportedOperationException e) {
            throw e;
        } catch (RuntimeException e) {
            fail(what + ": expected IllegalArgumentException, got " + e);
        }
    }

    // runs one group of tests: an unexpected exception fails the group, and testing goes on
    // a group running longer than 5 seconds fails: probably an infinite loop, or too slow
    static void run(String name, Runnable tests) {
        System.out.println("== " + name);
        Thread t = new Thread(() -> {
            try {
                tests.run();
            } catch (UnsupportedOperationException e) {
                fail("not written yet: " + e.getMessage() + "\n      at " + e.getStackTrace()[0]);
            } catch (RuntimeException e) {
                fail("threw " + e + "\n      at " + e.getStackTrace()[0]);
            }
        });
        t.setDaemon(true);      // a group still running does not keep the program alive
        t.start();
        try {
            t.join(5000);
        } catch (InterruptedException e) {
            return;
        }
        if (t.isAlive())
            fail("still running after 5 seconds: an infinite loop, or too slow?");
    }

    static String show(double[] a) {
        StringBuilder s = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            if (i > 0) s.append(", ");
            s.append(a[i] == Math.rint(a[i]) ? String.valueOf((long) a[i]) : String.valueOf(a[i]));
        }
        return s + "]";
    }

    // ==== Part 1 ====

    static void checkTail(String text, int n, List<String> expected) {
        String shown = text.replace("\n", "\\n");
        check("tail(\"" + shown + "\", " + n + ")", expected, new ArrayList<>(Tail.tail(new Scanner(text), n)));
    }

    static void tail() {
        checkTail("a\nb\nc\nd\ne", 2, List.of("d", "e"));
        checkTail("a\nb\nc\nd\ne\n", 3, List.of("c", "d", "e"));
        checkTail("a\nb\nc", 3, List.of("a", "b", "c"));
        checkTail("a\nb", 10, List.of("a", "b"));
        checkTail("a\nb\nc", 1, List.of("c"));
        checkTail("a\nb\nc", 0, List.of());
        checkTail("", 5, List.of());
        checkTail("first line\n\nthird line", 2, List.of("", "third line"));
        checkThrows("tail(..., -1)", () -> Tail.tail(new Scanner("a"), -1));
    }

    static void tailLong() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 100_000; i++) text.append("line ").append(i).append('\n');
        List<String> expected = new ArrayList<>();
        for (int i = 100_000 - 50; i < 100_000; i++) expected.add("line " + i);
        check("the last 50 of 100000 lines", expected, new ArrayList<>(Tail.tail(new Scanner(text.toString()), 50)));
    }

    static void movingAverage() {
        MovingAverage m = new MovingAverage(3);
        checkClose("k = 3: addNext(1), average of [1]", 1, m.addNext(1));
        checkClose("addNext(2), average of [1, 2]", 1.5, m.addNext(2));
        checkClose("addNext(6), average of [1, 2, 6]", 3, m.addNext(6));
        checkClose("addNext(4), average of [2, 6, 4]", 4, m.addNext(4));
        checkClose("addNext(-3), average of [6, 4, -3]", 7.0 / 3, m.addNext(-3));
        checkClose("addNext(0), average of [4, -3, 0]", 1.0 / 3, m.addNext(0));

        m = new MovingAverage(1);
        checkClose("k = 1: addNext(5)", 5, m.addNext(5));
        checkClose("addNext(7)", 7, m.addNext(7));

        checkThrows("new MovingAverage(0)", () -> new MovingAverage(0));
    }

    static void movingAverageRandom() {
        Random rnd = new Random(102);
        for (int k : new int[] {1, 2, 5, 30, 365}) {
            MovingAverage m = new MovingAverage(k);
            List<Double> seen = new ArrayList<>();
            for (int i = 0; i < 2000; i++) {
                double x = rnd.nextInt(201) - 100;
                seen.add(x);
                double sum = 0;
                int from = Math.max(0, seen.size() - k);
                for (int j = from; j < seen.size(); j++) sum += seen.get(j);
                double expected = sum / (seen.size() - from);
                double actual = m.addNext(x);
                if (Math.abs(expected - actual) > 1e-6) {
                    fail("k = " + k + ", value number " + (i + 1) + ": expected " + expected + ", got " + actual);
                    return;
                }
            }
            ok("k = " + k + ": 2000 random values");
        }
    }

    static void movingAverageFast() {
        MovingAverage m = new MovingAverage(1_000_000);
        for (int i = 0; i < 3_000_000; i++) m.addNext(i % 100);
        ok("3 million values, k = 1000000");
    }

    // ==== Part 2 ====

    static void hitCounter() {
        HitCounter h = new HitCounter(60);
        check("window 60: count(0) with no hits", 0, h.count(0));
        h.hit(1);
        h.hit(1);
        h.hit(30);
        check("hits at 1, 1, 30: count(30)", 3, h.count(30));
        check("count(60)", 3, h.count(60));
        check("count(61): the hits at 1 are out", 1, h.count(61));
        check("count(61) again", 1, h.count(61));
        h.hit(70);
        check("hit at 70: count(70)", 2, h.count(70));
        check("count(89)", 2, h.count(89));
        check("count(90): the hit at 30 is out", 1, h.count(90));
        check("count(1000)", 0, h.count(1000));
        h.hit(1000);
        check("hit at 1000: count(1000)", 1, h.count(1000));

        h = new HitCounter(1);
        h.hit(5);
        h.hit(5);
        check("window 1: hits at 5, 5: count(5)", 2, h.count(5));
        check("count(6)", 0, h.count(6));

        checkThrows("new HitCounter(0)", () -> new HitCounter(0));
    }

    static void hitCounterRandom() {
        Random rnd = new Random(102);
        for (long window : new long[] {1, 10, 60, 1000}) {
            HitCounter h = new HitCounter(window);
            List<Long> hits = new ArrayList<>();
            long time = 0;
            for (int i = 0; i < 3000; i++) {
                time += rnd.nextInt(4) == 0 ? rnd.nextInt((int) window + 5) : 0;
                if (rnd.nextBoolean()) {
                    h.hit(time);
                    hits.add(time);
                } else {
                    int expected = 0;
                    for (long t : hits) if (t > time - window) expected++;
                    int actual = h.count(time);
                    if (expected != actual) {
                        fail("window " + window + ", operation " + (i + 1) + ", count(" + time + "): expected "
                                + expected + ", got " + actual);
                        return;
                    }
                }
            }
            ok("window " + window + ": 3000 random hits and counts");
        }
    }

    static void hitCounterFast() {
        HitCounter h = new HitCounter(1_000_000);
        long total = 0;
        for (long t = 0; t < 2_000_000; t++) {
            h.hit(t);
            total += h.count(t);
        }
        ok("2 million hits and counts, window 1000000");
    }

    static void rateLimiter() {
        RateLimiter r = new RateLimiter(3, 10);
        check("3 per 10: allow(0)", true, r.allow(0));
        check("allow(1)", true, r.allow(1));
        check("allow(1)", true, r.allow(1));
        check("allow(2): 3 already", false, r.allow(2));
        check("allow(9)", false, r.allow(9));
        check("allow(10): the one at 0 is out", true, r.allow(10));
        check("allow(10)", false, r.allow(10));
        check("allow(11): the two at 1 are out", true, r.allow(11));
        check("allow(11)", true, r.allow(11));
        check("allow(11)", false, r.allow(11));
        // rejected requests are not recorded: at 20 the window holds only 10, 11, 11
        check("allow(20)", true, r.allow(20));
        check("allow(21)", true, r.allow(21));

        r = new RateLimiter(1, 5);
        check("1 per 5: allow(0)", true, r.allow(0));
        check("allow(4)", false, r.allow(4));
        check("allow(5)", true, r.allow(5));

        checkThrows("new RateLimiter(0, 10)", () -> new RateLimiter(0, 10));
        checkThrows("new RateLimiter(5, 0)", () -> new RateLimiter(5, 0));
    }

    static void rateLimiterRandom() {
        Random rnd = new Random(102);
        for (int limit : new int[] {1, 3, 20}) {
            long window = 10L * limit;
            RateLimiter r = new RateLimiter(limit, window);
            List<Long> allowed = new ArrayList<>();
            long time = 0;
            for (int i = 0; i < 3000; i++) {
                time += rnd.nextInt(3) == 0 ? rnd.nextInt(10) : 0;
                int inWindow = 0;
                for (long t : allowed) if (t > time - window) inWindow++;
                boolean expected = inWindow < limit;
                boolean actual = r.allow(time);
                if (expected != actual) {
                    fail(limit + " per " + window + ", request " + (i + 1) + ", allow(" + time + "): expected "
                            + expected + ", got " + actual);
                    return;
                }
                if (expected) allowed.add(time);
            }
            ok(limit + " per " + window + ": 3000 random requests");
        }
    }

    // ==== Part 3 ====

    // the reference: the maximum (or minimum) of each window, computed directly
    static double[] reference(double[] a, int k, boolean max) {
        double[] result = new double[a.length - k + 1];
        for (int i = 0; i < result.length; i++) {
            double[] window = Arrays.copyOfRange(a, i, i + k);
            Arrays.sort(window);
            result[i] = max ? window[k - 1] : window[0];
        }
        return result;
    }

    interface WindowMethod { double[] apply(double[] a, int k); }

    static void checkWindows(String name, WindowMethod method, double[] a, int k, boolean max) {
        double[] copy = a.clone();
        String expected = show(reference(a, k, max));
        check(name + "(" + show(a) + ", " + k + ")", expected, show(method.apply(copy, k)));
        if (!Arrays.equals(a, copy)) fail(name + " changed the array: " + show(copy));
    }

    static final double[][] SMALL = {
        {3, 1, 4, 1, 5},
        {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5},
        {1, 2, 3, 4, 5, 6},
        {6, 5, 4, 3, 2, 1},
        {2, 2, 2, 2},
        {5, 1, 1, 1, 1, 5, 1, 1},
        {-1.5, 0.5, -2.5, 7},
        {42},
    };

    static void smallWindows(String name, WindowMethod method, boolean max) {
        for (double[] a : SMALL)
            for (int k : new int[] {1, 2, 3, a.length})
                if (k <= a.length) checkWindows(name, method, a, k, max);
        checkThrows(name + "([1, 2, 3], 0)", () -> method.apply(new double[] {1, 2, 3}, 0));
        checkThrows(name + "([1, 2, 3], 4)", () -> method.apply(new double[] {1, 2, 3}, 4));
        checkThrows(name + "([], 1)", () -> method.apply(new double[0], 1));
    }

    static void randomWindows(String name, WindowMethod method, boolean max) {
        Random rnd = new Random(102);
        for (int trial = 0; trial < 500; trial++) {
            double[] a = new double[1 + rnd.nextInt(60)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(10);
            int k = 1 + rnd.nextInt(a.length);
            String expected = show(reference(a, k, max));
            String actual = show(method.apply(a.clone(), k));
            if (!expected.equals(actual)) {
                fail(name + "(" + show(a) + ", " + k + "): expected " + expected + ", got " + actual);
                return;
            }
        }
        ok(name + ": 500 random arrays");
    }

    // a window of 200000 in an array of 1000000: the naive version takes minutes
    static void fastWindows(String name, WindowMethod method, boolean max) {
        Random rnd = new Random(102);
        double[] a = new double[1_000_000];
        for (int i = 0; i < a.length; i++) a[i] = rnd.nextDouble();
        int k = 200_000;
        double[] result = method.apply(a, k);
        // spot checks
        for (int i : new int[] {0, 1, 12345, 500_000, result.length - 1}) {
            double expected = a[i];
            for (int j = i; j < i + k; j++) expected = max ? Math.max(expected, a[j]) : Math.min(expected, a[j]);
            if (result[i] != expected) {
                fail(name + ", window " + i + ": expected " + expected + ", got " + result[i]);
                return;
            }
        }
        ok(name + ": 1000000 elements, k = 200000");
    }

    // ==== optional ====

    static int[] longestStableReference(double[] a, double d) {
        int bestI = 0, bestJ = 0;
        for (int i = 0; i < a.length; i++) {
            double lo = a[i], hi = a[i];
            for (int j = i; j < a.length; j++) {
                lo = Math.min(lo, a[j]);
                hi = Math.max(hi, a[j]);
                if (hi - lo > d) break;
                if (j - i > bestJ - bestI) {
                    bestI = i;
                    bestJ = j;
                }
            }
        }
        return new int[] {bestI, bestJ};
    }

    static void checkStable(double[] a, double d) {
        double[] copy = a.clone();
        check("longestStable(" + show(a) + ", " + d + ")", Arrays.toString(longestStableReference(a, d)),
              Arrays.toString(Windows.longestStable(copy, d)));
        if (!Arrays.equals(a, copy)) fail("longestStable changed the array: " + show(copy));
    }

    static void longestStable() {
        checkStable(new double[] {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5}, 3);
        checkStable(new double[] {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5}, 0);
        checkStable(new double[] {1, 2, 3, 4, 5, 6}, 2);
        checkStable(new double[] {1, 2, 3, 4, 5, 6}, 10);
        checkStable(new double[] {5, 5, 1, 5, 5, 5}, 1);
        checkStable(new double[] {42}, 0);
        checkThrows("longestStable([], 1)", () -> Windows.longestStable(new double[0], 1));
        checkThrows("longestStable([1, 2], -1)", () -> Windows.longestStable(new double[] {1, 2}, -1));

        Random rnd = new Random(102);
        for (int trial = 0; trial < 500; trial++) {
            double[] a = new double[1 + rnd.nextInt(60)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(10);
            double d = rnd.nextInt(6);
            String expected = Arrays.toString(longestStableReference(a, d));
            String actual = Arrays.toString(Windows.longestStable(a.clone(), d));
            if (!expected.equals(actual)) {
                fail("longestStable(" + show(a) + ", " + d + "): expected " + expected + ", got " + actual);
                return;
            }
        }
        ok("longestStable: 500 random arrays");

        double[] big = new double[1_000_000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(1000);
        Windows.longestStable(big, 990);
        ok("longestStable: 1000000 elements");
    }

    public static void main(String[] args) {
        boolean optional = args.length > 0 && args[0].equals("optional");

        run("Part 1: tail", Test::tail);
        run("Part 1: tail of a long text", Test::tailLong);
        run("Part 1: MovingAverage", Test::movingAverage);
        run("Part 1: MovingAverage, random", Test::movingAverageRandom);
        run("Part 1: MovingAverage is O(1)", Test::movingAverageFast);
        run("Part 2: HitCounter", Test::hitCounter);
        run("Part 2: HitCounter, random", Test::hitCounterRandom);
        run("Part 2: HitCounter is O(1) amortized", Test::hitCounterFast);
        run("Part 2: RateLimiter", Test::rateLimiter);
        run("Part 2: RateLimiter, random", Test::rateLimiterRandom);
        run("Part 3: maxNaive", () -> smallWindows("maxNaive", Windows::maxNaive, true));
        run("Part 3: maxNaive, random", () -> randomWindows("maxNaive", Windows::maxNaive, true));
        run("Part 3: max", () -> smallWindows("max", Windows::max, true));
        run("Part 3: max, random", () -> randomWindows("max", Windows::max, true));
        run("Part 3: max is O(N)", () -> fastWindows("max", Windows::max, true));
        run("Part 3: min", () -> smallWindows("min", Windows::min, false));
        run("Part 3: min, random", () -> randomWindows("min", Windows::min, false));
        run("Part 3: min is O(N)", () -> fastWindows("min", Windows::min, false));
        if (optional)
            run("Optional: longestStable", Test::longestStable);

        System.out.println(failures == 0 ? "\nall tests passed" : "\n" + failures + " failed");
    }
}
