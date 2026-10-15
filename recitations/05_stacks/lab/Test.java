/*  Tests Parts 1 to 3.
 *      javac Test.java
 *      java Test          tests Tags and StackEditor
 *      java Test gap      tests GapEditor
 *
 *  An editor is shown with '|' at the cursor and newlines as \n: "ab|c\nd".
 */
import editor.Editor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import tags.Tags;

public class Test {
    static String[] args;
    static volatile int failures = 0;

    // ==== helpers ====

    static Editor newEditor() { return Driver.newEditor(args); }

    // a new editor with the given text, inserted with insert, the cursor at the end
    static Editor editor(String text) {
        Editor e = newEditor();
        for (char c : text.toCharArray()) e.insert(c);
        return e;
    }

    static void ok(String what) { System.out.println("OK    " + what); }

    static void fail(String what) {
        failures++;
        System.out.println("FAIL  " + what);
    }

    static void check(String what, Object expected, Object actual) {
        if (expected == null ? actual == null : expected.equals(actual)) ok(what);
        else fail(what + ": expected " + expected + ", got " + actual);
    }

    // runs one group of tests: an unexpected exception fails the group, and testing goes on
    // a group running longer than 5 seconds fails: probably an infinite loop
    static void run(String name, Runnable tests) {
        System.out.println("== " + name);
        Thread t = new Thread(() -> {
            try {
                tests.run();
            } catch (UnsupportedOperationException e) {
                fail("not written yet: " + e.getMessage() + "\n      at " + e.getStackTrace()[0]);
            } catch (RuntimeException e) {
                fail("threw " + e + "\n      at " + e.getStackTrace()[0]);
            }
        });
        t.setDaemon(true);      // a group still running does not keep the program alive
        t.start();
        try {
            t.join(5000);
        } catch (InterruptedException e) {
            return;
        }
        if (t.isAlive())
            fail("still running after 5 seconds: an infinite loop?");
    }

    // ==== Part 1 ====

    static void checkTags(String html, int expected) {
        check("firstError(\"" + html + "\")", expected, Tags.firstError(Tags.parse(html)));
    }

    static void tags() {
        checkTags("", -1);
        checkTags("no tags", -1);
        checkTags("<p>hi</p>", -1);
        checkTags("<div><p><b>x</b></p><p>y</p></div>", -1);
        checkTags("<b><i>x</b></i>", 2);
        checkTags("</p>", 0);
        checkTags("<p>x</p></p>", 2);
        checkTags("<div><p>", 2);
        checkTags("<div><p></div>", 2);
        checkTags("<P>case</p>", -1);
    }

    static void voidTags() {
        checkTags("<p>a<br>b</p>", -1);
        checkTags("<p>a<br/>b<img src=\"x.png\"></p>", -1);
        checkTags("<p><hr></b></p>", 2);
    }

    static void checkFile(String file, String expected) {
        try {
            List<Tags.Tag> tags = Tags.parse(Files.readString(Path.of("data", file)));
            int i = Tags.firstError(tags);
            String actual = i == -1 ? "balanced"
                          : i == tags.size() ? "end of file"
                          : i >= 0 && i < tags.size() ? "line " + tags.get(i).line() + " " + tags.get(i)
                          : "returned " + i;
            check(file, expected, actual);
        } catch (IOException e) {
            fail(file + ": cannot read data/" + file + ", run Test from the lab folder");
        }
    }

    static void files() {
        checkFile("page.html", "balanced");
        checkFile("broken1.html", "line 28 </p>");
        checkFile("broken2.html", "line 15 </ul>");
        checkFile("broken3.html", "line 34 </div>");
        checkFile("broken4.html", "end of file");
    }

    // ==== Part 2 ====

    static void insertText() {
        Editor e = newEditor();
        check("new editor: cursor", 0, e.cursor());
        check("new editor: textBeforeCursor", "", e.textBeforeCursor());
        check("new editor: textAfterCursor", "", e.textAfterCursor());
        e.insert('a');
        check("insert a: cursor", 1, e.cursor());
        check("insert a: textBeforeCursor", "a", e.textBeforeCursor());
        e.insert('b');
        e.insert('c');
        check("insert b, c: textBeforeCursor", "abc", e.textBeforeCursor());
        check("textBeforeCursor again", "abc", e.textBeforeCursor());
        check("textAfterCursor", "", e.textAfterCursor());
        check("cursor", 3, e.cursor());
        e.insert('d');
        check("insert d after calling textBeforeCursor", "abcd|", e.toString());
    }

