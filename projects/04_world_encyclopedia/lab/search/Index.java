package search;

import java.util.List;
import java.util.Map;
import java.util.Set;

// Part 1 and Part 2.
public class Index {

    // Given: common English words, skipped by topWords and topWordsIn. Otherwise "the" and "and"
    // would always be the top result, in any document and in the corpus as a whole.
    private static final Set<String> STOPWORDS = Set.of(
            "a", "an", "and", "are", "as", "at", "be", "been", "but", "by", "can", "for", "from",
            "had", "has", "have", "he", "her", "him", "his", "how", "i", "in", "into", "is", "it",
            "its", "just", "more", "most", "no", "nor", "not", "of", "on", "only", "or", "other",
            "over", "own", "s", "same", "she", "since", "so", "some", "such", "t", "than", "that",
            "the", "their", "then", "there", "these", "they", "this", "to", "too", "under", "until",
            "very", "was", "we", "were", "what", "when", "where", "which", "who", "why", "will",
            "with", "you");

    // TODO Part 1: fields for the inverted index (word -> set of document ids) and the term
    // frequencies (document id -> word -> count). Every map is a HashMap, every set a HashSet.
    // Also a field for the next id to assign: ids are assigned once, in increasing order, by
    // add, and are never reused, even after a remove.
    //
    // Also two fields for name <-> id lookup (see add(name, document) and idOf below): name to
    // id, and id to name. A name is just a String key, with no required format.

    // Builds the index from an initial list of documents, assigning ids 0, 1, 2, ... in order.
    public Index(List<String> documents) {
        // TODO Part 1: call add(document) for each document, so this works exactly like adding
        // them one at a time afterward.
    }

    // Like Index(documents), but also names each document, as add(name, document) would.
    // names and documents must be the same size: document i is named names.get(i).
    public Index(List<String> names, List<String> documents) {
        // TODO Part 1: call add(name, document) for each pair, in order.
    }

    // Adds document under a new id, one more than the highest id ever assigned. Returns that id.
    public int add(String document) {
        // TODO Part 1
        return -1;
    }

    // Like add(document), but also lets doc be found later by name, with idOf(name). A name
    // claimed again (by a later add) is only ever associated with the most recent of its
    // documents; idOf(name) returns that one. An older document under the same, now-shadowed,
    // name is still in the index, just no longer reachable by that name, only by its id.
    public int add(String name, String document) {
        // TODO Part 1
        return -1;
    }

    // The id of the document most recently added under name. IllegalArgumentException if no
    // document currently holds that name (it was never used, or every document that held it,
    // including the most recent one, has since been removed).
    public int idOf(String name) {
        // TODO Part 1
        return -1;
    }

    // Removes document doc, undoing exactly what add did for it.
    // IllegalArgumentException if doc is not currently in the index.
    //
    // If doc was added with a name, and is still that name's current holder (idOf(name) == doc),
    // that name is forgotten too: idOf(name) then throws, even though an older document that once
    // held the same name, now shadowed, may still be in the index. But if doc is an older,
    // already-shadowed holder of its name, removing it must not disturb the current holder.
    public void remove(int doc) {
        // TODO Part 1
    }

    // The number of documents currently in the index. Removed documents do not count, and ids
    // are not necessarily contiguous: do not assume size() - 1 is the highest id.
    public int size() {
        // TODO Part 1
        return 0;
    }

    // The ids of the documents containing word, or an empty set. A copy: the caller is free to
    // modify the result without affecting the index.
    public Set<Integer> documentsWith(String word) {
        // TODO Part 1
        return null;
    }

    // How many times word appears in document doc, 0 if it does not appear (or doc does not exist).
    public int frequency(int doc, String word) {
        // TODO Part 1
        return 0;
    }

    // The n words appearing in the most documents, STOPWORDS excluded, each paired with its
    // document frequency (the size of its postings set), highest first, ties broken
    // alphabetically. Fewer than n pairs if the vocabulary has fewer than n (non-stopword) words.
    public List<Map.Entry<String, Integer>> topWords(int n) {
        // TODO Part 1
        return null;
    }

    // The n most frequent words of document doc, STOPWORDS excluded, each paired with its count
    // in doc, highest first, ties broken alphabetically. Fewer than n pairs if doc does not exist,
    // or has fewer than n (non-stopword) words.
    public List<Map.Entry<String, Integer>> topWordsIn(int doc, int n) {
        // TODO Part 1
        return null;
    }

    // Part 2: and, or, not, none of which may modify any set in sets or a.
    // and, or: at least one set, else IllegalArgumentException.
    // not: relative to the documents currently in the index (not a range 0..size()-1).

    public Set<Integer> and(List<Set<Integer>> sets) {
        // TODO Part 2
        return null;
    }

    public Set<Integer> or(List<Set<Integer>> sets) {
        // TODO Part 2
        return null;
    }

    public Set<Integer> not(Set<Integer> a) {
        // TODO Part 2
        return null;
    }
}
