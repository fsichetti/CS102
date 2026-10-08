package poly;

import java.util.Iterator;
import java.util.List;

// Only nonzero terms are stored: 3x^4 + 1 is [3x^4, 1x^0].
public final class SparsePolynomial extends Polynomial {

    // Your choice of ArrayList or LinkedList, and of ascending or descending exponent order
    // (terms() must return highest first either way).
    // Invariant: sorted by exponent, each exponent at most once, no zero coefficients.
    // The zero polynomial is the empty list.
    private List<Term> termList;

    // `terms` may come in any order, repeat an exponent (the coefficients add up),
    // and contain zero coefficients.
    public SparsePolynomial(List<Term> terms) {
        //TODO: create the empty list
        for (Term t : terms) addTerm(t.getCoef(), t.getExp());
    }

    @Override
    protected Polynomial newZero() { return new SparsePolynomial(List.of()); }

    @Override
    public Iterator<Term> terms() {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public int degree() {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void addTerm(Rational c, int e) {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void addInPlace(Polynomial other) {
        if (other == this) other = copy();  // given: p.addInPlace(p) reads a copy of p, not the list it is changing
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void multiplyInPlace(Rational c, int e) {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void derivativeInPlace() {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

}
