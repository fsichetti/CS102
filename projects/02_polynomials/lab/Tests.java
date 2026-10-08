import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import poly.*;

// Given, complete. Run with
//     java Tests                  every section
//     java Tests add multiply     only the named sections
//
// Results are checked through terms() and degree() only, never through your equals,
// so a bug in it does not make every other test fail. Every binary operation is tested
// on all four combinations: dense/dense, dense/sparse, sparse/dense, sparse/sparse.
public class Tests {

    static final String[] REPS = {"dense", "sparse"};

    static int passed, failed;
    static boolean timedOut;  // set by the first timeout in a section: its remaining tests are not run

    public static void main(String[] args) {
        Set<String> only = new HashSet<>(Arrays.asList(args));
        section(only, "construct", Tests::construct);
        section(only, "equals", Tests::equalsTests);
        section(only, "evaluate", Tests::evaluate);
        section(only, "addInPlace", Tests::addInPlace);
        section(only, "multiplyInPlace", Tests::multiplyInPlace);
        section(only, "derivativeInPlace", Tests::derivativeInPlace);
        section(only, "add", Tests::add);
        section(only, "negate", Tests::negate);
        section(only, "subtract", Tests::subtract);
        section(only, "multiply", Tests::multiply);
        section(only, "derivative", Tests::derivative);
        section(only, "divide", Tests::divide);
        System.out.printf("%n%d/%d passed%n", passed, passed + failed);
    }

    // ---------------------------------------------------------------------------------------------
    // Sections

    static void construct() {
        for (String rep : REPS) {
            expect(rep, "3x^2 - x + 5", "3x^2 - x + 5");
            expect(rep, "5 - x + 3x^2", "3x^2 - x + 5");
            expect(rep, "x + x", "2x");
            expect(rep, "x^3 + 2x^3 - 3x^3 + 1", "1");
            expect(rep, "x - x", "0");
            expect(rep, "0", "0");
            expect(rep, "0x^5 + 1", "1");
            expect(rep, "7", "7");
            expect(rep, "x^1000 + 1", "x^1000 + 1");
            expect(rep, "1 + x^2 + x^4 - x^2", "x^4 + 1");
            expect(rep, "(1/2)x + (1/2)x + (1/3)", "x + 1/3");
            test(rep + ": terms() can be called more than once", () -> {
                Polynomial p = make(rep, "x^2 + 1");
                p.terms();
                assertTerms(p, "x^2 + 1");
            });
        }
    }

    static void equalsTests() {
        for (String ra : REPS) {
            for (String rb : REPS) {
                String pair = ra + "/" + rb + ": ";
                isEqual(pair, ra, rb, "x + 1", "1 + x", true);
                isEqual(pair, ra, rb, "x + x", "2x", true);
                isEqual(pair, ra, rb, "0", "x - x", true);
                isEqual(pair, ra, rb, "x + 1", "x + 2", false);
                isEqual(pair, ra, rb, "x", "x^2", false);
                isEqual(pair, ra, rb, "x + 1", "x", false);
                isEqual(pair, ra, rb, "x", "x + 1", false);
                isEqual(pair, ra, rb, "0", "1", false);
                test(pair + "(1/2)x equals (2/4)x", () ->
                        assertTrue(make(ra, "(1/2)x").equals(make(rb, "(2/4)x")), "expected true"));
            }
            test(ra + ": not equal to a String", () -> assertTrue(!make(ra, "x").equals("x"), "expected false"));
            test(ra + ": not equal to null", () -> assertTrue(!make(ra, "x").equals(null), "expected false"));
        }
    }

    static void evaluate() {
        for (String rep : REPS) {
            evaluates(rep, "3x^2 - x + 5", 2, 15);
            evaluates(rep, "3x^2 - x + 5", 0, 5);
            evaluates(rep, "x^3 + x", -2, -10);
            evaluates(rep, "x^10", 2, 1024);
            evaluates(rep, "x^10 + x^3", 1, 2);
            evaluates(rep, "x^3", 0, 0);
            evaluates(rep, "7", 5, 7);
            evaluates(rep, "0", 5, 0);
            test(rep + ": (1/2)x^2 + 1 at 1/2", () ->
                    assertEquals(Rational.parse("9/8"), make(rep, "(1/2)x^2 + 1").evaluate(Rational.parse("1/2"))));
        }
    }

