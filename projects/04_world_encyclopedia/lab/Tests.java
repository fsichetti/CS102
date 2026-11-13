import java.util.*;

import search.*;

// Given, complete. Run with
//     java Tests                  every section
//     java Tests index query      only the named sections
//
// Sections: index, setops, query.
public class Tests {

    static int passed, failed;
    static boolean timedOut;

    // A small fixed corpus, shared by every test in this file: document i is docs.get(i).
    static final List<String> DOCS = List.of(
            "the quick brown fox jumps over the lazy dog",      // 0
            "a quick fox and a quick dog play in the yard",     // 1
            "the lazy cat sleeps all day",                      // 2
            "dogs and cats are not friends",                    // 3
            "foxes hunt at night");                             // 4

    public static void main(String[] args) {
        Set<String> only = new HashSet<>(Arrays.asList(args));
        section(only, "index", Tests::index);
        section(only, "setops", Tests::setops);
        section(only, "query", Tests::query);
        System.out.printf("%n%d/%d passed%n", passed, passed + failed);
    }

    // ---------------------------------------------------------------------------------------------
    // Part 1: the index

    static void index() {
        test("size is the number of documents", () -> {
            assertEquals(5, new Index(DOCS).size());
            assertEquals(0, new Index(List.of()).size());
        });
        test("documentsWith: present and absent words", () -> {
            Index idx = new Index(DOCS);
            assertEquals(Set.of(0, 1), idx.documentsWith("fox"));
            assertEquals(Set.of(0, 1), idx.documentsWith("quick"));
            assertEquals(Set.of(0, 1, 2), idx.documentsWith("the"));
            assertEquals(Set.of(2), idx.documentsWith("cat"));
            assertEquals(Set.of(), idx.documentsWith("zzz"));
        });
        test("words are matched exactly: dog and dogs are different words", () -> {
            Index idx = new Index(DOCS);
            assertEquals(Set.of(0, 1), idx.documentsWith("dog"));
            assertEquals(Set.of(3), idx.documentsWith("dogs"));
        });
        test("documentsWith returns a copy", () -> {
            Index idx = new Index(DOCS);
            Set<Integer> s = idx.documentsWith("fox");
            s.clear();
            assertEquals(Set.of(0, 1), idx.documentsWith("fox"), "the index must be unaffected");
        });
        test("frequency counts repeats, 0 if absent", () -> {
            Index idx = new Index(DOCS);
            assertEquals(2, idx.frequency(1, "quick"));
            assertEquals(1, idx.frequency(0, "fox"));
            assertEquals(0, idx.frequency(0, "cat"));
            assertEquals(0, idx.frequency(2, "fox"));
        });
        test("case does not matter: words are lowercased", () -> {
            Index idx = new Index(List.of("Fox FOX fox"));
            assertEquals(3, idx.frequency(0, "fox"));
        });
        test("add: assigns the next id, after the initial documents", () -> {
            Index idx = new Index(DOCS);
            int id = idx.add("wolves howl at the moon");
            assertEquals(5, id);
            assertEquals(6, idx.size());
            assertEquals(Set.of(5), idx.documentsWith("wolves"));
        });
        test("remove: undoes add, in postings and term frequency", () -> {
            Index idx = new Index(DOCS);
            idx.remove(0);
            assertEquals(4, idx.size());
            assertEquals(Set.of(1), idx.documentsWith("fox"));
            assertEquals(0, idx.frequency(0, "fox"));
        });
        test("remove: a word with no other occurrence disappears from postings", () -> {
            Index idx = new Index(DOCS);
            idx.remove(2);
            assertEquals(Set.of(), idx.documentsWith("cat"));
        });
        test("remove: an id that does not exist throws IllegalArgumentException", () -> {
            Index idx = new Index(DOCS);
            assertThrows(IllegalArgumentException.class, () -> idx.remove(99), "remove(99)");
            idx.remove(0);
            assertThrows(IllegalArgumentException.class, () -> idx.remove(0), "remove(0) twice");
        });
        test("ids are never reused, even after a remove", () -> {
            Index idx = new Index(DOCS);
            idx.remove(0);
            idx.remove(1);
            int id = idx.add("new document");
            assertEquals(5, id, "the new id");
            assertEquals(Set.of(2, 3, 4, 5), idx.not(Set.of()), "the live ids");
        });
        test("add(name, document), idOf: round trip", () -> {
            Index idx = new Index(DOCS);
            int doc = idx.add("wolves", "wolves howl at the moon");
            assertEquals(doc, idx.idOf("wolves"));
        });
        test("idOf: an unused name throws IllegalArgumentException", () -> {
            Index idx = new Index(DOCS);
            assertThrows(IllegalArgumentException.class, () -> idx.idOf("nope"), "idOf(\"nope\")");
        });
        test("idOf: a name added twice finds the most recent document", () -> {
            Index idx = new Index(DOCS);
            idx.add("x", "alpha");
            int d2 = idx.add("x", "beta");
            assertEquals(d2, idx.idOf("x"));
        });
        test("Index(names, documents): names and ids line up", () -> {
            Index idx = new Index(List.of("x", "y"), List.of("alpha", "beta"));
            assertEquals(0, idx.idOf("x"));
            assertEquals(1, idx.idOf("y"));
        });
        test("remove: forgetting the current holder of a name makes idOf throw", () -> {
            Index idx = new Index(DOCS);
            int doc = idx.add("wolves", "wolves howl at the moon");
            idx.remove(doc);
            assertThrows(IllegalArgumentException.class, () -> idx.idOf("wolves"), "idOf(\"wolves\") after remove");
        });
        test("remove: removing an older, shadowed holder of a name does not disturb the current one", () -> {
            Index idx = new Index(DOCS);
            int d1 = idx.add("x", "alpha");
            int d2 = idx.add("x", "beta");
            idx.remove(d1);
            assertEquals(d2, idx.idOf("x"), "idOf(\"x\") must still find the current holder");
        });
        test("topWords: stopwords are excluded, even though they are the most frequent", () -> {
            Index idx = new Index(DOCS);
            for (Map.Entry<String, Integer> e : idx.topWords(100)) {
                assertTrue(!e.getKey().equals("the") && !e.getKey().equals("and") && !e.getKey().equals("a"),
                        "stopword in topWords: " + e.getKey());
            }
        });
        test("topWords: the most common non-stopword first", () -> {
            Index idx = new Index(DOCS);
            assertEquals(List.of(Map.entry("dog", 2)), idx.topWords(1));
        });
        test("topWords: ties broken alphabetically", () -> {
            Index idx = new Index(DOCS);
            assertEquals(List.of(Map.entry("dog", 2), Map.entry("fox", 2), Map.entry("lazy", 2)), idx.topWords(3));
        });
        test("topWords: fewer than n if the vocabulary is smaller", () -> {
            Index idx = new Index(DOCS);
            assertEquals(18, idx.topWords(100).size());
        });
        test("topWords: n = 0 is empty", () -> {
            assertEquals(List.of(), new Index(DOCS).topWords(0));
        });
        test("topWords: reflects add and remove", () -> {
            Index idx = new Index(List.of("fox fox", "fox"));
            assertEquals(List.of(Map.entry("fox", 2)), idx.topWords(1));
            idx.remove(0);
            assertEquals(List.of(Map.entry("fox", 1)), idx.topWords(1));
        });
        test("topWordsIn: the most frequent non-stopword words of one document", () -> {
            Index idx = new Index(DOCS);
            assertEquals(List.of(Map.entry("quick", 2)), idx.topWordsIn(1, 1));
        });
        test("topWordsIn: ties broken alphabetically", () -> {
            Index idx = new Index(DOCS);
            assertEquals(List.of(Map.entry("brown", 1), Map.entry("dog", 1), Map.entry("fox", 1)), idx.topWordsIn(0, 3));
        });
        test("topWordsIn: stopwords are excluded", () -> {
            Index idx = new Index(DOCS);
            for (Map.Entry<String, Integer> e : idx.topWordsIn(1, 100)) {
                assertTrue(!e.getKey().equals("the") && !e.getKey().equals("a") && !e.getKey().equals("in"),
                        "stopword in topWordsIn: " + e.getKey());
            }
        });
        test("topWordsIn: a document that does not exist is empty", () -> {
            assertEquals(List.of(), new Index(DOCS).topWordsIn(99, 5));
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Part 2: and, or, not

    static void setops() {
        test("and: intersection", () -> {
            Index idx = new Index(DOCS);
            assertEquals(Set.of(0, 1), idx.and(List.of(idx.documentsWith("fox"), idx.documentsWith("quick"))));
            assertEquals(Set.of(), idx.and(List.of(idx.documentsWith("fox"), idx.documentsWith("cat"))));
        });
        test("and: three sets", () -> {
            Index idx = new Index(DOCS);
            Set<Integer> all = Set.of(0, 1, 2, 3, 4);
            assertEquals(Set.of(0, 1),
                    idx.and(List.of(all, idx.documentsWith("fox"), idx.documentsWith("quick"))));
        });
        test("and: one set is that set", () -> {
            Index idx = new Index(DOCS);
            assertEquals(Set.of(0, 1), idx.and(List.of(idx.documentsWith("fox"))));
        });
        test("and: no sets is an error", () -> {
            Index idx = new Index(DOCS);
            assertThrows(IllegalArgumentException.class, () -> idx.and(List.of()), "and()");
        });
        test("or: union", () -> {
            Index idx = new Index(DOCS);
            assertEquals(Set.of(0, 1, 2), idx.or(List.of(idx.documentsWith("cat"), idx.documentsWith("fox"))));
        });
        test("or: no sets is an error", () -> {
            Index idx = new Index(DOCS);
            assertThrows(IllegalArgumentException.class, () -> idx.or(List.of()), "or()");
        });
        test("not: every other document", () -> {
            Index idx = new Index(DOCS);
            assertEquals(Set.of(2, 3, 4), idx.not(idx.documentsWith("fox")));
            assertEquals(Set.of(0, 1, 2, 3, 4), idx.not(Set.of()));
        });
        test("and, or, not leave their arguments unchanged", () -> {
            Index idx = new Index(DOCS);
            Set<Integer> a = idx.documentsWith("fox");
            Set<Integer> b = idx.documentsWith("quick");
            Set<Integer> aBefore = new HashSet<>(a), bBefore = new HashSet<>(b);
            idx.and(List.of(a, b));
            idx.or(List.of(a, b));
            idx.not(a);
            assertEquals(aBefore, a, "a changed");
            assertEquals(bBefore, b, "b changed");
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Part 3: the query language

    static void query() {
        test("a single word", () -> assertSearch("fox", Set.of(0, 1)));
        test("AND of two words", () -> assertSearch("fox AND quick", Set.of(0, 1)));
        test("OR of two words", () -> assertSearch("cat OR fox", Set.of(0, 1, 2)));
        test("NOT a word", () -> assertSearch("NOT fox", Set.of(2, 3, 4)));
        test("AND of three words, chained", () -> assertSearch("the AND lazy AND dog", Set.of(0)));
        test("nested AND/OR/NOT, with parentheses", () -> assertSearch("NOT fox AND (cat OR dogs)", Set.of(2, 3)));
        test("precedence: NOT, then AND, then OR, without parentheses",
                () -> assertSearch("fox OR NOT quick AND lazy", Set.of(0, 1, 2)));
        test("parentheses override precedence", () -> assertSearch("(fox OR cat) AND NOT dog", Set.of(2)));
        test("no matches", () -> assertSearch("fox AND cat", Set.of()));
        test("whitespace around tokens does not matter", () -> assertSearch("  fox   AND  quick  ", Set.of(0, 1)));

        for (String bad : new String[] {"", "AND fox", "fox AND", "(fox", "fox)", "fox fox", "NOT", "()"}) {
            rejects("search(\"" + bad + "\")", bad);
        }
        test("search reflects documents added and removed afterward", () -> {
            Index idx = new Index(DOCS);
            idx.remove(0);                            // doc 0 was the other fox document
            int id = idx.add("a fox and a fox");       // id 5
            assertSearch(idx, "fox", Set.of(1, 5));
        });
    }

    static void assertSearch(String queryText, Set<Integer> expected) { assertSearch(new Index(DOCS), queryText, expected); }

    static void assertSearch(Index idx, String queryText, Set<Integer> expected) {
        assertEquals(expected, Query.search(idx, queryText), "search(\"" + queryText + "\")");
    }

    static void rejects(String name, String queryText) {
        test(name + " throws IllegalArgumentException", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> Query.search(new Index(DOCS), queryText), name));
    }

    // ---------------------------------------------------------------------------------------------
    // Helpers

    interface Test { void run() throws Exception; }

    static void section(Set<String> only, String name, Runnable body) {
        if (!only.isEmpty() && !only.contains(name)) return;
        System.out.println("== " + name);
        int p = passed, f = failed;
        timedOut = false;
        body.run();
        System.out.printf("   %d/%d passed%n", passed - p, passed - p + failed - f);
    }

    static final long TIMEOUT_MS = 5000;

    static void test(String name, Test t) {
        if (timedOut) {
            failed++;
            return;
        }
        Throwable[] error = new Throwable[1];
        Thread thread = new Thread(() -> {
            try {
                t.run();
            } catch (Throwable e) {
                error[0] = e;
            }
        });
        thread.setDaemon(true);
        thread.start();
        try {
            thread.join(TIMEOUT_MS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String problem = null;
        if (thread.isAlive()) {
            timedOut = true;
            problem = "did not finish within " + TIMEOUT_MS + " ms: infinite loop? The rest of this section is skipped.";
        } else if (error[0] instanceof AssertionError) problem = error[0].getMessage();
        else if (error[0] != null) problem = "threw " + error[0];
        if (problem == null) {
            passed++;
        } else {
            failed++;
            System.out.println("   FAIL " + name + "\n        " + problem);
        }
    }

    static void assertThrows(Class<? extends Throwable> type, Test t, String what) {
        try {
            t.run();
        } catch (Throwable e) {
            if (type.isInstance(e)) return;
            throw new AssertionError(what + ": expected " + type.getSimpleName() + ", got " + e);
        }
        throw new AssertionError(what + ": expected " + type.getSimpleName() + ", nothing was thrown");
    }

    static void assertEquals(Object expected, Object actual) { assertEquals(expected, actual, null); }

    static void assertEquals(Object expected, Object actual, String what) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError((what == null ? "" : what + ": ") + "expected " + expected + ", got " + actual);
        }
    }

    static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
