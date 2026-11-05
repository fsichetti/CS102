# Maps Lab

This lab contains five short Java programming challenges, all about using maps, plus one bonus challenge.
Your TA will work through Challenge 1 with you; then you can continue in pairs. Finish what you can in the room and complete the rest on your own.

## Maps in Java

A **map** is a collection of (key, value) pairs. Each key appears at most once and has exactly one value. You look a value up by its key, not by its position:

- the capital of a country, given the country;
- the record of a student, given the NetID;
- the number of times a word appears, given the word.

In Java, `Map<K, V>` is an interface: `K` is the type of the keys, `V` the type of the values. `HashMap<K, V>` is the class that implements it, the one you will use in this lab. How a `HashMap` works inside is the topic of the lectures; here we only use it.

```java
Map<String, String> capital = new HashMap<>();
capital.put("Italy", "Rome");
capital.put("Japan", "Tokyo");

capital.get("Italy");               // "Rome"
capital.get("Spain");               // null: "Spain" is not a key
capital.containsKey("Spain");       // false
capital.put("Italy", "Florence");   // replaces "Rome", and returns it
capital.size();                     // 2: "Italy" is still there only once
```

The methods you will need most often:

| method | |
|---|---|
| `put(k, v)` | maps `k` to `v`, replacing the old value if `k` was already there; returns the old value, or `null` |
| `get(k)` | the value of `k`, or `null` if `k` is not in the map |
| `getOrDefault(k, d)` | the value of `k`, or `d` if `k` is not in the map |
| `containsKey(k)` | is `k` in the map? |
| `remove(k)` | removes `k` and its value; returns the value, or `null` |
| `size()`, `isEmpty()` | |
| `keySet()`, `values()`, `entrySet()` | the keys, the values, and the pairs, to go through with a `for` loop |

Three things to keep in mind:

- **Keys and values are objects.** A map from characters to counts is a `Map<Character, Integer>`, not a `Map<char, int>`. Java converts between `char` and `Character`, and between `int` and `Integer`, automatically. This fails when there is no object to convert: if `c` is not in `counts`, then `int n = counts.get(c);` throws a `NullPointerException`.
- **A `HashMap` has no order.** It does not keep the keys in the order you added them, nor sorted. A `for` loop over `keySet()`, or printing the map, may list the pairs in any order.
- **`equals` compares the pairs.** Two maps are equal if they hold the same pairs, whatever order they are printed in.

The documentation of `Map` lists every method, with what it promises: <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html>. Keep it open while you work.

## How the lab is organized

As in the warmup lab, each challenge lives in its own folder, `ch1` through `ch6`. Every folder contains:

- a `Main.java` with a `main` method that reads an input and runs your solution on it, and a method stub marked `//TODO` that you need to complete;
- a `Test.java` that runs your solution on a list of test cases.

Compile and run them from inside the challenge folder:

```
javac Main.java
java Main
javac Test.java
java Test
```

The `Test.java` of Challenge 1 is filled in for you. For Challenges 2 to 6 it has a single case: add one line for each further case you want to check. Each challenge below ends with a list of cases worth testing, but do not forget some "normal" inputs as well.

Some of the tests compare maps. Look at what they print: the expected and the actual map may list their pairs in different orders, and the test still passes.

## Challenge 1 — `expand`

`Main.abbreviations()` returns a map from some common abbreviations to their meaning: `"brb"` to `"be right back"`, `"u"` to `"you"`, and a few more.

Write a method that, given a message whose words are separated by single spaces, replaces every word that is an abbreviation with its meaning, and leaves the other words as they are. For example:

- `expand("ok brb")` returns `"ok be right back"`
- `expand("see u later")` returns `"see you later"`

**Hint.** `message.split(" ")` gives you the words, and `String.join(" ", words)` puts them back together. Which of the methods above saves you an `if`?

**Testing.** `Test.java` already contains these cases:

- an empty message;
- a message with no abbreviations at all;
- the same abbreviation twice;
- an abbreviation in upper case, such as `"BRB"`;
- an abbreviation inside a longer word, such as `"brbx"`.

## Challenge 2 — `canWrite`

You want to write a note by cutting single letters out of a magazine and gluing them together. Write a method that, given the note and the letters available in the magazine, returns whether the note can be written. Each letter of the magazine can be used only once, and upper and lower case letters are different. The spaces in the note do not need to be cut out. For example:

- `canWrite("hello world", "dlrowolleh")` returns true
- `canWrite("aab", "baa")` returns true
- `canWrite("aa", "ab")` returns false: there is only one `a`
- `canWrite("Hi", "hi")` returns false: there is no `H`

**Hint.** Count how many times each letter is available, in a `Map<Character, Integer>`. Then use the letters up, one at a time.

**Testing.** Does your method work if you give it:

- an empty note?
- an empty set of letters?
- a note that uses a letter exactly as many times as it is available? One more time than that?
- a note with a letter that is not available at all?
- a note with spaces, and letters with no spaces?

## Challenge 3 — `byFirstLetter`

