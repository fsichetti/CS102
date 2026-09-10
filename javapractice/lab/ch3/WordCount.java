/*  ch3 - WordCount
 *
 * Should print
 *
 *     sanity check: true
 *     "the" appears 3 times
 *
 * The sanity check passes, so the comparison in the loop looks fine. It is
 * not fine. Work out why the check passes, and why that tells you nothing
 * about the strings sitting in the array.
 */
public class WordCount {
    public static void main( String [] args ) {
        String target = "the";

        String sample = "the";
        System.out.println("sanity check: " + (sample == target));

        String sentence = "the quick brown fox jumps over the lazy dog and the fox runs";
        String [] words = sentence.split(" ");

        int count = 0;
        for ( int i = 0; i < words.length; i++ )
            if ( words[i] == target )
                count++;

        System.out.println("\"" + target + "\" appears " + count + " times");
    }
}
