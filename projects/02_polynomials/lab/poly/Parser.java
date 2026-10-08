package poly;

import java.util.ArrayList;
import java.util.List;

// Given, complete. Turns text such as "3x^2 - x + 5" or "(3/4)x^10 + 1/2" into a list of terms,
// in the order they appear. The list is NOT normalized: "x + x + 0" gives three terms.
//
// Accepted: spaces anywhere; "+"/"-" between terms and before the first; a coefficient
// ("3", "3/4" or "(3/4)") and/or x with an optional "^n".
public final class Parser {

    private Parser() {}

    public static List<Term> parse(String text) {
        String s = text.replaceAll("\\s+", "");
        if (s.isEmpty()) throw new IllegalArgumentException("empty polynomial");
        List<Term> terms = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            boolean negative = false;
            char c = s.charAt(i);
            if (c == '+' || c == '-') {
                negative = (c == '-');
                i++;
            } else if (!terms.isEmpty()) {
                throw error(s, i, "expected + or -");
            }

            // Coefficient, if any.
            Rational coef = null;
            if (i < s.length() && s.charAt(i) == '(') {
                int close = s.indexOf(')', i);
                if (close == -1) throw error(s, i, "unmatched (");
                coef = Rational.parse(s.substring(i, close + 1));
                i = close + 1;
            } else {
                int j = i;
                while (j < s.length() && (Character.isDigit(s.charAt(j)) || s.charAt(j) == '/')) j++;
                if (j > i) coef = Rational.parse(s.substring(i, j));
                i = j;
            }
            if (i < s.length() && s.charAt(i) == '*') i++;

            // x and exponent, if any.
            int exp = 0;
            if (i < s.length() && s.charAt(i) == 'x') {
                i++;
                exp = 1;
                if (i < s.length() && s.charAt(i) == '^') {
                    int j = ++i;
                    while (j < s.length() && Character.isDigit(s.charAt(j))) j++;
                    if (j == i) throw error(s, i, "expected exponent");
                    exp = Integer.parseInt(s.substring(i, j));
                    i = j;
                }
            }

            if (coef == null && exp == 0) throw error(s, i, "expected a term");
            if (coef == null) coef = Rational.ONE;
            if (negative) coef = coef.negate();
            terms.add(new Term(coef, exp));
        }
        return terms;
    }

    private static IllegalArgumentException error(String s, int i, String message) {
        return new IllegalArgumentException(message + " at position " + i + " in \"" + s + "\"");
    }

}
