import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        System.out.println("Enter a height h: ");
        Scanner in = new Scanner(System.in);
        int h = Integer.parseInt( in.nextLine().trim() );
        System.out.println();
        System.out.print( ruler(h) );
        System.out.println();
        in.close();
    }

    // Returns the ruler of height h as a single string, one tick per line
    // (each line ends in its own '\n', including the last one).
    public static String ruler(int h) {
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return "";
    }

}
