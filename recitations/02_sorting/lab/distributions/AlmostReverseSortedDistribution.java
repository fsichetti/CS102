package distributions;

// Almost sorted, then reversed.
public class AlmostReverseSortedDistribution extends AlmostSortedDistribution {

    @Override
    public int[] generate(int size, int range) {
        int[] a = super.generate(size, range);
        for (int i = 0; i < a.length / 2; i++) {
            int t = a[i];
            a[i] = a[a.length - 1 - i];
            a[a.length - 1 - i] = t;
        }
        return a;
    }

}
