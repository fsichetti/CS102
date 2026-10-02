/*  ch1 - Enrollment
 *
 * The list holds the IDs of the students enrolled in a course.
 * The students with IDs 3 and 7 dropped the course: remove them,
 * then print the students who are left.
 *
 * Look up remove in the documentation of List.
 *
 * You may edit: the loop that removes the dropped students.
 * Do not edit:  the initial list, the dropped IDs, the final println.
 */
import java.util.ArrayList;
import java.util.List;

public class Enrollment {
    public static void main( String [] args ) {
        List<Integer> ids = new ArrayList<>(List.of(12, 3, 25, 18, 7, 40, 9, 31, 5, 22));
        int [] dropped = {3, 7};

        for ( int id : dropped )
            ids.remove(id);

        System.out.println(ids.size() + " students left: " + ids);
    }
}
