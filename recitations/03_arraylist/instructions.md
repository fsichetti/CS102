# ArrayList Lab

In lecture we built our own array-based list. Java's `ArrayList` is built on the same ideas. In this lab you use it: first fixing programs that misuse it, then writing your own.

The lab has two parts:

1. debugging seven short programs that misuse `ArrayList`, with the documentation as your reference;
2. writing a few methods that work on lists.

## Part 1: debugging

Each program lives in its own folder, `ch1` through `ch7`, as a single `.java` file. **Each program describes what it is supposed to do in a comment at the top**, sometimes with a hint, and which parts of the code you may edit (adding `import` lines is always allowed). They all compile.
Your TA will go through `ch1` to `ch3` with the class; `ch4` to `ch7` are for you.

The documentation describes what each method promises: it is your reference for this part.
Keep the page of `ArrayList` open: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/ArrayList.html>

| | | |
|---|---|---|
| `ch1` | `Enrollment` | wrong answer |
| `ch2` | `Playlist` | crashes |
| `ch3` | `CleanReadings` | right answer, far too slow |
| `ch4` | `DiceHistogram` | crashes |
| `ch5` | `GuestList` | crashes |
| `ch6` | `Inventory` | wrong answer |
| `ch7` | `PassingGrades` | wrong answer |

The programs that give a wrong answer do not tell you what the right one is: working it out from the description is part of the job.

For each folder:

1. Read the comment at the top of the file and all of the code.
2. Compile it and run it, from inside its folder:
   ```
   cd ch1
   javac Enrollment.java
   java Enrollment
   ```
3. Find out why it does not do what it should. For every method involved, check what the documentation promises. The comment at the top tells you where to start looking.
4. Fix it.

When a program crashes, read the whole stack trace: the top lines are inside the Java library, the first line in your own file tells you which of your calls caused it.

## Part 2: using lists

`lists/Lists.java` contains five methods to implement, each described by the comment above it. The last one is optional.
Use the methods of `List` (`get`, `set`, `add`, `remove`, `size`, `contains`, ...), and look them up in the documentation when in doubt.

Check your methods with `TestLists`, from inside the `lists` folder:

```
cd lists
javac TestLists.java
java TestLists
```

Everything compiles before you have written anything: `TestLists` shows the methods failing until you implement them.

## What to submit

Upload the whole lab folder to Brightspace, with the seven programs fixed and `lists/Lists.java` completed.
