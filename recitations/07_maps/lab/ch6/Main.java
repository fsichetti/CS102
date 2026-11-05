import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        System.out.println("Enter the tickets (like JFK-LAX, separated by spaces): ");
        Scanner in = new Scanner(System.in);
        String [] tickets = parseStrings( in.nextLine() );
        System.out.printf("\nroute:\n %s\n\n", route(tickets));
        in.close();

        // To try many inputs at once instead of typing them in one at a time,
        // edit Test.java in this folder and run it:  javac Test.java
        //                                            java Test
    }

    public static List<String> route(String [] tickets) {
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return new ArrayList<>();
    }

    // Provided for you: turns a line like "SFO-ORD JFK-LAX" into the array {"SFO-ORD", "JFK-LAX"}.
    public static String [] parseStrings(String line) {
        line = line.trim();
        if (line.isEmpty()) {
            return new String [0];
        }
        return line.split("\\s+");
    }

}
