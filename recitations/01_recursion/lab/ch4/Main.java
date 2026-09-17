import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        System.out.println("Enter a string: ");
        Scanner in = new Scanner(System.in);
        String str = in.nextLine();
        System.out.printf("\nrecursive:  %b\niterative:  %b\n\n",
                        isPalindromeRecursive(str),
                        isPalindromeIterative(str) );
        in.close();
    }

    public static boolean isPalindromeRecursive(String s) {
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return false;
    }

    // Given, complete: strips everything but letters and digits, and lowercases what's left.
    public static String clean(String s) {
        if (s == null) {
            throw new IllegalArgumentException("s must not be null");
        }
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    // Given, complete: an iterative solution to compare yours against.
    public static boolean isPalindromeIterative(String s) {
        String cleaned = clean(s);
        int left = 0, right = cleaned.length() - 1;
        while (left < right) {
            if (cleaned.charAt(left) != cleaned.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

}
