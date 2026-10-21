import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

import calc.*;
import deque.MyQueue;

// Given, complete: the calculator's REPL. Type "help" for the commands.
public class Calculator {

    private static final String HELP = String.join("\n",
            "  <expression>           its value, e.g. 1 + 2 * 3",
            "  <name> = <expression>  defines a variable, e.g. x = 2 ^ 10",
            "  tokens <expression>    the tokens, as a queue",
            "  postfix <expression>   the expression in postfix",
            "  expand <expression>    in postfix, with every variable replaced",
            "  vars                   the definitions, newest first",
            "  clear                  forgets every definition",
            "  load <file>            runs each line of a file as if typed",
            "  help, quit");

    // Longer expansions are summarized by `expand`.
    private static final int PRINT_LIMIT = 60;

    private static final Definitions definitions = new Definitions();

    public static void main(String[] args) {
        System.out.println("CS102 calculator. Type help for the commands.");
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            if (!in.hasNextLine()) break;
            if (!run(in.nextLine())) break;
        }
        in.close();
    }

    // Runs one line. False on quit.
    private static boolean run(String line) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) return true;   // # starts a comment, for files

        String[] first = line.split("\\s+", 2);
        String command = first[0];
        String rest = first.length > 1 ? first[1] : "";
        if (rest.startsWith("=")) command = "";                  // "vars = 3" is a definition, not a command

        try {
            switch (command) {
                case "quit":
                    return false;
                case "help":
                    System.out.println(HELP);
                    break;
                case "tokens":
                    System.out.println(Tokenizer.tokenize(rest));
                    break;
                case "postfix":
                    System.out.println(Token.join(ShuntingYard.toPostfix(Tokenizer.tokenize(rest))));
                    break;
                case "expand": {
                    MyQueue<Token> expanded = definitions.substitute(ShuntingYard.toPostfix(Tokenizer.tokenize(rest)));
                    if (expanded.size() <= PRINT_LIMIT) System.out.print(Token.join(expanded) + "    ");
                    System.out.println("(" + expanded.size() + " tokens)");
                    break;
                }
                case "vars":
                    for (Definitions.Definition d : definitions.all()) System.out.println(d);
                    break;
                case "clear":
                    definitions.clear();
                    break;
                case "load":
                    load(rest);
                    break;
                default:
                    calculate(line);
            }
        } catch (RuntimeException e) {
            System.out.println("error: " + e.getMessage());
        }
        return true;
    }

    // An expression, or a definition "name = expression".
    private static void calculate(String line) {
        MyQueue<Token> tokens = Tokenizer.tokenize(line);
        String name = null;
        Iterator<Token> it = tokens.iterator();
        if (tokens.size() >= 2 && it.next().isVariable() && it.next().isAssign()) {
            name = tokens.dequeue().name();
            tokens.dequeue();
        }
        MyQueue<Token> postfix = ShuntingYard.toPostfix(tokens);     // throws on any other "="
        double value = Evaluator.evaluate(definitions.substitute(postfix));
        if (name == null) {
            System.out.println(Token.format(value));
        } else {
            definitions.define(name, postfix);    // only once the value is known: no undefined variables in it
            System.out.println(name + " = " + Token.format(value));
        }
    }

    private static void load(String path) {
        try (Scanner file = new Scanner(new File(path))) {
            while (file.hasNextLine()) {
                String line = file.nextLine();
                System.out.println("> " + line);
                if (!run(line)) return;
            }
        } catch (FileNotFoundException e) {
            System.out.println("could not open " + path);
        }
    }

}
