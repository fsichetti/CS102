import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        System.out.println("Enter a number: ");
        Scanner in = new Scanner(System.in);
        int num = Integer.parseInt( in.nextLine().trim() );
        if (divideSelf(num)) {
            System.out.printf("\n%d divides itself.\n\n", num);
        }
        else {
            System.out.printf("\n%d does NOT divide itself.\n\n", num);
        }
        in.close();

        // To try many inputs at once instead of typing them in one at a time,
        // edit Test.java in this folder and run it:  javac Test.java
        //                                            java Test
    }

    public static boolean divideSelf(int num ) {
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return false;
    }

}