    static void leftRight() {
        Editor e = editor("abc");
        e.left();
        check("left", "ab|c", e.toString());
        check("cursor", 2, e.cursor());
        e.left();
        e.left();
        check("left twice", "|abc", e.toString());
        e.left();
        check("left at the start", "|abc", e.toString());
        e.right();
        check("right", "a|bc", e.toString());
        e.insert('x');
        check("insert x in the middle", "ax|bc", e.toString());
        e.right();
        e.right();
        e.right();
        check("right three times: at the end", "axbc|", e.toString());
        check("cursor", 4, e.cursor());
    }

    static void delete() {
        Editor e = editor("abc");
        check("delete returns the character before the cursor", 'c', e.delete());
        check("after delete", "ab|", e.toString());
        e.left();
        check("delete in the middle", 'a', e.delete());
        check("after delete", "|b", e.toString());
        e.right();
        e.delete();
        check("delete the last one", "|", e.toString());
        check("cursor", 0, e.cursor());
        e.insert('z');
        check("insert after emptying", "z|", e.toString());
    }

    static void newlines() {
        Editor e = editor("ab\ncd");
        e.left();
        e.left();
        e.left();
        check("left over a newline", "ab|\\ncd", e.toString());
        check("textBeforeCursor", "ab", e.textBeforeCursor());
        check("textAfterCursor", "\ncd", e.textAfterCursor());
    }

    static void textUnchanged() {
        Editor e = editor("hello");
        e.left();
        e.left();
        check("textBeforeCursor", "hel", e.textBeforeCursor());
        check("textBeforeCursor again", "hel", e.textBeforeCursor());
        check("textAfterCursor", "lo", e.textAfterCursor());
        check("textAfterCursor again", "lo", e.textAfterCursor());
        check("cursor unchanged", 3, e.cursor());
        e.insert('_');
        check("insert afterwards", "hel_|lo", e.toString());
    }

    // the reference for the random tests: the text in a StringBuilder, and the position of the cursor
    static class Reference extends Editor {
        private final StringBuilder text = new StringBuilder();
        private int cursor;

        public void insert(char c) { text.insert(cursor++, c); }
        public char delete() {
            char c = text.charAt(--cursor);
            text.deleteCharAt(cursor);
            return c;
        }
        public void left() { if (cursor > 0) cursor--; }
        public void right() { if (cursor < text.length()) cursor++; }
        public int cursor() { return cursor; }
        public String textBeforeCursor() { return text.substring(0, cursor); }
        public String textAfterCursor() { return text.substring(cursor); }
    }

    // many short random sequences of operations, on your editor and on Reference
    // a difference prints the sequence, as Driver commands
    static void randomAgainstReference() {
        Random rnd = new Random(102);
        String[] ops = {"type", "type", "enter", "back", "left", "right"};
        for (int seq = 0; seq < 1000; seq++) {
            Editor yours = newEditor();
            Editor reference = new Reference();
            List<String> done = new ArrayList<>();
            for (int k = 0; k < 30; k++) {
                String op = ops[rnd.nextInt(ops.length)];
                char c = (char) ('a' + rnd.nextInt(3));
                done.add(op.equals("type") ? "type " + c : op);
                for (Editor e : new Editor[] {yours, reference}) {
                    switch (op) {
                        case "type"  -> e.insert(c);
                        case "enter" -> e.insert('\n');
                        case "back"  -> { if (e.cursor() > 0) e.delete(); }
                        case "left"  -> e.left();
                        case "right" -> e.right();
                    }
                }
                if (!yours.toString().equals(reference.toString()) || yours.cursor() != reference.cursor()) {
                    fail("after these commands: expected " + reference + ", got " + yours
                            + "\n        " + String.join("\n        ", done));
                    return;
                }
            }
        }
        ok("1000 random sequences, same as the reference");
    }

    // ==== Part 3 ====

