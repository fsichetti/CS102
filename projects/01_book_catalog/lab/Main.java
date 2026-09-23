import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    // Given, complete: the four field orders are fixed and known in advance, unlike algorithms.
    private static BookOrder makeOrder(String fieldName) {
        switch (fieldName) {
            case "id": return new ById();
            case "title": return new ByTitle();
            case "author": return new ByAuthor();
            case "year": return new ByYear();
            default: throw new IllegalArgumentException("unknown field: " + fieldName);
        }
    }

    private static SortingAlgorithm makeSortAlgorithm(String name, BookOrder order) {
        switch (name) {
            //TODO: add one case per sorting algorithm you write, e.g.
            // case "insertion": return new InsertionSort(order);
            default: throw new IllegalArgumentException("unknown algorithm: " + name);
        }
    }

    private static SearchAlgorithm makeSearchAlgorithm(String name, BookOrder order) {
        switch (name) {
            //TODO: add one case per search algorithm you write, e.g.
            // case "linear": return new LinearSearch(order);
            default: throw new IllegalArgumentException("unknown algorithm: " + name);
        }
    }

    // Given, complete: prints the smallest/largest value of one field across the whole catalog.
    // Works the moment Range.add is implemented - nothing else to wire up.
    private static void doRange(Catalog catalog, String field) {
        Book[] books = catalog.toArray();
        if (books.length == 0) {
            System.out.println("catalog is empty");
            return;
        }
        switch (field) {
            case "id": {
                Range<Integer> r = new Range<>();
                for (Book b : books) r.add(b.getId());
                System.out.println("lowest id: " + r.getMin() + "  highest id: " + r.getMax());
                break;
            }
            case "title": {
                Range<String> r = new Range<>();
                for (Book b : books) r.add(b.getTitle());
                System.out.println("first title: " + r.getMin() + "  last title: " + r.getMax());
                break;
            }
            case "author": {
                Range<String> r = new Range<>();
                for (Book b : books) r.add(b.getAuthor());
                System.out.println("first author: " + r.getMin() + "  last author: " + r.getMax());
                break;
            }
            case "year": {
                Range<Integer> r = new Range<>();
                for (Book b : books) r.add(b.getYear());
                System.out.println("earliest: " + r.getMin() + "  latest: " + r.getMax());
                break;
            }
            default: throw new IllegalArgumentException("unknown field: " + field);
        }
    }

    public static void main(String[] args) {
        Catalog catalog = new Catalog();
        Scanner in = new Scanner(System.in);

        System.out.println("commands: add <title>,<author>,<year> | load <filename> | "
                + "sort <field> <algorithm> | find <field> <algorithm> <value> | range <field> | "
                + "list | count | quit");
        System.out.println("fields: id title author year");

        while (true) {
            System.out.print("> ");
            if (!in.hasNextLine()) break;
            String line = in.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] first = line.split("\\s+", 2);
            String command = first[0];
            String rest = first.length > 1 ? first[1] : "";

            try {
                switch (command) {
                    case "quit":
                        in.close();
                        return;

                    case "add": {
                        String[] fields = rest.split(",", 3);
                        if (fields.length != 3) {
                            System.out.println("usage: add <title>,<author>,<year>");
                            break;
                        }
                        int year = Integer.parseInt(fields[2].trim());
                        catalog.add(new Book(fields[0].trim(), fields[1].trim(), year));
                        System.out.println("added");
                        break;
                    }

                    case "load": {
                        String path = rest.trim();
                        try {
                            int before = catalog.size();
                            catalog.loadFromFile(path);
                            System.out.println("loaded " + (catalog.size() - before) + " books");
                        } catch (FileNotFoundException e) {
                            System.out.println("could not open " + path + ": " + e.getMessage());
                        }
                        break;
                    }

                    case "sort": {
                        String[] parts = rest.split("\\s+", 2);
                        if (parts.length != 2) {
                            System.out.println("usage: sort <field> <algorithm>");
                            break;
                        }
                        BookOrder order = makeOrder(parts[0]);
                        SortingAlgorithm algorithm = makeSortAlgorithm(parts[1], order);
                        long start = System.nanoTime();
                        catalog.applySort(algorithm);
                        double ms = (System.nanoTime() - start) / 1_000_000.0;
                        System.out.printf("sorted %d books by %s using %s: %d comparisons, "
                                        + "%d moves, %.3f ms%n",
                                catalog.size(), algorithm.getOrderName(), algorithm.getName(),
                                algorithm.getComparisons(), algorithm.getMoves(), ms);
                        break;
                    }

                    case "find": {
                        String[] parts = rest.split("\\s+", 3);
                        if (parts.length != 3) {
                            System.out.println("usage: find <field> <algorithm> <value>");
                            break;
                        }
                        BookOrder order = makeOrder(parts[0]);
                        SearchAlgorithm algorithm = makeSearchAlgorithm(parts[1], order);
                        long start = System.nanoTime();
                        int index = catalog.find(algorithm, parts[2]);
                        double ms = (System.nanoTime() - start) / 1_000_000.0;
                        String result = (index == -1) ? "not found" : ("found: " + catalog.toArray()[index]);
                        System.out.printf("%s (%d comparisons, %.3f ms)%n",
                                result, algorithm.getComparisons(), ms);
                        break;
                    }

                    case "range":
                        doRange(catalog, rest.trim());
                        break;

                    case "list":
                        catalog.list();
                        break;

                    case "count":
                        System.out.println(catalog.size());
                        break;

                    default:
                        System.out.println("unknown command: " + command);
                }
            } catch (RuntimeException e) {
                System.out.println("error: " + e.getMessage());
            }
        }

        in.close();
    }

}