Write a method that, given an array of words, groups them by their first character: the result maps each character to the list of the words that start with it, in the order in which they appear in the array. Every word has at least one character. For example:

- `byFirstLetter(["apple", "bob", "avocado", "cherry", "banana"])` returns `{a=[apple, avocado], b=[bob, banana], c=[cherry]}`

**Hint.** The first time you meet a character there is no list for it yet: you need to create one. The list that `get` returns is the very same object that is stored in the map. Once you have added a word to it, do you need to `put` it back?

**Testing.** Does your method work if you give it:

- an empty array?
- words that all start with the same character?
- the same word twice?
- `"Apple"` and `"apple"`?

## Challenge 4 — `add`

A **sparse vector** is a vector in which almost all the entries are 0, for example a vector of a billion entries where only a dozen are not 0. Storing it in an array wastes a lot of memory. Instead, we store it as a `Map<Integer, Integer>` from the index of each non-zero entry to its value: the vector with 3 at index 0, 2 at index 5, and 0 everywhere else is the map `{0=3, 5=2}`. An index that is not in the map has value 0, so the map **never** stores a 0.

Write a method that returns the sum of two sparse vectors `u` and `v`, as a new sparse vector. As usual, the sum is computed index by index. For example:

- `add({0=3, 5=2}, {5=-2, 7=1})` returns `{0=3, 7=1}`: at index 5, `2 + (-2)` is 0, so 5 is not in the result

Your method must not modify `u` or `v`: the `check` method in `Test.java` verifies this for you.

**Hint 1.** The `HashMap` class has a constructor that copies a map. Look it up in the documentation.

**Hint 2.** To go through the pairs of a map, use `entrySet()`:

```java
for ( Map.Entry<Integer, Integer> e : v.entrySet() ) {
    // e.getKey() is the index, e.getValue() is the value
}
```

**Testing.** Does your method work if you give it:

- two empty vectors? One empty vector?
- two vectors with no index in common?
- two vectors that have the same index, with values that do not cancel out?
- a vector and its opposite, such as `{1=2, 3=4}` and `{1=-2, 3=-4}`?
- very large indices, such as 1,000,000,000?

The maps `{7=0}` and `{}` represent the same vector, but they are not equal as maps. Which one should your method return?

**To think about.** How does the cost of your method depend on the number of entries in `u` and `v`? And on the length of the vectors?

## Challenge 5 — `isomorphic`

Two strings are **isomorphic** if the characters of the first can be replaced to obtain the second, with these rules:

- every occurrence of a character is replaced by the same character;
- two different characters cannot be replaced by the same character;
- a character may be replaced by itself.

Write a method that, given two strings, returns whether they are isomorphic. For example:

- `isomorphic("egg", "add")` returns true: `e` becomes `a`, `g` becomes `d`
- `isomorphic("paper", "title")` returns true
- `isomorphic("foo", "bar")` returns false: the first `o` should become `a`, the second one `r`
- `isomorphic("bar", "foo")` returns false: `a` and `r` should both become `o`

**Hint.** A `Map<Character, Character>` can record which character replaces which. Does a single map catch both ways to fail? The `Map` interface has a `containsValue` method: what does the documentation say about its cost?

The values you get out of a map are objects, here `Character`s: compare them with `equals`, not with `==`, exactly as you do with `String`s.

**Testing.** Does your method work if you give it:

- two empty strings?
- two strings of different lengths?
- two identical strings?
- `"ab"` and `"aa"`? `"aa"` and `"ab"`?
- `"ab"` and `"ba"`?
- characters that are not plain ASCII, such as `"ééà"` and `"ççñ"`?

This last case is there for `==`. When a `char` is turned into a `Character`, Java reuses one shared object for each of the first 128 characters (the ASCII ones, which include all the letters and digits). So two `Character`s holding `'a'` are the same object, and `==` between them is true. For any other character, each conversion creates a new object, and `==` between two `Character`s holding `'é'` is false.

## Challenge 6 — `route` (bonus, not required)

You find a pile of plane tickets from a trip, in random order. Each ticket is a string such as `"JFK-LAX"`: the airport of departure, a dash, and the airport of arrival. The tickets belong to a single trip: each flight leaves from the airport where the previous one arrived, and no airport is visited twice.

Write a method that, given the tickets, returns the list of the airports of the trip, in the order they were visited. With no tickets, the list is empty. For example:

- `route(["SFO-ORD", "JFK-LAX", "LAX-SFO"])` returns `[JFK, LAX, SFO, ORD]`

**Hint.** A `Map<String, String>` from each airport to the next one lets you follow the trip, once you know where it starts. Where does it start? What is special about that airport?

**To think about.** What is the cost of your method, for `N` tickets? If you used `containsValue`, what would it be?

**Testing.** Does your method work if you give it:

- no tickets?
- a single ticket?
- tickets already in the order of the trip?
- tickets in the reverse order?

You do not need to test tickets that do not form a single trip, or a trip that visits an airport twice, because the assignment promises those will not happen.

## What to submit

Upload the whole lab folder to Brightspace, with the methods of Challenges 1 to 5 completed, and Challenge 6 if you did it.
