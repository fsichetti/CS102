package poly;

// Given, complete. The result of a long division: dividend = quotient * divisor + remainder. Immutable.
public final class DivisionResult {

    private final Polynomial quotient;
    private final Polynomial remainder;

    public DivisionResult(Polynomial quotient, Polynomial remainder) {
        this.quotient = quotient;
        this.remainder = remainder;
    }

    public Polynomial getQuotient() { return quotient; }
    public Polynomial getRemainder() { return remainder; }

    @Override
    public String toString() { return "quotient " + quotient + ", remainder " + remainder; }

}
