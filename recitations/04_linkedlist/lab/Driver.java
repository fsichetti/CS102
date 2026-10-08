/*  Try your list by hand, one command at a time.
 *      javac Driver.java
 *      java Driver          uses SentinelLinkedList
 *      java Driver dll      uses SentinelDLinkedList
 *
 *  Elements are words. After every command it prints the list and checks its links.
 */
import java.util.Iterator;
import java.util.Scanner;

public class Driver {
    static final String HELP = """
        commands (i is a position, x a word):
          add x        add i x      addFirst x
          get i        set i x      indexOf x      contains x
          remove i     removeFirst  removeLast (dll only)
          clear        middle       reverse
          iterate      prints the elements with a for-each loop
          backward     prints them with descendingIterator (dll only)
          removeAll x  removes every x with the iterator's remove
          equals x...  compares the list with a new list holding x...
          help         quit""";

    public static void main(String[] args) {
        boolean dll = args.length > 0 && args[0].equals("dll");
        MyList<String> list = dll ? new SentinelDLinkedList<>() : new SentinelLinkedList<>();
        System.out.println("using " + list.getClass().getName());
        System.out.println(HELP);

        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            if (!in.hasNextLine()) break;
            String[] w = in.nextLine().trim().split("\\s+");
            if (w[0].isEmpty()) continue;
            if (w[0].equals("quit")) break;
            if (w[0].equals("help")) {
                System.out.println(HELP);
                continue;
            }
            try {
                System.out.println(execute(list, w));
            } catch (RuntimeException e) {
                System.out.println("threw " + e);
            }
            try {
                list.checkInvariants();
                System.out.println("list " + list + ", size " + list.size());
            } catch (IllegalStateException e) {
                System.out.println("BROKEN LIST: " + e.getMessage());
            }
        }
    }

    // runs one command, returns what to print
    static String execute(MyList<String> list, String[] w) {
        switch (w[0]) {
            case "add":
                if (w.length == 2) list.add(w[1]);
                else list.add(Integer.parseInt(w[1]), w[2]);
                return "done";
            case "addFirst":
                list.addFirst(w[1]);
                return "done";
            case "get":         return "returned " + list.get(Integer.parseInt(w[1]));
            case "set":         return "returned " + list.set(Integer.parseInt(w[1]), w[2]);
            case "indexOf":     return "returned " + list.indexOf(w[1]);
            case "contains":    return "returned " + list.contains(w[1]);
            case "remove":      return "returned " + list.remove(Integer.parseInt(w[1]));
            case "removeFirst": return "returned " + list.removeFirst();
            case "removeLast":  return "returned " + dll(list).removeLast();
            case "middle":      return "returned " + list.middle();
            case "clear":
                list.clear();
                return "done";
            case "reverse":
                list.reverse();
                return "done";
            case "iterate": {
                StringBuilder sb = new StringBuilder("visited");
                for (String s : list)
                    sb.append(" ").append(s);
                return sb.toString();
            }
            case "backward": {
                StringBuilder sb = new StringBuilder("visited");
                Iterator<String> it = dll(list).descendingIterator();
                while (it.hasNext())
                    sb.append(" ").append(it.next());
                return sb.toString();
            }
            case "removeAll": {
                int removed = 0;
                Iterator<String> it = list.iterator();
                while (it.hasNext())
                    if (it.next().equals(w[1])) {
                        it.remove();
                        removed++;
                    }
                return "removed " + removed;
            }
            case "equals": {
                MyList<String> other = list instanceof SentinelDLinkedList ? new SentinelDLinkedList<>() : new SentinelLinkedList<>();
                for (int i = 1; i < w.length; i++)
                    other.add(w[i]);
                return "returned " + list.equals(other) + " (compared with " + other + ")";
            }
            default:
                return "unknown command, type help";
        }
    }

    static SentinelDLinkedList<String> dll(MyList<String> list) {
        if (!(list instanceof SentinelDLinkedList))
            throw new UnsupportedOperationException("only for dll: run java Driver dll");
        return (SentinelDLinkedList<String>) list;
    }
}
