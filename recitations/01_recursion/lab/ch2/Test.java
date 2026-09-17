// This class holds your test cases for fib. Compile and run it on its own:
//
//     javac Test.java
//     java Test
//
// Add one line to main for every case you want to check. The check method at
// the bottom runs both your recursive solution and the given iterative one,
// and reports each against what you said to expect; you do not need to change it.

public class Test {

    public static void main(String [] args) {
        check( 0,   0 );
        check( 1,   1 );
        check( 2,   1 );
        check( 3,   2 );
        check( 4,   3 );
        check( 5,   5 );
        check( 7,   13 );
        check( 10,  55 );

        checkThrows( -1 );

        //TODO: add a line like the ones above for each case you want to test.
    }

    public static void check(int n, long expected) {
        long rec = Main.fib(n);
        long it  = Main.fibIterative(n);
        String call = "fib(" + n + ")";
        System.out.printf("%-4s  recursive %-12s expected %d, got %d\n",
                (rec == expected ? "PASS" : "FAIL"), call, expected, rec);
        System.out.printf("%-4s  iterative %-12s expected %d, got %d\n",
                (it  == expected ? "PASS" : "FAIL"), call, expected, it);
    }

    public static void checkThrows(int n) {
        String call = "fib(" + n + ")";
        try {
            long actual = Main.fib(n);
            System.out.printf("FAIL  recursive %-12s expected an IllegalArgumentException, got %d\n", call, actual);
        } catch (IllegalArgumentException e) {
            System.out.printf("PASS  recursive %-12s threw IllegalArgumentException as expected\n", call);
        }
        try {
            long actual = Main.fibIterative(n);
            System.out.printf("FAIL  iterative %-12s expected an IllegalArgumentException, got %d\n", call, actual);
        } catch (IllegalArgumentException e) {
            System.out.printf("PASS  iterative %-12s threw IllegalArgumentException as expected\n", call);
        }
    }

}
