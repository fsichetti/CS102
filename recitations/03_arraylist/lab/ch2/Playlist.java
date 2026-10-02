/*  ch2 - Playlist
 *
 * Removes the live recordings (titles starting with "Live") from a playlist,
 * then prints what is left.
 *
 * It crashes. Look up the exception in the documentation: when is it thrown?
 *
 * You may edit: the loop that removes the live recordings.
 * Do not edit:  the initial playlist, the final println.
 */
import java.util.ArrayList;
import java.util.List;

public class Playlist {
    public static void main( String [] args ) {
        List<String> songs = new ArrayList<>(List.of(
                "Intro", "Live at Wembley", "Blue", "Live in Tokyo", "Home", "Outro"));

        for ( String s : songs )
            if ( s.startsWith("Live") )
                songs.remove(s);

        System.out.println(songs);
    }
}
