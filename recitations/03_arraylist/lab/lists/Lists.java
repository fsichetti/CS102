/*  Part 2 - using lists
 *
 * Implement each method. Check them with TestLists:
 *     javac TestLists.java
 *     java TestLists
 */
import java.util.ArrayList;
import java.util.List;

public class Lists {

    // Returns a new list with the elements of list, without duplicates:
    // each value appears once, at the position of its first occurrence.
    // list is not modified.
    //   dedupe([3, 1, 3, 2, 1]) -> [3, 1, 2]
    public static List<Integer> dedupe( List<Integer> list ) {
        // TODO
        return new ArrayList<>();
    }

    // sorted is in ascending order: inserts x so that it stays in ascending order.
    // Do not call any sorting method.
    //   sorted = [1, 4, 7], x = 5 -> sorted becomes [1, 4, 5, 7]
    public static void insertSorted( List<Integer> sorted, int x ) {
        // TODO
    }

    // a and b are in ascending order. Returns a new list with all the elements
    // of a and b, in ascending order. a and b are not modified.
    // Do not call any sorting method: use the fact that a and b are sorted.
    //   merge([1, 4, 9], [2, 3, 10, 11]) -> [1, 2, 3, 4, 9, 10, 11]
    public static List<Integer> merge( List<Integer> a, List<Integer> b ) {
        // TODO
        return new ArrayList<>();
    }

    // n people, numbered 1 to n, stand in a circle. Starting from person 1,
    // count k people around the circle: the k-th one leaves the circle, and
    // the count starts again from the next person. Returns the people in the
    // order in which they leave.
    // Hint: keep the people still in the circle in a list.
    //   josephus(7, 3) -> [3, 6, 2, 7, 5, 1, 4]
    public static List<Integer> josephus( int n, int k ) {
        // TODO
        return new ArrayList<>();
    }

    // (Optional) Splits list into runs: maximal groups of consecutive
    // elements in non-decreasing order. list is not modified.
    //   runs([1, 3, 5, 2, 4, 4, 1]) -> [[1, 3, 5], [2, 4, 4], [1]]
    public static List<List<Integer>> runs( List<Integer> list ) {
        // TODO
        return new ArrayList<>();
    }
}