    static void undo() {
        Editor e = newEditor();
        e.undo();
        check("undo with nothing to undo", "|", e.toString());
        for (char c : "abc".toCharArray()) e.type(c);
        e.undo();
        check("type abc, undo", "ab|", e.toString());
        e.undo();
        e.undo();
        check("undo twice more", "|", e.toString());
        e.undo();
        check("undo once too many", "|", e.toString());

        e = newEditor();
        for (char c : "abc".toCharArray()) e.type(c);
        e.backspace();
        check("type abc, backspace", "ab|", e.toString());
        e.undo();
        check("undo the backspace", "abc|", e.toString());

        e = newEditor();
        e.type('a');
        e.left();
        e.backspace();
        check("backspace at the start: nothing", "|a", e.toString());
        e.undo();
        check("undo: the backspace was not an edit", "|", e.toString());
    }

    static void undoAway() {
        Editor e = newEditor();
        for (char c : "abc".toCharArray()) e.type(c);
        e.left();
        e.left();
        e.undo();
        check("type abc, left twice, undo: the c goes", "ab|", e.toString());

        e = newEditor();
        for (char c : "abc".toCharArray()) e.type(c);
        e.left();
        e.left();
        e.backspace();
        e.right();
        e.right();
        check("type abc, left twice, backspace, right twice", "bc|", e.toString());
        e.undo();
        check("undo: the a comes back", "a|bc", e.toString());
    }

    static void redo() {
        Editor e = newEditor();
        e.redo();
        check("redo with nothing to redo", "|", e.toString());
        for (char c : "abc".toCharArray()) e.type(c);
        e.undo();
        e.undo();
        e.redo();
        check("type abc, undo twice, redo", "ab|", e.toString());
        e.redo();
        check("redo again", "abc|", e.toString());
        e.redo();
        check("redo once too many", "abc|", e.toString());

        e.backspace();
        e.backspace();
        e.undo();
        e.undo();
        e.redo();
        check("backspace twice, undo twice, redo", "ab|", e.toString());

        e = newEditor();
        e.type('a');
        e.type('b');
        e.undo();
        e.type('x');
        e.redo();
        check("type ab, undo, type x: nothing to redo", "ax|", e.toString());
    }

    // a random session; then undo everything, then redo everything
    static void undoAll() {
        Random rnd = new Random(102);
        Editor e = newEditor();
        List<String> texts = new ArrayList<>();     // the text before each edit
        for (int k = 0; k < 300; k++) {
            int op = rnd.nextInt(6);
            String text = e.textBeforeCursor() + e.textAfterCursor();
            if (op < 3) {
                texts.add(text);
                e.type((char) ('a' + rnd.nextInt(26)));
            } else if (op == 3 && e.cursor() > 0) {
                texts.add(text);
                e.backspace();
            } else if (op == 4) e.left();
            else if (op == 5) e.right();
        }
        String last = e.textBeforeCursor() + e.textAfterCursor();
        for (int k = texts.size() - 1; k >= 0; k--) {
            e.undo();
            String text = e.textBeforeCursor() + e.textAfterCursor();
            if (!text.equals(texts.get(k))) {
                fail("undo number " + (texts.size() - k) + ": expected text " + texts.get(k) + ", got " + text);
                return;
            }
        }
        ok("random session of " + texts.size() + " edits, undone one by one");
        for (int k = 1; k < texts.size(); k++) e.redo();
        e.redo();
        check("redone one by one", last, e.textBeforeCursor() + e.textAfterCursor());
    }

    public static void main(String[] a) {
        args = a;
        boolean gap = a.length > 0 && a[0].equals("gap");
        System.out.println("testing " + newEditor().getClass().getSimpleName());

        if (!gap) {
            run("Part 1: firstError", Test::tags);
            run("Part 1: void tags", Test::voidTags);
            run("Part 1: the files in data", Test::files);
        }
        run("Part 2: insert, cursor, textBeforeCursor, textAfterCursor", Test::insertText);
        run("Part 2: left, right", Test::leftRight);
        run("Part 2: the text methods leave the editor unchanged", Test::textUnchanged);
        run("Part 2: delete", Test::delete);
        run("Part 2: newlines", Test::newlines);
        run("Part 2: random", Test::randomAgainstReference);
        run("Part 3: undo", Test::undo);
        run("Part 3: undo away from the cursor", Test::undoAway);
        run("Part 3: redo", Test::redo);
        run("Part 3: undo and redo everything", Test::undoAll);

        System.out.println(failures == 0 ? "\nall tests passed" : "\n" + failures + " failed");
    }
}
