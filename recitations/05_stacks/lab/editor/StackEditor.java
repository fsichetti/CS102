/*  Part 2: an editor on two stacks, one on each side of the cursor.
 *
 *  text "abcde", cursor between c and d:   ab c|d e
 *      before: a b c      top c: the character just before the cursor
 *      after:  e d        top d: the character just after the cursor
 */
package editor;

import stacks.ArrayStack;
import stacks.LinkedStack;
import stacks.MyStack;

public class StackEditor extends Editor {
    private final MyStack<Character> before = new LinkedStack<>();
    private final MyStack<Character> after = new LinkedStack<>();

    // TODO Part 2: every method below, in this order
    // what each one does: see Editor

    public void insert(char c) {
        throw new UnsupportedOperationException("insert");
    }

    public int cursor() {
        throw new UnsupportedOperationException("cursor");
    }

    // for-each visits a stack from the top: which character of the text is the top of before?
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
