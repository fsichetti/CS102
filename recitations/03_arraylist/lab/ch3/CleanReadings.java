/*  ch3 - CleanReadings
 *
 * A sensor produced 500,000 readings. Negative readings are errors: remove
 * them, then print how many valid readings are left and their average.
 *
 * The answer it prints is correct. The problem is the time: cleaning the
 * list should take a few milliseconds, not seconds. Work out where the time
 * goes and fix it: the paragraphs at the top of the documentation of ArrayList
 * say how much each operation costs.
 *
 * Try a few sizes (change N). When N doubles, what happens to the time?
 *
 * You may edit: the cleaning code between the two timing lines (and N, to experiment).
 * Do not edit:  how the readings are generated, the timing code, the final printlns.
 */
import java.util.ArrayList;
import java.util.Random;

public class CleanReadings {
    public static void main( String [] args ) {
        final int N = 500_000;

        Random rng = new Random(102);
        ArrayList<Double> readings = new ArrayList<>();
        for ( int i = 0; i < N; i++ )
            readings.add(rng.nextDouble() * 100 - 30);     // about 30% are negative

        long start = System.nanoTime();

        // backwards, so that removing an element never makes us skip the next one
        for ( int i = readings.size() - 1; i >= 0; i-- )
            if ( readings.get(i) < 0 )
                readings.remove(i);

        long ms = (System.nanoTime() - start) / 1_000_000;

        double total = 0;
        for ( double r : readings )
            total += r;

        System.out.println("cleaning took " + ms + " ms");
        System.out.println(readings.size() + " valid readings, average " + total / readings.size());
    }
}
