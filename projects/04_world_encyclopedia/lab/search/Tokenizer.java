package search;

import java.util.ArrayDeque;
import java.util.Queue;

// Given, complete. Splits a query into tokens: words, the keywords AND, OR, NOT (case-insensitive),
// "(", ")". A word is a maximal run of letters and digits.
public final class Tokenizer {

    public static Queue<Token> tokenize(String query) {
        Queue<Token> tokens = new ArrayDeque<>();
        int i = 0;
        while (i < query.length()) {
            char c = query.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
            } else if (c == '(') {
                tokens.add(Token.of(Token.Type.LPAREN));
                i++;
            } else if (c == ')') {
                tokens.add(Token.of(Token.Type.RPAREN));
                i++;
            } else if (Character.isLetterOrDigit(c)) {
                int start = i;
                while (i < query.length() && Character.isLetterOrDigit(query.charAt(i))) i++;
                String w = query.substring(start, i);
                switch (w.toUpperCase()) {
                    case "AND": tokens.add(Token.of(Token.Type.AND)); break;
                    case "OR": tokens.add(Token.of(Token.Type.OR)); break;
                    case "NOT": tokens.add(Token.of(Token.Type.NOT)); break;
                    default: tokens.add(Token.word(w));
                }
            } else {
                throw new IllegalArgumentException("unexpected character '" + c + "' in query");
            }
        }
        return tokens;
    }
}
