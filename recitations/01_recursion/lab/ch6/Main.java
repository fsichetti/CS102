import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        System.out.println("Enter a length n: ");
        Scanner in = new Scanner(System.in);
        int n = Integer.parseInt( in.nextLine().trim() );
        System.out.println( Arrays.toString( binarySequences(n) ) );
        in.close();
    }

    // Bonus, not required: returns every binary string of length n.
    public static String [] binarySequences(int n) {
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return new String [0];
    }

}
