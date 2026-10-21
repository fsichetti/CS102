# Calculator

In this assignment you build a calculator: it reads expressions such as `2 * (x + 1) ^ 2`, converts them to **postfix** notation with the **shunting-yard** algorithm, and evaluates them. It also supports variables, `x = 3`.

Every stack and every queue in the program is the same data structure, which you write first: a **deque** on a circular array. The rest of the program uses it only through the `MyStack` and `MyQueue` interfaces.

The scaffold compiles as given, but tests fail until you fill in the `TODO`s.
You should submit the whole lab folder, so it's easier to test on our end.
Also submit short written answers to the questions at the end of this document, a paragraph each. One pdf or plain text file, submitted alongside your code.

## Checklist

For each item, the test sections in brackets check that functionality.

**Part 1: the deque, in `deque/MyArrayDeque.java`**

- [ ] Constructor, `size`, `isEmpty`, and adding, removing and peeking at both ends. [`deque`]
- [ ] `iterator()`: an inner class. [`iterator`]
- [ ] Growing when full, shrinking when a quarter full. [`resize`]

The methods of `MyStack` and `MyQueue` are given: they pass once your deque does. [`stackqueue`]

**Part 2: infix to postfix, in `calc/ShuntingYard.java`** [`postfix`]

- [ ] `toPostfix(infix)`: the shunting-yard algorithm, with numbers, variables, the five binary operators, `neg` and parentheses.

**Part 3: evaluation, in `calc/Evaluator.java`** [`evaluate`, `calculate`]

- [ ] `evaluate(postfix)`: the value of a postfix expression.

**Part 4: variables, in `calc/Definitions.java`** [`variables`]

- [ ] `define(name, body)`: adds a definition.
- [ ] `substitute(postfix)`: replaces every variable with its definition.

Parts 2 and 3 can be written in either order: the `evaluate` tests build their postfix input directly, without `toPostfix`.

**Questions**

- [ ] Short answers to the questions at the end of this document, a paragraph each, in a pdf or plain text file.

**Optional tasks**

These are not needed to pass the other tests.

- [ ] Functions: `sqrt(16)`, `max(1, 2)`, ... in `toPostfix` (see *Functions*). [`functions`]
- [ ] Rejecting invalid infix in `toPostfix` (see *Checking the syntax*). [`syntax`]

## Rules

- In `deque/` and `calc/`, no collections from `java.util` (`ArrayList`, `ArrayDeque`, `Stack`, `LinkedList`, ...). Every stack is a `MyStack` and every queue a `MyQueue`, created as a `MyArrayDeque`; the only array is the one inside `MyArrayDeque`.
- `toPostfix`, `evaluate` and `substitute` leave their argument unchanged, and so does `substitute` with the bodies of the definitions. Read a stack or a queue without changing it with a for-each loop.
- Errors in the input (unbalanced parentheses, a missing operand, an undefined variable, ...) throw `IllegalArgumentException`, with a message: the REPL prints it.
- You may add private methods, fields and classes. Don't change anything else in the given files.

\newpage

## Files

| File | Status |
|---|---|
| `deque/MyDeque.java`, `MyStack.java`, `MyQueue.java` | given, complete: the interfaces |
| `deque/MyArrayDeque.java` | Part 1, except the `MyStack` and `MyQueue` methods |
| `calc/Token.java` | given, complete |
| `calc/Tokenizer.java` | given, complete: text to a queue of tokens |
| `calc/ShuntingYard.java` | Part 2 |
| `calc/Evaluator.java` | Part 3 |
| `calc/Definitions.java` | Part 4, except what is marked as given |
| `Calculator.java` | given, complete: the REPL |
| `Tests.java` | given, complete |
| `data/examples.txt`, `data/doubling.txt` | inputs for the REPL's `load` command |

Everything except Part 1 uses your deque, since the `Tokenizer` returns one: start from Part 1. Test each part as soon as you finish it, running only its sections (in brackets in the checklist). Compile and run from the `lab` folder, compiling again after every change:

```
javac Tests.java Calculator.java
java Calculator                                the REPL
java Tests deque                               after Part 1, without the iterator and resizing
java Tests deque iterator resize stackqueue    after Part 1
java Tests postfix                             after Part 2
java Tests evaluate calculate                  after Part 3
java Tests variables                           after Part 4
java Tests functions syntax                    after the optional tasks
java Tests                                     everything, before submitting
```

A test running longer than 5 seconds fails, and the rest of its section is skipped. The `deque` and `resize` sections also compare 100000 random operations against `java.util.ArrayDeque`.

## The REPL

```
> 1 + 2 * 3
7
> x = 2 ^ 10
x = 1024
> postfix -x / (4 - 2)
x neg 4 2 - /
> -x / (4 - 2)
-512
```

Commands: an expression; `name = expression`; `tokens`, `postfix` or `expand` followed by an expression, to see the result of each step; `vars` to list the definitions; `clear`; `load <file>`; `help`; `quit`. `load data/examples.txt` runs a short session.

