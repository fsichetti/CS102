import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import distributions.*;
import sorting.*;

// Runs every algorithm on every kind of input, prints the number of operations and the
// running time, and draws the charts into the folder charts/.
public class Benchmark {

    static final int [] ALL_SIZES = { 50, 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600 };
    static int [] SIZES = ALL_SIZES;
    static final int RANGE = 1_000_000;

    // the algorithms to compare
    static SortingAlgorithm [] algorithms() {
        return new SortingAlgorithm [] {
            new BubbleSort(), new SelectionSort(), new InsertionSort(), new MergeSort(),
            new QuickSortFirst(), new QuickSortMiddle(), new QuickSortRandom(), new QuickSortMedianOfThree()
        };
    }

    // the kinds of input: a distribution, the range its values are drawn from, and a label
    static final Distribution [] DISTRIBUTIONS = {
        new UniformDistribution(), new AlmostSortedDistribution(),
        new AlmostReverseSortedDistribution(), new UniformDistribution()
    };
    static final int [] RANGES = { RANGE, RANGE, RANGE, 3 };
    static final String [] LABELS = { "uniform", "almost sorted", "almost reverse sorted", "few unique" };

    // Runs the benchmark on a thread with a larger stack (256 MB): a quicksort that degenerates
    // recurses once per element, deeper than the default stack allows.
    // java Benchmark        runs all sizes
    // java Benchmark 800    runs only sizes up to 800
    public static void main (String [] args) throws InterruptedException {
        if (args.length > 0) SIZES = upTo(Integer.parseInt(args[0]));
        Thread t = new Thread(null, new Runnable() {
            public void run() {
                runBenchmark();
            }
        }, "benchmark", 256L << 20);
        t.start();
        t.join();
    }

    static int [] upTo(int max) {
        int count = 0;
        while (count < ALL_SIZES.length && ALL_SIZES[count] <= max) count++;
        return Arrays.copyOf(ALL_SIZES, Math.max(count, 2));   // at least two sizes, for the ratio
    }

    static void runBenchmark() {
        SortingAlgorithm [] algorithms = keepWorking(algorithms());
        if (algorithms.length == 0) return;
        String [] names = new String[algorithms.length];
        for (int s = 0; s < algorithms.length; s++) names[s] = algorithms[s].getName();

        // [k][s][i]: input kind k, algorithm s, size SIZES[i]
        double [][][] operations = new double[LABELS.length][algorithms.length][SIZES.length];
        double [][][] millis = new double[LABELS.length][algorithms.length][SIZES.length];

        for (int k = 0; k < LABELS.length; k++) {
            // generated once, so that every algorithm sorts exactly the same arrays
            int [][] inputs = DISTRIBUTIONS[k].generateAll(SIZES, RANGES[k]);
            for (int i = 0; i < SIZES.length; i++) {
                for (int s = 0; s < algorithms.length; s++) {
                    int [] copy = Arrays.copyOf(inputs[i], inputs[i].length);   // sorting is destructive
                    algorithms[s].resetOperations();
                    long start = System.nanoTime();
                    algorithms[s].sort(copy);
                    millis[k][s][i] = (System.nanoTime() - start) / 1e6;
                    operations[k][s][i] = algorithms[s].getOperations();
                    if (!isSorted(copy)) {
                        System.out.println("!! " + names[s] + " did not sort \"" + LABELS[k] + "\" correctly");
                    }
                }
            }
            System.out.println("=== " + LABELS[k] + " ===\n");
            printTable("operations", names, operations[k], "%10.0f");
            printTable("time (ms)", names, millis[k], "%10.3f");
        }

        // the charts are a bonus: if drawing fails on some machine, the tables above are enough
        try {
            drawCharts(names, operations, millis);
            System.out.println("charts written to charts/");
        } catch (Throwable e) {
            System.out.println("could not draw the charts (" + e + "); the tables above have the same data");
        }
    }

    // Leaves out the algorithms that don't sort yet (e.g. still stubs), with a message.
    // The ones that work get one untimed warm-up run: the JVM compiles code the first times it
    // runs it, and that would otherwise be included in the first measurements.
    static SortingAlgorithm [] keepWorking(SortingAlgorithm [] all) {
        ArrayList<SortingAlgorithm> ok = new ArrayList<>();
        int [] warmUp = new UniformDistribution().generate(2000, RANGE);
        for (SortingAlgorithm s : all) {
            int [] a = new UniformDistribution().generate(100, RANGE);
            boolean works;
            try {
                s.sort(a);
                works = isSorted(a) && s.getOperations() > 0;
                if (works) s.sort(Arrays.copyOf(warmUp, warmUp.length));
            } catch (RuntimeException e) {
                works = false;
            }
            if (works) ok.add(s);
            else System.out.println("skipping " + s.getName() + ": it does not sort yet (run Test)");
        }
        System.out.println();
        return ok.toArray(new SortingAlgorithm[0]);
    }

    static boolean isSorted(int [] a) {
        for (int i = 0; i + 1 < a.length; i++) if (a[i] > a[i + 1]) return false;
        return true;
    }

    static void printTable(String title, String [] names, double [][] v, String format) {
        int w = title.length();
        for (String name : names) w = Math.max(w, name.length());
        String nameCol = "%-" + (w + 1) + "s";
        System.out.printf(nameCol, title);
        for (int n : SIZES) System.out.printf("%10d", n);
        System.out.printf("%8s%n", "ratio");
        for (int s = 0; s < names.length; s++) {
            System.out.printf(nameCol, names[s]);
            for (int i = 0; i < SIZES.length; i++) System.out.printf(format, v[s][i]);
            // how much the value grew over the last step in N
            int last = SIZES.length - 1;
            System.out.printf("%8.2f%n", v[s][last] / Math.max(1e-9, v[s][last - 1]));
        }
        System.out.println();
    }

    // Per kind of input: operations and time, all algorithms.
    // Per algorithm: operations, all kinds of input.
    static void drawCharts(String [] names, double [][][] operations, double [][][] millis) throws IOException {
        new File("charts").mkdirs();
        double [] n = new double[SIZES.length];
        for (int i = 0; i < SIZES.length; i++) n[i] = SIZES[i];

        for (int k = 0; k < LABELS.length; k++) {
            String file = LABELS[k].replace(' ', '_');
            chart("Operations: " + LABELS[k], "operations", names, n, operations[k],
                    "charts/operations_" + file + ".png");
            chart("Time: " + LABELS[k], "milliseconds", names, n, millis[k],
                    "charts/time_" + file + ".png");
        }
        for (int s = 0; s < names.length; s++) {
            double [][] ys = new double[LABELS.length][];
            for (int k = 0; k < LABELS.length; k++) ys[k] = operations[k][s];
            chart("Operations: " + names[s], "operations", LABELS, n, ys,
                    "charts/algorithm_" + names[s] + ".png");
        }
    }

    // log-log and linear chart side by side, one line per series, all over the same sizes
    static void chart(String title, String yLabel, String [] series, double [] n, double [][] ys,
                      String path) throws IOException {
        double [][] xs = new double[series.length][];
        double [][] positive = new double[series.length][];
        for (int s = 0; s < series.length; s++) {
            xs[s] = n;
            positive[s] = new double[ys[s].length];
            for (int i = 0; i < ys[s].length; i++) positive[s][i] = Math.max(ys[s][i], 1e-4);   // log scale
        }
        Chart.draw(title, "N", yLabel, series, xs, positive, path);
    }
}
