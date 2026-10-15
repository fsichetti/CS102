/*  Your editor, one command at a time, without a window.
 *      javac Driver.java
 *      java Driver          uses StackEditor
 *      java Driver gap      uses GapEditor
 *
 *  After every command it prints the text, with '|' at the cursor and newlines as \n.
 */
import editor.Editor;
import editor.GapEditor;
import editor.StackEditor;
import java.util.Scanner;

public class Driver {
    static final String HELP = """
        commands (n: how many times, 1 if missing):
          type some text    types the characters after "type "
          enter             types a newline
          back n            Backspace
          left n   right n
          undo n   redo n
          help     quit""";

    // the editor named on the command line: gap, or none for StackEditor
    static Editor newEditor(String[] args) {
        if (args.length > 0 && args[0].equals("gap")) return new GapEditor();
        return new StackEditor();
    }

    public static void main(String[] args) {
        Editor editor = newEditor(args);
        System.out.println("using " + editor.getClass().getSimpleName());
        System.out.println(HELP);

        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            if (!in.hasNextLine()) break;
            String line = in.nextLine();
            String[] w = line.trim().split("\\s+");
            if (w[0].isEmpty()) continue;
            if (w[0].equals("quit")) break;
            if (w[0].equals("help")) {
                System.out.println(HELP);
                continue;
            }
            try {
                execute(editor, w, line);
                System.out.println(editor + "    cursor " + editor.cursor());
            } catch (UnsupportedOperationException e) {
                System.out.println("not written yet: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("threw " + e + "\n  at " + e.getStackTrace()[0]);
            }
        }
    }

    static void execute(Editor editor, String[] w, String line) {
        if (w[0].equals("type")) {
            String text = line.substring(line.indexOf("type") + 4);
            if (text.startsWith(" ")) text = text.substring(1);
            for (char c : text.toCharArray()) editor.type(c);
            return;
        }
        int n = w.length > 1 ? Integer.parseInt(w[1]) : 1;
        for (int k = 0; k < n; k++) {
            switch (w[0]) {
                case "enter" -> editor.type('\n');
                case "back"  -> editor.backspace();
                case "left"  -> editor.left();
                case "right" -> editor.right();
                case "undo"  -> editor.undo();
                case "redo"  -> editor.redo();
                default -> throw new IllegalArgumentException("unknown command " + w[0] + ", type help");
            }
        }
    }
}
