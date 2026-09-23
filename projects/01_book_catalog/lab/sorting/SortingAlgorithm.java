import java.util.Arrays;

public abstract class SortingAlgorithm {

    // Yours to maintain: one comparison and one array write each. A swap is two moves.
    protected long comparisons;
    protected long moves;

    // Compare two books with order.compare(x, y): negative, zero, or positive.
    protected final BookOrder order;

    protected SortingAlgorithm(BookOrder order) { this.order = order; }

    // The one method you implement. Sort `a` in place, ascending by `order`, counting as you go.
    public abstract void sort(Book[] a);

    public long getComparisons() { return comparisons; }
    public long getMoves() { return moves; }
    public long getOpCount() { return comparisons + moves; }
    public void resetCounters() { comparisons = 0; moves = 0; }

    public String getName() { return getClass().getSimpleName(); }
    public String getOrderName() { return order.getName(); }

    // Given, complete: sorts a copy of `a` and compares it against the JDK's own sort. A BookOrder
    // is a Comparator, so Arrays.sort can use it directly. Compared with order.compare rather than
    // equals, since two books that tie on the sort field may legitimately end up in either order.
    public boolean testSort(Book[] a) {
        Book[] result = Arrays.copyOf(a, a.length);
        Book[] expected = Arrays.copyOf(a, a.length);
        sort(result);
        Arrays.sort(expected, order);
        for (int i = 0; i < result.length; i++) {
            if (order.compare(result[i], expected[i]) != 0) return false;
        }
        return true;
    }

}
