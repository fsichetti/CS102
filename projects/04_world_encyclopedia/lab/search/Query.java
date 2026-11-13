package search;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

// Given, complete. Evaluates an infix boolean query, e.g. "fox AND (quick OR lazy)", against an
// index. The parser only ever asks index for documentsWith, and, or and not: it never touches a
// Set itself, so and/or/not (Part 2) decide everything about how the sets are combined.
//
// Precedence, highest first: NOT, AND, OR, as in "fox OR NOT quick AND lazy", which is
// "fox OR ((NOT quick) AND lazy)". Parentheses override precedence, as usual.
public final class Query {

    public static Set<Integer> search(Index index, String query) {
        Queue<Token> tokens = Tokenizer.tokenize(query);
        Set<Integer> result = or(index, tokens);
        if (!tokens.isEmpty()) throw new IllegalArgumentException("unexpected " + tokens.peek());
        return result;
    }

    // An OR of one or more and-expressions: a, a OR b, a OR b OR c, ...
    private static Set<Integer> or(Index index, Queue<Token> tokens) {
        List<Set<Integer>> operands = new ArrayList<>();
        operands.add(and(index, tokens));
        while (peekIs(tokens, Token.Type.OR)) {
            tokens.poll();
            operands.add(and(index, tokens));
        }
        return operands.size() == 1 ? operands.get(0) : index.or(operands);
    }

    // An AND of one or more not-expressions: a, a AND b, a AND b AND c, ...
    private static Set<Integer> and(Index index, Queue<Token> tokens) {
        List<Set<Integer>> operands = new ArrayList<>();
        operands.add(not(index, tokens));
        while (peekIs(tokens, Token.Type.AND)) {
            tokens.poll();
            operands.add(not(index, tokens));
        }
        return operands.size() == 1 ? operands.get(0) : index.and(operands);
    }

    // NOT, applied to another not-expression (so NOT NOT a is legal), or just an atom.
    private static Set<Integer> not(Index index, Queue<Token> tokens) {
        if (peekIs(tokens, Token.Type.NOT)) {
            tokens.poll();
            return index.not(not(index, tokens));
        }
        return atom(index, tokens);
    }

    // A single word, or a parenthesized expression: '(' is the only place a whole expression can
    // be nested inside another, so this is the only place that calls back into or().
    private static Set<Integer> atom(Index index, Queue<Token> tokens) {
        Token t = tokens.poll();
        if (t == null) throw new IllegalArgumentException("expected a word, 'NOT' or '('");
        if (t.is(Token.Type.WORD)) return index.documentsWith(t.word);
        if (t.is(Token.Type.LPAREN)) {
            Set<Integer> result = or(index, tokens);
            Token close = tokens.poll();
            if (close == null || !close.is(Token.Type.RPAREN)) throw new IllegalArgumentException("missing ')'");
            return result;
        }
        throw new IllegalArgumentException("unexpected " + t);
    }

    private static boolean peekIs(Queue<Token> tokens, Token.Type type) {
        Token t = tokens.peek();
        return t != null && t.is(type);
    }
}
