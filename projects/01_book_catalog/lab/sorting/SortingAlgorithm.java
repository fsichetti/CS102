package sorting;

import catalog.Book;
import java.util.Arrays;
import order.BookOrder;

public abstract class SortingAlgorithm {

    // Yours to maintain: +1 per comparison and +1 per array write (so a swap is +2).
    protected long operations;

    // Compare two books with order.compare(x, y): negative, zero, or positive.
    protected final BookOrder order;

    protected SortingAlgorithm(BookOrder order) { this.order = order; }

    // The one method you implement. Sort `a` in place, ascending by `order`, counting as you go.
    public abstract void sort(Book[] a);

    public long getOperations() { return operations; }
    public void resetOperations() { operations = 0; }

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
