package poly;

import java.math.BigInteger;

// Given, complete. An immutable fraction of arbitrary size, always kept in lowest terms with a
// positive denominator, so that equal values have equal fields and equals can compare them directly.
public final class Rational {

    public static final Rational ZERO = new Rational(0);
    public static final Rational ONE = new Rational(1);

    private final BigInteger num;
    private final BigInteger den;

    private Rational(BigInteger num, BigInteger den) {
        if (den.signum() == 0) throw new ArithmeticException("zero denominator");
        if (den.signum() < 0) {
            num = num.negate();
            den = den.negate();
        }
        BigInteger g = num.gcd(den);
        this.num = num.divide(g);
        this.den = den.divide(g);
    }

    // Already in lowest terms: no gcd needed.
    private Rational(BigInteger num) {
        this.num = num;
        this.den = BigInteger.ONE;
    }

    public Rational(long num, long den) { this(BigInteger.valueOf(num), BigInteger.valueOf(den)); }

    public Rational(long n) { this(n, 1); }

    // The shortcuts for zeros and whole numbers skip the gcd, by far the most expensive step.
    public Rational add(Rational o) {
        if (isZero()) return o;
        if (o.isZero()) return this;
        if (isInteger() && o.isInteger()) return new Rational(num.add(o.num));
        return new Rational(num.multiply(o.den).add(o.num.multiply(den)), den.multiply(o.den));
    }

    public Rational negate() { return isInteger() ? new Rational(num.negate()) : new Rational(num.negate(), den); }

    public Rational multiply(Rational o) {
        if (isZero() || o.isZero()) return ZERO;
        if (isInteger() && o.isInteger()) return new Rational(num.multiply(o.num));
        return new Rational(num.multiply(o.num), den.multiply(o.den));
    }

    public Rational divide(Rational o) { return new Rational(num.multiply(o.den), den.multiply(o.num)); }

    // this^k, for k >= 0. Takes about log k multiplications (BigInteger.pow squares repeatedly).
    public Rational pow(int k) {
        if (k < 0) throw new IllegalArgumentException("negative exponent: " + k);
        return isInteger() ? new Rational(num.pow(k)) : new Rational(num.pow(k), den.pow(k));
    }

    public boolean isZero() { return num.signum() == 0; }
    public boolean isNegative() { return num.signum() < 0; }
    public boolean isInteger() { return den.equals(BigInteger.ONE); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rational)) return false;
        Rational r = (Rational) o;
        return num.equals(r.num) && den.equals(r.den);
    }

    @Override
    public int hashCode() { return num.hashCode() * 31 + den.hashCode(); }

    // "3", "-3", "3/4".
    @Override
    public String toString() { return isInteger() ? num.toString() : num + "/" + den; }

    // Accepts "3", "-3", "3/4", "(3/4)", "( -3 / 4 )".
    public static Rational parse(String s) {
        s = s.trim();
        if (s.startsWith("(") && s.endsWith(")")) s = s.substring(1, s.length() - 1);
        String[] parts = s.split("/");
        if (parts.length == 1) return new Rational(new BigInteger(parts[0].trim()), BigInteger.ONE);
        if (parts.length == 2) return new Rational(new BigInteger(parts[0].trim()), new BigInteger(parts[1].trim()));
        throw new NumberFormatException("not a fraction: " + s);
    }

}
