import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Catalog {

    private ArrayList<Book> books = new ArrayList<>();
    private String sortedBy = null;   // null = not known to be sorted by anything; else e.g. "title"

    public void add(Book b) { books.add(b); sortedBy = null; }
    public int size() { return books.size(); }
    public String getSortedBy() { return sortedBy; }
    public Book[] toArray() { return books.toArray(new Book[0]); }

    public void applySort(SortingAlgorithm algorithm) {
        Book[] a = books.toArray(new Book[0]);
        algorithm.sort(a);
        books = new ArrayList<>(Arrays.asList(a));
        sortedBy = algorithm.getOrderName();
    }

    public int find(SearchAlgorithm algorithm, String query) {
        return algorithm.find(books.toArray(new Book[0]), query);
    }

    public void list() {
        for (Book b : books) System.out.println(b);
    }

    public void loadFromFile(String path) throws FileNotFoundException {
        //TODO: implement this method
        //      read one book per line from `path` (format: title,author,year) and call add(...)
        //      for each valid line. FileNotFoundException is checked, and declared here rather
        //      than caught - Main's REPL loop catches it. Your choice how to handle a malformed
        //      line (skipping it with a message is reasonable); say what you chose in a comment.
    }

}
