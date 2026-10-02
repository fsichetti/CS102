# ArrayList Lab: TA notes

Goals of this lab:

- use the documentation as the reference for how a library class behaves;
- debug programs whose problem lies in *how a library method behaves*;
- write simple methods on lists with the `List` API.

Lecture covered: the List ADT, our own `MyArrayListDouble` (a list of `double`s), growing the array (additive vs. multiplicative, amortized cost), insertion/removal with shifting, a first look at `java.util.ArrayList`, `equals`, iterators.

## Running it

Rough timing: ch1 to ch3 ~20 min (you, live), ch4 to ch7 ~20-25 min on their own, Part 2 the rest (~30-40 min). Whatever is left of Part 2 they finish at home.

## Part 1: debugging

Do **ch1 to ch3 live**, with the class: they are the least obvious ones. For each, run it, let them suggest the diagnosis before you give it, and show the **docs** entry that explains it: this is the model for how they should work on ch4 to ch7. Before ch1, open the docs page of `ArrayList` on the projector and show its layout: the paragraphs at the top, the constructors, the method summary, one method entry.

| ch | Diagnosis | Docs to show | Fix |
|---|---|---|---|
| 1 | `ids.remove(id)` with an `int` calls `remove(int index)`, not `remove(Object o)`: it removes the elements at positions 3 and 7 (18 and 5) | `List`: the two `remove` methods | `ids.remove(Integer.valueOf(id))` |
| 2 | removing from the list inside a for-each loop: the iterator notices the list changed and throws | `ConcurrentModificationException`; `Iterator.remove` | use an explicit `Iterator` and `it.remove()` |
| 3 | each `remove(i)` shifts every element after `i`: up to N shifts per removal, O(N²) overall (the loop being backwards does not help) | `ArrayList`, top: "All of the other operations run in linear time" | copy the valid readings into a new `ArrayList` in one pass, O(N) |
| 4 | `new ArrayList<>(6)` sets the capacity, not the size: the list is empty, so `get(face)` is out of bounds | `ArrayList(int)`: "Constructs an empty list with the specified initial capacity" | add six zeros first (`for (...) counts.add(0);`) |
| 5 | `Arrays.asList` returns a fixed-size list backed by the array: `add` throws `UnsupportedOperationException` | `Arrays.asList`: "Returns a fixed-size list backed by the specified array" | `new ArrayList<>(Arrays.asList(confirmed))` |
| 6 | `contains` uses `equals`, and `Item` inherits `Object.equals`, which compares references: the request is a different object | `contains`: "such that `Objects.equals(o, e)`" | override `equals(Object)` in `Item`, comparing `name` and `size` |
| 7 | after `remove(i)` the next element slides into position `i`, and `i++` skips it: 52 and 59 survive | `remove(int index)`: "Shifts any subsequent elements to the left" | `i--` after removing, or loop backwards |

Details, per program:

