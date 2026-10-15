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

    private final MyStack<Edit> undos = new LinkedStack<>();
    private final MyStack<Edit> redos = new LinkedStack<>();

    // a character typed by the user
    public void type(char c) {
        undos.push(new Edit(true, c, cursor()));
        redos.clear();
        insert(c);
    }

    // Backspace: nothing at the start of the text
    public void backspace() {
        if (cursor() == 0)
            return;
        char c = delete();
        undos.push(new Edit(false, c, cursor()));
        redos.clear();
    }

    // cancels the last edit not undone yet; nothing if there is none
    public void undo() {
        if (undos.isEmpty())
            return;
        Edit e = undos.pop();
        if (e.inserted) deleteAt(e.pos);
        else insertAt(e.pos, e.c);
        redos.push(e);
    }

    // applies again the last undone edit; nothing if there is none,
    // or if there was an edit after the last undo
    public void redo() {
        if (redos.isEmpty())
            return;
        Edit e = redos.pop();
        if (e.inserted) insertAt(e.pos, e.c);
        else deleteAt(e.pos);
        undos.push(e);
    }

    // c ends up at position pos, the cursor after it
    private void insertAt(int pos, char c) {
        moveTo(pos);
        insert(c);
    }

    // deletes the character at position pos, the cursor ends up at pos
    private void deleteAt(int pos) {
        moveTo(pos + 1);
        delete();
    }

    // O(distance)
    private void moveTo(int pos) {
        while (cursor() > pos) left();
        while (cursor() < pos) right();
    }

    // ==== given ====

    // the text with '|' at the cursor, newlines as \n: "ab|c\nd"
    @Override
    public String toString() {
        return (textBeforeCursor() + "|" + textAfterCursor()).replace("\n", "\\n");
    }
}
