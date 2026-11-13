import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the first string: ");
        String a = in.nextLine();
        System.out.println("Enter the second string: ");
        String b = in.nextLine();
        System.out.printf("\nThe two strings %s isomorphic.\n\n",
                        isomorphic(a, b) ? "are" : "are not");
        in.close();

        // To try many inputs at once instead of typing them in one at a time,
        // edit Test.java in this folder and run it:  javac Test.java
        //                                            java Test
    }

    public static boolean isomorphic(String a, String b) {
        if ( a.length() != b.length() )
            return false;

        Map<Character, Character> aToB = new HashMap<>();
        Map<Character, Character> bToA = new HashMap<>();
        for ( int i = 0; i < a.length(); i++ ) {
            char x = a.charAt(i);
            char y = b.charAt(i);
            if ( aToB.containsKey(x) && aToB.get(x) != y )      // x already replaced by something else
                return false;
            if ( bToA.containsKey(y) && bToA.get(y) != x )      // y already replaces something else
                return false;
            aToB.put(x, y);
            bToA.put(y, x);
        }
        return true;
    }

}
