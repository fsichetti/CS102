import java.util.Scanner;

public class Main {
    public static void main (String [] args) {
        System.out.println("Enter a value for n: ");
        Scanner in = new Scanner(System.in);
        int n = Integer.parseInt( in.nextLine().trim() );

        long start = System.nanoTime();
        long recursiveResult = fib(n);
        long recursiveMillis = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        long iterativeResult = fibIterative(n);
        long iterativeMillis = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("\nrecursive:  fib(%d) = %d  (%d ms)\niterative:  fib(%d) = %d  (%d ms)\n\n",
                        n, recursiveResult, recursiveMillis,
                        n, iterativeResult, iterativeMillis );
        in.close();
    }

    public static long fib(int n) {
        //TODO: implement this method
        //      you will need to remove or modify the return statement below
        return 0;
    }

    // Given, complete: an iterative solution to compare yours against.
    public static long fibIterative(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative, got " + n);
        }
        if (n == 0) return 0;
        long prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }

}
