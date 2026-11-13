/*  Tests Parts 1 to 3.
 *      javac *.java
 *      java Test          tests Parts 1 to 3
 *      java Test bonus    also tests QuadraticProbingMap
 *
 *  The map classes are not part of the lab folder: you create them.
 *  Until you do, their tests are skipped.
 */
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Test {
    static volatile int failures = 0;

    // ==== helpers ====

    static void ok(String what) { System.out.println("OK    " + what); }

    static void fail(String what) {
        failures++;
        System.out.println("FAIL  " + what);
    }

    static void check(String what, Object expected, Object actual) {
        if (expected == null ? actual == null : expected.equals(actual)) ok(what);
        else fail(what + ": expected " + expected + ", got " + actual);
    }

    static void checkThrows(String what, Runnable r) {
        try {
            r.run();
            fail(what + ": expected IllegalArgumentException, nothing thrown");
        } catch (IllegalArgumentException e) {
            ok(what + ": throws IllegalArgumentException");
        } catch (UnsupportedOperationException e) {
            throw e;
        } catch (RuntimeException e) {
            fail(what + ": expected IllegalArgumentException, got " + e);
        }
    }

    // runs one group of tests: an unexpected exception fails the group, and testing goes on
    // a group running longer than 5 seconds fails: probably an infinite loop, or too slow
    static void run(String name, Runnable tests) {
        System.out.println("== " + name);
        Thread t = new Thread(() -> {
            try {
                tests.run();
            } catch (UnsupportedOperationException e) {
                fail("not written yet: " + e.getMessage() + where(e));
            } catch (RuntimeException e) {
                fail("threw " + e + where(e));
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
            fail("still running after 5 seconds: an infinite loop, or too slow?");
    }

    // where e was thrown; the JVM may omit the stack trace of an exception thrown many times
    static String where(Exception e) {
        return e.getStackTrace().length == 0 ? "" : "\n      at " + e.getStackTrace()[0];
    }

    // a new map of the given class, with the given capacity (null: the default one)
    static StringMap newMap(String className, Integer capacity) {
        try {
            Class<?> c = Class.forName(className);
            Object map = capacity == null ? c.getDeclaredConstructor().newInstance()
                                          : c.getDeclaredConstructor(int.class).newInstance(capacity);
            return (StringMap) map;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException r) throw r;
            throw new RuntimeException(e.getCause());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(className + " has no constructor " + className
                                       + (capacity == null ? "()" : "(int capacity)"));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean exists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    // a StringMap that works, to test Part 3 on its own
    static class Reference implements StringMap {
        private final Map<String, String> map = new HashMap<>();
        public String put(String key, String value) {
            if (key == null || value == null) throw new IllegalArgumentException();
            return map.put(key, value);
        }
        public String get(String key) { return map.get(key); }
        public boolean containsKey(String key) { return map.containsKey(key); }
        public int size() { return map.size(); }
        public int capacity() { return map.size(); }
        public String cells() { return map.toString(); }
    }

    static String quote(char c) { return "'" + c + "'"; }

    static String quote(String s) { return "\"" + s + "\""; }

    // ==== Part 1 ====

    static void treeSmall() {
        MorseTree t = new MorseTree();
        t.insert('E', ".");
        t.insert('T', "-");
        t.insert('A', ".-");
        check("insert E ., T -, A .-: decode(\".\")", 'E', t.decode("."));
        check("decode(\"-\")", 'T', t.decode("-"));
        check("decode(\".-\")", 'A', t.decode(".-"));
        check("the root has no letter", null, t.root.letter);
        check("root.left holds E", 'E', t.root.left == null ? null : t.root.left.letter);
        check("root.right holds T", 'T', t.root.right == null ? null : t.root.right.letter);
        check("root.left.right holds A",
              'A', t.root.left == null || t.root.left.right == null ? null : t.root.left.right.letter);
        check("root.left.left is null: no code .. yet", null, t.root.left == null ? "no root.left" : t.root.left.left);
        checkThrows("decode(\"..\"): no node", () -> t.decode(".."));
        checkThrows("decode(\".-.-\"): no node", () -> t.decode(".-.-"));
        checkThrows("decode(\"\"): the root has no letter", () -> t.decode(""));
    }

    static void treeDeepFirst() {
        MorseTree t = new MorseTree();
        t.insert('X', "-..-");
        check("insert X -..-: decode(\"-..-\")", 'X', t.decode("-..-"));
        check("root.right exists, with no letter", true, t.root.right != null && t.root.right.letter == null);
        checkThrows("decode(\"-\"): a node with no letter", () -> t.decode("-"));
        checkThrows("decode(\"-.\")", () -> t.decode("-."));
        checkThrows("decode(\"-..\")", () -> t.decode("-.."));
        MorseTree.Node xNode = t.root.right.left.left.right;
        t.insert('T', "-");
        t.insert('D', "-..");
        check("then insert T -: decode(\"-\")", 'T', t.decode("-"));
        check("then insert D -..: decode(\"-..\")", 'D', t.decode("-.."));
        check("decode(\"-..-\") still X", 'X', t.decode("-..-"));
        check("X is still in the same node", true, t.root.right.left.left.right == xNode);
    }

    static void treeErrors() {
        MorseTree t = new MorseTree();
        t.insert('E', ".");
        checkThrows("insert('Z', \"\")", () -> t.insert('Z', ""));
        checkThrows("insert('Z', \".x\")", () -> t.insert('Z', ".x"));
        checkThrows("insert('Z', \"-_\")", () -> t.insert('Z', "-_"));
        checkThrows("insert('I', \".\"): . is already E", () -> t.insert('I', "."));
        check("decode(\".\") still E", 'E', t.decode("."));
        checkThrows("decode(\"x\")", () -> t.decode("x"));
        checkThrows("decode(\".x\")", () -> t.decode(".x"));
    }

    static void treeTable() {
        List<String> table = Morse.lines(Morse.TABLE);
        MorseTree t = new MorseTree();
        for (String line : table) t.insert(line.charAt(0), line.substring(2));
        int wrong = 0;
        for (String line : table) {
            char letter = line.charAt(0);
            String code = line.substring(2);
            char got = t.decode(code);
            if (got != letter) {
                if (wrong++ < 5) fail("decode(\"" + code + "\"): expected " + quote(letter) + ", got " + quote(got));
            }
        }
        if (wrong == 0) ok("the whole table: " + table.size() + " codes decoded");
        checkThrows("decode(\"..--\"): a node with no letter", () -> t.decode("..--"));
        checkThrows("decode(\"-------\"): no node", () -> t.decode("-------"));
        checkThrows("decode(\".-.-.-.\"): no node", () -> t.decode(".-.-.-."));
    }

    // ==== Part 2 and Bonus: the maps ====

    // the hash code of a string of one character is the number of the character: "A" is 65

    static void mapBasics(String cls, int defaultCapacity) {
        StringMap m = newMap(cls, null);
        check("new " + cls + "(): capacity()", defaultCapacity, m.capacity());
        check("size()", 0, m.size());
        check("cells()", "_" + " _".repeat(defaultCapacity - 1), m.cells());
        check("get(\".-\")", null, m.get(".-"));
        check("containsKey(\".-\")", false, m.containsKey(".-"));
        check("put(\".-\", \"A\") returns", null, m.put(".-", "A"));
        check("get(\".-\")", "A", m.get(".-"));
        check("containsKey(\".-\")", true, m.containsKey(".-"));
        check("size()", 1, m.size());
        check("put(\".-\", \"X\") returns the old value", "A", m.put(".-", "X"));
        check("get(\".-\")", "X", m.get(".-"));
        check("size() is still 1", 1, m.size());
        check("get(\"-.\")", null, m.get("-."));
        checkThrows("put(\"-.\", null)", () -> m.put("-.", null));
        checkThrows("put(null, \"N\")", () -> m.put(null, "N"));
        check("size() is still 1", 1, m.size());

        StringMap e = newMap(cls, null);
        e.put(new String("..."), "S");
        check("put and get with two different String objects, both \"...\"", "S", e.get(new String("...")));
        e.put(new String("..."), "s");
        check("put again with a third one: size()", 1, e.size());
    }

    static void linearCollisions() {
        String cls = "LinearProbingMap";
        StringMap m = newMap(cls, null);
        m.put("A", "a");    // 65 % 8 = 1
        m.put("I", "i");    // 73 % 8 = 1
        m.put("Q", "q");    // 81 % 8 = 1
        m.put("H", "h");    // 72 % 8 = 0
        check("put A, I, Q, H (home cells 1, 1, 1, 0): cells()", "H A I Q _ _ _ _", m.cells());
        for (String k : List.of("A", "I", "Q", "H"))
            check("get(" + quote(k) + ")", k.toLowerCase(), m.get(k));
        check("get(\"Y\"), home cell 1, not there", null, m.get("Y"));
        check("containsKey(\"Y\")", false, m.containsKey("Y"));
        check("put(\"Q\", \"Q\") returns", "q", m.put("Q", "Q"));
        check("cells() did not change", "H A I Q _ _ _ _", m.cells());
        check("size()", 4, m.size());
        check("capacity(): 4 keys in 8 cells, no rehash yet", 8, m.capacity());
        m.put("G", "g");    // 71 % 8 = 7
        check("put G: 5 keys > 8 / 2, capacity()", 16, m.capacity());
        check("cells() after the rehash", "_ A Q _ _ _ _ G H I _ _ _ _ _ _", m.cells());
        for (String k : List.of("A", "I", "Q", "H", "G"))
            check("get(" + quote(k) + ") after the rehash", k.equals("Q") ? "Q" : k.toLowerCase(), m.get(k));
        check("size()", 5, m.size());

        StringMap w = newMap(cls, null);
        w.put("G", "g");    // 71 % 8 = 7
        w.put("O", "o");    // 79 % 8 = 7
        check("put G, O (home cells 7, 7): wraps around", "O _ _ _ _ _ _ G", w.cells());
        check("get(\"O\")", "o", w.get("O"));
        check("get(\"W\"), home cell 7, not there", null, w.get("W"));

        StringMap s = newMap(cls, 3);
        s.put("A", "a");    // 65 % 3 = 2
        s.put("B", "b");    // 66 % 3 = 0: 2 keys > 3 / 2, rehash to 6
        check("new " + cls + "(3): put A, B: capacity()", 6, s.capacity());
        check("cells()", "B _ _ _ _ A", s.cells());
        checkThrows("new " + cls + "(0)", () -> newMap(cls, 0));
        checkThrows("new " + cls + "(-8)", () -> newMap(cls, -8));
    }

    static void negativeHash(String cls) {
        StringMap m = newMap(cls, null);
        String key = ".......";     // hash code -763664658
        int home = Math.floorMod(key.hashCode(), m.capacity());
        m.put(key, "x");
        check(quote(key) + ".hashCode() is " + key.hashCode() + ": get", "x", m.get(key));
        check("its home cell is " + home + ": cells()",
              "_ ".repeat(home) + key + " _".repeat(m.capacity() - home - 1), m.cells());
    }

    static void quadraticCollisions() {
        String cls = "QuadraticProbingMap";
        StringMap m = newMap(cls, 11);
        m.put("B", "b");    // 66 % 11 = 0
        m.put("M", "m");    // 77 % 11 = 0
        m.put("X", "x");    // 88 % 11 = 0
        m.put("c", "c");    // 99 % 11 = 0
        check("new " + cls + "(11): put B, M, X, c (home cell 0): cells()", "B M _ _ X _ _ _ _ c _", m.cells());
        for (String k : List.of("B", "M", "X", "c"))
            check("get(" + quote(k) + ")", k.toLowerCase(), m.get(k));
        check("get(\"n\"), home cell 0, not there", null, m.get("n"));
        m.put("n", "n");    // 110 % 11 = 0: cells 0, 1, 4, 9, 16 % 11 = 5
        check("put n, home cell 0: cells()", "B M _ _ X n _ _ _ c _", m.cells());
        check("capacity(): 5 keys in 11 cells, no rehash yet", 11, m.capacity());
        m.put("D", "d");
        check("put D: 6 keys > 11 / 2, capacity() is the smallest prime >= 22", 23, m.capacity());
        check("cells() after the rehash", "_ _ _ _ _ _ _ c M _ _ _ _ _ _ _ _ _ n X B _ D", m.cells());
        for (String k : List.of("B", "M", "X", "c", "n", "D"))
            check("get(" + quote(k) + ") after the rehash", k.toLowerCase(), m.get(k));

        StringMap d = newMap(cls, null);
        d.put("A", "a");
        d.put("B", "b");
        d.put("C", "c");
        check("new " + cls + "(): put A, B, C: capacity()", 7, d.capacity());
        d.put("D", "d");
        check("put D: 4 keys > 7 / 2, capacity()", 17, d.capacity());

        for (int bad : new int[] {0, 1, 4, 8, 9, 15, 21, -7})
            checkThrows("new " + cls + "(" + bad + "): not prime", () -> newMap(cls, bad));
        for (int prime : new int[] {2, 3, 13, 101})
            check("new " + cls + "(" + prime + "): capacity()", prime, newMap(cls, prime).capacity());
    }

    static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int d = 2; d * d <= n; d++) if (n % d == 0) return false;
        return true;
    }

    // the capacity after adding one more key to a map with this capacity and keys
    static int grow(int capacity, int keys, boolean prime) {
        if (keys <= capacity / 2.0) return capacity;
        int next = 2 * capacity;
        while (prime && !isPrime(next)) next++;
        return next;
    }

    static void mapGrowth(String cls, int defaultCapacity, boolean prime) {
        StringMap m = newMap(cls, null);
        int expected = defaultCapacity;
        String sequence = String.valueOf(expected);
        for (int i = 1; i <= 1000; i++) {
            m.put("key" + i, "v");
            int next = grow(expected, i, prime);
            if (next != expected) sequence += ", " + next;
            expected = next;
            if (m.capacity() != expected) {
                fail("after " + i + " keys: expected capacity " + expected + ", got " + m.capacity());
                return;
            }
        }
        ok("capacity while adding 1000 keys: " + sequence);
    }

    static void mapRandom(String cls, int[] capacities) {
        Random rnd = new Random(102);
        for (int capacity : capacities) {
            StringMap m = newMap(cls, capacity);
            Map<String, String> ref = new HashMap<>();
            for (int step = 1; step <= 3000; step++) {
                StringBuilder k = new StringBuilder();
                for (int i = 1 + rnd.nextInt(5); i > 0; i--) k.append(rnd.nextBoolean() ? '.' : '-');
                String key = k.toString();
                String what, expected, actual;
                switch (rnd.nextInt(3)) {
                    case 0 -> {
                        String v = "v" + rnd.nextInt(5);
                        what = "put(" + quote(key) + ", \"" + v + "\")";
                        expected = ref.put(key, v);
                        actual = m.put(key, v);
                    }
                    case 1 -> {
                        what = "get(" + quote(key) + ")";
                        expected = ref.get(key);
                        actual = m.get(key);
                    }
                    default -> {
                        what = "containsKey(" + quote(key) + ")";
                        expected = String.valueOf(ref.containsKey(key));
                        actual = String.valueOf(m.containsKey(key));
                    }
                }
                if (expected == null ? actual != null : !expected.equals(actual)) {
                    fail("capacity " + capacity + ", step " + step + ": " + what + ": expected " + expected + ", got " + actual);
                    return;
                }
                if (m.size() != ref.size()) {
                    fail("capacity " + capacity + ", step " + step + ": " + what + ": expected size "
                         + ref.size() + ", got " + m.size());
                    return;
                }
                if (2 * m.size() > m.capacity()) {
                    fail("capacity " + capacity + ", step " + step + ": " + m.size() + " keys in "
                         + m.capacity() + " cells: more than half full");
                    return;
                }
            }
            ok("new " + cls + "(" + capacity + "): 3000 random operations");
        }
    }

    static void mapMany(String cls) {
        StringMap m = newMap(cls, null);
        int n = 200_000;
        for (int i = 0; i < n; i++) m.put("key" + i, "" + i);
        check("put " + n + " keys: size()", n, m.size());
        for (int i = 0; i < n; i++) {
            if (!("" + i).equals(m.get("key" + i))) {
                fail("get(\"key" + i + "\"): expected \"" + i + "\", got " + m.get("key" + i));
                return;
            }
        }
        ok("get every key");
        check("get(\"key" + n + "\"), not there", null, m.get("key" + n));
    }

    static void runMap(String cls, int defaultCapacity, boolean prime, Runnable collisions, int[] capacities) {
        if (!exists(cls)) {
            run(cls, () -> fail(cls + " not found: create " + cls + ".java"));
            return;
        }
        run(cls + ": basics", () -> mapBasics(cls, defaultCapacity));
        run(cls + ": collisions and rehashing", collisions);
        run(cls + ": a negative hash code", () -> negativeHash(cls));
        run(cls + ": capacity", () -> mapGrowth(cls, defaultCapacity, prime));
        run(cls + ": random", () -> mapRandom(cls, capacities));
        run(cls + ": many keys", () -> mapMany(cls));
    }

    // ==== Part 3 ====

    static void checkEncode(Morse m, String text, String expected) {
        check("encode(" + quote(text) + ")", expected, m.encode(text));
    }

    static void checkDecode(Morse m, String morse, String expected) {
        check("decode(" + quote(morse) + ")", expected, m.decode(morse));
        check("decodeWithMap(" + quote(morse) + ")", expected, m.decodeWithMap(morse));
    }

    static void checkDecodeThrows(Morse m, String morse) {
        checkThrows("decode(" + quote(morse) + ")", () -> m.decode(morse));
        checkThrows("decodeWithMap(" + quote(morse) + ")", () -> m.decodeWithMap(morse));
    }

    static void encode() {
        Morse m = new Morse(Morse.TABLE, new Reference());
        checkEncode(m, "SOS", "... --- ...");
        checkEncode(m, "sos", "... --- ...");
        checkEncode(m, "SOS HELP", "... --- ... / .... . .-.. .--.");
        checkEncode(m, "E", ".");
        checkEncode(m, "", "");
        checkEncode(m, "41.46 N", "....- .---- .-.-.- ....- -.... / -.");
        checkEncode(m, "IT'S", ".. - .----. ...");
        checkEncode(m, "a b c", ".- / -... / -.-.");
        checkThrows("encode(\"HI!\"): no code for !", () -> m.encode("HI!"));
        checkThrows("encode(\"caffè\"): è is character 232", () -> m.encode("caffè"));
        checkThrows("encode(\"\\u2603\"): character 9731", () -> m.encode("\u2603"));
    }

    static void decode() {
        Morse m = new Morse(Morse.TABLE, new Reference());
        checkDecode(m, "... --- ...", "SOS");
        checkDecode(m, "... --- ... / .... . .-.. .--.", "SOS HELP");
        checkDecode(m, ".", "E");
        checkDecode(m, "", "");
        checkDecode(m, "....- .---- .-.-.- ....- -.... / -.", "41.46 N");
        checkDecode(m, ".- / -... / -.-.", "A B C");
        checkDecodeThrows(m, "..--");
        checkDecodeThrows(m, "... ------- ...");
        checkDecodeThrows(m, "... --x ...");
    }

    static final String EVERY = "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG. 0123456789, \"(HELLO)\" @ 1+1=2? 'ME-YOU/' :";

    static void roundTrip() {
        Morse m = new Morse(Morse.TABLE, new Reference());
        String morse = m.encode(EVERY);
        check("decode(encode(text)) == text, every character of the table", EVERY, m.decode(morse));
        check("decodeWithMap(encode(text)) == text", EVERY, m.decodeWithMap(morse));
    }

    static void withLinear() {
        Morse m = new Morse(Morse.TABLE, newMap("LinearProbingMap", null));
        String morse = m.encode(EVERY);
        check("decodeWithMap(encode(text)) == text, every character of the table", EVERY, m.decodeWithMap(morse));
    }

    static void fast() {
        Morse m = new Morse(Morse.TABLE, new Reference());
        StringBuilder text = new StringBuilder();
        Random rnd = new Random(102);
        while (text.length() < 300_000) {
            for (int i = 1 + rnd.nextInt(8); i > 0; i--) text.append((char) ('A' + rnd.nextInt(26)));
            text.append(' ');
        }
        String t = text.toString().trim();
        String morse = m.encode(t);
        ok("encode a text of " + t.length() + " characters");
        check("decode it back", t, m.decode(morse));
        check("decodeWithMap it back", t, m.decodeWithMap(morse));
    }

    public static void main(String[] args) {
        boolean bonus = args.length > 0 && args[0].equals("bonus");

        run("Part 1: insert and decode", Test::treeSmall);
        run("Part 1: a long code first", Test::treeDeepFirst);
        run("Part 1: errors", Test::treeErrors);
        run("Part 1: the whole table", Test::treeTable);
        runMap("LinearProbingMap", 8, false, Test::linearCollisions, new int[] {1, 2, 3, 8, 10});
        run("Part 3: encode", Test::encode);
        run("Part 3: decode", Test::decode);
        run("Part 3: round trip", Test::roundTrip);
        run("Part 3: long texts", Test::fast);
        if (exists("LinearProbingMap"))
            run("Part 3: decodeWithMap with your LinearProbingMap", Test::withLinear);
        if (bonus)
            runMap("QuadraticProbingMap", 7, true, Test::quadraticCollisions, new int[] {2, 3, 5, 7, 11});

        System.out.println(failures == 0 ? "\nall tests passed" : "\n" + failures + " failed");
    }
}
