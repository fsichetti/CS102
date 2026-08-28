import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        System.out.println("Enter a string: ");
        Scanner in = new Scanner(System.in);
        String str = in.nextLine();
        System.out.printf("\nThe sum of digits in your string is %d.\n\n",
                        sumDigits(str) );
        in.close();

        // To try many inputs at once instead of typing them in one at a time,
        // edit Test.java in this folder and run it:  javac Test.java
        //                                            java Test
    }

    public static int sumDigits(String str) {
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return 0;
    }

}
