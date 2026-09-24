package sorting;

// Quicksort without a pivot rule: each subclass decides which element is the pivot.
public abstract class QuickSort extends SortingAlgorithm {

    public void sort(int [] a) {
        if (a.length < 2) return;
        quickSort(a, 0, a.length - 1);
    }

    private void quickSort(int [] a, int left, int right) {
        //TODO: implement this method
        //      choosePivot, then partition, then recurse on the sides that need it
    }

    // Moves the pivot a[pivotIndex] to its final position, smaller elements to its left,
    // the others to its right, and returns the pivot's final index.
    protected int partition(int [] a, int left, int right, int pivotIndex) {
        //TODO: implement this method
        return left;
    }

    // Returns the INDEX of the pivot, somewhere in [left, right].
    protected abstract int choosePivot(int [] a, int left, int right);
}
