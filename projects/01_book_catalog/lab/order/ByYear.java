public class ByYear implements BookOrder {

    @Override
    public int compare(Book a, Book b) {
        return Integer.compare(a.getYear(), b.getYear());
    }

    @Override
    public int compareToQuery(Book b, String query) {
        return Integer.compare(b.getYear(), Integer.parseInt(query));
    }

    @Override
    public String getName() { return "year"; }

}