    static void addInPlace() {
        String[][] cases = {
            {"x + 1", "x - 1", "2x"},
            {"3x^2 - x + 5", "x^3 + x", "x^3 + 3x^2 + 5"},
            {"x^2 + x", "-x^2", "x"},
            {"x^2 + 1", "-x^2 - 1", "0"},
            {"x^3 + x^2 + 1", "-x^3", "x^2 + 1"},
            {"0", "x^2 + 3", "x^2 + 3"},
            {"x^2 + 3", "0", "x^2 + 3"},
            {"x^1000 + 1", "-1", "x^1000"},
            {"x^1000", "x^999 + x", "x^1000 + x^999 + x"},
            {"x^6 + x^4 + x^2", "x^5 + x^3 + x", "x^6 + x^5 + x^4 + x^3 + x^2 + x"},
            {"(1/2)x + 1/3", "(1/2)x + 1/6", "x + 1/2"},
        };
        for (String[] c : cases) inPlaceBinary(c[0], "+=", c[1], c[2], Polynomial::addInPlace);
        for (String rep : REPS) {
            test(rep + ": p += p", () -> {
                Polynomial p = make(rep, "x^3 - x + 1/2");
                p.addInPlace(p);
                assertTerms(p, "2x^3 - 2x + 1");
            });
            test(rep + ": p += q three times", () -> {
                Polynomial p = make(rep, "x^2");
                Polynomial q = make(rep, "x^4 - x^2 + 1");
                for (int i = 0; i < 3; i++) p.addInPlace(q);
                assertTerms(p, "3x^4 - 2x^2 + 3");
            });
            test(rep + ": p -= p", () -> {
                Polynomial p = make(rep, "x^3 - x + 1/2");
                p.subtractInPlace(p);
                assertTerms(p, "0");
            });
        }
    }

    static void multiplyInPlace() {
        String[][] cases = {
            {"3x^2 - x + 5", "2", "3", "6x^5 - 2x^4 + 10x^3"},
            {"3x^2 - x + 5", "1", "0", "3x^2 - x + 5"},
            {"3x^2 - x + 5", "-1/2", "1", "(-3/2)x^3 + (1/2)x^2 - (5/2)x"},
            {"3x^2 - x + 5", "0", "2", "0"},
            {"0", "5", "3", "0"},
            {"7", "1", "4", "7x^4"},
            {"x^1000 + 1", "1", "1000", "x^2000 + x^1000"},
        };
        for (String rep : REPS) {
            for (String[] c : cases) {
                test(rep + ": (" + c[0] + ") *= " + c[1] + "x^" + c[2], () -> {
                    Polynomial p = make(rep, c[0]);
                    p.multiplyInPlace(Rational.parse(c[1]), Integer.parseInt(c[2]));
                    assertTerms(p, c[3]);
                });
            }
        }
    }

    static void derivativeInPlace() {
        String[][] cases = {
            {"3x^2 - x + 5", "6x - 1"},
            {"x^3", "3x^2"},
            {"x", "1"},
            {"7", "0"},
            {"0", "0"},
            {"x^1000 + 5", "1000x^999"},
            {"(1/3)x^3 + (1/2)x^2", "x^2 + x"},
        };
        for (String[] c : cases) inPlaceUnary("derivativeInPlace", c[0], c[1], Polynomial::derivativeInPlace);
    }

    static void add() {
        binary("x + 1", "+", "x - 1", "2x", Polynomial::add);
        binary("3x^2 - x + 5", "+", "x^3 + x", "x^3 + 3x^2 + 5", Polynomial::add);
        binary("x^2 + x", "+", "-x^2", "x", Polynomial::add);
        binary("x^2 + 1", "+", "-x^2 - 1", "0", Polynomial::add);
        binary("x^3 + x^2 + 1", "+", "-x^3", "x^2 + 1", Polynomial::add);
        binary("0", "+", "x^2 + 3", "x^2 + 3", Polynomial::add);
        binary("x^2 + 3", "+", "0", "x^2 + 3", Polynomial::add);
        binary("0", "+", "0", "0", Polynomial::add);
        binary("x^1000 + 1", "+", "-1", "x^1000", Polynomial::add);
        binary("x^1000", "+", "x^999 + x", "x^1000 + x^999 + x", Polynomial::add);
        binary("(1/2)x + 1/3", "+", "(1/2)x + 1/6", "x + 1/2", Polynomial::add);
    }

