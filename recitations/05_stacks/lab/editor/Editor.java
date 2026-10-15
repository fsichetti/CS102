/*  A text editor: a text, and a cursor between two of its characters.
 *  Positions count characters from the start: the cursor is at position cursor(),
 *  the character just before it at cursor() - 1.
 *
 *  The subclasses store the text: StackEditor (Part 2), GapEditor (optional).
 *  This class adds undo and redo on top of any of them (Part 3).
 */
package editor;

import stacks.LinkedStack;
import stacks.MyStack;

public abstract class Editor {

    // ==== storage: written in each subclass ====

    // inserts c before the cursor: the cursor ends up after c
    public abstract void insert(char c);

    // deletes and returns the character before the cursor
    // valid: cursor() > 0
    public abstract char delete();

    // moves the cursor by one character
    // nothing at the start (left) or at the end (right) of the text
    public abstract void left();
    public abstract void right();

    // the position of the cursor: the number of characters before it
    public abstract int cursor();

    // the two parts of the text; the editor is unchanged
    public abstract String textBeforeCursor();
    public abstract String textAfterCursor();

    // ==== Part 3: what the user does, written once here ====

    // one edit: c was inserted at position pos, or deleted from position pos
    private static class Edit {
        final boolean inserted;
        final char c;
        final int pos;

        Edit(boolean inserted, char c, int pos) {
            this.inserted = inserted;
            this.c = c;
            this.pos = pos;
        }
    }

    // a character typed by the user
    public void type(char c) {
        insert(c);      // TODO Part 3: also record the edit
    }

    // Backspace: nothing at the start of the text
    public void backspace() {
        if (cursor() > 0)
            delete();   // TODO Part 3: also record the edit
    }

    // TODO Part 3
    // cancels the last edit not undone yet; nothing if there is none
    public void undo() {
        throw new UnsupportedOperationException("undo");
    }

    // TODO Part 3
    // applies again the last undone edit; nothing if there is none,
    // or if there was an edit after the last undo
    public void redo() {
        throw new UnsupportedOperationException("redo");
    }

    // ==== given ====

    // the text with '|' at the cursor, newlines as \n: "ab|c\nd"
    @Override
    public String toString() {
        return (textBeforeCursor() + "|" + textAfterCursor()).replace("\n", "\\n");
    }
}
