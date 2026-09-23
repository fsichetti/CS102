package searching;

import catalog.Book;
import order.BookOrder;

public abstract class SearchAlgorithm {

    // Yours to maintain: +1 per comparison. Searching moves nothing.
    protected long operations;

    // Compare a book against the value typed at the prompt with
    // order.compareToQuery(book, query): negative, zero, or positive.
    protected final BookOrder order;

    protected SearchAlgorithm(BookOrder order) { this.order = order; }

    public long getOperations() { return operations; }
    public void resetOperations() { operations = 0; }
    public String getOrderName() { return order.getName(); }

    // The one method you implement. Returns the index of a matching book, or -1.
    public abstract int find(Book[] a, String query);

}
