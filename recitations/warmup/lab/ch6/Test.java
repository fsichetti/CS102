import java.util.Arrays;

// This class holds your test cases for merge. Compile and run it on its own:
//
//     javac Test.java
//     java Test
//
// Add one line to main for every case you want to check. The check method at
// the bottom runs your solution and reports what it got against what you said
// to expect; you do not need to change it.

public class Test {

    public static void main(String [] args) {
        check( new String [] {"a", "c", "k", "p"},
               new String [] {"c", "e", "m", "z"}, 3,
               new String [] {"a", "c", "e"} );

        //TODO: add a line like the one above for each case you want to test.
        //      The assignment lists some cases that are worth thinking about.
    }

    public static void check(String [] a, String [] b, int num, String [] expected) {
        String [] actual = Main.merge(a, b, num);
        String call = "merge(" + Arrays.toString(a) + ", " + Arrays.toString(b) + ", " + num + ")";
        // Arrays.equals compares the values the arrays hold. Using == here would
        // instead ask whether the two variables point at the very same array.
        System.out.printf("%-4s  %-44s expected %s, got %s\n",
                (Arrays.equals(actual, expected) ? "PASS" : "FAIL"), call,
                Arrays.toString(expected), Arrays.toString(actual));
    }

}
