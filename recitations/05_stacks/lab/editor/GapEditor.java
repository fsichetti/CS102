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

    // TODO optional: every method below
    // what each one does: see Editor

    // O(1) amortized: when the gap is full, twice the capacity
    public void insert(char c) {
        throw new UnsupportedOperationException("insert");
    }

    public int cursor() {
        throw new UnsupportedOperationException("cursor");
    }

    public String textBeforeCursor() {
        throw new UnsupportedOperationException("textBeforeCursor");
    }

    public String textAfterCursor() {
        throw new UnsupportedOperationException("textAfterCursor");
    }

    public void left() {
        throw new UnsupportedOperationException("left");
    }

    public void right() {
        throw new UnsupportedOperationException("right");
    }

    public char delete() {
        throw new UnsupportedOperationException("delete");
    }
}