    static void negate() {
        unary("negate", "3x^2 - x + 5", "-3x^2 + x - 5", Polynomial::negate);
        unary("negate", "-x", "x", Polynomial::negate);
        unary("negate", "0", "0", Polynomial::negate);
        unary("negate", "x^1000 + 1", "-x^1000 - 1", Polynomial::negate);
    }

    static void subtract() {
        binary("3x^2 - x + 5", "-", "x^2 - x", "2x^2 + 5", Polynomial::subtract);
        binary("x^2 + x", "-", "x^2 + x", "0", Polynomial::subtract);
        binary("0", "-", "x + 1", "-x - 1", Polynomial::subtract);
        binary("x + 1", "-", "x^3", "-x^3 + x + 1", Polynomial::subtract);
        binary("x^1000 + x", "-", "x", "x^1000", Polynomial::subtract);
    }

    static void multiply() {
        binary("x + 1", "*", "x - 1", "x^2 - 1", Polynomial::multiply);
        binary("x + 1", "*", "x + 1", "x^2 + 2x + 1", Polynomial::multiply);
        binary("x^2 + x + 1", "*", "x - 1", "x^3 - 1", Polynomial::multiply);
        binary("3x^2 - x + 5", "*", "2", "6x^2 - 2x + 10", Polynomial::multiply);
        binary("3x^2 - x + 5", "*", "0", "0", Polynomial::multiply);
        binary("0", "*", "x + 1", "0", Polynomial::multiply);
        binary("x^3", "*", "x^4", "x^7", Polynomial::multiply);
        binary("x^1000 + 1", "*", "x^1000 - 1", "x^2000 - 1", Polynomial::multiply);
        binary("2x^3 - 4x + 1", "*", "-x^2 + 3", "-2x^5 + 10x^3 - x^2 - 12x + 3", Polynomial::multiply);
        binary("(1/2)x + 1", "*", "2x - 4", "x^2 - 4", Polynomial::multiply);
    }

    static void derivative() {
        unary("derivative", "3x^2 - x + 5", "6x - 1", Polynomial::derivative);
        unary("derivative", "x^3", "3x^2", Polynomial::derivative);
        unary("derivative", "x", "1", Polynomial::derivative);
        unary("derivative", "7", "0", Polynomial::derivative);
        unary("derivative", "0", "0", Polynomial::derivative);
        unary("derivative", "x^1000 + 5", "1000x^999", Polynomial::derivative);
        unary("derivative", "(1/3)x^3 + (1/2)x^2", "x^2 + x", Polynomial::derivative);
    }

