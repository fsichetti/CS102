import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        System.out.println("Enter a starting value n (n >= 1): ");
        Scanner in = new Scanner(System.in);
        int n = Integer.parseInt( in.nextLine().trim() );
        System.out.printf("\nThe Collatz path from %d reaches 1 in %d steps.\n\n",
                        n, collatzLength(n) );
        in.close();
    }

    public static int collatzLength(int n) {
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return 0;
    }

}
