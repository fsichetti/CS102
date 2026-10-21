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
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

}