    static void divide() {
        divides("x^2 - 1", "x - 1", "x + 1", "0");
        divides("x^3 + 2x + 1", "x^2 + 1", "x", "x + 1");
        divides("x^3 - 1", "x - 1", "x^2 + x + 1", "0");
        divides("x + 1", "x^2", "0", "x + 1");
        divides("x^1000 - 1", "x^500 - 1", "x^500 + 1", "0");
        divides("x^2 + 1", "2x", "(1/2)x", "1");
        divides("x^3 + 3x^2 + 3x + 1", "x + 1", "x^2 + 2x + 1", "0");
        divides("x^2 + x + 1", "2x - 1", "(1/2)x + 3/4", "7/4");
        for (String rep : REPS) {
            test(rep + ": dividing by the zero polynomial throws ArithmeticException", () -> {
                try {
                    make(rep, "x + 1").divide(make(rep, "0"));
                } catch (ArithmeticException e) {
                    return;
                }
                throw new AssertionError("no exception");
            });
        }
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

    static Polynomial make(String rep, String text) {
        // Unmodifiable: constructors must not change the list they are given.
        List<Term> terms = Collections.unmodifiableList(Parser.parse(text));
        return rep.equals("dense") ? new DensePolynomial(terms) : new SparsePolynomial(terms);
    }

    static void expect(String rep, String text, String expected) {
        test(rep + ": construct \"" + text + "\"", () -> assertTerms(make(rep, text), expected));
    }

    // Tests rep_a.op(rep_b) for all four combinations, and checks that neither operand changed.
    static void binary(String a, String symbol, String b, String expected, BinaryOperator<Polynomial> op) {
        for (String ra : REPS) {
            for (String rb : REPS) {
                test(ra + "/" + rb + ": (" + a + ") " + symbol + " (" + b + ")", () -> {
                    Polynomial p = make(ra, a);
                    Polynomial q = make(rb, b);
                    Polynomial r = op.apply(p, q);
                    assertSameClass(r, p);
                    assertTerms(r, expected);
                    assertTerms(p, a);
                    assertTerms(q, b);
                });
            }
        }
    }

    static void unary(String name, String a, String expected, UnaryOperator<Polynomial> op) {
        for (String rep : REPS) {
            test(rep + ": " + name + " of " + a, () -> {
                Polynomial p = make(rep, a);
                Polynomial r = op.apply(p);
                assertSameClass(r, p);
                assertTerms(r, expected);
                assertTerms(p, a);
            });
        }
    }

    // Tests rep_a.op(rep_b) in place for all four combinations: `this` changes, `other` does not.
    static void inPlaceBinary(String a, String symbol, String b, String expected, BiConsumer<Polynomial, Polynomial> op) {
        for (String ra : REPS) {
            for (String rb : REPS) {
                test(ra + "/" + rb + ": (" + a + ") " + symbol + " (" + b + ")", () -> {
                    Polynomial p = make(ra, a);
                    Polynomial q = make(rb, b);
                    op.accept(p, q);
                    assertTerms(p, expected);
                    assertTerms(q, b);
                });
            }
        }
    }

    static void inPlaceUnary(String name, String a, String expected, Consumer<Polynomial> op) {
        for (String rep : REPS) {
            test(rep + ": " + name + " of " + a, () -> {
                Polynomial p = make(rep, a);
                op.accept(p);
                assertTerms(p, expected);
            });
        }
    }

    static void divides(String a, String b, String quotient, String remainder) {
        binary(a, "/", b, quotient, (p, q) -> p.divide(q).getQuotient());
        binary(a, "mod", b, remainder, (p, q) -> p.divide(q).getRemainder());
    }

    static void isEqual(String pair, String ra, String rb, String a, String b, boolean expected) {
        test(pair + "(" + a + ").equals(" + b + ")", () ->
                assertTrue(make(ra, a).equals(make(rb, b)) == expected, "expected " + expected));
    }

    static void evaluates(String rep, String a, int x, int expected) {
        test(rep + ": " + a + " at " + x, () ->
                assertEquals(new Rational(expected), make(rep, a).evaluate(new Rational(x))));
    }

    // Checks terms() and degree() of `actual` against the normalized terms of `expected`.
    static void assertTerms(Polynomial actual, String expected) {
        List<Term> want = normalize(Parser.parse(expected));
        List<Term> got = new ArrayList<>();
        Iterator<Term> it = actual.terms();
        while (it.hasNext()) {
            if (got.size() > 1_000_000) throw new AssertionError("terms() never stops");
            got.add(it.next());
        }
        boolean same = want.size() == got.size();
        for (int i = 0; same && i < want.size(); i++) {
            same = want.get(i).getExp() == got.get(i).getExp()
                    && want.get(i).getCoef().equals(got.get(i).getCoef());
        }
        if (!same) throw new AssertionError("expected terms " + want + "\n        got terms      " + got);
        int degree = want.isEmpty() ? -1 : want.get(0).getExp();
        if (actual.degree() != degree) {
            throw new AssertionError("terms " + got + " are right, but degree() is " + actual.degree());
        }
    }

    // Highest exponent first, equal exponents combined, zeros dropped.
    static List<Term> normalize(List<Term> terms) {
        TreeMap<Integer, Rational> byExp = new TreeMap<>(Comparator.reverseOrder());
        for (Term t : terms) byExp.merge(t.getExp(), t.getCoef(), Rational::add);
        List<Term> result = new ArrayList<>();
        for (Map.Entry<Integer, Rational> e : byExp.entrySet()) {
            if (!e.getValue().isZero()) result.add(new Term(e.getValue(), e.getKey()));
        }
        return result;
    }

    static void assertSameClass(Polynomial result, Polynomial self) {
        if (result.getClass() != self.getClass()) {
            throw new AssertionError("result is a " + result.getClass().getSimpleName()
                    + ", expected a " + self.getClass().getSimpleName() + " like this");
        }
    }

    static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) throw new AssertionError("expected " + expected + ", got " + actual);
    }

    static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

}
