import java.util.Arrays;

import sorting.*;

// Runs every algorithm on a few small arrays and prints the result.
public class Test {

    public static void main (String [] args) {
        int [][] cases = {
            {},
            {5},
            {2, 1},
            {1, 2},
            {3, 3, 3},
            {10, 7, 12, 3, 8, 9, 1},
            {1, 3, 7, 8, 9, 10, 12},
            {12, 10, 9, 8, 7, 3, 1},
            {2, 0, 1, 2, 0, 1, 1, 0},
            {-4, 2, 0, -1, 3},
        };
        for (SortingAlgorithm s : Benchmark.algorithms()) {
            boolean ok = true;
            System.out.println(s.getName());
            for (int [] c : cases) {
                int [] result = Arrays.copyOf(c, c.length);
                try {
                    s.sort(result);
                } catch (RuntimeException e) {
                    System.out.println("  !! " + Arrays.toString(c) + " -> " + e);
                    ok = false;
                    continue;
                }
                int [] expected = Arrays.copyOf(c, c.length);
                Arrays.sort(expected);
                boolean good = Arrays.equals(result, expected);
                ok &= good;
                System.out.println("  " + (good ? "   " : "!! ") + Arrays.toString(c) + " -> " + Arrays.toString(result));
            }
            System.out.println(ok ? "  OK\n" : "  FAILED\n");
        }
    }
}
