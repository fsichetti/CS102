package distributions;

import java.util.Random;

public abstract class Distribution {

    protected Random rng;

    public Distribution() {
        this(42L);
    }

    public Distribution(long seed) {
        rng = new Random(seed);
    }

    // Values are drawn from [0, range). A small range gives "few unique values": that is
    // a property of how you call generate, not a separate kind of distribution.
    // Must return a fresh array every call: the caller is free to sort it in place.
    public abstract int[] generate(int size, int range);

    // Given, complete: one row per requested size, same range for all of them.
    // Rows may have different lengths.
    public int[][] generateAll(int[] sizes, int range) {
        int[][] rows = new int[sizes.length][];
        for (int i = 0; i < sizes.length; i++) {
            rows[i] = generate(sizes[i], range);
        }
        return rows;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

}
