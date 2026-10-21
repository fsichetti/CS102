package calc;

import deque.MyArrayDeque;
import deque.MyQueue;
import deque.MyStack;

// Infix to postfix: "1 + 2 * 3" becomes "1 2 3 * +".
public final class ShuntingYard {

    private ShuntingYard() {}

    // The tokens of `infix`, in postfix order, in a new queue. `infix` is left unchanged.
    // Throws IllegalArgumentException on unbalanced parentheses, on a comma outside parentheses,
    // and on "=" anywhere.
    public static MyQueue<Token> toPostfix(MyQueue<Token> infix) {
        checkSyntax(infix);
        MyQueue<Token> output = new MyArrayDeque<>();
        MyStack<Token> operators = new MyArrayDeque<>();    // operators, functions and "("
        for (Token t : infix) {
            if (t.isNumber() || t.isVariable()) {
                output.enqueue(t);
            } else if (t.isFunction() || t.isLeftParen()) {
                operators.push(t);
            } else if (t.isOperator()) {
                // A prefix operator has no left operand: nothing on the stack belongs to it.
                if (!t.isPrefix()) {
                    while (!operators.isEmpty() && operators.peek().isOperator()
                            && popsBefore(operators.peek(), t)) {
                        output.enqueue(operators.pop());
                    }
                }
                operators.push(t);
            } else if (t.isComma()) {
                popUntilLeftParen(operators, output, "comma outside parentheses");
            } else if (t.isRightParen()) {
                popUntilLeftParen(operators, output, "unmatched )");
                operators.pop();
                if (!operators.isEmpty() && operators.peek().isFunction()) {
                    output.enqueue(operators.pop());
                }
            } else {
                throw new IllegalArgumentException("unexpected " + t);
            }
        }
        while (!operators.isEmpty()) {
            Token top = operators.pop();
            if (top.isLeftParen()) throw new IllegalArgumentException("unmatched (");
            output.enqueue(top);
        }
        return output;
    }

    // True if `top`, on the stack, must go to the output before the binary operator `incoming` is pushed.
    private static boolean popsBefore(Token top, Token incoming) {
        if (top.precedence() != incoming.precedence()) return top.precedence() > incoming.precedence();
        return !incoming.isRightAssociative();
    }

    // Moves operators from the stack to the output until a "(" is on top, which stays there.
    private static void popUntilLeftParen(MyStack<Token> operators, MyQueue<Token> output, String error) {
        while (!operators.isEmpty() && !operators.peek().isLeftParen()) {
            output.enqueue(operators.pop());
        }
        if (operators.isEmpty()) throw new IllegalArgumentException(error);
    }

    // Optional part: rejects a sequence of tokens that is not valid infix, e.g. "1 2 +" or "(1 +) 2".
    // Operands and binary operators must alternate, starting and ending with an operand.
    private static void checkSyntax(MyQueue<Token> infix) {
        boolean operandExpected = true;
        for (Token t : infix) {
            boolean startsOperand = t.isNumber() || t.isVariable() || t.isFunction() || t.isLeftParen()
                    || (t.isOperator() && t.isPrefix());
            if (startsOperand != operandExpected) throw new IllegalArgumentException("unexpected " + t);
            // after a number, a variable or ")", an operator is expected; after anything else, an operand
            operandExpected = !(t.isNumber() || t.isVariable() || t.isRightParen());
        }
        if (operandExpected) throw new IllegalArgumentException("incomplete expression");
    }

}
