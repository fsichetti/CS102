/*  Part 1: checks that the tags of an HTML file are balanced.
 *  To run it on files: CheckTags.
 */
package tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import stacks.LinkedStack;
import stacks.MyStack;

public class Tags {

    // one tag of the file: <p>, </p>, <br>, ...
    public static class Tag {
        private final String name;        // lowercase, no '<', '/', '>' or attributes: "p"
        private final boolean closing;    // </p>
        private final boolean isVoid;     // never closed: <br>, <img ...>, <hr/>, ...
        private final int line;           // line of the file where the tag starts

        Tag(String name, boolean closing, boolean isVoid, int line) {
            this.name = name;
            this.closing = closing;
            this.isVoid = isVoid;
            this.line = line;
        }

        public String name() { return name; }
        public boolean isClosing() { return closing; }
        public boolean isVoid() { return isVoid; }
        public int line() { return line; }

        @Override
        public String toString() { return "<" + (closing ? "/" : "") + name + ">"; }
    }

    // the position in tags of the first tag where the file is found to be wrong:
    //   a closing tag with no open tag left, or that does not match the last open tag
    // tags.size() if the file ends with tags still open
    // -1 if the tags are balanced
    // void tags are skipped
    public static int firstError(List<Tag> tags) {
        MyStack<Tag> open = new LinkedStack<>();
        for (int i = 0; i < tags.size(); i++) {
            Tag t = tags.get(i);
            if (t.isVoid())
                continue;
            if (!t.isClosing())
                open.push(t);
            else if (open.isEmpty() || !open.pop().name().equals(t.name()))
                return i;
        }
        return open.isEmpty() ? -1 : tags.size();
    }

    // ==== given: everything below ====

    // elements that have no closing tag in HTML
    static final Set<String> VOID = Set.of("area", "base", "br", "col", "embed", "hr", "img",
                                           "input", "link", "meta", "source", "track", "wbr");

    // the tags of an HTML text, in order; comments and <!DOCTYPE ...> are skipped
    // simplified: no '>' inside attribute values, no <script> or <style> contents
    public static List<Tag> parse(String html) {
        List<Tag> tags = new ArrayList<>();
        int line = 1;
        int counted = 0;    // newlines counted up to here
        int i = 0;
        while (true) {
            int open = html.indexOf('<', i);
            if (open < 0) break;
            for (; counted < open; counted++)
                if (html.charAt(counted) == '\n') line++;

            if (html.startsWith("<!--", open)) {
                int end = html.indexOf("-->", open);
                i = end < 0 ? html.length() : end + 3;
                continue;
            }
            int close = html.indexOf('>', open);
            if (close < 0) break;
            i = close + 1;

            String inside = html.substring(open + 1, close).trim();
            boolean closing = inside.startsWith("/");
            if (closing) inside = inside.substring(1);
            boolean selfClosing = inside.endsWith("/");
            String name = inside.split("[\\s/]", 2)[0].toLowerCase();
            if (!name.matches("[a-z][a-z0-9-]*")) {     // "<!DOCTYPE html>", "a < b", ...
                i = open + 1;
                continue;
            }
            tags.add(new Tag(name, closing, selfClosing || VOID.contains(name), line));
        }
        return tags;
    }
}
