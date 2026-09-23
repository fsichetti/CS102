package searching;

import catalog.Book;
import order.BookOrder;

public abstract class SearchAlgorithm {

    // Yours to maintain: one per comparison. Searching moves nothing, so there is no move counter.
    protected long comparisons;

    // Compare a book against the value typed at the prompt with
    // order.compareToQuery(book, query): negative, zero, or positive.
    protected final BookOrder order;

    protected SearchAlgorithm(BookOrder order) { this.order = order; }

    public long getComparisons() { return comparisons; }
    public void resetCounters() { comparisons = 0; }
    public String getOrderName() { return order.getName(); }

    // The one method you implement. Returns the index of a matching book, or -1.
    public abstract int find(Book[] a, String query);

}
