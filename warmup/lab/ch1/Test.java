// This class holds your test cases for countTriples. Compile and run it on its own:
//
//     javac Test.java
//     java Test
//
// Add one line to main for every case you want to check. The check method at
// the bottom runs your solution and reports what it got against what you said
// to expect; you do not need to change it.

public class Test {

    public static void main(String [] args) {
        // A full set of test cases, written out for you as an example.
        //
        // Notice what happens before countTriples is implemented: most of these
        // fail, but the ones expecting 0 pass, because the placeholder already
        // returns 0. That does not make them bad tests, and it certainly does
        // not mean the program works. A test that passes only tells you that
        // it did not catch a bug this time.

        check( "abcXXXabc",     1 );
        check( "xxxabcyyyydef", 3 );
        check( "",              0 );
        check( "xxxabc",        1 );
        check( "abcxxx",        1 );
        check( "abcdef",        0 );
        check( "aa",            0 );
        check( "aaaa",          2 );
        check( "aaaaa",         3 );
    }

    public static void check(String str, int expected) {
        int actual = Main.countTriples(str);
        String call = "countTriples(\"" + str + "\")";
        System.out.printf("%-4s  %-32s expected %d, got %d\n",
                (actual == expected ? "PASS" : "FAIL"), call, expected, actual);
    }

}
