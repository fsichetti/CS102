package search;

// Given, complete. One token of a query: a word, the operators AND/OR/NOT, "(" or ")".
// Exactly one of is(Type.WORD), is(Type.AND), ... is true for every token.
public final class Token {

    public enum Type { WORD, AND, OR, NOT, LPAREN, RPAREN }

    public final Type type;
    public final String word;   // only for WORD tokens, already lowercased; null otherwise

    private Token(Type type, String word) {
        this.type = type;
        this.word = word;
    }

    public static Token word(String w) { return new Token(Type.WORD, w.toLowerCase()); }
    public static Token of(Type type) { return new Token(type, null); }

    public boolean is(Type t) { return type == t; }

    @Override
    public String toString() { return type == Type.WORD ? word : type.toString(); }
}
