import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        System.out.println("Enter n and k separated by a space: ");
        Scanner in = new Scanner(System.in);
        String [] parts = in.nextLine().trim().split("\\s+");
        int n = Integer.parseInt(parts[0]);
        int k = Integer.parseInt(parts[1]);
        System.out.printf("\nbinom(%d, %d) = %d\n\n", n, k, binom(n, k));
        in.close();
    }

    public static long binom(int n, int k) {
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return 0;
    }

}
