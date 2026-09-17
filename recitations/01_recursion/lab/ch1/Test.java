// This class holds your test cases for collatzLength. Compile and run it on its own:
//
//     javac Test.java
//     java Test
//
// Add one line to main for every case you want to check. The check method at
// the bottom runs your solution and reports what it got against what you said
// to expect; the checkThrows method below it does the same for inputs that
// should raise an exception instead of returning a value. You do not need to
// change either.

public class Test {

    public static void main(String [] args) {
        check( 1,  0 );
        check( 5,  5 );
        check( 6,  8 );

        checkThrows( 0 );
        checkThrows( -7 );

        //TODO: add a line like the ones above for each case you want to test.
        //      The assignment lists some cases that are worth thinking about.
    }

    public static void check(int n, int expected) {
        int actual = Main.collatzLength(n);
        String call = "collatzLength(" + n + ")";
        System.out.printf("%-4s  %-20s expected %d, got %d\n",
                (actual == expected ? "PASS" : "FAIL"), call, expected, actual);
    }

    public static void checkThrows(int n) {
        String call = "collatzLength(" + n + ")";
        try {
            int actual = Main.collatzLength(n);
            System.out.printf("FAIL  %-20s expected an IllegalArgumentException, got %d\n", call, actual);
        } catch (IllegalArgumentException e) {
            System.out.printf("PASS  %-20s threw IllegalArgumentException as expected\n", call);
        }
    }

}
