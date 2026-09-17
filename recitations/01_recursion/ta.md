# Recursion Lab — TA notes

Goals of this lab:

- get comfortable applying the recipe from lecture (1. base case, 2. recursive case, 3. combine step, 4. does every path terminate) to problems they have not seen worked before;
- see a call *tree* (two recursive calls) for the first time, not just the call *chains* lecture covered (factorial, string reverse, binary search);
- build a feel for when recursion is a natural fit for a problem, when it costs you running time, and when it merely restates a loop.

Lecture covered the recipe, the base/recursive case vocabulary, the public-wrapper-private-recursive pattern, and factorial/reverse/recursive-binary-search — all single recursive call, all chains.

This lab deliberately stays away from teaching multiple solutions / backtracking. `ch6` is a taste of it as an **optional** bonus for pairs who finish everything else, not something to walk through; if it comes up, let them figure it out or nudge lightly, but do not turn it into a live example.

For the first two it may be useful to trace the call stack for small values like `collatzLength(1)` and `fib(4)` so they understand execution order (can be done by hand or by adding prints to the solution), and for the Fibonacci sequence example, drawing the call graph as a tree on the blackboard.

The Fibonacci naive implementation will get slow for high n. This is not an issue with recursion itself but we need to pay attention to it. Leave it open to them to think of how this issue can be resolved (e.g. with a supporting array), and we will discuss it again in class.

All methods should throw `IllegalArgumentException` for invalid inputs.

## ch1 — `collatzLength`, live on the board

Base case `n == 1` → 0; else `1 + collatzLength(n/2)` (even) or `1 + collatzLength(3n+1)` (odd). Worth saying out loud: nobody has proven this always reaches 1 — that's the open conjecture.

Wrapper pattern for the exception: validate once in `collatzLength`, recurse in a private `collatzLengthRec` that trusts its input — same shape as lecture's `reverse`. Name it explicitly; the rest of the lab reuses it.

## ch2 — `fib`, live on the board

Trace `fib(5)` as a tree, not a chain — first time a call branches into two. Point at `fib(3)`: computed twice, from scratch both times.

Timing demo is in `Main.java`; run it live with `n` around 40 (recursive ≈0.5s, iterative 0ms — push to 45 for more contrast).

## ch3 — `binom`

Independent — same two-call shape as `fib`, new recurrence (Pascal's rule). Let them transfer the recipe themselves. If a pair asks why `binom(35, 17)` is slow, let them notice "it's the Fibonacci thing again" before confirming it.

## ch4 — `isPalindromeRecursive`

Not reached in lecture, so worth a reminder before releasing them: `clean(s)` (given) handles punctuation/case and the null check; write the recursion on top of it via substrings — a cleaned string of length ≤ 1 is a palindrome, otherwise compare the ends and recurse on what's between them.

- The substring version copies on every call — total work is O(n²), not O(n). Worth flagging explicitly, not just "extra stack frames."
- Discussion once both versions pass: did recursion actually help here, or was the two-pointer loop just as natural? Contrast with `fib`/`binom`, where the recursive definition *is* the math.

## ch5 — `ruler`

Combine step is string concatenation, not arithmetic — a new shape. `"-".repeat(h) + "\n"` is the whole trick for a tick.

## ch6 — `binarySequences` (bonus)

Checked by eye, no `Test.java`. If someone starts asking how to explore both the 0 and 1 choice at each position, that's backtracking — next recitation's subject. Let them run with it rather than teaching it now.

## Reference solutions

Not shipped in the lab zip — for your own reference only.

```java
static int collatzLength(int n) {
    if (n <= 0) throw new IllegalArgumentException("n must be positive, got " + n);
    return collatzLengthRec(n);
}
private static int collatzLengthRec(int n) {
    if (n == 1) return 0;
    if (n % 2 == 0) return 1 + collatzLengthRec(n / 2);
    return 1 + collatzLengthRec(3 * n + 1);
}

static long fib(int n) {
    if (n < 0) throw new IllegalArgumentException("n must be non-negative, got " + n);
    return fibRec(n);
}
private static long fibRec(int n) {
    if (n == 0) return 0;
    if (n == 1) return 1;
    return fibRec(n-1) + fibRec(n-2);
}

static long binom(int n, int k) {
    if (n < 0 || k < 0 || k > n) {
        throw new IllegalArgumentException("need 0 <= k <= n, got n=" + n + " k=" + k);
    }
    return binomRec(n, k);
}
private static long binomRec(int n, int k) {
    if (k == 0 || k == n) return 1;
    return binomRec(n-1, k-1) + binomRec(n-1, k);
}

// isPalindromeRecursive relies on clean(s) (given) for both the null check
// and the case/punctuation normalization; only the recursion below is new.
static boolean isPalindromeRecursive(String s) {
    return isPalindromeRec( clean(s) );
}
private static boolean isPalindromeRec(String s) {
    if (s.length() <= 1) return true;
    if (s.charAt(0) != s.charAt(s.length() - 1)) return false;
    return isPalindromeRec( s.substring(1, s.length() - 1) );
}

static String ruler(int h) {
    if (h < 0) throw new IllegalArgumentException("h must be non-negative, got " + h);
    return rulerRec(h);
}
private static String rulerRec(int h) {
    if (h == 0) return "";
    return rulerRec(h - 1) + "-".repeat(h) + "\n" + rulerRec(h - 1);
}

static String [] binarySequences(int n) {
    if (n < 0) throw new IllegalArgumentException("n must be non-negative, got " + n);
    if (n == 0) return new String [] { "" };
    String [] rest = binarySequences(n - 1);
    String [] result = new String [rest.length * 2];
    for (int i = 0; i < rest.length; i++) {
        result[i] = "0" + rest[i];
        result[rest.length + i] = "1" + rest[i];
    }
    return result;
}
```
