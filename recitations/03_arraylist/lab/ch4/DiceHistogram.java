/*  ch4 - DiceHistogram
 *
 * Rolls a die 600 times and counts how many times each face comes up.
 * Should print six lines, one per face, each count somewhere near 100.
 *
 * It crashes. Read the documentation of the constructor ArrayList(int):
 * what is the size of the list it creates?
 *
 * You may edit: how counts is created and initialized (you may add lines before the loop).
 * Do not edit:  the rolling loop, the printing loop.
 */
import java.util.ArrayList;
import java.util.Random;

public class DiceHistogram {
    public static void main( String [] args ) {
        ArrayList<Integer> counts = new ArrayList<>(6);     // one counter per face

        Random rng = new Random(102);
        for ( int i = 0; i < 600; i++ ) {
            int face = rng.nextInt(6);                      // 0 to 5
            counts.set(face, counts.get(face) + 1);
        }

        for ( int f = 0; f < 6; f++ )
            System.out.println("face " + (f + 1) + ": " + counts.get(f));
    }
}
