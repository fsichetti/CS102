package calc;

import deque.MyArrayDeque;
import deque.MyQueue;
import deque.MyStack;

// Evaluation of a postfix expression: "1 2 3 * +" is 7.
public final class Evaluator {

    private Evaluator() {}

    // The value of `postfix`, which is left unchanged.
    // Throws IllegalArgumentException if it contains a variable, if an operator or function is missing
    // an operand, or if more than one value is left at the end (also when there are none: the empty expression).
    public static double evaluate(MyQueue<Token> postfix) {
        MyStack<Double> values = new MyArrayDeque<>();
        for (Token t : postfix) {
            if (t.isNumber()) {
                values.push(t.value());
            } else if (t.isVariable()) {
                throw new IllegalArgumentException("undefined variable: " + t);
            } else if (t.isOperator() || t.isFunction()) {
                if (values.size() < t.arity()) throw new IllegalArgumentException("missing operand for " + t);
                if (t.arity() == 1) {
                    values.push(t.apply(values.pop()));
                } else {
                    double b = values.pop();    // the right operand is on top
                    double a = values.pop();
                    values.push(t.apply(a, b));
                }
            } else {
                throw new IllegalArgumentException("unexpected " + t);
            }
        }
        if (values.size() != 1) throw new IllegalArgumentException("malformed expression");
        return values.pop();
    }

}