# Reference

## Tokens

`Tokenizer.tokenize("x = -2 * (3 + y)")` returns a `MyQueue<Token>` with the tokens in the order of the text: `[x, =, neg, 2, *, (, 3, +, y, )]`. It checks single tokens only: `1 + * 2` is tokenized without complaint.

A `Token` is immutable. Exactly one of `isNumber()`, `isVariable()`, `isOperator()`, `isFunction()`, `isLeftParen()`, `isRightParen()`, `isComma()`, `isAssign()` is true.

- `value()`: numbers only, a `double`.
- `name()`: the text of the token, e.g. the name of a variable.
- Operators: `+ - * / ^` and `neg`, the unary minus. The tokenizer turns a `-` into `neg` where an operand is expected: at the start, after an operator, after `(`.
    - `precedence()`: `+ -` 1, `* /` 2, `neg` 3, `^` 4. Higher binds tighter.
    - `isRightAssociative()`: true only for `^`, since $2^{3^2} = 2^{(3^2)}$.
    - `isPrefix()`: true only for `neg`.
- Operators and functions: `arity()`, the number of operands (1 or 2); `apply(x)` and `apply(a, b)` compute the result, with `a` the left operand. `apply` throws `ArithmeticException` on division by zero.
- `Token.join(tokens)` is `"1 2 +"`: useful for printing while debugging.

## Part 1: `MyArrayDeque`

The circular array deque from lecture, with two additions: an iterator, and shrinking. Write it yourself from the interface.

**Indices.** Element `i`, counting from the front, is at `data[(front + i) % data.length]`. `addFirst` moves `front` one cell back: `Math.floorMod(front - 1, data.length)`, since `-1 % n` is `-1` in Java.

**Growing.** Before adding to a full array, double its capacity: copy the elements to a new array, in order, at indices `0` to `size - 1`, and set `front = 0`. `Arrays.copyOf` is not enough when the elements wrap around the end.

**Shrinking.** After a removal, if the capacity is greater than `INITIAL_CAPACITY` and `size <= capacity / 4`, halve the capacity, with the same copy. The tests check the exact capacity, so use exactly these rules.

**Removed elements.** Set the cell of a removed element to `null`, so that the deque does not keep the object alive.

**The iterator.** An inner class implementing `Iterator<E>`, as for `MyArrayListDouble`, with the position of the next element counted from the front. `next()` throws `NoSuchElementException` when nothing is left. `toString` (given) uses it, so it prints `[1, 2, 3]` only once the iterator works.

**Generic array.** `(E[]) new Object[capacity]`, with `@SuppressWarnings("unchecked")` on the method, as in lecture. Because of type erasure, Java cannot check this cast at runtime (the array really is an `Object[]`), hence the warning; it is safe here because `data` is private, never leaves the class, and only ever receives elements of type `E`.

**Stack and queue.** `MyArrayDeque` also implements `MyStack` and `MyQueue`, the interfaces from lecture, with given methods that call the deque ones: `push` is `addFirst`, `pop` is `removeFirst`, `enqueue` is `addLast`, `dequeue` is `removeFirst`. The top of a stack and the front of a queue are both the first element, so one `peek`, which calls `peekFirst`, serves both interfaces. Declare each variable with the interface of its role, e.g. `MyStack<Token> operators = new MyArrayDeque<>();`: then `operators.removeLast()` does not compile.

## Postfix

In **postfix** notation an operator comes after its operands: `1 + 2` is `1 2 +`.

| infix | postfix |
|---|---|
| `1 + 2 * 3` | `1 2 3 * +` |
| `(1 + 2) * 3` | `1 2 + 3 *` |
| `1 - 2 - 3` | `1 2 - 3 -` |
| `2 ^ 3 ^ 2` | `2 3 2 ^ ^` |
| `-2 ^ 2` | `2 2 ^ neg` |

Postfix needs no parentheses and no precedence: the order of the tokens alone determines the order of the operations. The numbers and variables keep their order; only the operators move.

\newpage

## Part 2: shunting-yard

`toPostfix` reads the infix tokens one at a time, with a queue, the **output**, and a stack of **operators**, which also holds `(`.

For each token:

1. A number or a variable: add it to the output.
2. `(`: push it.
3. `)`: pop operators to the output until `(` is on top; pop the `(` and discard it. If the stack runs out first, the parentheses are unbalanced.
4. A binary operator `o`: while the top of the stack is an operator `p` (not `(`) that must be applied before `o`, pop `p` to the output. Then push `o`. `p` must be applied before `o` if
    - `p` has higher precedence than `o`, or
    - they have the same precedence and `o` is left-associative.
5. `neg`, a prefix operator: push it, without popping anything.
6. `=`: an error. (The REPL removes the `=` of a definition before calling `toPostfix`.)

