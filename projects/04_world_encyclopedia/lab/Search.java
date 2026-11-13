import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import search.Corpus;
import search.Index;
import search.Query;

// Given, complete: the search REPL. Type "help" for the commands.
//
// Document names and text are tracked here, by id, separately from Corpus: Corpus only loads an
// initial folder, but documents added or removed afterward have no place in its two lists anymore.
public class Search {

    private static final String HELP = String.join("\n",
            "  index <folder>      loads the .txt files of a folder and builds the index",
            "  add <file>          adds one more document, under a new id",
            "  remove <doc>        removes document <doc> (by id or name)",
            "  search <query>      documents matching the query, e.g. fox AND (quick OR lazy)",
            "  top <n>             the n words appearing in the most documents",
            "  top <doc> <n>       the n most frequent words of document <doc> (by id or name)",
            "  words <doc>         the distinct words of document <doc> (by id or name)",
            "  stats               vocabulary size, number of documents, average documents per word",
            "  help, quit");

    private static Index index;
    private static final Map<Integer, String> names = new HashMap<>();
    private static final Map<Integer, String> texts = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("CS102 search. Type help for the commands.");
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            if (!in.hasNextLine()) break;
            if (!run(in.nextLine())) break;
        }
        in.close();
    }

    private static boolean run(String line) {
        line = line.trim();
        if (line.isEmpty()) return true;

        String[] first = line.split("\\s+", 2);
        String command = first[0];
        String rest = first.length > 1 ? first[1] : "";

        try {
            switch (command) {
                case "quit":
                    return false;
                case "help":
                    System.out.println(HELP);
                    break;
                case "index":
                    loadIndex(rest);
                    break;
                case "add":
                    requireIndex();
                    add(rest);
                    break;
                case "remove":
                    requireIndex();
                    remove(parseDoc(rest.trim()));
                    break;
                case "search":
                    requireIndex();
                    search(rest);
                    break;
                case "top":
                    requireIndex();
                    top(rest.trim().split("\\s+"));
                    break;
                case "words":
                    requireIndex();
                    words(parseDoc(rest.trim()));
                    break;
                case "stats":
                    requireIndex();
                    stats();
                    break;
                default:
                    System.out.println("unknown command: " + command);
            }
        } catch (RuntimeException e) {
            System.out.println("error: " + e.getMessage());
        }
        return true;
    }

    private static void loadIndex(String folder) {
        Corpus corpus = new Corpus(folder);
        List<String> keys = new ArrayList<>();
        for (String name : corpus.names) keys.add(nameKey(name));
        index = new Index(keys, corpus.documents);
        names.clear();
        texts.clear();
        for (int doc = 0; doc < corpus.size(); doc++) {
            names.put(doc, corpus.names.get(doc));
            texts.put(doc, corpus.documents.get(doc));
        }
        System.out.println(corpus.size() + " documents indexed");
    }

    private static void add(String file) {
        try {
            String text = Files.readString(Path.of(file));
            int doc = index.add(nameKey(file), text);
            names.put(doc, file);
            texts.put(doc, text);
            System.out.println("added as document " + doc);
        } catch (IOException e) {
            System.out.println("could not read " + file);
        }
    }

    // A doc argument, by id ("12") or by name ("switzerland"): Index owns the name lookup, this
    // only decides which of the two the user meant.
    private static int parseDoc(String token) {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            return index.idOf(token);
        }
    }

    // The name Index looks documents up by: the file name, without its folder or ".txt".
    private static String nameKey(String path) {
        String base = Path.of(path).getFileName().toString();
        return base.endsWith(".txt") ? base.substring(0, base.length() - 4) : base;
    }

    private static void remove(int doc) {
        index.remove(doc);     // throws IllegalArgumentException if doc does not exist: names and
        names.remove(doc);     // texts stay as they were, so this line only runs once that succeeds
        texts.remove(doc);
    }

    private static void words(int doc) {
        if (!texts.containsKey(doc)) throw new IllegalArgumentException("no such document: " + doc);
        System.out.println(new TreeSet<>(Corpus.words(texts.get(doc))));
    }

    private static void search(String query) {
        Set<Integer> matches = Query.search(index, query);
        if (matches.isEmpty()) {
            System.out.println("no matches");
            return;
        }
        for (int doc : new TreeSet<>(matches)) System.out.println(names.get(doc));
    }

    private static void top(String[] args) {
        if (args.length == 1) {
            for (Map.Entry<String, Integer> e : index.topWords(Integer.parseInt(args[0])))
                System.out.println(e.getKey() + "  " + e.getValue() + " document(s)");
        } else if (args.length == 2) {
            int doc = parseDoc(args[0]), n = Integer.parseInt(args[1]);
            for (Map.Entry<String, Integer> e : index.topWordsIn(doc, n))
                System.out.println(e.getKey() + "  " + e.getValue() + " occurrence(s)");
        } else {
            throw new IllegalArgumentException("usage: top <n> or top <doc> <n>");
        }
    }

    private static void requireIndex() {
        if (index == null) throw new IllegalStateException("no index loaded: use 'index <folder>' first");
    }

    // Vocabulary and average documents per word (average postings-list length, in IR terms),
    // recomputed here from the documents rather than stored on Index: it is only ever needed for
    // this one report.
    private static void stats() {
        Set<String> vocabulary = new HashSet<>();
        for (String text : texts.values()) vocabulary.addAll(Corpus.words(text));
        int totalPostings = 0;
        for (String w : vocabulary) totalPostings += index.documentsWith(w).size();
        double average = vocabulary.isEmpty() ? 0.0 : (double) totalPostings / vocabulary.size();
        System.out.printf("%d documents, %d distinct words, average %.2f documents per word%n",
                index.size(), vocabulary.size(), average);
    }
}
