package search;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Given, complete. A corpus loaded from the .txt files of a folder: one document per file, in
// alphabetical order of file name. Document i is documents.get(i), from the file names.get(i).
public final class Corpus {

    public final List<String> names = new ArrayList<>();
    public final List<String> documents = new ArrayList<>();

    public Corpus(String folder) {
        File[] files = new File(folder).listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) throw new IllegalArgumentException("no such folder: " + folder);
        Arrays.sort(files);
        for (File f : files) {
            names.add(f.getName());
            try {
                documents.add(Files.readString(f.toPath()));
            } catch (IOException e) {
                throw new RuntimeException("could not read " + f, e);
            }
        }
    }

    public int size() { return documents.size(); }

    // The words of a document (with repeats, in order): lowercase, split on anything that is not a
    // letter or a digit. Index and the query Tokenizer both normalize words the same way: lowercase,
    // letters and digits only. Use this method in Index so the two stay consistent.
    public static List<String> words(String text) {
        List<String> words = new ArrayList<>();
        for (String w : text.toLowerCase().split("[^a-z0-9]+"))
            if (!w.isEmpty()) words.add(w);
        return words;
    }
}
