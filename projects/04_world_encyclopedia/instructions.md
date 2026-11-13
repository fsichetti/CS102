# World Encyclopedia

In this assignment you build a small search engine over a collection of country profiles. Given a folder of text files, it builds an **inverted index**: a map from each word to the set of documents containing it. It answers boolean queries such as `treaty AND (border OR trade)` ("documents containing 'treaty', and either 'border' or 'trade'").

The scaffold compiles as given, but tests fail until you fill in the `TODO`s.
You should submit the whole lab folder, so it's easier to test on our end.

## Checklist

For each item, the test sections in brackets check that functionality.

**Part 1: the index, in `search/Index.java`** [`index`]

- [ ] Constructor: builds the postings (word to set of document ids) and the term frequencies (document id to word to count) from a list of documents.
- [ ] `add(document)`, `remove(doc)`: add and remove one document after construction.
- [ ] `add(name, document)`, `idOf(name)`, and the `Index(names, documents)` constructor: the same, but letting a document be found later by name instead of only by id.
- [ ] `size()`, `documentsWith(word)`, `frequency(doc, word)`.
- [ ] `topWords(n)`: the `n` words appearing in the most documents.
- [ ] `topWordsIn(doc, n)`: the `n` most frequent words of one document.

**Part 2: set operations, in `search/Index.java`** [`setops`]

- [ ] `and(sets)`, `or(sets)`, `not(a)`.

Both parts are also exercised end to end by the `query` tests, through the given `Query.search` (below): a boolean query is only ever evaluated by calling `documentsWith`, `and`, `or` and `not`.

## Rules

- Every `Map` is a `HashMap`, every `Set` is a `HashSet`. For other interfaces you can use whatever you like.
- `documentsWith` returns a copy: modifying the result must never affect the index. `and`, `or` and `not` must not modify any set in their argument.
- `and` and `or` need at least one set in their argument, otherwise they throw `IllegalArgumentException`.
- `add` assigns ids in increasing order, starting from 0; an id is never reused, even once its document is removed. `remove` on an id that is not currently in the index throws `IllegalArgumentException`.
- `add(name, document)` lets `name` be claimed again later; `idOf(name)` always returns the most recent holder. `idOf` on a name with no current holder throws `IllegalArgumentException`. `remove` forgets a name only if the removed document was still its current holder; removing an older, already-shadowed holder of a name must not disturb the current one.
- You may add private methods and fields. Don't change anything else in the given files.

## Files

| File | Status |
|---|---|
| `search/Token.java` | given, complete |
| `search/Tokenizer.java` | given, complete |
| `search/Corpus.java` | given, complete: loads the documents, and normalizes their words |
| `search/Index.java` | Part 1 and Part 2 |
| `search/Query.java` | given, complete: parses and evaluates a boolean query (see Reference, below) |
| `Search.java` | given, complete: the REPL |
| `Tests.java` | given, complete |
| `data/corpus/` | 260 text files for the REPL to index: short profiles of territories around the world[^1] |
| `data/world.txt` | one more such summary (for "World" as a whole), kept out of `data/corpus/` so there is something to feed to `add` |

`Search` tracks document text itself, by id, in a map: `Corpus` only loads the initial folder, and has no place for documents added or removed afterward. Names are different: `Search` still keeps a display name per id (the exact file path, for printing), but which id a name like `switzerland` resolves to is `Index.idOf`'s job (Part 1), not `Search`'s.

[^1]: in the format of the CIA World Factbook (discontinued by the CIA in 2026), as republished by the independent, unofficial site theworldfactbook.org

`data/corpus/` is real data, not something you can eyeball to predict `search` or `topWords` results by hand. For that, point `index` at a folder of your own, two or three short `.txt` files with words you chose, where you can work out the expected answer yourself. `Tests.java` already checks correctness on its own small fixture; the REPL is for exploring behavior, not a substitute for it.

Test each part as soon as you finish it, running only its section. Compile and run from the `lab` folder, compiling again after every change:

```
javac Tests.java Search.java
java Search                       the REPL
java Tests index                  after Part 1
java Tests index setops           after Part 1 and 2
java Tests                        everything, before submitting
```

