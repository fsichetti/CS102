import java.util.*;
import calc.*;
import deque.*;

// Given, complete. Run with
//     java Tests                  every section
//     java Tests deque postfix    only the named sections
//
// Sections: deque, iterator, resize, stackqueue, postfix, evaluate, calculate, variables, and the optional
// functions and syntax. Everything after stackqueue uses your deque: the Tokenizer returns one.
public class Tests {

    static int passed, failed;
    static boolean timedOut;  // set by the first timeout in a section: its remaining tests are not run

    public static void main(String[] args) {
        Set<String> only = new HashSet<>(Arrays.asList(args));
        section(only, "deque", Tests::deque);
        section(only, "iterator", Tests::iterator);
        section(only, "resize", Tests::resize);
        section(only, "stackqueue", Tests::stackQueue);
        section(only, "postfix", Tests::postfix);
        section(only, "evaluate", Tests::evaluate);
        section(only, "calculate", Tests::calculate);
        section(only, "variables", Tests::variables);
        section(only, "functions", Tests::functions);
        section(only, "syntax", Tests::syntax);
        System.out.printf("%n%d/%d passed%n", passed, passed + failed);
    }

    // ---------------------------------------------------------------------------------------------
    // Part 1: the deque

    static void deque() {
        test("new deque is empty", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            assertTrue(d.isEmpty() && d.size() == 0, "expected empty, size 0");
        });
        test("operations on an empty deque throw NoSuchElementException", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            assertThrows(NoSuchElementException.class, d::removeFirst, "removeFirst");
            assertThrows(NoSuchElementException.class, d::removeLast, "removeLast");
            assertThrows(NoSuchElementException.class, d::peekFirst, "peekFirst");
            assertThrows(NoSuchElementException.class, d::peekLast, "peekLast");
        });
        test("addLast, removeFirst: a queue", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            for (int i = 1; i <= 5; i++) d.addLast(i);
            assertEquals(5, d.size());
            for (int i = 1; i <= 5; i++) assertEquals(i, d.removeFirst());
            assertTrue(d.isEmpty(), "expected empty at the end");
        });
        test("addFirst, removeFirst: a stack", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            for (int i = 1; i <= 5; i++) d.addFirst(i);
            for (int i = 5; i >= 1; i--) assertEquals(i, d.removeFirst());
        });
        test("addLast, removeLast: a stack at the other end", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            for (int i = 1; i <= 5; i++) d.addLast(i);
            for (int i = 5; i >= 1; i--) assertEquals(i, d.removeLast());
        });
        test("addFirst, removeLast: a queue the other way", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            for (int i = 1; i <= 5; i++) d.addFirst(i);
            for (int i = 1; i <= 5; i++) assertEquals(i, d.removeLast());
        });
        test("peekFirst, peekLast do not remove", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            d.addLast(2);
            d.addFirst(1);
            d.addLast(3);
            assertEquals(1, d.peekFirst());
            assertEquals(3, d.peekLast());
            assertEquals(3, d.size());
        });
        test("one element is both first and last", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            d.addFirst(7);
            assertEquals(7, d.peekLast());
            assertEquals(7, d.removeLast());
            assertTrue(d.isEmpty(), "expected empty");
            d.addLast(8);
            assertEquals(8, d.peekFirst());
            assertEquals(8, d.removeFirst());
            assertTrue(d.isEmpty(), "expected empty");
        });
        test("addFirst on a new deque wraps around the start of the array", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            d.addFirst(3);
            d.addFirst(2);
            d.addFirst(1);
            d.addLast(4);
            assertDeque(d, "[1, 2, 3, 4]");
            assertEquals(1, d.removeFirst());
            assertEquals(4, d.removeLast());
            assertDeque(d, "[2, 3]");
        });
        test("a queue moving around the array many times", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            for (int i = 0; i < 5; i++) d.addLast(i);
            for (int i = 5; i < 1000; i++) {
                d.addLast(i);
                assertEquals(i - 5, d.removeFirst());
            }
            assertDeque(d, "[995, 996, 997, 998, 999]");
        });
        test("null is an element like any other", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            d.addLast(null);
            d.addLast(1);
            assertEquals(2, d.size());
            assertEquals(null, d.removeFirst());
        });
        test("1000 elements at both ends", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            for (int i = 0; i < 500; i++) {
                d.addFirst(-i - 1);
                d.addLast(i);
            }
            assertEquals(1000, d.size());
            for (int i = -500; i < 500; i++) assertEquals(i, d.removeFirst());
        });
        test("100000 random operations, against java.util.ArrayDeque", () -> randomOperations(100_000, false));
    }

    static void iterator() {
        test("for-each, first to last", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            for (int i = 1; i <= 4; i++) d.addLast(i);
            List<Integer> seen = new ArrayList<>();
            for (int x : d) seen.add(x);
            assertEquals(List.of(1, 2, 3, 4), seen);
        });
        test("for-each over an empty deque", () -> {
            for (Integer x : new MyArrayDeque<Integer>()) throw new AssertionError("visited " + x);
        });
        test("iterating does not change the deque", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            for (int i = 1; i <= 4; i++) d.addLast(i);
            for (int x : d) { }
            for (int x : d) { }
            assertDeque(d, "[1, 2, 3, 4]");
            assertEquals(4, d.size());
        });
        test("for-each after wrapping around", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            for (int i = 0; i < 6; i++) d.addLast(i);
            for (int i = 0; i < 4; i++) d.removeFirst();
            for (int i = 6; i < 10; i++) d.addLast(i);
            d.addFirst(3);
            assertDeque(d, "[3, 4, 5, 6, 7, 8, 9]");
        });
        test("two iterators are independent", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            for (int i = 1; i <= 3; i++) d.addLast(i);
            Iterator<Integer> a = d.iterator();
            Iterator<Integer> b = d.iterator();
            assertEquals(1, a.next());
            assertEquals(2, a.next());
            assertEquals(1, b.next());
            assertEquals(3, a.next());
            assertEquals(2, b.next());
        });
        test("hasNext, then next() at the end throws NoSuchElementException", () -> {
            MyDeque<Integer> d = new MyArrayDeque<>();
            d.addLast(1);
            Iterator<Integer> it = d.iterator();
            assertTrue(it.hasNext(), "hasNext() should be true");
            assertTrue(it.hasNext(), "hasNext() twice should still be true");
            assertEquals(1, it.next());
            assertTrue(!it.hasNext(), "hasNext() should be false");
            assertThrows(NoSuchElementException.class, it::next, "next() at the end");
        });
    }

    static void resize() {
        test("capacity starts at INITIAL_CAPACITY and stays while there is room", () -> {
            MyArrayDeque<Integer> d = new MyArrayDeque<>();
            assertCapacity(d, MyArrayDeque.INITIAL_CAPACITY);
            for (int i = 0; i < MyArrayDeque.INITIAL_CAPACITY; i++) d.addLast(i);
            assertCapacity(d, MyArrayDeque.INITIAL_CAPACITY);
        });
        test("doubles when full, keeping the order", () -> {
            MyArrayDeque<Integer> d = new MyArrayDeque<>();
            int capacity = MyArrayDeque.INITIAL_CAPACITY;
            for (int i = 0; i < 100; i++) {
                if (d.size() == capacity) capacity *= 2;
                if (i % 2 == 0) d.addLast(i);
                else d.addFirst(i);
                assertCapacity(d, capacity);
            }
            StringBuilder want = new StringBuilder("[");
            for (int i = 99; i >= 1; i -= 2) want.append(i).append(", ");
            for (int i = 0; i <= 98; i += 2) want.append(i).append(i < 98 ? ", " : "]");
            assertDeque(d, want.toString());
        });
        test("grows correctly when the elements wrap around the end of the array", () -> {
            MyArrayDeque<Integer> d = new MyArrayDeque<>();
            for (int i = 0; i < 8; i++) d.addLast(i);
            for (int i = 0; i < 5; i++) d.removeFirst();
            for (int i = 8; i < 13; i++) d.addLast(i);     // full again, wrapped
            d.addLast(13);                                 // grows
            assertDeque(d, "[5, 6, 7, 8, 9, 10, 11, 12, 13]");
            d.addFirst(4);
            assertDeque(d, "[4, 5, 6, 7, 8, 9, 10, 11, 12, 13]");
        });
        test("halves when a quarter full, never below INITIAL_CAPACITY", () -> {
            MyArrayDeque<Integer> d = new MyArrayDeque<>();
            for (int i = 0; i < 64; i++) d.addLast(i);
            assertCapacity(d, 64);
            // size after each removal -> expected capacity
            for (int size = 63; size >= 0; size--) {
                if (size % 2 == 0) d.removeFirst();
                else d.removeLast();
                int expected = size > 16 ? 64 : size > 8 ? 32 : size > 4 ? 16 : 8;
                if (d.capacity() != expected) {
                    throw new AssertionError("size " + size + ": expected capacity " + expected + ", got " + d.capacity());
                }
            }
        });
        test("shrinking keeps the order, also when wrapped", () -> {
            MyArrayDeque<Integer> d = new MyArrayDeque<>();
            for (int i = 0; i < 32; i++) d.addFirst(i);    // front moves backwards around the array
            for (int i = 0; i < 26; i++) d.removeLast();
            assertDeque(d, "[31, 30, 29, 28, 27, 26]");
            assertCapacity(d, 16);
        });
        test("100000 random operations, also checking the capacity", () -> randomOperations(100_000, true));
        test("a million addLast, then a million removeFirst", () -> {
            MyArrayDeque<Integer> d = new MyArrayDeque<>();
            for (int i = 0; i < 1_000_000; i++) d.addLast(i);
            for (int i = 0; i < 1_000_000; i++) {
                if (d.removeFirst() != i) throw new AssertionError("wrong element after " + i + " removals");
            }
            assertCapacity(d, MyArrayDeque.INITIAL_CAPACITY);
        });
    }

    static void stackQueue() {
        test("push, pop, peek: last in, first out", () -> {
            MyStack<Integer> s = new MyArrayDeque<>();
            for (int i = 1; i <= 20; i++) s.push(i);
            assertEquals(20, s.peek());
            assertEquals(20, s.size());
            for (int i = 20; i >= 1; i--) assertEquals(i, s.pop());
            assertTrue(s.isEmpty(), "expected empty at the end");
            assertThrows(NoSuchElementException.class, s::pop, "pop on an empty stack");
            assertThrows(NoSuchElementException.class, s::peek, "peek on an empty stack");
        });
        test("enqueue, dequeue, peek: first in, first out", () -> {
            MyQueue<Integer> q = new MyArrayDeque<>();
            for (int i = 1; i <= 20; i++) q.enqueue(i);
            assertEquals(1, q.peek());
            assertEquals(20, q.size());
            for (int i = 1; i <= 20; i++) assertEquals(i, q.dequeue());
            assertTrue(q.isEmpty(), "expected empty at the end");
            assertThrows(NoSuchElementException.class, q::dequeue, "dequeue on an empty queue");
            assertThrows(NoSuchElementException.class, q::peek, "peek on an empty queue");
        });
        test("a stack iterates from the top, a queue from the front", () -> {
            MyStack<Integer> s = new MyArrayDeque<>();
            MyQueue<Integer> q = new MyArrayDeque<>();
            for (int i = 1; i <= 4; i++) {
                s.push(i);
                q.enqueue(i);
            }
            List<Integer> fromStack = new ArrayList<>();
            for (int x : s) fromStack.add(x);
            List<Integer> fromQueue = new ArrayList<>();
            for (int x : q) fromQueue.add(x);
            assertEquals(List.of(4, 3, 2, 1), fromStack);
            assertEquals(List.of(1, 2, 3, 4), fromQueue);
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Part 2: shunting-yard

    static void postfix() {
        String[][] cases = {
            {"1 + 2", "1 2 +"},
            {"1 + 2 * 3", "1 2 3 * +"},
            {"1 * 2 + 3", "1 2 * 3 +"},
            {"(1 + 2) * 3", "1 2 + 3 *"},
            {"1 * 2 + 3 * 4", "1 2 * 3 4 * +"},
            {"1 - 2 - 3", "1 2 - 3 -"},
            {"1 - (2 - 3)", "1 2 3 - -"},
            {"8 / 4 / 2", "8 4 / 2 /"},
            {"1 - 2 + 3", "1 2 - 3 +"},
            {"2 * 3 / 4", "2 3 * 4 /"},
            {"2 ^ 3 ^ 2", "2 3 2 ^ ^"},
            {"(2 ^ 3) ^ 2", "2 3 ^ 2 ^"},
            {"2 * 3 ^ 2", "2 3 2 ^ *"},
            {"2 ^ 3 * 2", "2 3 ^ 2 *"},
            {"((1))", "1"},
            {"42", "42"},
            {"x * (y + z)", "x y z + *"},
            {"3 + 4 * 2 / (1 - 5) ^ 2 ^ 3", "3 4 2 * 1 5 - 2 3 ^ ^ / +"},
            {"((1 + 2) * (3 - 4)) / 5", "1 2 + 3 4 - * 5 /"},
        };
        for (String[] c : cases) toPostfix(c[0], c[1]);
        String[][] unary = {
            {"-3", "3 neg"},
            {"-x * y", "x neg y *"},
            {"1 - -1", "1 1 neg -"},
            {"--x", "x neg neg"},
            {"-2 ^ 2", "2 2 ^ neg"},
            {"2 ^ -1", "2 1 neg ^"},
            {"2 * -(3 + 4)", "2 3 4 + neg *"},
            {"-(1 + 2) * 3", "1 2 + neg 3 *"},
            {"2 ^ -3 ^ 2", "2 3 2 ^ neg ^"},
        };
        for (String[] c : unary) toPostfix(c[0], c[1]);
        for (String bad : new String[] {"(1 + 2", "1 + 2)", ")(", "((1)", "(1))", "1 = 2"}) {
            rejects("toPostfix(\"" + bad + "\")", () -> ShuntingYard.toPostfix(infix(bad)), IllegalArgumentException.class);
        }
        test("a long expression: 1 + 1 + ... + 1, 1000 times", () -> {
            String text = String.join(" + ", Collections.nCopies(1000, "1"));
            MyQueue<Token> out = ShuntingYard.toPostfix(infix(text));
            assertEquals(1999, out.size());
            assertEquals("1 1 + 1 +", Token.join(firstTokens(out, 5)));
        });
        test("1000 nested parentheses", () -> {
            String text = "(".repeat(1000) + "1" + ")".repeat(1000);
            assertEquals("1", Token.join(ShuntingYard.toPostfix(infix(text))));
        });
        test("1 ^ 1 ^ ... ^ 1, 1000 times: the stack holds 999 operators", () -> {
            String text = String.join(" ^ ", Collections.nCopies(1000, "1"));
            String want = String.join(" ", Collections.nCopies(1000, "1")) + " " + String.join(" ", Collections.nCopies(999, "^"));
            assertEquals(want, Token.join(ShuntingYard.toPostfix(infix(text))));
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Part 3: evaluation

    static void evaluate() {
        Object[][] cases = {
            {"1 2 +", 3.0},
            {"1 2 3 * +", 7.0},
            {"5 2 -", 3.0},
            {"2 5 -", -3.0},
            {"8 2 /", 4.0},
            {"1 2 /", 0.5},
            {"2 3 ^", 8.0},
            {"2 3 2 ^ ^", 512.0},
            {"2 3 ^ 2 ^", 64.0},
            {"1 2 - 3 -", -4.0},
            {"1 2 + 3 4 + *", 21.0},
            {"3 neg", -3.0},
            {"2 2 ^ neg", -4.0},
            {"2 1 neg ^", 0.5},
            {"3 neg neg", 3.0},
            {"7", 7.0},
            {"3 4 2 * 1 5 - 2 3 ^ ^ / +", 3.0001220703125},
        };
        for (Object[] c : cases) evaluates((String) c[0], (double) c[1]);
        for (String bad : new String[] {"", "+", "1 +", "1 2", "neg", "1 2 3 +", "x", "1 x +"}) {
            rejects("evaluate(\"" + bad + "\")", () -> Evaluator.evaluate(postfix(bad)), IllegalArgumentException.class);
        }
        rejects("evaluate(\"1 0 /\")", () -> Evaluator.evaluate(postfix("1 0 /")), ArithmeticException.class);
        test("1000 values on the stack: 1 1 ... 1 + + ... +", () -> {
            String text = String.join(" ", Collections.nCopies(1000, "1")) + " " + String.join(" ", Collections.nCopies(999, "+"));
            assertClose(1000.0, Evaluator.evaluate(postfix(text)));
        });
    }

    static void calculate() {
        Object[][] cases = {
            {"1 + 2 * 3", 7.0},
            {"(1 + 2) * 3", 9.0},
            {"10 - 4 - 3", 3.0},
            {"100 / 10 / 5", 2.0},
            {"2 ^ 3 ^ 2", 512.0},
            {"-2 ^ 2", -4.0},
            {"(-2) ^ 2", 4.0},
            {"2 ^ -1", 0.5},
            {"-(-(3))", 3.0},
            {"1.5 * 4", 6.0},
            {".5 + .25", 0.75},
            {"2 * -(3 + 4)", -14.0},
            {"3 + 4 * 2 / (1 - 5) ^ 2 ^ 3", 3.0001220703125},
            {"(((((1 + 2)))))", 3.0},
        };
        for (Object[] c : cases) calculates((String) c[0], (double) c[1]);
        for (String bad : new String[] {"1 +", "(1", "1)", "()", "1 2", "* 3"}) {
            rejects("calculating \"" + bad + "\"", () -> Evaluator.evaluate(ShuntingYard.toPostfix(infix(bad))), IllegalArgumentException.class);
        }
        test("1 - 1 - ... - 1, 1000 times", () -> {
            String text = String.join(" - ", Collections.nCopies(1000, "1"));
            assertClose(-998.0, Evaluator.evaluate(ShuntingYard.toPostfix(infix(text))));
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Part 4: variables

    static void variables() {
        test("nothing to substitute", () -> {
            Definitions defs = new Definitions();
            assertSubstitutes(defs, "1 2 +", "1 2 +");
        });
        test("an undefined variable throws IllegalArgumentException", () -> {
            Definitions defs = new Definitions();
            assertThrows(IllegalArgumentException.class, () -> defs.substitute(postfix("x 1 +")), "x undefined");
            defs.define("x", postfix("1"));
            assertThrows(IllegalArgumentException.class, () -> defs.substitute(postfix("x y +")), "y undefined");
        });
        test("one variable, used twice", () -> {
            Definitions defs = new Definitions();
            defs.define("x", postfix("3"));
            assertSubstitutes(defs, "x x *", "3 3 *");
        });
        test("the body of a definition is an expression", () -> {
            Definitions defs = new Definitions();
            defs.define("x", postfix("1 2 +"));
            assertSubstitutes(defs, "x 3 *", "1 2 + 3 *");
        });
        test("a definition using an older one", () -> {
            Definitions defs = new Definitions();
            defs.define("x", postfix("1"));
            defs.define("y", postfix("x 1 +"));
            assertSubstitutes(defs, "y y *", "1 1 + 1 1 + *");
        });
        test("three levels", () -> {
            Definitions defs = new Definitions();
            defs.define("a", postfix("2"));
            defs.define("b", postfix("a a *"));
            defs.define("c", postfix("b a -"));
            assertSubstitutes(defs, "c", "2 2 * 2 -");
        });
        test("redefinition: x = x + 1 uses the previous x", () -> {
            Definitions defs = new Definitions();
            defs.define("x", postfix("1"));
            defs.define("x", postfix("x 1 +"));
            assertSubstitutes(defs, "x", "1 1 +");
            defs.define("x", postfix("x 2 *"));
            assertSubstitutes(defs, "x", "1 1 + 2 *");
        });
        test("y keeps the x it was defined with", () -> {
            Definitions defs = new Definitions();
            defs.define("x", postfix("1"));
            defs.define("y", postfix("x 1 +"));
            defs.define("x", postfix("10"));
            assertSubstitutes(defs, "y", "1 1 +");
            assertSubstitutes(defs, "x y +", "10 1 1 + +");
        });
        test("the bodies are unchanged: substituting twice gives the same result", () -> {
            Definitions defs = new Definitions();
            defs.define("x", postfix("2"));
            defs.define("y", postfix("x x +"));
            assertSubstitutes(defs, "y", "2 2 +");
            assertSubstitutes(defs, "y", "2 2 +");
            List<String> bodies = new ArrayList<>();
            for (Definitions.Definition d : defs.all()) bodies.add(d.toString());
            assertEquals(List.of("y = x x +", "x = 2"), bodies);
        });
        test("clear forgets everything", () -> {
            Definitions defs = new Definitions();
            defs.define("x", postfix("2"));
            defs.clear();
            assertThrows(IllegalArgumentException.class, () -> defs.substitute(postfix("x")), "x undefined");
        });
        test("doubling: x0 = 1, x1 = x0 x0 +, ..., x15 expands to 65535 tokens", () -> {
            Definitions defs = new Definitions();
            defs.define("x0", postfix("1"));
            for (int i = 1; i <= 15; i++) defs.define("x" + i, postfix("x" + (i - 1) + " x" + (i - 1) + " +"));
            MyQueue<Token> e = defs.substitute(postfix("x15"));
            assertEquals(65535, e.size());
            assertClose(32768.0, Evaluator.evaluate(e));
        });
        test("1000 redefinitions: n = n + 1", () -> {
            Definitions defs = new Definitions();
            defs.define("n", postfix("0"));
            for (int i = 0; i < 1000; i++) defs.define("n", postfix("n 1 +"));
            assertClose(1000.0, Evaluator.evaluate(defs.substitute(postfix("n"))));
        });
        test("end to end, as in the REPL", () -> {
            Definitions defs = new Definitions();
            assertClose(9.81, define(defs, "g", "9.81"));
            assertClose(3.0, define(defs, "t", "1 + 2"));
            assertClose(44.145, define(defs, "h", "g * t ^ 2 / 2"));
            assertClose(4.0, define(defs, "t", "t + 1"));
            assertClose(44.145, Evaluator.evaluate(defs.substitute(ShuntingYard.toPostfix(infix("h")))));
            assertClose(-4.0, Evaluator.evaluate(defs.substitute(ShuntingYard.toPostfix(infix("-t")))));
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Optional parts

    static void functions() {
        String[][] cases = {
            {"sqrt(16)", "16 sqrt"},
            {"sqrt(16) + 1", "16 sqrt 1 +"},
            {"max(1, 2)", "1 2 max"},
            {"max(1 + 2, 3 * 4)", "1 2 + 3 4 * max"},
            {"min(max(1, 2), 3)", "1 2 max 3 min"},
            {"max(1, min(2, 3))", "1 2 3 min max"},
            {"-abs(-3)", "3 neg abs neg"},
            {"sin(0) ^ 2", "0 sin 2 ^"},
            {"2 ^ sqrt(4)", "2 4 sqrt ^"},
            {"2 * max((1 + 2) * 3, 4)", "2 1 2 + 3 * 4 max *"},
        };
        for (String[] c : cases) toPostfix(c[0], c[1]);
        for (String bad : new String[] {"1, 2", "max(1, 2", "(1, 2)) + max(3"}) {
            rejects("toPostfix(\"" + bad + "\")", () -> ShuntingYard.toPostfix(infix(bad)), IllegalArgumentException.class);
        }
        calculates("max(3, 7) - min(3, 7)", 4.0);
        calculates("sqrt(abs(-16))", 4.0);
        calculates("exp(ln(5))", 5.0);
        calculates("cos(0) + sin(0)", 1.0);
        calculates("max(2, 3) ^ 2", 9.0);
        rejects("calculating \"max(1)\"", () -> Evaluator.evaluate(ShuntingYard.toPostfix(infix("max(1)"))), IllegalArgumentException.class);
    }

    static void syntax() {
        for (String bad : new String[] {"1 2 +", "1 2", "(1 +) 2", "2 (3)", "x y", "+", "1 +", "* 3", "()",
                                        "max(1,)", "max(, 1)", "1 + (2 *)", "(1)(2)"}) {
            rejects("toPostfix(\"" + bad + "\")", () -> ShuntingYard.toPostfix(infix(bad)), IllegalArgumentException.class);
        }
        // valid expressions are still accepted
        toPostfix("-2 ^ -x", "2 x neg ^ neg");
        toPostfix("(1 + 2) * -(3)", "1 2 + 3 neg *");
        toPostfix("max(-1, -(2))", "1 neg 2 neg max");
    }

    // ---------------------------------------------------------------------------------------------
    // Helpers

    interface Test { void run() throws Exception; }

    static void section(Set<String> only, String name, Runnable body) {
        if (!only.isEmpty() && !only.contains(name)) return;
        System.out.println("== " + name);
        int p = passed, f = failed;
        timedOut = false;
        body.run();
        System.out.printf("   %d/%d passed%n", passed - p, passed - p + failed - f);
    }

    // Runs one test. Any exception, or taking longer than TIMEOUT_MS (e.g. an infinite loop),
    // counts as a failure; the remaining tests still run.
    static final long TIMEOUT_MS = 5000;

    static void test(String name, Test t) {
        if (timedOut) {
            failed++;
            return;
        }
        Throwable[] error = new Throwable[1];
        Thread thread = new Thread(() -> {
            try {
                t.run();
            } catch (Throwable e) {
                error[0] = e;
            }
        });
        thread.setDaemon(true);  // a test stuck in a loop must not keep the program alive
        thread.start();
        try {
            thread.join(TIMEOUT_MS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String problem = null;
        if (thread.isAlive()) {
            timedOut = true;
            problem = "did not finish within " + TIMEOUT_MS + " ms: infinite loop? The rest of this section is skipped.";
        }
        else if (error[0] instanceof AssertionError) problem = error[0].getMessage();
        else if (error[0] != null) problem = "threw " + error[0];
        if (problem == null) {
            passed++;
        } else {
            failed++;
            System.out.println("   FAIL " + name + "\n        " + problem);
        }
    }

    // Tokens of an infix expression, from the given Tokenizer.
    static MyQueue<Token> infix(String text) { return Tokenizer.tokenize(text); }

    // Tokens of a postfix expression written with spaces, e.g. "1 2 + neg", without going through
    // the Tokenizer: "neg" is an operator here, not a variable.
    static MyQueue<Token> postfix(String text) {
        MyQueue<Token> tokens = new MyArrayDeque<>();
        for (String s : text.trim().split("\\s+")) {
            if (s.isEmpty()) continue;
            if (Character.isDigit(s.charAt(0)) || s.charAt(0) == '.') tokens.enqueue(Token.number(Double.parseDouble(s)));
            else if (Token.isOperatorSymbol(s)) tokens.enqueue(Token.operator(s));
            else if (Token.isFunctionName(s)) tokens.enqueue(Token.function(s));
            else tokens.enqueue(Token.variable(s));
        }
        return tokens;
    }

    static void toPostfix(String text, String expected) {
        test("toPostfix(\"" + text + "\")", () -> {
            MyQueue<Token> in = infix(text);
            String before = Token.join(in);
            MyQueue<Token> out = ShuntingYard.toPostfix(in);
            assertEquals(expected, Token.join(out));
            assertUnchanged(before, in, "infix");
        });
    }

    static void evaluates(String text, double expected) {
        test("evaluate(\"" + text + "\")", () -> {
            MyQueue<Token> in = postfix(text);
            String before = Token.join(in);
            assertClose(expected, Evaluator.evaluate(in));
            assertUnchanged(before, in, "postfix");
        });
    }

    static void calculates(String text, double expected) {
        test("calculating \"" + text + "\"", () ->
                assertClose(expected, Evaluator.evaluate(ShuntingYard.toPostfix(infix(text)))));
    }

    // name = text, as the REPL does it: evaluated first, defined only if that succeeds.
    static double define(Definitions defs, String name, String text) {
        MyQueue<Token> body = ShuntingYard.toPostfix(infix(text));
        double value = Evaluator.evaluate(defs.substitute(body));
        defs.define(name, body);
        return value;
    }

    static void assertSubstitutes(Definitions defs, String text, String expected) {
        MyQueue<Token> in = postfix(text);
        String before = Token.join(in);
        assertEquals(expected, Token.join(defs.substitute(in)));
        assertUnchanged(before, in, "postfix");
    }

    static void rejects(String name, Test t, Class<? extends Throwable> type) {
        test(name + " throws " + type.getSimpleName(), () -> assertThrows(type, t, name));
    }

    static void assertThrows(Class<? extends Throwable> type, Test t, String what) {
        try {
            t.run();
        } catch (Throwable e) {
            if (type.isInstance(e)) return;
            throw new AssertionError(what + ": expected " + type.getSimpleName() + ", got " + e);
        }
        throw new AssertionError(what + ": expected " + type.getSimpleName() + ", nothing was thrown");
    }

    // Random operations on a MyArrayDeque and a java.util.ArrayDeque, compared after each one.
    static void randomOperations(int n, boolean checkCapacity) {
        Random random = new Random(102);
        MyArrayDeque<Integer> d = new MyArrayDeque<>();
        ArrayDeque<Integer> ref = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            // drift between growing and shrinking phases
            boolean grow = (i / 5000) % 2 == 0;
            int op = random.nextInt(grow ? 6 : 10);
            String what;
            if (op <= 1 || ref.isEmpty()) {
                d.addFirst(i);
                ref.addFirst(i);
                what = "addFirst(" + i + ")";
            } else if (op <= 3) {
                d.addLast(i);
                ref.addLast(i);
                what = "addLast(" + i + ")";
            } else if (op <= 5) {
                what = "removeFirst()";
                assertEquals(ref.removeFirst(), d.removeFirst(), what);
            } else if (op <= 7) {
                what = "removeLast()";
                assertEquals(ref.removeLast(), d.removeLast(), what);
            } else if (op == 8) {
                what = "peekFirst()";
                assertEquals(ref.peekFirst(), d.peekFirst(), what);
            } else {
                what = "peekLast()";
                assertEquals(ref.peekLast(), d.peekLast(), what);
            }
            if (d.size() != ref.size()) {
                throw new AssertionError("after operation " + i + ", " + what + ": size " + d.size() + ", expected " + ref.size());
            }
            if (checkCapacity) {
                int c = d.capacity();
                if (c < d.size() || (c > MyArrayDeque.INITIAL_CAPACITY && d.size() <= c / 4)) {
                    throw new AssertionError("after operation " + i + ", " + what + ": capacity " + c + " for size " + d.size());
                }
            }
            if (i % 1000 == 0 && !d.toString().equals(ref.toString())) {
                throw new AssertionError("after operation " + i + ", " + what + ": contents differ");
            }
        }
    }

    static MyQueue<Token> firstTokens(MyQueue<Token> tokens, int k) {
        MyQueue<Token> result = new MyArrayDeque<>();
        for (Token t : tokens) {
            if (result.size() == k) break;
            result.enqueue(t);
        }
        return result;
    }

    static void assertUnchanged(String before, MyQueue<Token> tokens, String name) {
        if (!before.equals(Token.join(tokens))) {
            throw new AssertionError(name + " was changed: it was \"" + before + "\", now \"" + Token.join(tokens) + "\"");
        }
    }

    static void assertDeque(Object d, String expected) {
        if (!d.toString().equals(expected)) throw new AssertionError("expected " + expected + ", got " + d);
    }

    static void assertCapacity(MyArrayDeque<?> d, int expected) {
        if (d.capacity() != expected) {
            throw new AssertionError("size " + d.size() + ": expected capacity " + expected + ", got " + d.capacity());
        }
    }

    static void assertClose(double expected, double actual) {
        if (Math.abs(expected - actual) > 1e-9 * Math.max(1, Math.abs(expected))) {
            throw new AssertionError("expected " + Token.format(expected) + ", got " + Token.format(actual));
        }
    }

    static void assertEquals(Object expected, Object actual) { assertEquals(expected, actual, null); }

    static void assertEquals(Object expected, Object actual, String what) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError((what == null ? "" : what + ": ") + "expected " + expected + ", got " + actual);
        }
    }

    static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

}
