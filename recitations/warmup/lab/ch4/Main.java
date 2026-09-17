import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        System.out.println("Enter a list of integers separated by spaces: ");
        Scanner in = new Scanner(System.in);
        int [] nums = parseInts( in.nextLine() );
        System.out.printf("\nThe longest span of %s is %d.\n\n",
                        Arrays.toString(nums),
                        longestSpan(nums) );
        in.close();

        // To try many inputs at once instead of typing them in one at a time,
        // edit Test.java in this folder and run it:  javac Test.java
        //                                            java Test
    }

    public static int longestSpan(int [] nums ) {
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return 0;
    }

    // Provided for you: turns a line like "1 2 3" into the array {1, 2, 3}.
    public static int [] parseInts(String line) {
        line = line.trim();
        if (line.isEmpty()) {
            return new int [0];
        }
        String [] parts = line.split("\\s+");
        int [] nums = new int [parts.length];
        for (int i = 0; i < parts.length; i++) {
            nums[i] = Integer.parseInt(parts[i]);
        }
        return nums;
    }

}