A test running longer than 5 seconds fails, and the rest of its section is skipped.

## The REPL

```
> index data/corpus
260 documents indexed
> search explorers AND settlers
solomon-islands.txt
tokelau.txt
wallis-and-futuna.txt
> search superpower
no matches
> add data/world.txt
added as document 260
> search superpower
data/world.txt
> top 5
after  162 document(s)
became  152 document(s)
independence  152 document(s)
first  143 document(s)
century  139 document(s)
> top switzerland 5
switzerland  4 occurrence(s)
confederation  3 occurrence(s)
un  3 occurrence(s)
country  2 occurrence(s)
international  2 occurrence(s)
> remove world
> top world 5
error: no such document: world
> stats
260 documents, 8032 distinct words, average 5.35 documents per word
```

Commands: `index <folder>`; `add <file>`, one more document, under a new id; `remove <doc>`, by id or name; `search <query>`; `top <n>`, the words in the most documents; `top <doc> <n>`, the most frequent words of one document, `<doc>` by id or name; `words <doc>`, every distinct word of a document, by id or name; `stats`; `help`; `quit`.

A `<doc>` that is a name (not parseable as an integer) is looked up with `Index.idOf`: the name a document was given, by `add`, with its folder and `.txt` stripped (so `data/corpus/switzerland.txt` becomes `switzerland`). `remove`-ing a document forgets its name along with it, exactly as `top world 5` does above: `world.txt` was the one document added under the name `world`, so after removing it, `idOf("world")` has nothing to return.

# Reference

## Part 1: the index

`Corpus.words(text)` is given: lowercase, split on anything that is not a letter or a digit. Use it for every document, so the index and the query language normalize words the same way.

**`add(document)`**: assigns the next id (a field, starting at 0, incremented after every call, never decremented), then, for each word of `document`:

- `postings`: word to the set of documents containing it. Add the new id to that word's set.
- `termFrequency`: the new id's map from word to count, built with `getOrDefault` or `merge` as in the `wordCounts` exercise from lecture.

The constructor is `add`, called once per document of its argument, in order: ids `0, 1, 2, ...` on an index built from a list are no different from the same documents added one at a time. `Index(names, documents)` is the same idea, pairing each document with a name: call `add(name, document)` for each pair.

**`add(name, document)`, `idOf(name)`**: two more fields, `idByName` (name to id) and `nameById` (id to name), both `HashMap`s. `add(name, document)` calls `add(document)` to do everything the one-argument version already does, then records the id both ways: `idByName.put(name, doc)`, `nameById.put(doc, name)`. `idOf(name)` is a single lookup in `idByName`; `IllegalArgumentException` if `name` is not a key.

Calling `add` twice with the same `name` is allowed, and not a bug: `idByName.put` simply overwrites the old id with the new one, so `idOf(name)` always returns the most recently added document under that name. The older document is still in the index, with its own id; it is just no longer reachable by `name`, only by that id. `remove` (below) has to respect this: it must only forget a name if the document being removed is still that name's current holder.

**`remove(doc)`**: the reverse of `add`. Removes `doc`'s entry from `termFrequency`; `IllegalArgumentException` if there is none. For every word in that entry, remove `doc` from `postings.get(word)`; if the set is now empty, remove the word from `postings` entirely, or it would sit there forever, empty and sad, for every word that ever appeared in a now-deleted document.

Then, the name bookkeeping: `nameById.remove(doc)` both removes and tells you the name `doc` was added under, or `null` if it was never named. If it had one, check whether `idByName` still maps that name to `doc` specifically, with `idByName.get(name)`, before removing that entry too. Skip the removal if `idByName` now maps the name to some other, newer id: that id is still alive and still owns the name, this `doc` was already just a shadowed, unreachable-by-name older holder of it.

`documentsWith(word)` returns `postings.getOrDefault(word, Set.of())`, copied into a new `HashSet`. `frequency(doc, word)` reads the inner map the same way, defaulting to 0 if either is missing. `size()` is the number of documents currently in `termFrequency`, which is also the set of ids currently in the index, used by `not` below.

