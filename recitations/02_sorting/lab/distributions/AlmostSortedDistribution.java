package distributions;

import java.util.Arrays;

// Sorted, then a few elements swapped with close neighbors.
public class AlmostSortedDistribution extends UniformDistribution {

    private static final int WINDOW = 8;    // how far a swap can reach
    private static final int RATE = 100;    // roughly 1 swap per RATE elements

    @Override
    public int[] generate(int size, int range) {
        int[] a = super.generate(size, range);
        Arrays.sort(a);
        if (size > 1) {
            int swaps = Math.max(1, size / RATE);
            for (int s = 0; s < swaps; s++) {
                int i = rng.nextInt(size);
                int j = Math.min(size - 1, i + 1 + rng.nextInt(WINDOW));
                int t = a[i];
                a[i] = a[j];
                a[j] = t;
            }
        }
        return a;
    }

}
