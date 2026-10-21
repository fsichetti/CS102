package calc;

import java.math.BigDecimal;
import java.util.List;

// Given, complete. One token of an expression: a number, a variable, an operator, a function name,
// a parenthesis, a comma or the assignment sign "=". Immutable.
//
// Exactly one of the is...() methods is true for every token. Methods that only make sense for
// some kinds (value(), precedence(), apply(...), ...) throw IllegalStateException on the others.
public final class Token {

    private static final int KIND_NUMBER = 0, KIND_VARIABLE = 1, KIND_OPERATOR = 2, KIND_FUNCTION = 3,
                             KIND_LEFT_PAREN = 4, KIND_RIGHT_PAREN = 5, KIND_COMMA = 6, KIND_ASSIGN = 7;

    // Binary operators, then the prefix operator "neg" (unary minus). Same positions in all four lists.
    private static final List<String> OPERATORS = List.of("+", "-", "*", "/", "^", "neg");
    private static final List<Integer> PRECEDENCE = List.of(1, 1, 2, 2, 4, 3);
    // Functions: fixed number of arguments.
    private static final List<String> FUNCTIONS = List.of("sqrt", "abs", "sin", "cos", "tan", "exp", "ln", "min", "max");
    private static final List<Integer> FUNCTION_ARITY = List.of(1, 1, 1, 1, 1, 1, 1, 2, 2);

    public static final Token LEFT_PAREN = new Token(KIND_LEFT_PAREN, "(", 0);
    public static final Token RIGHT_PAREN = new Token(KIND_RIGHT_PAREN, ")", 0);
    public static final Token COMMA = new Token(KIND_COMMA, ",", 0);
    public static final Token ASSIGN = new Token(KIND_ASSIGN, "=", 0);

    private final int kind;
    private final String text;      // as printed: "3.5", "x", "+", "neg", "max", "(", ...
    private final double value;     // numbers only

    private Token(int kind, String text, double value) {
        this.kind = kind;
        this.text = text;
        this.value = value;
    }

    public static Token number(double value) { return new Token(KIND_NUMBER, format(value), value); }

    public static Token variable(String name) { return new Token(KIND_VARIABLE, name, 0); }

    // symbol: one of "+", "-", "*", "/", "^", "neg"
    public static Token operator(String symbol) {
        if (!isOperatorSymbol(symbol)) throw new IllegalArgumentException("unknown operator: " + symbol);
        return new Token(KIND_OPERATOR, symbol, 0);
    }

    // name: one of sqrt, abs, sin, cos, tan, exp, ln, min, max
    public static Token function(String name) {
        if (!isFunctionName(name)) throw new IllegalArgumentException("unknown function: " + name);
        return new Token(KIND_FUNCTION, name, 0);
    }

    public static boolean isOperatorSymbol(String s) { return OPERATORS.contains(s); }

    public static boolean isFunctionName(String s) { return FUNCTIONS.contains(s); }

    // ---------------------------------------------------------------------------------------------
    // Kind

    public boolean isNumber() { return kind == KIND_NUMBER; }
    public boolean isVariable() { return kind == KIND_VARIABLE; }
    public boolean isOperator() { return kind == KIND_OPERATOR; }
    public boolean isFunction() { return kind == KIND_FUNCTION; }
    public boolean isLeftParen() { return kind == KIND_LEFT_PAREN; }
    public boolean isRightParen() { return kind == KIND_RIGHT_PAREN; }
    public boolean isComma() { return kind == KIND_COMMA; }
    public boolean isAssign() { return kind == KIND_ASSIGN; }

    // ---------------------------------------------------------------------------------------------
    // Properties

    // Numbers only.
    public double value() {
        require(isNumber(), "value");
        return value;
    }

    // The text of the token: the name of a variable or function, the symbol of an operator, ...
    public String name() { return text; }

    // Operators only. Higher binds tighter: + - 1, * / 2, neg 3, ^ 4.
    public int precedence() {
        require(isOperator(), "precedence");
        return PRECEDENCE.get(OPERATORS.indexOf(text));
    }

    // Operators only. Only ^ is right-associative: 2 ^ 3 ^ 2 is 2 ^ (3 ^ 2).
    public boolean isRightAssociative() {
        require(isOperator(), "isRightAssociative");
        return text.equals("^");
    }

    // Operators only. Only neg is prefix: it comes before its operand, and it has no left operand.
    public boolean isPrefix() {
        require(isOperator(), "isPrefix");
        return text.equals("neg");
    }

    // Operators and functions: the number of operands, 1 or 2.
    public int arity() {
        require(isOperator() || isFunction(), "arity");
        if (isOperator()) return isPrefix() ? 1 : 2;
        return FUNCTION_ARITY.get(FUNCTIONS.indexOf(text));
    }

    // ---------------------------------------------------------------------------------------------
    // Applying operators and functions

    // Operators and functions with arity 1.
    public double apply(double x) {
        require(arity() == 1, "apply(x)");
        switch (text) {
            case "neg": return -x;
            case "sqrt": return Math.sqrt(x);
            case "abs": return Math.abs(x);
            case "sin": return Math.sin(x);
            case "cos": return Math.cos(x);
            case "tan": return Math.tan(x);
            case "exp": return Math.exp(x);
            case "ln": return Math.log(x);
            default: throw new IllegalStateException(text);
        }
    }

    // Operators and functions with arity 2: a is the left operand (or the first argument).
    // Throws ArithmeticException on division by zero.
    public double apply(double a, double b) {
        require(arity() == 2, "apply(a, b)");
        switch (text) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) throw new ArithmeticException("division by zero");
                return a / b;
            case "^": return Math.pow(a, b);
            case "min": return Math.min(a, b);
            case "max": return Math.max(a, b);
            default: throw new IllegalStateException(text);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String toString() { return text; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Token t && t.kind == kind && t.text.equals(text);
    }

    @Override
    public int hashCode() { return 31 * kind + text.hashCode(); }

    // The tokens separated by spaces: "1 2 3 * +".
    public static String join(Iterable<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        for (Token t : tokens) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(t);
        }
        return sb.toString();
    }

    // 3.0 as "3", 0.5 as "0.5", without exponent notation.
    public static String format(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return Double.toString(x);
        if (x == 0) return "0";   // also -0.0
        return BigDecimal.valueOf(x).stripTrailingZeros().toPlainString();
    }

    private void require(boolean condition, String method) {
        if (!condition) throw new IllegalStateException(method + "() called on token " + text);
    }

}
