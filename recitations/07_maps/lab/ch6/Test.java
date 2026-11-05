import java.util.Arrays;
import java.util.List;

// This class holds your test cases for route. Compile and run it on its own:
//
//     javac Test.java
//     java Test
//
// Add one line to main for every case you want to check. The check method at
// the bottom runs your solution and reports what it got against what you said
// to expect; you do not need to change it.

public class Test {

    public static void main(String [] args) {
        check( new String [] {"SFO-ORD", "JFK-LAX", "LAX-SFO"},
               List.of("JFK", "LAX", "SFO", "ORD") );

        //TODO: add a line like the one above for each case you want to test.
        //      The assignment lists some cases that are worth thinking about.
    }

    public static void check(String [] tickets, List<String> expected) {
        List<String> actual = Main.route(tickets);
        String call = "route(" + Arrays.toString(tickets) + ")";
        System.out.printf("%-4s  %-40s expected %s, got %s\n",
                (expected.equals(actual) ? "PASS" : "FAIL"), call, expected, actual);
    }

}
