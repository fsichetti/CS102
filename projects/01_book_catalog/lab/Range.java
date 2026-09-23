public class Range<T extends Comparable<T>> {

    private T min;
    private T max;

    public void add(T value) {
        //TODO: implement this method
        //      after every call, getMin()/getMax() must reflect the smallest/largest value
        //      seen so far. Both fields start null - handle the first call.
    }

    public T getMin() { return min; }
    public T getMax() { return max; }

}
