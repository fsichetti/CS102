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
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return false;
    }

}