At the end, pop everything left to the output. A `(` left on the stack means unbalanced parentheses.

Example: `1 - 2 * 3 + 4`, with the top of the stack on the left.

| token | output | stack |
|---|---|---|
| `1` | `1` | |
| `-` | `1` | `-` |
| `2` | `1 2` | `-` |
| `*` | `1 2` | `* -` |
| `3` | `1 2 3` | `* -` |
| `+` | `1 2 3 * -` | `+` |
| `4` | `1 2 3 * - 4` | `+` |
| end | `1 2 3 * - 4 +` | |

At `*`, `-` has lower precedence and stays. At `+`, `*` has higher precedence and is popped; then `-` has the same and is popped too, since `+` is left-associative: `1 - 2 + 4` is `(1 - 2) + 4`.

**Why `neg` pops nothing.** In `2 ^ -1`, when `neg` arrives `^` is on the stack, waiting for its right operand, which starts with this very `neg`. On the stack `neg` behaves like any other operator: its precedence decides when the operators after it pop it, so `-2 * 3` is `(-2) * 3` while `-2 ^ 2` is `-(2 ^ 2)`.

## Part 3: evaluation

`evaluate` reads the postfix tokens one at a time, with a stack of values, a `MyStack<Double>`:

- A number: push its value.
- An operator with arity 1: pop `x`, push `apply(x)`.
- An operator with arity 2: pop `b`, then pop `a`, push `apply(a, b)`. The right operand is on top.
- A variable: an error, the variable is undefined. (The REPL substitutes the variables before evaluating.)

At the end, the stack must hold exactly one value: the result. Check the size of the stack before popping, so that `1 +` throws `IllegalArgumentException`, not the `NoSuchElementException` of the stack.

Example: `1 2 3 * +`. The stack, top on the left: `1`; `2 1`; `3 2 1`; at `*`, pop 3 and 2, push 6: `6 1`; at `+`, pop 6 and 1, push 7: `7`.

## Part 4: variables

For `x = 2 * y`, the REPL converts `2 * y` to postfix, `2 y *`, and stores it with `define("x", body)`: definitions are stored as **expressions**, not as values. To compute an expression, the REPL calls `substitute`, then `evaluate`.

The definitions are on a stack, the newest on top; `define` pushes, without checking anything. The REPL computes the value of a definition before calling `define`, so every stored body uses only variables defined before it.

`substitute(postfix)`: one pass over the definitions, newest first. For each definition `name = body`, build a new queue: the current tokens, with every occurrence of the variable `name` replaced by the tokens of `body`. At the end of the pass, any variable left is undefined: throw `IllegalArgumentException`.

Example: `x = 1`, `y = x + 1`, `x = 10`, then `x * y`.

| definition | tokens |
|---|---|
| | `x y *` |
| `x = 10` | `10 y *` |
| `y = x 1 +` | `10 x 1 + *` |
| `x = 1` | `10 1 1 + *` |

The result is $10 \cdot 2 = 20$: `y` keeps the `x` it was defined with, and `x = x + 1` means "the previous `x`, plus 1".

`substitute` must not change the bodies: a definition can be used many times.

## Functions (optional)

The tokenizer recognizes `sqrt abs sin cos tan exp ln` (one argument) and `min max` (two), always followed by `(`. Arguments are separated by `,`. In postfix a function comes after its arguments, like an operator: `max(1 + 2, 3)` is `1 2 + 3 max`.

Three changes to the algorithm:

- A function: push it.
- `,`: pop operators to the output until `(` is on top, which stays. If the stack runs out first, the comma is not inside parentheses.
- `)`: after discarding the `(`, if a function is on top, pop it to the output.

If `evaluate` uses `arity()` and `apply`, it already handles functions. It also catches a wrong number of arguments: `max(1)` is `1 max`, which is missing an operand.

## Checking the syntax (optional)

Shunting-yard accepts some sequences of tokens that are not valid infix: `1 2 +` is converted to `1 2 +`, and evaluates to 3. Before converting, check that operands and operators alternate, with one boolean: whether an operand is expected.

- At the start an operand is expected; at the end, it must not be.
- When an operand is expected: a number or a variable is fine, and then an operator is expected; `(`, a function or `neg` is fine, and an operand is still expected. Anything else is an error.
- When an operator is expected: a binary operator or `,` is fine, and then an operand is expected; `)` is fine, and an operator is still expected. Anything else is an error.

## Questions

Answer these in your submitted file, a paragraph each.

1. Why does `MyArrayDeque` shrink when a quarter full, and not when half full? Describe a sequence of operations that would be slow with the second rule.
2. `substitute` makes one pass over the definitions, newest first. Why is one pass enough? What would go wrong going oldest first?
3. Nothing in the calculator uses `MyDeque` directly: every `MyArrayDeque` is used through `MyStack` or `MyQueue`. What do these two interfaces add, if `MyArrayDeque` already has every operation? Why does it still make sense to write a single class for both?
