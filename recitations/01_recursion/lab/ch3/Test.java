// This class holds your test cases for binom. Compile and run it on its own:
//
//     javac Test.java
//     java Test
//
// Add one line to main for every case you want to check. The check method at
// the bottom runs your solution and reports what it got against what you said
// to expect; you do not need to change it.

public class Test {

    public static void main(String [] args) {
        check( 5, 2,  10 );

        checkThrows( -1, 0 );
        checkThrows( 5, -1 );
        checkThrows( 3, 5 );

        //TODO: add a line like the one above for each case you want to test.
        //      The assignment lists some cases that are worth thinking about.
    }

    public static void check(int n, int k, long expected) {
        long actual = Main.binom(n, k);
        String call = "binom(" + n + ", " + k + ")";
        System.out.printf("%-4s  %-16s expected %d, got %d\n",
                (actual == expected ? "PASS" : "FAIL"), call, expected, actual);
    }

    public static void checkThrows(int n, int k) {
        String call = "binom(" + n + ", " + k + ")";
        try {
            long actual = Main.binom(n, k);
            System.out.printf("FAIL  %-16s expected an IllegalArgumentException, got %d\n", call, actual);
        } catch (IllegalArgumentException e) {
            System.out.printf("PASS  %-16s threw IllegalArgumentException as expected\n", call);
        }
    }

}
