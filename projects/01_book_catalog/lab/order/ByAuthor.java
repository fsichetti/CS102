package order;

import catalog.Book;

public class ByAuthor implements BookOrder {

    @Override
    public int compare(Book a, Book b) {
        return a.getAuthor().compareTo(b.getAuthor());
    }

    @Override
    public int compareToQuery(Book b, String query) {
        return b.getAuthor().compareTo(query);
    }

    @Override
    public String getName() { return "author"; }

}
