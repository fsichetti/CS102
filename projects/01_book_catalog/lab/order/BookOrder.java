package order;

import catalog.Book;
import java.util.Comparator;

// The field-selection mechanism: genuinely a Comparator<Book>, not just shaped like one.
// compare(Book, Book) is inherited from Comparator - not redeclared here.
public interface BookOrder extends Comparator<Book> {

    // Compares a book's field against a raw value typed at the REPL prompt. Numeric fields
    // parse the query themselves, so callers never need to know a field's Java type.
    int compareToQuery(Book b, String query);

    // "id" / "title" / "author" / "year" - used both for REPL routing and for Catalog's
    // sortedBy bookkeeping.
    String getName();

}
