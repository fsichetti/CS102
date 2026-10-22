/*  Part 3: maxNaive against max, on random arrays.
 *      javac Benchmark.java
 *      java Benchmark
 *
 *  Table 1: N fixed, k doubling.  Table 2: k fixed, N doubling.  Times in milliseconds.
 *  maxNaive is skipped once it takes more than 3 seconds.
 */
import java.util.Arrays;
import java.util.Random;
import windows.Windows;

public class Benchmark {
    static final Random rnd = new Random(102);
    static boolean naiveTooSlow;

    public static void main(String[] args) {
        try {
            Windows.max(new double[] {1, 2}, 1);
            Windows.maxNaive(new double[] {1, 2}, 1);
        } catch (UnsupportedOperationException e) {
            System.out.println("not written yet: " + e.getMessage());
            return;
        }
        warmUp();

        int n = 1_000_000;
        System.out.printf("N = %d%n%8s %10s %10s%n", n, "k", "maxNaive", "max");
        double[] a = random(n);
        naiveTooSlow = false;
        for (int k = 1; k <= 1 << 16; k *= 4) row(k, a, k);

        int k = 100;
        System.out.printf("%nk = %d%n%8s %10s %10s%n", k, "N", "maxNaive", "max");
        naiveTooSlow = false;
        for (n = 250_000; n <= 8_000_000; n *= 2) row(n, random(n), k);
    }

    static void row(int label, double[] a, int k) {
        long start = System.nanoTime();
        double[] fast = Windows.max(a, k);
        double fastMillis = (System.nanoTime() - start) / 1e6;

        String naive = "-";
        if (!naiveTooSlow) {
            start = System.nanoTime();
            double[] slow = Windows.maxNaive(a, k);
            double naiveMillis = (System.nanoTime() - start) / 1e6;
            naive = String.format("%.0f", naiveMillis);
            naiveTooSlow = naiveMillis > 3000;
            if (!Arrays.equals(slow, fast)) System.out.println("!! maxNaive and max give different results");
        }
        System.out.printf("%8d %10s %10.0f%n", label, naive, fastMillis);
    }

    static double[] random(int n) {
        double[] a = new double[n];
        for (int i = 0; i < n; i++) a[i] = rnd.nextDouble();
        return a;
    }

    // the first runs are slower: the JVM is still compiling the code
    static void warmUp() {
        double[] a = random(100_000);
        for (int r = 0; r < 20; r++) {
            Windows.max(a, 10);
            Windows.maxNaive(a, 10);
        }
    }
}
