/*  Optional: an editor on a gap buffer.
 *
 *  text "abcdef", cursor between c and d, capacity 10:
 *
 *      index   0 1 2 3 4 5 6 7 8 9
 *      buf    [a b c . . . . d e f]        . : the gap, never read
 *                    ^       ^
 *              gapStart     gapEnd
 *
 *  The text is buf[0..gapStart-1] followed by buf[gapEnd..buf.length-1].
 *  The cursor is always at the gap: cursor() is gapStart.
 */
package editor;

public class GapEditor extends Editor {
    private char[] buf = new char[16];
    private int gapStart = 0;           // first cell of the gap
    private int gapEnd = buf.length;    // first cell after the gap

    // O(1) amortized
    public void insert(char c) {
        if (gapStart == gapEnd)
            grow();
        buf[gapStart++] = c;
    }

    // twice the capacity: the gap gets the new cells
    private void grow() {
        char[] bigger = new char[2 * buf.length];
        int tail = buf.length - gapEnd;     // characters after the gap
        System.arraycopy(buf, 0, bigger, 0, gapStart);
        System.arraycopy(buf, gapEnd, bigger, bigger.length - tail, tail);
        gapEnd = bigger.length - tail;
        buf = bigger;
    }

    public int cursor() {
        return gapStart;
    }

    public String textBeforeCursor() {
        return new String(buf, 0, gapStart);
    }

    public String textAfterCursor() {
        return new String(buf, gapEnd, buf.length - gapEnd);
    }

    // one character crosses the gap
    public void left() {
        if (gapStart > 0)
            buf[--gapEnd] = buf[--gapStart];
    }

    public void right() {
        if (gapEnd < buf.length)
            buf[gapStart++] = buf[gapEnd++];
    }

    public char delete() {
        return buf[--gapStart];
    }
}
