/*  ch2 - GradeAverage
 *
 * Should print
 *
 *     average: 88
 *
 * It compiles with a warning, which you should read, and then it crashes.
 * Fix it so that the mistake in this program becomes impossible to make
 * rather than merely unlikely: after your change, the line that causes the
 * crash should be rejected by the compiler.
 */
import java.util.ArrayList;

public class GradeAverage {
    public static void main( String [] args ) {
        ArrayList scores = new ArrayList();
        scores.add(90);
        scores.add(85);
        scores.add("78");
        scores.add(100);

        int total = 0;
        for ( int i = 0; i < scores.size(); i++ )
            total += (Integer) scores.get(i);

        System.out.println("average: " + (total / scores.size()));
    }
}
