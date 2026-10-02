/*  ch5 - GuestList
 *
 * Starts from the guests who already confirmed, adds two late
 * confirmations, and prints the full guest list.
 *
 * It crashes. Read the documentation of Arrays.asList: what kind of list
 * does it return?
 *
 * You may edit: how guests is created.
 * Do not edit:  the confirmed array, the two add calls, the final println.
 */
import java.util.Arrays;
import java.util.List;

public class GuestList {
    public static void main( String [] args ) {
        String [] confirmed = {"Ada", "Alan", "Grace"};
        List<String> guests = Arrays.asList(confirmed);

        guests.add("Edsger");
        guests.add("Barbara");

        System.out.println(guests.size() + " guests: " + guests);
    }
}
