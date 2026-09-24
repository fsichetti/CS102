package sorting;

// Every sorting algorithm extends this class.
public abstract class SortingAlgorithm {

    // +1 per comparison, +1 per array write (a swap is +2).
    // less, set and swap count automatically; anything else you count yourself.
    protected long operations = 0;

    // Sort `a` in place, in ascending order.
    public abstract void sort(int [] a);

    // is a[i] < a[j] ?
    protected boolean less(int [] a, int i, int j) {
        operations++;
        return a[i] < a[j];
    }

    // is x < y ? for a value already taken out of the array
    protected boolean less(int x, int y) {
        operations++;
        return x < y;
    }

    // a[i] = v
    protected void set(int [] a, int i, int v) {
        operations++;
        a[i] = v;
    }

    protected void swap(int [] a, int i, int j) {
        int t = a[i];
        set(a, i, a[j]);
        set(a, j, t);
    }

    public long getOperations() { return operations; }
    public void resetOperations() { operations = 0; }

    public String getName() { return getClass().getSimpleName(); }
}
