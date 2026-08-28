import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the first sorted array (words separated by spaces): ");
        String [] a = parseStrings( in.nextLine() );
        System.out.println("Enter the second sorted array (words separated by spaces): ");
        String [] b = parseStrings( in.nextLine() );
        System.out.println("Enter n: ");
        int num = Integer.parseInt( in.nextLine().trim() );

        System.out.printf("\nmerged array:\n %s \n\n", Arrays.toString( merge(a, b, num) ));
        in.close();

        // To try many inputs at once instead of typing them in one at a time,
        // edit Test.java in this folder and run it:  javac Test.java
        //                                            java Test
    }

    public static String [] merge (String [] a, String [] b, int num ) {
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return null;
    }

    // Provided for you: turns a line like "a c k p" into the array {"a", "c", "k", "p"}.
    public static String [] parseStrings(String line) {
        line = line.trim();
        if (line.isEmpty()) {
            return new String [0];
        }
        return line.split("\\s+");
    }

}
