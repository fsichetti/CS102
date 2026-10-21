package calc;

import deque.MyArrayDeque;
import deque.MyQueue;

// Given, complete. Turns a line of text such as "x = -2 * (3 + max(y, 1))" into a queue of tokens,
// in the order they appear. It checks single tokens only: "1 + * 2" or "(1" are tokenized
// without complaint.
//
// Accepted:
// - spaces anywhere
// - numbers: 3, 3.5, .5 (no exponent notation)
// - names: a letter, then letters, digits or _. A function name (sqrt, max, ...) must be followed by "(";
//   any other name is a variable.
// - + - * / ^ ( ) , =
// A "-" is unary minus, the operator "neg", where an operand is expected: at the start, after an
// operator, after "(", after "," and after "=". A "+" in the same position is skipped.
public final class Tokenizer {

    private Tokenizer() {}

    public static MyQueue<Token> tokenize(String text) {
        MyQueue<Token> tokens = new MyArrayDeque<>();
        Token previous = null;      // the last token added, to tell unary from binary minus
        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);
            Token t = null;         // the token starting at i, if any
            if (Character.isWhitespace(c)) {
                i++;
            } else if (Character.isDigit(c) || c == '.') {
                int j = i;
                while (j < text.length() && Character.isDigit(text.charAt(j))) j++;
                if (j < text.length() && text.charAt(j) == '.') {
                    j++;
                    while (j < text.length() && Character.isDigit(text.charAt(j))) j++;
                }
                String number = text.substring(i, j);
                if (number.equals(".")) throw error(text, i, "expected a digit");
                t = Token.number(Double.parseDouble(number));
                i = j;
            } else if (Character.isLetter(c)) {
                int j = i;
                while (j < text.length() && (Character.isLetterOrDigit(text.charAt(j)) || text.charAt(j) == '_')) j++;
                String name = text.substring(i, j);
                if (Token.isFunctionName(name)) {
                    if (!nextNonSpaceIs(text, j, '(')) throw error(text, j, "expected ( after " + name);
                    t = Token.function(name);
                } else {
                    t = Token.variable(name);
                }
                i = j;
            } else {
                boolean operandExpected = previous == null || startsOperand(previous);
                switch (c) {
                    case '(': t = Token.LEFT_PAREN; break;
                    case ')': t = Token.RIGHT_PAREN; break;
                    case ',': t = Token.COMMA; break;
                    case '=': t = Token.ASSIGN; break;
                    case '-': t = Token.operator(operandExpected ? "neg" : "-"); break;
                    case '+': if (!operandExpected) t = Token.operator("+"); break;     // unary + is skipped
                    case '*': case '/': case '^': t = Token.operator(String.valueOf(c)); break;
                    default: throw error(text, i, "unexpected character '" + c + "'");
                }
                i++;
            }
            if (t != null) {
                tokens.enqueue(t);
                previous = t;
            }
        }
        return tokens;
    }

    // True if what follows `previous` must be an operand, so that a "-" there is unary.
    private static boolean startsOperand(Token previous) {
        return previous.isOperator() || previous.isLeftParen() || previous.isComma() || previous.isAssign();
    }

    private static boolean nextNonSpaceIs(String text, int i, char c) {
        while (i < text.length() && Character.isWhitespace(text.charAt(i))) i++;
        return i < text.length() && text.charAt(i) == c;
    }

    private static IllegalArgumentException error(String text, int i, String message) {
        return new IllegalArgumentException(message + " at position " + i + " in \"" + text + "\"");
    }

}
