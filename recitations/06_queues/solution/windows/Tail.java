/*  Part 1: the last n lines of a text, as the command tail -n does.
 */
package windows;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;

public class Tail {

    // TODO Part 1
    // reads every line of in; returns the last n of them, in order (all of them if there are fewer)
    // holds at most n lines at any time
    // throws IllegalArgumentException if n < 0
    public static Queue<String> tail(Scanner in, int n) {
        if (n < 0) throw new IllegalArgumentException("n < 0: " + n);
        Queue<String> last = new ArrayDeque<>();
        while (in.hasNextLine()) {
            last.offer(in.nextLine());
            if (last.size() > n) last.poll();
        }
        return last;
    }
}
