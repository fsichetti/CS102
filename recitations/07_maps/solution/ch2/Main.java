import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the note: ");
        String note = in.nextLine();
        System.out.println("Enter the letters you can cut out: ");
        String letters = in.nextLine();
        System.out.printf("\nThe note %s be written with these letters.\n\n",
                        canWrite(note, letters) ? "can" : "cannot");
        in.close();

        // To try many inputs at once instead of typing them in one at a time,
        // edit Test.java in this folder and run it:  javac Test.java
        //                                            java Test
    }

    public static boolean canWrite(String note, String letters) {
        Map<Character, Integer> available = new HashMap<>();
        for ( char c : letters.toCharArray() )
            available.put(c, available.getOrDefault(c, 0) + 1);

        for ( char c : note.toCharArray() ) {
            if ( c == ' ' )
                continue;
            int left = available.getOrDefault(c, 0);
            if ( left == 0 )
                return false;
            available.put(c, left - 1);
        }
        return true;
    }

}
