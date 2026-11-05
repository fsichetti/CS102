// This class holds your test cases for isomorphic. Compile and run it on its own:
//
//     javac Test.java
//     java Test
//
// Add one line to main for every case you want to check. The check method at
// the bottom runs your solution and reports what it got against what you said
// to expect; you do not need to change it.

public class Test {

    public static void main(String [] args) {
        check( "egg", "add", true );

        //TODO: add a line like the one above for each case you want to test.
        //      The assignment lists some cases that are worth thinking about.
    }

    public static void check(String a, String b, boolean expected) {
        boolean actual = Main.isomorphic(a, b);
        String call = "isomorphic(\"" + a + "\", \"" + b + "\")";
        System.out.printf("%-4s  %-32s expected %b, got %b\n",
                (actual == expected ? "PASS" : "FAIL"), call, expected, actual);
    }

}
