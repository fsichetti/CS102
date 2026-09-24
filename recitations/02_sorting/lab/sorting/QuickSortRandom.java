package sorting;

import java.util.Random;

public class QuickSortRandom extends QuickSort {

    // created once, with a fixed seed, so that every run gives the same result
    private Random rng = new Random(42);

    protected int choosePivot(int [] a, int left, int right) {
        //TODO: implement this method
        return -1;
    }
}
