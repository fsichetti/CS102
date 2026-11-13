# Morse Code Lab

Morse code writes every character as a short sequence of dots and dashes: `E` is `.`, `T` is `-`, `S` is `...`, `O` is `---`.
From the 1840s on, it carried messages over telegraph wires and then by radio, as short and long pulses.

A program that reads and writes Morse code needs both directions:

- **encoding**, from a character to its code: an array indexed by the character is enough;
- **decoding**, from a code to its character: the keys are now strings. You decode in two ways, with a **binary tree**, where a dot goes left and a dash goes right, and with a **hash map** from codes to characters.

In this lab you write the tree and the map, and use them to decode a message.

1. The tree, to decode one character.
2. A hash map with linear probing, to decode one character.
3. Encoding and decoding whole messages.
4. (bonus) Quadratic probing.

## The format

`data/morse.txt` is the table: one character per line, a space, then its code. It contains the upper case letters, the digits, and some punctuation.

In a message, the codes of the characters of a word are separated by single spaces, and the words by ` / `:

```
SOS HELP   is   ... --- ... / .... . .-.. .--.
```

## The files

| | |
|---|---|
| `StringMap.java` | the interface of the map: what every method does. Do not modify |
| `LinearProbingMap.java` | Part 2: **you create this file** |
| `QuadraticProbingMap.java` | Bonus: **you create this file** |
| `MorseTree.java` | Part 1 |
| `Morse.java` | Part 3; also the program that decodes the message |
| `Test.java` | the tests |
| `data/` | the table and the message |

Compile and run everything from the top folder:

```
javac *.java
java Test
```

Everything compiles before you write anything: `Test` reports the classes you have not created yet.
The tests are grouped by part. A group that throws an exception, or runs for more than 5 seconds, fails, and `Test` moves on to the next group.
`java Test bonus` also tests the bonus.

## Part 1: the tree

Every node of the tree is reached from the root by a code: from the root, a dot goes left and a dash goes right.
The node reached by a code holds the character with that code. The top of the tree:

```
                         root
               .  /              \  -
                E                  T
             /     \            /     \
            I       A          N       M
           / \     / \        / \     / \
          S   U   R   W      D   K   G   O
```

`R` is `.-.`: left, right, left. The root is the empty code, and holds no character.

`MorseTree.java` gives you the class `Node`: a `Character letter`, `null` if no character has that code, and the two children.
The code that reads the table and calls `insert` for every line is given.

**`insert(letter, code)`** starts from the root and follows `code`, creating the missing nodes along the way; the last node gets `letter`.
It throws an `IllegalArgumentException` if `code` is empty, contains a symbol other than `.` and `-`, or is already the code of a character.

**`decode(code)`** follows `code` from the root and returns the character in the last node.
It throws an `IllegalArgumentException` if `code` contains another symbol, if it leaves the tree, or if it ends in a node with no character.

The lines of the table are not in the order of the tree: `insert` may have to create nodes whose character is inserted later, or never.
`..--` is not the code of any character, but `..--..` is `?`.

`java Morse tree` prints every node of your tree: its code, then its character.

- What is the height of the tree?
- How many nodes does `decode` visit for a code of length `k`? How does that compare with searching the table line by line?

## Part 2: the map

`StringMap` is the interface of a map from strings to strings.
Create the file `LinearProbingMap.java`, with a class `LinearProbingMap` that implements `StringMap` with **open addressing** and **linear probing**, as in lecture.

- **Constructors.** `LinearProbingMap()` has 8 cells; `LinearProbingMap(int capacity)` has `capacity` cells, and throws an `IllegalArgumentException` if `capacity < 1`.
- **Home cell.** The home cell of `key` is `Math.floorMod(key.hashCode(), M)`, where `M` is the number of cells. `hashCode()` may be negative, and then so is `key.hashCode() % M`: `floorMod` is always between `0` and `M - 1`.
- **Probing.** `put`, `get` and `containsKey` start from the home cell of the key and try the next cell, then the one after, wrapping around at the end of the array. Compare keys with `equals`, not `==`.
- **Rehashing.** After a `put` that adds a new key, if the map holds more than `M / 2` keys, it rehashes: a new table of `2M` cells, where the pairs are put back in the order of their cells in the old table.
- **`cells()`** shows the table, `_` for an empty cell. Useful to debug, and the tests use it.

Two arrays, `String[] keys` and `String[] values`, with `keys[i] == null` for an empty cell.

The hash code of a string of one character is the number of the character: `"A".hashCode()` is 65, and its home cell in 8 cells is 1.

```
new LinearProbingMap(): put "A", "I", "Q", "H"     home cells 1, 1, 1, 0
cells():  H A I Q _ _ _ _
put "G", home cell 7: 5 keys > 8 / 2, rehash to 16 cells
cells():  _ A Q _ _ _ _ G H I _ _ _ _ _ _
```

Before writing any code, check the last line by hand.

Once your map passes the tests, `java Morse` prints the cells of the map built from the table, with the codes as keys.

- Encoding uses an array indexed by the character. Why can we not do the same, directly, with the codes?

## Part 3: encode and decode

The constructor of `Morse` is given. It reads the table, and adds every character to three places:

- `codes`, an array of 128 cells: `codes[c]` is the code of the character `c`, or `null`;
- the tree;
- the map, from the code to the character, as a string of length 1.

In `Morse.java`, write:

- **`encode(text)`**: `text` has words separated by single spaces; the result is the message in Morse code, in the format above. Lower case letters are encoded as upper case ones. It throws an `IllegalArgumentException` if a character has no code, including every character `>= 128`. `encode("")` is `""`.
- **`decode(morse)`**: the opposite, decoding every character with the tree. The result is in upper case, with words separated by single spaces. It throws an `IllegalArgumentException` if one of the codes is not the code of a character. `decode("")` is `""`.
- **`decodeWithMap(morse)`**: the same, with the map. Write the loop over the words and the codes only once, in a private method.

Build the result in a `StringBuilder`, not with `+=` on a `String`: the tests encode a text of 300,000 characters. Why would `+=` be too slow?

Then run

```
java Morse
```

It decodes `data/message.txt`: the farewell the French Navy broadcast on January 31, 1997, when it stopped using Morse code, translated into English.
After the message, type a line of text to encode it, or a line of Morse code to decode it.

- On the radio, a code arrives one symbol at a time. What can the tree do with it before the code is complete? What can the map do?

## Bonus: quadratic probing

Create `QuadraticProbingMap.java`: the same as `LinearProbingMap`, except for three things.

- **Probing.** From the home cell `h`, try the cells `h + 1`, `h + 4`, `h + 9`, ..., `h + j²`, ..., all `mod M`.
- **Constructors.** `M` is always prime: `QuadraticProbingMap()` has 7 cells, and `QuadraticProbingMap(int capacity)` throws an `IllegalArgumentException` if `capacity` is not prime.
- **Rehashing.** The new table has the smallest prime number of cells that is `>= 2M`.

```
new QuadraticProbingMap(11): put "B", "M", "X", "c"     66, 77, 88, 99: home cell 0 for all
cells():  B M _ _ X _ _ _ _ c _
```

- With `M = 16`, which cells does the probing from cell 0 try? Why must `M` be prime?
- Why must the table stay at most half full?

`java Test bonus` tests it, and `java Morse quadratic` uses it.

## What to submit

Upload the whole lab folder to Brightspace, with `MorseTree.java` and `Morse.java` completed and your `LinearProbingMap.java`.
Include `QuadraticProbingMap.java` if you did the bonus.
