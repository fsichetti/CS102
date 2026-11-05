import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the first vector (index:value pairs separated by spaces): ");
        Map<Integer, Integer> u = parseVector( in.nextLine() );
        System.out.println("Enter the second vector (index:value pairs separated by spaces): ");
        Map<Integer, Integer> v = parseVector( in.nextLine() );
        System.out.printf("\nu + v:\n %s\n\n", add(u, v));
        in.close();

        // To try many inputs at once instead of typing them in one at a time,
        // edit Test.java in this folder and run it:  javac Test.java
        //                                            java Test
    }

    public static Map<Integer, Integer> add(Map<Integer, Integer> u, Map<Integer, Integer> v) {
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return new HashMap<>();
    }

    // Provided for you: turns a line like "0:3 5:2" into the map {0=3, 5=2}.
    public static Map<Integer, Integer> parseVector(String line) {
        Map<Integer, Integer> vector = new HashMap<>();
        line = line.trim();
        if (line.isEmpty()) {
            return vector;
        }
        for (String pair : line.split("\\s+")) {
            String [] parts = pair.split(":");
            vector.put( Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) );
        }
        return vector;
    }

}
