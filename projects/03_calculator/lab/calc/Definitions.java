package calc;

import deque.MyArrayDeque;
import deque.MyQueue;
import deque.MyStack;

// The variables defined so far, each with its expression in postfix: after "x = 2 * y", x is "2 y *".
// The definitions are kept on a stack, the newest on top. A definition may use only the variables
// defined before it, so "x = x + 1" means: the previous x, plus 1.
public class Definitions {

    // Given, complete: one definition, name = body.
    public static class Definition {
        private final String name;
        private final MyQueue<Token> body;     // postfix, may contain variables

        public Definition(String name, MyQueue<Token> body) {
            this.name = name;
            this.body = body;
        }

        public String name() { return name; }
        public MyQueue<Token> body() { return body; }

        @Override
        public String toString() { return name + " = " + Token.join(body); }
    }

    private MyStack<Definition> stack = new MyArrayDeque<>();

    // Given: the definitions, newest first.
    public Iterable<Definition> all() { return stack; }

    // Given: forgets every definition.
    public void clear() { stack = new MyArrayDeque<>(); }

    // Adds the definition name = body. `body` is stored as it is, not copied: the caller must not change it later.
    public void define(String name, MyQueue<Token> body) {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

    // A new queue: `postfix` with every variable replaced by the body of its definition, repeatedly, until no
    // variable is left. `postfix` and the bodies of the definitions are left unchanged.
    // Throws IllegalArgumentException if a variable has no definition.
    public MyQueue<Token> substitute(MyQueue<Token> postfix) {
        //TODO
        throw new UnsupportedOperationException("TODO");
    }

}
