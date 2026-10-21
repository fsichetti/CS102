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
        //Optional: first check that infix is valid (see "Checking the syntax")
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

}
