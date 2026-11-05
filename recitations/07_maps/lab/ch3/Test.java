import java.util.Arrays;
import java.util.List;
import java.util.Map;

// This class holds your test cases for byFirstLetter. Compile and run it on its own:
//
//     javac Test.java
//     java Test
//
// Add one line to main for every case you want to check. The check method at
// the bottom runs your solution and reports what it got against what you said
// to expect; you do not need to change it.

public class Test {

    public static void main(String [] args) {
        // Map.of(k1, v1, k2, v2, ...) builds a map from its arguments: keys and
        // values alternate. List.of does the same for a list.
        check( new String [] {"apple", "bob", "avocado", "cherry", "banana"},
               Map.of( 'a', List.of("apple", "avocado"),
                       'b', List.of("bob", "banana"),
                       'c', List.of("cherry") ) );

        //TODO: add a line like the one above for each case you want to test.
        //      The assignment lists some cases that are worth thinking about.
    }

    public static void check(String [] words, Map<Character, List<String>> expected) {
        Map<Character, List<String>> actual = Main.byFirstLetter(words);
        String call = "byFirstLetter(" + Arrays.toString(words) + ")";
        // Two maps are equal if they hold the same pairs, whatever order they
        // are printed in.
        System.out.printf("%-4s  %s\n      expected %s\n      got      %s\n",
                (expected.equals(actual) ? "PASS" : "FAIL"), call, expected, actual);
    }

}
