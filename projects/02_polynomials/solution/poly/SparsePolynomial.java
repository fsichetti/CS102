package poly;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

// Only nonzero terms are stored: 3x^4 + 1 is [3x^4, 1x^0].
public final class SparsePolynomial extends Polynomial {

    // LinkedList, descending exponents: addInPlace inserts in the middle through a ListIterator.
    // Invariant: sorted by exponent, each exponent at most once, no zero coefficients.
    private List<Term> termList;

    public SparsePolynomial(List<Term> terms) {
        termList = new LinkedList<>();
        for (Term t : terms) addTerm(t.getCoef(), t.getExp());
    }

    @Override
    protected Polynomial newZero() { return new SparsePolynomial(List.of()); }

    @Override
    public Iterator<Term> terms() { return Collections.unmodifiableList(termList).iterator(); }

    @Override
    public int degree() { return termList.isEmpty() ? -1 : termList.get(0).getExp(); }

    @Override
    public void addTerm(Rational c, int e) { merge(termList.listIterator(), c, e); }

    // Adds c x^e at or after the position of `it`, which must be before the place of exponent e.
    // Leaves `it` right after that place, so that a smaller exponent can continue from there.
    private static void merge(ListIterator<Term> it, Rational c, int e) {
        if (c.isZero()) return;
        while (it.hasNext()) {
            Term s = it.next();
            if (s.getExp() > e) continue;
            if (s.getExp() == e) {
                Rational sum = s.getCoef().add(c);
                if (sum.isZero()) it.remove();
                else it.set(new Term(sum, e));
                return;
            }
            it.previous();  // s goes after the new term
            break;
        }
        it.add(new Term(c, e));
    }

    // The merge step of merge sort, into termList itself: one pass over both lists, which stops
    // as soon as the terms of other run out.
    @Override
    public void addInPlace(Polynomial other) {
        if (other == this) other = copy();  // given: p.addInPlace(p) reads a copy of p, not the list it is changing
        ListIterator<Term> it = termList.listIterator();
        for (Iterator<Term> b = other.terms(); b.hasNext(); ) {
            Term t = b.next();
            merge(it, t.getCoef(), t.getExp());
        }
    }

    @Override
    public void multiplyInPlace(Rational c, int e) {
        if (c.isZero()) {
            termList.clear();
            return;
        }
        for (ListIterator<Term> it = termList.listIterator(); it.hasNext(); ) {
            Term t = it.next();
            it.set(new Term(t.getCoef().multiply(c), t.getExp() + e));
        }
    }

    @Override
    public void derivativeInPlace() {
        for (ListIterator<Term> it = termList.listIterator(); it.hasNext(); ) {
            Term t = it.next();
            if (t.getExp() == 0) it.remove();
            else it.set(new Term(t.getCoef().multiply(new Rational(t.getExp())), t.getExp() - 1));
        }
    }

}
