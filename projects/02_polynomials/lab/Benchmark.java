import java.util.*;
import java.util.function.Supplier;
import poly.*;

// Given, complete. Times one operation on dense and on sparse polynomials.
//
//     java Benchmark <operation> <degree> <terms>   two random polynomials of that degree,
//                                                   each with that many nonzero terms
//     java Benchmark <operation> <degree>           the same, for an increasing number of terms
//
// operation: add, subtract, multiply, derivative, evaluate.
// For subtract, the inherited subtractInPlace (subtractByNegationInPlace) is timed too.
public class Benchmark {

    private static final Random random = new Random(102);  // fixed seed: same polynomials every run
    private static final double GIVE_UP_MS = 5000;           // stop timing a column once it gets this slow

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("usage: java Benchmark add|subtract|multiply|derivative|evaluate <degree> [<terms>]");
            return;
        }
        String op = args[0];
        int degree = Integer.parseInt(args[1]);

        List<Integer> sizes = new ArrayList<>();
        if (args.length > 2) {
            sizes.add(Integer.parseInt(args[2]));
        } else {
            for (int scale = 1; scale <= degree + 1; scale *= 10) {
                for (int k : new int[] {1, 2, 5}) if (k * scale <= degree + 1) sizes.add(k * scale);
            }
            if (sizes.get(sizes.size() - 1) != degree + 1) sizes.add(degree + 1);
        }

        String[] columns = op.equals("subtract")
                ? new String[] {"dense", "dense inherited", "sparse", "sparse inherited"}
                : new String[] {"dense", "sparse"};
        boolean[] gaveUp = new boolean[columns.length];

        System.out.printf("%s, degree %d (milliseconds per operation)%n%n%8s %8s", op, degree, "terms", "density");
        for (String c : columns) System.out.printf(" %17s", c);
        System.out.println();

        for (int terms : sizes) {
            if (terms < 1 || terms > degree + 1) throw new IllegalArgumentException("terms must be between 1 and degree + 1");
            List<Term> a = randomTerms(degree, terms);
            List<Term> b = randomTerms(degree, terms);
            System.out.printf("%8d %7.2f%%", terms, 100.0 * terms / (degree + 1));
            for (int c = 0; c < columns.length; c++) {
                if (gaveUp[c]) {
                    System.out.printf(" %17s", "-");
                    continue;
                }
                boolean dense = columns[c].startsWith("dense");
                boolean inherited = columns[c].endsWith("inherited");
                try {
                    Polynomial p = dense ? new DensePolynomial(a) : new SparsePolynomial(a);
                    Polynomial q = dense ? new DensePolynomial(b) : new SparsePolynomial(b);
                    double ms = time(() -> run(op, p, q, inherited));
                    System.out.printf(" %17.4f", ms);
                    if (ms > GIVE_UP_MS) gaveUp[c] = true;
                } catch (UnsupportedOperationException e) {
                    System.out.printf(" %17s", "not implemented");
                    gaveUp[c] = true;
                }
            }
            System.out.println();
        }
    }

    private static Object run(String op, Polynomial p, Polynomial q, boolean inherited) {
        switch (op) {
            case "add": return p.add(q);
            case "subtract": {
                if (!inherited) return p.subtract(q);
                Polynomial r = p.copy();
                r.subtractByNegationInPlace(q);
                return r;
            }
            case "multiply": return p.multiply(q);
            case "derivative": return p.derivative();
            case "evaluate": return p.evaluate(new Rational(2));
            default: throw new IllegalArgumentException("unknown operation: " + op);
        }
    }

    // `count` terms with distinct exponents, one of them `degree`, coefficients 1 to 9.
    private static List<Term> randomTerms(int degree, int count) {
        List<Integer> exps = new ArrayList<>();
        for (int e = 0; e < degree; e++) exps.add(e);
        Collections.shuffle(exps, random);
        List<Term> terms = new ArrayList<>();
        terms.add(new Term(new Rational(1 + random.nextInt(9)), degree));
        for (int i = 0; i < count - 1; i++) terms.add(new Term(new Rational(1 + random.nextInt(9)), exps.get(i)));
        return terms;
    }

    // Runs `task` repeatedly for at least 300 ms (at least twice) and returns the fastest run.
    // Repeating lets the JVM warm up; the fastest run is the least disturbed by everything else.
    private static double time(Supplier<Object> task) {
        double best = Double.MAX_VALUE;
        long start = System.nanoTime();
        int runs = 0;
        while (runs < 2 || (System.nanoTime() - start) < 300_000_000L) {
            long t0 = System.nanoTime();
            task.get();
            best = Math.min(best, (System.nanoTime() - t0) / 1e6);
            runs++;
            if (best > GIVE_UP_MS) break;
        }
        return best;
    }

}
