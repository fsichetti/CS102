package poly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

// Every coefficient is stored, zeros included: 3x^4 + 1 is [1, 0, 0, 0, 3].
public final class DensePolynomial extends Polynomial {

    // ArrayList: growing and trimming happen at the end, and terms() needs random access.
    // Invariant: the last element, if any, is nonzero.
    private List<Rational> coefficients;

    public DensePolynomial(List<Term> terms) {
        coefficients = new ArrayList<>();
        for (Term t : terms) addTerm(t.getCoef(), t.getExp());
    }

    @Override
    protected Polynomial newZero() { return new DensePolynomial(List.of()); }

    private class TermIterator implements Iterator<Term> {
        private int next = coefficients.size() - 1;  // index of the next nonzero coefficient, or -1

        @Override
        public boolean hasNext() { return next >= 0; }

        @Override
        public Term next() {
            if (next < 0) throw new NoSuchElementException();
            Term t = new Term(coefficients.get(next), next);
            next--;
            while (next >= 0 && coefficients.get(next).isZero()) next--;
            return t;
        }
    }

    @Override
    public Iterator<Term> terms() { return new TermIterator(); }

    @Override
    public int degree() { return coefficients.size() - 1; }

    // Restores the invariant: no zeros at the end.
    private void trim() {
        while (!coefficients.isEmpty() && coefficients.get(coefficients.size() - 1).isZero()) {
            coefficients.remove(coefficients.size() - 1);
        }
    }

    @Override
    public void addTerm(Rational c, int e) {
        if (c.isZero()) return;
        while (coefficients.size() <= e) coefficients.add(Rational.ZERO);
        coefficients.set(e, coefficients.get(e).add(c));
        trim();
    }

    @Override
    public void addInPlace(Polynomial other) {
        if (other == this) other = copy();  // given: p.addInPlace(p) reads a copy of p, not the list it is changing
        if (other instanceof DensePolynomial d) {
            while (coefficients.size() < d.coefficients.size()) coefficients.add(Rational.ZERO);
            ListIterator<Rational> it = coefficients.listIterator();
            for (Rational b : d.coefficients) {
                Rational a = it.next();
                if (!b.isZero()) it.set(a.add(b));
            }
            trim();
        } else {
            for (Iterator<Term> it = other.terms(); it.hasNext(); ) {
                Term t = it.next();
                addTerm(t.getCoef(), t.getExp());
            }
        }
    }

    @Override
    public void multiplyInPlace(Rational c, int e) {
        if (c.isZero()) {
            coefficients.clear();
            return;
        }
        for (ListIterator<Rational> it = coefficients.listIterator(); it.hasNext(); ) it.set(it.next().multiply(c));
        if (!coefficients.isEmpty()) coefficients.addAll(0, Collections.nCopies(e, Rational.ZERO));
    }

    @Override
    public void derivativeInPlace() {
        if (coefficients.isEmpty()) return;
        coefficients.remove(0);
        for (ListIterator<Rational> it = coefficients.listIterator(); it.hasNext(); ) {
            int exp = it.nextIndex() + 1;  // its exponent before differentiating
            it.set(it.next().multiply(new Rational(exp)));
        }
    }

}
