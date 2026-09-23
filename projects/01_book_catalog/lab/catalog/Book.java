package catalog;

public class Book {

    private static int nextId = 1;

    private final int id;
    private final String title;
    private final String author;
    private final int year;

    public Book(String title, String author, int year) {
        this.id = nextId++;
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }

    @Override
    public String toString() {
        return id + ": " + title + " - " + author + " (" + year + ")";
    }

}
