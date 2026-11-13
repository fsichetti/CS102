/*  Part 3: encoding with an array, decoding with a tree or with a map.
 *
 *      javac *.java
 *      java Morse              decodes data/message.txt, then encodes or decodes the lines you type
 *      java Morse quadratic    the same, with QuadraticProbingMap (Bonus)
 *      java Morse tree         prints the tree (Part 1)
 *
 *  A message in Morse code: the codes of the characters separated by single spaces,
 *  the words separated by " / ".
 *      SOS HELP   is   ... --- ... / .... . .-.. .--.
 */
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Morse {
    static final String TABLE = "data/morse.txt";
    static final String MESSAGE = "data/message.txt";

    private final String[] codes = new String[128];     // codes[c]: the code of character c, or null
    private final MorseTree tree;                       // code -> character
    private final StringMap letters;                    // code -> character, as a string of length 1

    // reads the table, and adds every character to the array, the tree and the map. Do not modify
    public Morse(String table, StringMap letters) {
        this.letters = letters;
        tree = new MorseTree();
        for (String line : lines(table)) {
            char letter = line.charAt(0);
            String code = line.substring(2);
            codes[letter] = code;
            tree.insert(letter, code);
            letters.put(code, String.valueOf(letter));
        }
    }

    // TODO Part 3
    // text: words separated by single spaces; lower case letters are encoded as upper case
    // throws IllegalArgumentException if a character of text has no code
    public String encode(String text) {
        throw new UnsupportedOperationException("Morse.encode");
    }

    // TODO Part 3
    // the text of a message in Morse code: upper case, words separated by single spaces
    // decodes every character with the tree
    // throws IllegalArgumentException if a code is not the code of a character
    public String decode(String morse) {
        throw new UnsupportedOperationException("Morse.decode");
    }

    // TODO Part 3
    // the same as decode, but decodes every character with the map
    public String decodeWithMap(String morse) {
        throw new UnsupportedOperationException("Morse.decodeWithMap");
    }

    // ==== given: do not modify ====

    // the non-empty lines of a file
    static List<String> lines(String file) {
        try {
            List<String> lines = new ArrayList<>();
            for (String line : Files.readAllLines(Path.of(file)))
                if (!line.isEmpty()) lines.add(line);
            return lines;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // a new, empty map of the given class; null if the class does not exist yet
    static StringMap newMap(String className) {
        try {
            return (StringMap) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException r) throw r;
            throw new RuntimeException(e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(className + " needs a constructor with no arguments", e);
        }
    }

    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "linear";

        if (mode.equals("tree")) {
            MorseTree tree = new MorseTree();
            for (String line : lines(TABLE)) tree.insert(line.charAt(0), line.substring(2));
            System.out.print(tree);
            return;
        }

        String className = mode.equals("quadratic") ? "QuadraticProbingMap" : "LinearProbingMap";
        StringMap map = newMap(className);
        if (map == null) {
            System.out.println(className + " not found: create " + className + ".java first");
            return;
        }
        Morse morse = new Morse(TABLE, map);
        System.out.println(className + ": " + map.size() + " keys, " + map.capacity() + " cells");
        System.out.println(map.cells());

        System.out.println("\n" + MESSAGE + ":");
        for (String line : lines(MESSAGE)) {
            String text = morse.decode(line);
            System.out.println(text);
            if (!text.equals(morse.decodeWithMap(line)))
                System.out.println("    decodeWithMap gives " + morse.decodeWithMap(line));
        }

        System.out.println("\nType a text to encode it, or Morse code to decode it. Ctrl+D quits.");
        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String line = in.nextLine();
            try {
                System.out.println(line.matches("[-. /]+") ? morse.decode(line) : morse.encode(line));
            } catch (IllegalArgumentException | UnsupportedOperationException e) {
                System.out.println(e);
            }
        }
    }
}
