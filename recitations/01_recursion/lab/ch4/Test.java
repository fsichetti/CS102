// This class holds your test cases for isPalindromeRecursive. Compile and run it on its own:
//
//     javac Test.java
//     java Test
//
// Add one line to main for every case you want to check. The check method at
// the bottom runs both your recursive solution and the given iterative one,
// and reports each against what you said to expect; you do not need to change it.

public class Test {

    public static void main(String [] args) {
        check( "",                              true  );
        check( "a",                             true  );
        check( "Racecar",                       true  );
        check( "hello",                         false );
        check( "A man, a plan, a canal: Panama", true  );
        check( "No 'x' in Nixon",                true  );

        checkThrows();

        //TODO: add a line like the one above for each case you want to test.
        //      The assignment lists some cases that are worth thinking about.
    }

    public static void check(String s, boolean expected) {
        boolean rec = Main.isPalindromeRecursive(s);
        boolean it  = Main.isPalindromeIterative(s);
        String call = "isPalindrome(\"" + s + "\")";
        System.out.printf("%-4s  recursive %-40s expected %b, got %b\n",
                (rec == expected ? "PASS" : "FAIL"), call, expected, rec);
        System.out.printf("%-4s  iterative %-40s expected %b, got %b\n",
                (it  == expected ? "PASS" : "FAIL"), call, expected, it);
    }

    public static void checkThrows() {
        try {
            boolean actual = Main.isPalindromeRecursive(null);
            System.out.printf("FAIL  recursive isPalindrome(null) expected an IllegalArgumentException, got %b\n", actual);
        } catch (IllegalArgumentException e) {
            System.out.println("PASS  recursive isPalindrome(null) threw IllegalArgumentException as expected");
        }
        try {
            boolean actual = Main.isPalindromeIterative(null);
            System.out.printf("FAIL  iterative isPalindrome(null) expected an IllegalArgumentException, got %b\n", actual);
        } catch (IllegalArgumentException e) {
            System.out.println("PASS  iterative isPalindrome(null) threw IllegalArgumentException as expected");
        }
    }

}
