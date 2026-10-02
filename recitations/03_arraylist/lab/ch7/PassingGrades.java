/*  ch7 - PassingGrades
 *
 * Removes every failing grade (below 60) from the list, then prints the
 * grades that are left and their average.
 *
 * Read the documentation of remove(int index): what happens to the
 * elements after the removed one?
 *
 * You may edit: the loop that removes the failing grades.
 * Do not edit:  the initial list, the code that computes and prints the average.
 */
import java.util.ArrayList;
import java.util.List;

public class PassingGrades {
    public static void main( String [] args ) {
        List<Integer> grades = new ArrayList<>(List.of(88, 45, 52, 91, 30, 59, 77, 100, 12, 64));

        for ( int i = 0; i < grades.size(); i++ )
            if ( grades.get(i) < 60 )
                grades.remove(i);

        int total = 0;
        for ( int g : grades )
            total += g;

        System.out.println("passing: " + grades);
        System.out.println("average: " + (double) total / grades.size());
    }
}