- **ch1.** Overload resolution: `remove(int)` is an exact match for an `int` argument, so Java never considers boxing it to call `remove(Object)`. The size is right (8), only the content is wrong: a good example of a wrong answer that looks plausible. Correct output: `[12, 25, 18, 40, 9, 31, 5, 22]`.
- **ch2.** The stack trace shows `checkForComodification` inside `ArrayList$Itr.next`: the iterator remembers how many structural changes the list had when it was created and checks it at every `next()`; `it.remove()` keeps the two in sync. In lecture the students saw that removing the *second to last* element does not throw (the loop ends before `next()` is called again). If someone asks why: the docs of `ConcurrentModificationException` say it is a best-effort check, not a guarantee. Correct output: `[Intro, Blue, Home, Outro]`.
- **ch3.** This is the one place to show the source, briefly: open <https://github.com/openjdk/jdk/blob/jdk-21-ga/src/java.base/share/classes/java/util/ArrayList.java>, go to `remove(int)` (550) → `fastRemove` (719) → `System.arraycopy(es, i + 1, es, i, newSize - i)` (723): the shift we wrote in lecture, so the docs' "linear time" is no surprise. Measured on the reference machine: 1.9 s before, ~10 ms after, at N = 500,000; doubling N roughly multiplies the time by 4 before the fix. Their times will differ, the ratio should not. `removeIf` is also O(N) and is a fine answer if they found it in the docs.
- **ch4.** The message says "Index 5 out of bounds for length 0": the "length" is the **size**, not the capacity. The capacity is not visible from outside the class.
- **ch5.** `guests.getClass().getName()` prints `java.util.Arrays$ArrayList`: a different class from `java.util.ArrayList`, with the same simple name. It is also backed by the array: `set` on the list changes `confirmed`. Worth showing if someone gets there early.
- **ch6.** Their `equals` must take an `Object` (otherwise it is an overload, as in the Java-features lab). Ask for `@Override`. If someone mentions `hashCode`: yes, the convention is to override both; it matters for hash tables, later in the course. Not required here.
- **ch7.** Correct output: `passing: [88, 91, 77, 100, 64]`, average 84.0. If someone uses `removeIf`, accept it; lambdas have not been covered in class. Same effect as in `josephus` in Part 2, there used on purpose.

Reference fixes for all seven were compiled and run with Java 21.

## Part 2: using lists

`TestLists` checks every method, including that the arguments are not modified where the comment says so. Reference solutions (they pass all tests):

```java
public static List<Integer> dedupe( List<Integer> list ) {
    List<Integer> result = new ArrayList<>();
    for ( int x : list )
        if ( !result.contains(x) )
            result.add(x);
    return result;
}

public static void insertSorted( List<Integer> sorted, int x ) {
    int pos = 0;
    while ( pos < sorted.size() && sorted.get(pos) < x )
        pos++;
    sorted.add(pos, x);
}

public static List<Integer> merge( List<Integer> a, List<Integer> b ) {
    List<Integer> result = new ArrayList<>();
    int i = 0, j = 0;
    while ( i < a.size() && j < b.size() ) {
        if ( a.get(i) <= b.get(j) ) result.add(a.get(i++));
        else                        result.add(b.get(j++));
    }
    while ( i < a.size() ) result.add(a.get(i++));
    while ( j < b.size() ) result.add(b.get(j++));
    return result;
}

public static List<Integer> josephus( int n, int k ) {
    List<Integer> circle = new ArrayList<>();
    for ( int p = 1; p <= n; p++ )
        circle.add(p);
    List<Integer> order = new ArrayList<>();
    int i = 0;
    while ( !circle.isEmpty() ) {
        i = (i + k - 1) % circle.size();
        order.add(circle.remove(i));
    }
    return order;
}

public static List<List<Integer>> runs( List<Integer> list ) {
    List<List<Integer>> result = new ArrayList<>();
    for ( int i = 0; i < list.size(); i++ ) {
        if ( i == 0 || list.get(i) < list.get(i - 1) )
            result.add(new ArrayList<>());
        result.get(result.size() - 1).add(list.get(i));
    }
    return result;
}
```

Things to watch for:

- **dedupe.** `result.contains(x)` makes it O(N²); fine here, they will see a linear version with hash sets later in the course.
- **insertSorted** is one step of insertion sort, **merge** is the merge step of merge sort: point it out. If someone compares two elements with `==`: on `Integer` objects `==` compares references, and `a.get(i) == b.get(j)` can be false for equal values above 127. `<`, `<=`, `>`, `>=` unbox, `==` does not.
- **josephus.** After `remove(i)` the next person slides into position `i`, so the count restarts from `i` itself: the effect behind ch7, here used on purpose. The `% circle.size()` is the step most of them need help with: let them try it by hand on `josephus(7, 3)` first.
- **runs** (optional) builds a list of lists: a new inner list starts whenever an element is smaller than the previous one.