**`topWords(n)` and `topWordsIn(doc, n)`**. A `Map` can be iterated three ways: `keySet()`, just the keys; `values()`, just the values; `entrySet()`, both together, as a collection of `Map.Entry` objects, one per key-value pair. These methods return `Set`s, which are `Iterable`. The key-value pair's type is `Map.Entry`, with methods `getKey()` and `getValue()`. Outside of a map, `Map.entry(k, v)` (notice the lowercase) builds a standalone one, with no map involved.

`STOPWORDS` is given: a `Set` of common English words (`the`, `and`, `of`, ...). Skip any word in it, in both methods below, otherwise they would always answer "the" and "and": a membership test against a `Set` is exactly what `STOPWORDS.contains(word)` is for, no `and`/`or`/`not` needed.

`topWords(n)` needs each word with its count together, so iterate `postings.entrySet()`. For each entry, if its key is not a stopword, its key is the word and `getValue().size()` is its document frequency (how many documents contain it); pair the two with `Map.entry(word, count)` and collect them into a `List`. Sort that list by count, descending, breaking ties alphabetically by word. Return its first `n` elements (fewer, if the vocabulary has fewer than `n` non-stopword words).

`topWordsIn(doc, n)` is the same idea, scoped to one document: iterate `termFrequency.getOrDefault(doc, Map.of()).entrySet()` instead of `postings.entrySet()`, where each entry's value is already the count you want (no `.size()`), then sort and truncate exactly as `topWords` does. (If you wrote a private helper for that sort-and-truncate step, this is where it pays off.) Fewer than `n` pairs if `doc` does not exist, or has fewer than `n` non-stopword words.

## Part 2: set operations

- `and(sets)`: the intersection of every set in `sets`. Start from a copy of the first set, then `retainAll` the rest. At least one set, else `IllegalArgumentException`.
- `or(sets)`: the union. Start from an empty set, then `addAll` each one. At least one set, else `IllegalArgumentException`.
- `not(a)`: every id currently in the index, with `a` removed. Not `0` to `size() - 1`: once a document has been removed, ids are no longer contiguous.

None of the three may modify a set in `sets` or `a` itself: always copy before `retainAll`, `addAll` or `removeAll`.

## Questions to think about (optional, not submitted)

1. `documentsWith` returns a copy of the stored set rather than the set itself. Suppose it returned the set itself instead. Describe a sequence of calls, using `and`, `or` or `not`, that would silently corrupt the index.
2. `Index.and` and `Index.or` take a `List` of any number of sets, not just two. It is the parser's job to group together multiple `and`'s and `or`'s. Why is the `List`-taking version a better match for the two operators than calling them repeatedly with two sets at a time?
3. `add` never reuses an id, even one freed by `remove`. Describe a scenario, involving a second part of a program (or a person) that has stored a document id after calling `search` or `add`, where reusing a removed id would cause a bug. Why does `postings` removing a word entirely once its set is empty matter for the same reason, and not just for memory?
4. `add("x", "alpha")` then `add("x", "beta")`: two documents, both once named `"x"`, but `idOf("x")` only ever returns one of them. Suppose `remove` forgot a name unconditionally, instead of checking it still belongs to the document being removed. Give a short sequence of `add` and `remove` calls, using the same name twice, where that unconditional version would make `idOf` throw for a name that should still resolve to a document still in the index.
5. `topWords` iterates `postings.entrySet()`. Describe what the method would have to look like instead if it iterated `postings.keySet()`. Why is that worse here, not just differently written?
6. `STOPWORDS` is a `HashSet`, and `topWords`/`topWordsIn` call `STOPWORDS.contains(word)` once per candidate word. Why does it matter that it is a `HashSet` and not, say, a `List<String>` checked in a loop? For a document with `w` distinct words, about how many of those calls happen in the worst case?
7. `Query.java` contains the parser for the tokenized queries. It is a recursive-descent parser, very different from the shunting-yard algorithm you saw in the last project, evaluating the expression in infix form directly and using recursion to enforce operator precedence. Try to understand how it works.