import java.util.HashMap;
import java.util.Map;

// This class holds your test cases for add. Compile and run it on its own:
//
//     javac Test.java
//     java Test
//
// Add one line to main for every case you want to check. The check method at
// the bottom runs your solution and reports what it got against what you said
// to expect; you do not need to change it.

public class Test {

    public static void main(String [] args) {
        check( Map.of(0, 3, 5, 2),
               Map.of(5, -2, 7, 1),
               Map.of(0, 3, 7, 1) );

        //TODO: add a line like the one above for each case you want to test.
        //      The assignment lists some cases that are worth thinking about.
    }

    public static void check(Map<Integer, Integer> u, Map<Integer, Integer> v,
                             Map<Integer, Integer> expected) {
        // add gets copies of u and v, so that we can tell afterwards whether
        // it modified them
        Map<Integer, Integer> uCopy = new HashMap<>(u);
        Map<Integer, Integer> vCopy = new HashMap<>(v);
        Map<Integer, Integer> actual = Main.add(uCopy, vCopy);
        boolean unchanged = uCopy.equals(u) && vCopy.equals(v);

        String call = "add(" + u + ", " + v + ")";
        System.out.printf("%-4s  %-36s expected %s, got %s%s\n",
                (expected.equals(actual) && unchanged ? "PASS" : "FAIL"), call,
                expected, actual, (unchanged ? "" : ", and u or v was modified"));
    }

}
