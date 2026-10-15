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

    public void insert(char c) {
        before.push(c);
    }

    public int cursor() {
        return before.size();
    }

    // the top of before is the last character: read from the top, then reverse
    public String textBeforeCursor() {
        StringBuilder sb = new StringBuilder();
        for (char c : before)
            sb.append(c);
        return sb.reverse().toString();
    }

    // the top of after is the first character
    public String textAfterCursor() {
        StringBuilder sb = new StringBuilder();
        for (char c : after)
            sb.append(c);
        return sb.toString();
    }

    public void left() {
        if (!before.isEmpty())
            after.push(before.pop());
    }

    public void right() {
        if (!after.isEmpty())
            before.push(after.pop());
    }

    public char delete() {
        return before.pop();
    }
}
