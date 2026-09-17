// This class holds your test cases for ruler. Compile and run it on its own:
//
//     javac Test.java
//     java Test
//
// Add one line to main for every case you want to check. The check method at
// the bottom runs your solution and reports what it got against what you said
// to expect; you do not need to change it.

public class Test {

    public static void main(String [] args) {
        check( 0, "" );
        check( 1, "-\n" );
        check( 2, "-\n--\n-\n" );
        check( 3, "-\n--\n-\n---\n-\n--\n-\n" );

        checkThrows( -1 );

        //TODO: add a line like the one above for each case you want to test.
    }

    public static void check(int h, String expected) {
        String actual = Main.ruler(h);
        String call = "ruler(" + h + ")";
        System.out.printf("%-4s  %-10s expected %s, got %s\n",
                (actual.equals(expected) ? "PASS" : "FAIL"), call, display(expected), display(actual));
    }

    public static void checkThrows(int h) {
        String call = "ruler(" + h + ")";
        try {
            String actual = Main.ruler(h);
            System.out.printf("FAIL  %-10s expected an IllegalArgumentException, got %s\n", call, display(actual));
        } catch (IllegalArgumentException e) {
            System.out.printf("PASS  %-10s threw IllegalArgumentException as expected\n", call);
        }
    }

    // Shows a multi-line string as one line, with '\n' spelled out, so the table above stays readable.
    private static String display(String s) {
        return "\"" + s.replace("\n", "\\n") + "\"";
    }

}
