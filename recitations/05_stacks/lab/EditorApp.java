/*  Your editor, in a window.
 *      javac EditorApp.java
 *      java EditorApp          uses StackEditor
 *      java EditorApp gap      uses GapEditor
 *
 *  Keys: characters, Enter, Backspace, left and right arrows; Ctrl+Z undo, Ctrl+Y redo, Esc quit.
 *  (Cmd instead of Ctrl on a Mac.)
 *  No window available? Use Driver: it does the same with typed commands.
 *  Do not modify this file.
 */
import editor.Editor;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class EditorApp extends JComponent {
    static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 18);
    static final int MARGIN = 10;

    private final Editor editor;
    private String message = "";     // the last exception, shown in the status line

    EditorApp(Editor editor) {
        this.editor = editor;
        setPreferredSize(new Dimension(800, 500));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean shortcut = e.isControlDown() || e.isMetaDown();
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT       -> run(editor::left);
                    case KeyEvent.VK_RIGHT      -> run(editor::right);
                    case KeyEvent.VK_BACK_SPACE -> run(editor::backspace);
                    case KeyEvent.VK_ENTER      -> run(() -> editor.type('\n'));
                    case KeyEvent.VK_ESCAPE     -> System.exit(0);
                    case KeyEvent.VK_Z -> { if (shortcut) run(editor::undo); }
                    case KeyEvent.VK_Y -> { if (shortcut) run(editor::redo); }
                    default -> { }
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Ctrl+Alt is AltGr on some keyboards: it types characters like @
                boolean shortcut = (e.isControlDown() && !e.isAltDown()) || e.isMetaDown();
                if (c >= ' ' && c != KeyEvent.VK_DELETE && !shortcut)
                    run(() -> editor.type(c));
            }
        });
    }

    // runs one method of the editor; an exception is shown, not thrown
    private void run(Runnable action) {
        try {
            action.run();
            message = "";
        } catch (RuntimeException e) {
            message = describe(e);
        }
        repaint();
    }

    private static String describe(RuntimeException e) {
        if (e instanceof UnsupportedOperationException)
            return "not written yet: " + e.getMessage();
        e.printStackTrace();
        return e.toString();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setFont(FONT);
        FontMetrics fm = g.getFontMetrics();
        int h = fm.getHeight();

        String before, after;
        try {
            before = editor.textBeforeCursor();
            after = editor.textAfterCursor();
        } catch (RuntimeException e) {
            before = after = "";
            message = describe(e);
        }
        String[] lines = (before + after).split("\n", -1);
        int row = (int) before.chars().filter(c -> c == '\n').count();
        int col = before.length() - before.lastIndexOf('\n') - 1;

        // the status line takes the last row: scroll so that the cursor's row is visible
        int rows = Math.max(1, (getHeight() - 2 * MARGIN) / h - 1);
        int first = Math.max(0, row - rows + 1);

        g.setColor(Color.BLACK);
        for (int i = first; i < lines.length && i < first + rows; i++)
            g.drawString(lines[i], MARGIN, MARGIN + (i - first) * h + fm.getAscent());

        g.setColor(Color.RED);
        int x = MARGIN + fm.stringWidth(lines[row].substring(0, col));
        g.fillRect(x, MARGIN + (row - first) * h, 2, h);

        int y = getHeight() - MARGIN - h;
        g.setColor(new Color(235, 235, 235));
        g.fillRect(0, y - MARGIN / 2, getWidth(), h + MARGIN);
        g.setColor(message.isEmpty() ? Color.DARK_GRAY : Color.RED);
        String status = message.isEmpty() ? "cursor " + editor.cursor() : message;
        g.drawString(status, MARGIN, y + fm.getAscent());
    }

    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("no display available: use java Driver");
            return;
        }
        Editor editor = Driver.newEditor(args);
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(editor.getClass().getSimpleName() + "     Ctrl+Z undo   Ctrl+Y redo   Esc quit");
            EditorApp app = new EditorApp(editor);
            frame.add(app);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            app.requestFocusInWindow();
        });
    }
}
