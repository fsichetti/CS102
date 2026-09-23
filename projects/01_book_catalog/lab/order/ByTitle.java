package order;

import catalog.Book;

public class ByTitle implements BookOrder {

    @Override
    public int compare(Book a, Book b) {
        return a.getTitle().compareTo(b.getTitle());
    }

    @Override
    public int compareToQuery(Book b, String query) {
        return b.getTitle().compareTo(query);
    }

    @Override
    public String getName() { return "title"; }

}
