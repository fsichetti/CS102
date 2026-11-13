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
        Map<String, String> next = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        for ( String t : tickets ) {
            String [] ends = t.split("-");
            next.put(ends[0], ends[1]);
            previous.put(ends[1], ends[0]);
        }

        List<String> route = new ArrayList<>();
        if ( tickets.length == 0 )
            return route;

        String start = null;
        for ( String airport : next.keySet() )
            if ( !previous.containsKey(airport) )
                start = airport;

        for ( String airport = start; airport != null; airport = next.get(airport) )
            route.add(airport);
        return route;
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
