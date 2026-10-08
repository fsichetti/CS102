package poly;

// Given, complete. One term coef * x^exp. Immutable.
public final class Term {

    private final Rational coef;
    private final int exp;

    public Term(Rational coef, int exp) {
        if (exp < 0) throw new IllegalArgumentException("negative exponent: " + exp);
        this.coef = coef;
        this.exp = exp;
    }

    public Rational getCoef() { return coef; }
    public int getExp() { return exp; }

    // Raw form for debugging, e.g. "-3x^2". Not the polynomial format: that is Polynomial.toString.
    @Override
    public String toString() { return coef + "x^" + exp; }

}
