package poly;

import java.util.Iterator;
import java.util.List;

// Every coefficient is stored, zeros included: 3x^4 + 1 is [1, 0, 0, 0, 3].
public final class DensePolynomial extends Polynomial {

    // coefficients.get(i) is the coefficient of x^i. Your choice of ArrayList or LinkedList.
    // Invariant: the last element, if any, is nonzero. The zero polynomial is the empty list.
    private List<Rational> coefficients;

    // `terms` may come in any order, repeat an exponent (the coefficients add up),
    // and contain zero coefficients.
    public DensePolynomial(List<Term> terms) {
        //TODO: create the empty list
        for (Term t : terms) addTerm(t.getCoef(), t.getExp());
    }

    @Override
    protected Polynomial newZero() { return new DensePolynomial(List.of()); }

    // Walks the list from the highest index down, skipping zero coefficients.
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
        if (other instanceof DensePolynomial d) {
            //Optional: fast route, reading d.coefficients directly, then return
        }
        //TODO: general route, reading other only through other.terms() and other.degree()
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
