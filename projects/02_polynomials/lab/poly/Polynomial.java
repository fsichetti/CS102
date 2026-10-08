package poly;

import java.util.Iterator;

// A polynomial in x with rational coefficients. Two subclasses store it in different ways:
// DensePolynomial and SparsePolynomial.
//
// The methods whose name ends in InPlace, and addTerm, change `this`. All other operations return
// a new polynomial of the same class as `this`, and leave their operands unchanged.
public abstract class Polynomial {

    // ---------------------------------------------------------------------------------------------
    // Representation-specific: each subclass implements these.

    // The nonzero terms, highest exponent first, each exponent at most once.
    // The zero polynomial has no terms.
    public abstract Iterator<Term> terms();

    // The highest exponent with a nonzero coefficient; -1 for the zero polynomial.
    public abstract int degree();

    // this += c x^e
    public abstract void addTerm(Rational c, int e);

    // this += other. `other` may be of either subclass, and may be `this` itself (handled in the scaffold).
    // The general route reads it only through other.terms() and other.degree(); when `other` is of
    // the same class as `this`, a faster route is allowed. `other` is never changed (unless it is `this`).
    public abstract void addInPlace(Polynomial other);

    // this *= c x^e
    public abstract void multiplyInPlace(Rational c, int e);

    // this = the derivative of this
    public abstract void derivativeInPlace();

    // A new zero polynomial of the same class as `this`. Given in both subclasses.
    protected abstract Polynomial newZero();

    // ---------------------------------------------------------------------------------------------
    // Given, complete: every other operation, built on the ones above.

    // A copy of `this`, of the same class.
    public final Polynomial copy() {
        Polynomial result = newZero();
        result.addInPlace(this);
        return result;
    }

    public final Polynomial add(Polynomial other) {
        Polynomial result = copy();
        result.addInPlace(other);
        return result;
    }

    public final Polynomial subtract(Polynomial other) {
        Polynomial result = copy();
        result.subtractInPlace(other);
        return result;
    }

    // this -= other. Correct for every subclass. Optional: override it in a subclass with a faster version.
    public void subtractInPlace(Polynomial other) { subtractByNegationInPlace(other); }

    // The inherited subtractInPlace under its own name, so that Benchmark can time it against an override.
    public final void subtractByNegationInPlace(Polynomial other) {
        Polynomial negated = other.copy();
        negated.multiplyInPlace(new Rational(-1), 0);
        addInPlace(negated);
    }

    public final Polynomial negate() {
        Polynomial result = copy();
        result.multiplyInPlace(new Rational(-1), 0);
        return result;
    }

    // The sum of `this` times each term of `other`.
    public final Polynomial multiply(Polynomial other) {
        Polynomial result = newZero();
        for (Iterator<Term> it = other.terms(); it.hasNext(); ) {
            Term t = it.next();
            Polynomial partial = copy();
            partial.multiplyInPlace(t.getCoef(), t.getExp());
            result.addInPlace(partial);
        }
        return result;
    }

    public final Polynomial derivative() {
        Polynomial result = copy();
        result.derivativeInPlace();
        return result;
    }

    public boolean isZero() { return degree() == -1; }

    // The term with the highest exponent. Throws NoSuchElementException for the zero polynomial.
    public final Term leadingTerm() { return terms().next(); }

    // The number of nonzero terms.
    public int termCount() {
        int n = 0;
        for (Iterator<Term> it = terms(); it.hasNext(); it.next()) n++;
        return n;
    }

    // The fraction of the coefficients, from x^0 to x^degree, that are nonzero; 0 for the zero polynomial.
    public double density() { return isZero() ? 0 : (double) termCount() / (degree() + 1); }

    // ---------------------------------------------------------------------------------------------
    // Yours: written once, here, using terms() and the operations above. They work for both subclasses.

    // The value of the polynomial at x. One pass over terms().
    public Rational evaluate(Rational x) {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    // True if o is a Polynomial (of either subclass) with exactly the same terms.
    @Override
    public boolean equals(Object o) {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    // Long division of this by divisor: the quotient and the remainder, both of the same class as `this`.
    // Throws ArithmeticException when dividing by the zero polynomial.
    public DivisionResult divide(Polynomial divisor) {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    // ---------------------------------------------------------------------------------------------
    // Given, complete.

    // Highest exponent first, e.g. "3x^2 - x + 5", "(3/4)x - (1/2)", "0".
    @Override
    public String toString() {
        if (isZero()) return "0";
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Iterator<Term> it = terms(); it.hasNext(); ) {
            Term t = it.next();
            Rational c = t.getCoef();
            if (first) sb.append(c.isNegative() ? "-" : "");
            else sb.append(c.isNegative() ? " - " : " + ");
            first = false;
            if (c.isNegative()) c = c.negate();
            int e = t.getExp();
            // The coefficient 1 is left out, except in the constant term; fractions go in parentheses.
            if (e == 0 || !c.equals(Rational.ONE)) sb.append(c.isInteger() ? c.toString() : "(" + c + ")");
            if (e >= 1) sb.append("x");
            if (e >= 2) sb.append("^").append(e);
        }
        return sb.toString();
    }

    // Must be overridden together with equals: equal objects must have equal hash codes (you will
    // see why with hash tables). Equal polynomials print the same, so this holds.
    @Override
    public int hashCode() { return toString().hashCode(); }

}
