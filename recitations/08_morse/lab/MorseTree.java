/*  Part 1: the Morse code tree.
 *
 *  Every node is reached from the root by a code: a dot goes left, a dash goes right.
 *  A node holds the letter of its code, or null if no letter has that code.
 *  The root is the empty code: it never holds a letter.
 */
public class MorseTree {
    static class Node {
        Character letter;       // null: no letter has this code
        Node left, right;       // left: one more dot, right: one more dash
    }

    Node root = new Node();

    // TODO Part 1
    // adds letter with the given code, creating the missing nodes along the way
    // throws IllegalArgumentException if code is empty, contains a symbol other than '.' and '-',
    // or is already the code of a letter
    public void insert(char letter, String code) {
        throw new UnsupportedOperationException("MorseTree.insert");
    }

    // TODO Part 1
    // the letter with the given code
    // throws IllegalArgumentException if code contains a symbol other than '.' and '-',
    // or if no letter has that code
    public char decode(String code) {
        throw new UnsupportedOperationException("MorseTree.decode");
    }

    // every node except the root, one per line: its code, then its letter. Do not modify
    public String toString() {
        StringBuilder s = new StringBuilder();
        print(root, "", s);
        return s.toString();
    }

    private void print(Node node, String code, StringBuilder s) {
        if (node == null) return;
        if (node != root) s.append(node.letter == null ? code : String.format("%-7s %s", code, node.letter)).append('\n');
        print(node.left, code + ".", s);
        print(node.right, code + "-", s);
    }
}
