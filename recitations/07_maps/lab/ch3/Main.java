import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        System.out.println("Enter some words (separated by spaces): ");
        Scanner in = new Scanner(System.in);
        String [] words = parseStrings( in.nextLine() );
        System.out.printf("\nwords by first letter:\n %s\n\n", byFirstLetter(words));
        in.close();

        // To try many inputs at once instead of typing them in one at a time,
        // edit Test.java in this folder and run it:  javac Test.java
        //                                            java Test
    }

    public static Map<Character, List<String>> byFirstLetter(String [] words) {
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return new HashMap<>();
    }

    // Provided for you: turns a line like "apple bob avocado" into the array {"apple", "bob", "avocado"}.
    public static String [] parseStrings(String line) {
        line = line.trim();
        if (line.isEmpty()) {
            return new String [0];
        }
        return line.split("\\s+");
    }

}
