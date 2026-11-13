/*  A map from strings to strings. Do not modify.
 *
 *  LinearProbingMap (Part 2) and QuadraticProbingMap (Bonus) implement it.
 */
public interface StringMap {
    // maps key to value, replacing the old value if key was already there
    // returns the old value, or null if key was not there
    // throws IllegalArgumentException if key or value is null
    String put(String key, String value);

    // the value of key, or null if key is not in the map
    String get(String key);

    // is key in the map?
    boolean containsKey(String key);

    // the number of keys in the map
    int size();

    // the number of cells of the table: M
    int capacity();

    // the key of every cell, in order, separated by single spaces; _ for an empty cell
    // M = 8, with "A" in cell 1 and "I" in cell 2:   "_ A I _ _ _ _ _"
    String cells();
}
