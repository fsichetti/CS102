package order;

import catalog.Book;

public class ById implements BookOrder {

    @Override
    public int compare(Book a, Book b) {
        return Integer.compare(a.getId(), b.getId());
    }

    @Override
    public int compareToQuery(Book b, String query) {
        return Integer.compare(b.getId(), Integer.parseInt(query));
    }

    @Override
    public String getName() { return "id"; }

}
