/*  Runs every method of Lists on a few small inputs. */
import java.util.ArrayList;
import java.util.List;

public class TestLists {
    static int failures = 0;

    static void check( String what, Object expected, Object actual ) {
        if ( expected.equals(actual) )
            System.out.println("OK    " + what);
        else {
            failures++;
            System.out.println("FAIL  " + what + ": expected " + expected + ", got " + actual);
        }
    }

    static List<Integer> list( Integer... values ) {
        return new ArrayList<>(List.of(values));
    }

    public static void main( String [] args ) {
        List<Integer> in = list(3, 1, 3, 2, 1);
        check("dedupe([3, 1, 3, 2, 1])", list(3, 1, 2), Lists.dedupe(in));
        check("dedupe does not modify its argument", list(3, 1, 3, 2, 1), in);
        check("dedupe([5, 5, 5])", list(5), Lists.dedupe(list(5, 5, 5)));
        check("dedupe([])", list(), Lists.dedupe(list()));

        int [][] cases = { {5}, {0}, {8}, {4} };
        List<List<Integer>> expected = List.of(list(1, 4, 5, 7), list(0, 1, 4, 7), list(1, 4, 7, 8), list(1, 4, 4, 7));
        for ( int i = 0; i < cases.length; i++ ) {
            List<Integer> s = list(1, 4, 7);
            Lists.insertSorted(s, cases[i][0]);
            check("insertSorted([1, 4, 7], " + cases[i][0] + ")", expected.get(i), s);
        }
        List<Integer> empty = list();
        Lists.insertSorted(empty, 3);
        check("insertSorted([], 3)", list(3), empty);

        List<Integer> a = list(1, 4, 9), b = list(2, 3, 10, 11);
        check("merge([1, 4, 9], [2, 3, 10, 11])", list(1, 2, 3, 4, 9, 10, 11), Lists.merge(a, b));
        check("merge does not modify its arguments", List.of(list(1, 4, 9), list(2, 3, 10, 11)), List.of(a, b));
        check("merge([1, 2, 2], [2, 5])", list(1, 2, 2, 2, 5), Lists.merge(list(1, 2, 2), list(2, 5)));
        check("merge([], [1, 2])", list(1, 2), Lists.merge(list(), list(1, 2)));
        check("merge([], [])", list(), Lists.merge(list(), list()));

        check("josephus(7, 3)", list(3, 6, 2, 7, 5, 1, 4), Lists.josephus(7, 3));
        check("josephus(5, 1)", list(1, 2, 3, 4, 5), Lists.josephus(5, 1));
        check("josephus(4, 2)", list(2, 4, 3, 1), Lists.josephus(4, 2));
        check("josephus(3, 7)", list(1, 2, 3), Lists.josephus(3, 7));
        check("josephus(1, 4)", list(1), Lists.josephus(1, 4));

        System.out.println("(optional)");
        List<Integer> r = list(1, 3, 5, 2, 4, 4, 1);
        check("runs([1, 3, 5, 2, 4, 4, 1])", List.of(list(1, 3, 5), list(2, 4, 4), list(1)), Lists.runs(r));
        check("runs does not modify its argument", list(1, 3, 5, 2, 4, 4, 1), r);
        check("runs([3, 2, 1])", List.of(list(3), list(2), list(1)), Lists.runs(list(3, 2, 1)));
        check("runs([])", List.of(), Lists.runs(list()));

        System.out.println(failures == 0 ? "all tests passed" : failures + " test(s) failed");
    }
}
