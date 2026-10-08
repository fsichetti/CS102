/*  Tests your lists.
 *      javac Test.java
 *      java Test          tests SentinelLinkedList
 *      java Test dll      tests SentinelDLinkedList
 *
 *  After every change to a list, checkInvariants checks its links and fields.
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Test {
    static boolean dll;
    static int failures = 0;

    // ==== helpers ====

    static MyList<String> newList() {
        return dll ? new SentinelDLinkedList<>() : new SentinelLinkedList<>();
    }

    // a new list with the given elements, added one by one with add(e)
    static MyList<String> list(String... values) {
        MyList<String> l = newList();
        for (String v : values) {
            l.add(v);
            l.checkInvariants();
        }
        return l;
    }

    static void ok(String what) { System.out.println("OK    " + what); }

    static void fail(String what) {
        failures++;
        System.out.println("FAIL  " + what);
    }

    static void check(String what, Object expected, Object actual) {
        if (expected == null ? actual == null : expected.equals(actual)) ok(what);
        else fail(what + ": expected " + expected + ", got " + actual);
    }

    // checks the links first, then the contents
    static void checkList(String what, String expected, MyList<String> l) {
        try {
            l.checkInvariants();
        } catch (IllegalStateException e) {
            fail(what + ": broken list: " + e.getMessage());
            return;
        }
        check(what, expected, l.toString());
    }

    static void checkThrows(String what, Class<? extends RuntimeException> type, Runnable code) {
        try {
            code.run();
            fail(what + ": expected " + type.getSimpleName() + ", nothing thrown");
        } catch (RuntimeException e) {
            if (type.isInstance(e)) ok(what);
            else fail(what + ": expected " + type.getSimpleName() + ", got " + e);
        }
    }

    // runs one group of tests: an unexpected exception fails the group, and testing goes on
    static void run(String name, Runnable tests) {
        System.out.println("== " + name);
        try {
            tests.run();
        } catch (RuntimeException e) {
            fail("threw " + e + "\n      at " + e.getStackTrace()[0]);
        }
    }

    // ==== Part 1 ====

    static void add() {
        MyList<String> l = newList();
        checkList("new list", "[]", l);
        check("new list: size", 0, l.size());
        check("new list: isEmpty", true, l.isEmpty());
        l.add("a");
        checkList("add to empty list", "[a]", l);
        l.add("b");
        l.add("c");
        checkList("add twice more", "[a, b, c]", l);
        check("size", 3, l.size());
        check("isEmpty", false, l.isEmpty());
    }

    static void addFirst() {
        MyList<String> l = newList();
        l.addFirst("c");
        checkList("addFirst to empty list", "[c]", l);
        l.addFirst("b");
        l.addFirst("a");
        checkList("addFirst twice more", "[a, b, c]", l);
        l.add("d");
        checkList("then add", "[a, b, c, d]", l);
    }

    static void addAt() {
        MyList<String> l = list("a", "c");
        l.add(1, "b");
        checkList("add(1, b) in the middle", "[a, b, c]", l);
        l.add(0, "start");
        checkList("add(0, start)", "[start, a, b, c]", l);
        l.add(4, "end");
        checkList("add(4, end): at position size", "[start, a, b, c, end]", l);
        l.add("last");
        checkList("then add", "[start, a, b, c, end, last]", l);
        l = newList();
        l.add(0, "x");
        checkList("add(0, x) to empty list", "[x]", l);
        l.add("y");
        checkList("then add", "[x, y]", l);
        MyList<String> m = list("a", "b");
        checkThrows("add(3, x) on size 2", IndexOutOfBoundsException.class, () -> m.add(3, "x"));
        checkThrows("add(-1, x)", IndexOutOfBoundsException.class, () -> m.add(-1, "x"));
        checkList("unchanged after the exceptions", "[a, b]", m);
    }

    static void getSet() {
        MyList<String> l = list("a", "b", "c");
        check("get(0)", "a", l.get(0));
        check("get(1)", "b", l.get(1));
        check("get(2)", "c", l.get(2));
        check("set(1, x) returns the old element", "b", l.set(1, "x"));
        checkList("after set(1, x)", "[a, x, c]", l);
        l.set(0, "first");
        l.set(2, "last");
        checkList("set(0, ...) and set(2, ...)", "[first, x, last]", l);
        checkThrows("get(3) on size 3", IndexOutOfBoundsException.class, () -> l.get(3));
        checkThrows("get(-1)", IndexOutOfBoundsException.class, () -> l.get(-1));
        checkThrows("set(3, y) on size 3", IndexOutOfBoundsException.class, () -> l.set(3, "y"));
        checkThrows("get(0) on empty list", IndexOutOfBoundsException.class, () -> newList().get(0));
    }

    static void removeFirst() {
        MyList<String> l = list("a", "b", "c");
        check("removeFirst returns the first element", "a", l.removeFirst());
        checkList("after removeFirst", "[b, c]", l);
        l.removeFirst();
        check("removeFirst on one element", "c", l.removeFirst());
        checkList("now empty", "[]", l);
        l.add("d");
        checkList("add after emptying", "[d]", l);
        checkThrows("removeFirst on empty list", NoSuchElementException.class, () -> newList().removeFirst());
    }

    static void removeAt() {
        MyList<String> l = list("a", "b", "c", "d");
        check("remove(1) returns the element", "b", l.remove(1));
        checkList("after remove(1)", "[a, c, d]", l);
        check("remove(2): the last element", "d", l.remove(2));
        checkList("after remove(2)", "[a, c]", l);
        l.add("e");
        checkList("then add", "[a, c, e]", l);
        check("remove(0)", "a", l.remove(0));
        checkList("after remove(0)", "[c, e]", l);
        l.remove(1);
        l.remove(0);
        checkList("removed everything", "[]", l);
        l.add("f");
        checkList("add after emptying", "[f]", l);
        checkThrows("remove(1) on size 1", IndexOutOfBoundsException.class, () -> l.remove(1));
        checkThrows("remove(0) on empty list", IndexOutOfBoundsException.class, () -> newList().remove(0));
    }

    static void clear() {
        MyList<String> l = list("a", "b", "c");
        l.clear();
        checkList("clear", "[]", l);
        check("size after clear", 0, l.size());
        l.add("d");
        checkList("add after clear", "[d]", l);
    }

    // ==== Part 2 ====

    static void indexOf() {
        MyList<String> l = list("a", "b", "c", "b");
        check("indexOf(a)", 0, l.indexOf("a"));
        check("indexOf(b): first occurrence", 1, l.indexOf("b"));
        check("indexOf(z)", -1, l.indexOf("z"));
        check("indexOf(z) on empty list", -1, newList().indexOf("z"));
        check("indexOf(new String(\"c\")): same text, another object", 2, l.indexOf(new String("c")));
        check("contains(b)", true, l.contains("b"));
        check("contains(z)", false, l.contains("z"));
        check("contains(new String(\"a\"))", true, l.contains(new String("a")));
        check("contains(a) on empty list", false, newList().contains("a"));
    }

    static void iterator() {
        List<String> seen = new ArrayList<>();
        for (String s : list("a", "b", "c"))
            seen.add(s);
        check("for-each visits", List.of("a", "b", "c"), seen);
        check("hasNext on empty list", false, newList().iterator().hasNext());
        Iterator<String> it = list("a").iterator();
        check("hasNext", true, it.hasNext());
        check("next", "a", it.next());
        check("hasNext at the end", false, it.hasNext());
        checkThrows("next at the end", NoSuchElementException.class, it::next);
    }

    static void equalsTest() {
        MyList<String> l = list("a", "b", "c");
        check("same elements", true, l.equals(list("a", "b", "c")));
        check("equal strings, different objects", true, l.equals(list(new String("a"), new String("b"), new String("c"))));
        check("itself", true, l.equals(l));
        check("shorter", false, l.equals(list("a", "b")));
        check("longer", false, list("a", "b").equals(l));
        check("different order", false, l.equals(list("c", "b", "a")));
        check("both empty", true, newList().equals(newList()));
        check("not a list", false, l.equals("[a, b, c]"));
        check("null", false, l.equals(null));
    }

    static void middle() {
        check("odd size", "b", list("a", "b", "c").middle());
        check("even size", "c", list("a", "b", "c", "d").middle());
        check("one element", "a", list("a").middle());
        check("two elements", "b", list("a", "b").middle());
        checkThrows("empty list", NoSuchElementException.class, () -> newList().middle());
    }

    static void reverse() {
        MyList<String> l = list("a", "b", "c", "d");
        l.reverse();
        checkList("reverse", "[d, c, b, a]", l);
        l.add("e");
        checkList("then add", "[d, c, b, a, e]", l);
        l.reverse();
        checkList("reverse again", "[e, a, b, c, d]", l);
        l = list("a");
        l.reverse();
        checkList("one element", "[a]", l);
        l = newList();
        l.reverse();
        checkList("empty list", "[]", l);
        l.add("a");
        checkList("add after reversing an empty list", "[a]", l);
    }

    static void iteratorRemove() {
        MyList<String> l = list("x", "a", "x", "x", "b", "x");
        Iterator<String> it = l.iterator();
        while (it.hasNext())
            if (it.next().equals("x")) it.remove();
        checkList("remove every x", "[a, b]", l);
        check("size", 2, l.size());
        l.add("c");
        checkList("then add", "[a, b, c]", l);

        l = list("a", "b");
        it = l.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        checkList("remove everything", "[]", l);
        l.add("c");
        checkList("then add", "[c]", l);

        Iterator<String> it2 = list("a", "b").iterator();
        checkThrows("remove before next", IllegalStateException.class, it2::remove);
        it2.next();
        it2.remove();
        checkThrows("remove twice in a row", IllegalStateException.class, it2::remove);
        check("next after remove", "b", it2.next());
    }

    // ==== Part 3 ====

    static void removeLast() {
        SentinelDLinkedList<String> l = (SentinelDLinkedList<String>) list("a", "b", "c");
        check("removeLast returns the last element", "c", l.removeLast());
        checkList("after removeLast", "[a, b]", l);
        l.add("d");
        checkList("then add", "[a, b, d]", l);
        l.removeLast();
        l.removeLast();
        check("removeLast on one element", "a", l.removeLast());
        checkList("now empty", "[]", l);
        checkThrows("removeLast on empty list", NoSuchElementException.class, l::removeLast);
    }

    static void descendingIterator() {
        SentinelDLinkedList<String> l = (SentinelDLinkedList<String>) list("a", "b", "c");
        List<String> seen = new ArrayList<>();
        Iterator<String> it = l.descendingIterator();
        while (it.hasNext())
            seen.add(it.next());
        check("visits", List.of("c", "b", "a"), seen);
        checkThrows("next at the end", NoSuchElementException.class, it::next);
        check("hasNext on empty list", false, new SentinelDLinkedList<String>().descendingIterator().hasNext());
    }

    static void equalsAcross() {
        MyList<String> s = new SentinelLinkedList<>();
        s.add("a");
        s.add("b");
        check("DLL equals SLL", true, list("a", "b").equals(s));
        check("SLL equals DLL", true, s.equals(list("a", "b")));
        check("different elements", false, list("a", "c").equals(s));
    }

    public static void main(String[] args) {
        dll = args.length > 0 && args[0].equals("dll");
        System.out.println("testing " + (dll ? "SentinelDLinkedList" : "SentinelLinkedList"));

        System.out.println("\n######## Part 1");
        run("new list, add, size, isEmpty", Test::add);
        run("addFirst", Test::addFirst);
        run("add at a position", Test::addAt);
        run("get, set", Test::getSet);
        run("removeFirst", Test::removeFirst);
        run("remove at a position", Test::removeAt);
        run("clear", Test::clear);

        System.out.println("\n######## Part 2");
        run("indexOf, contains", Test::indexOf);
        run("iterator: hasNext, next", Test::iterator);
        run("equals", Test::equalsTest);
        run("middle", Test::middle);
        run("reverse", Test::reverse);
        run("iterator: remove", Test::iteratorRemove);

        if (dll) {
            System.out.println("\n######## Part 3");
            run("removeLast", Test::removeLast);
            run("descendingIterator", Test::descendingIterator);
            run("equals between the two lists", Test::equalsAcross);
        }

        System.out.println();
        System.out.println(failures == 0 ? "all tests passed" : failures + " test(s) failed");
    }
}
