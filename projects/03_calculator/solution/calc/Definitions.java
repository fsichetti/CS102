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
        stack.push(new Definition(name, body));
    }

    // A new queue: `postfix` with every variable replaced by the body of its definition, repeatedly, until no
    // variable is left. `postfix` and the bodies of the definitions are left unchanged.
    // Throws IllegalArgumentException if a variable has no definition.
    public MyQueue<Token> substitute(MyQueue<Token> postfix) {
        MyQueue<Token> current = copy(postfix);
        // Newest first: the body of a definition only uses older variables, which are replaced later.
        for (Definition d : stack) {
            MyQueue<Token> next = new MyArrayDeque<>();
            for (Token t : current) {
                if (t.isVariable() && t.name().equals(d.name())) {
                    for (Token b : d.body()) next.enqueue(b);
                } else {
                    next.enqueue(t);
                }
            }
            current = next;
        }
        for (Token t : current) {
            if (t.isVariable()) throw new IllegalArgumentException("undefined variable: " + t);
        }
        return current;
    }

    private static MyQueue<Token> copy(MyQueue<Token> tokens) {
        MyQueue<Token> result = new MyArrayDeque<>();
        for (Token t : tokens) result.enqueue(t);
        return result;
    }

}
