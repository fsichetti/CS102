# Advanced Java Features Lab — TA notes

Goals of this lab:

- give the students a reason to read a compiler error instead of flinching at it;
- make the point that a program that compiles and runs is not thereby correct — half of these compile;
- get them fluent enough with the lecture's features that a wrong one is visible rather than merely puzzling;
- get them to use inheritance, polymorphism, and generics.

## Running it

They can work in pairs, as in the warmup lab. Same rules: they work on each problem together rather than splitting the list.

One important habit to push here: **say what is wrong before touching the code.** Instead of trying things at random until the error goes away, show them how you read the messages starting from the first one, diagnose, and then fix.

Each program is a single `.java` file holding every class it needs, with its description in a comment at the top. Say once that this is a lab convenience rather than normal Java layout: only `public` classes require their own file, so this is legal, but real code gives each class its own.

### Live examples

The first three fail early - one at compile time, one on its first line of output, one with a wrong answer - and between them they cover the features least likely to be half-remembered from CS101.

**ch3 - `WordCount`, `==` against `equals`.** this contains a bit of new material because `equals` was not reached in lecture. I will go over it in the next lecture but I think it's good if they see it here already. In class we did see `Object` sitting at the root of the class hierarchy but stopped short of `equals`, so introduce it here:

- `==` on any reference asks *are these the same object* - the same address. It never looks inside, hence the error.
- `Object` supplies an `equals` that does exactly the same thing, and classes that care about their contents, `String` among them, override it to compare what they hold instead.

### Before you let them go: reading the API docs
Open <https://docs.oracle.com/en/java/javase/21/docs/api/> after doing ch3. Show them how to find `java.lang.Object` and its `equals`, then `java.lang.String` and its `equals`. Highlight the fact that this is an override.
While you're at it also show them the ArrayList class since some problems use it. They don't need to understand how it works yet but they probably saw it in CS101. Highlight that it's a generic class.

While the page is open, show them the shape of it in general: the class hierarchy at the top, the summary table of methods, and the detail entries below. The point is not to memorise anything, only that this is where the answer lives - go here rather than to a search engine.

### Answers

| ch | Diagnosis | Fix |
|---|---|---|
| 1 | `Shape` is abstract and cannot be instantiated; `Square` never implements `area()` | delete the `new Shape(...)` line; implement `area()` in `Square` |
| 2 | raw `ArrayList` accepts anything, and the `(Integer)` cast fails at runtime on the `String` | `ArrayList<Integer>` — then `add("78")` no longer compiles, which is the point |
| 3 | `==` compares references; the literals are interned so the sanity check passes, but the strings `split` builds are separate objects | `words[i].equals(target)` |
| 4 | `Box<int>` is illegal: a type argument must be a reference type | `Box<Integer>`; autoboxing handles the `42` |
| 5 | `SavingsAccount`'s constructor has no explicit `super(...)`, so Java inserts `super()`, and `Account` has no no-argument constructor | call `super("1234", "savings")` as the first statement |
| 6 | `pay(double)` is an overload, not an override, so `Employee.pay()` runs and returns the base only | make it `pay()`; add `@Override` |
| 7 | `Dog` hides the inherited `sound` field rather than setting it | delete `Dog`'s field, set `sound` in a constructor |
| 8 | `header` in an interface is implicitly `public static final`, so it cannot be assigned; `print()` is implicitly public, so a package-private `print()` weakens it | give `Receipt` its own `header` field; declare `public void print()` |

## Code writing exercise
About halfway through the lecture, or when someone finishes the 8 problems, introduce the second part of the lab. For this they need to write a few classes.

Read `Driver.java` aloud with them and compile it in front of the room. It fails with one error per missing type, and that error list *is* the assignment — say so explicitly. Students who have only ever started from a working file do not know that this is a normal way to work.

A reference solution is in `solution-university/`, outside `lab/` so it is not shipped in the zip.

Some things they might get stuck on:

- **Storage for `Roster`.** The handout says to use an `ArrayList<T>`. If somebody reaches for an array anyway, `new T[n]` does not compile - tell them generic arrays are a Java wart we will discuss in the next lecture, and keep them moving.

- **What `Roster` cannot do.** `Roster<T>` cannot call `describe()` on its own members, because a plain `T` might be any type at all - which is why `Driver` does the printing. If someone asks whether there is a way to tell the compiler more about `T`, say yes, it is bounded type parameters and that it is mentioned in the next lecture.