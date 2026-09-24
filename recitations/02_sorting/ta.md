# Sorting Lab: TA notes

Students implement all sorting algorithms in `sorting/`, test them with `Test`, then compare them with `Benchmark`. Everything else is given; they don't need to read it (make it clear so they don't waste time reading complicated classes they should not touch anyway).

After explaining the lab's goal give them ~5 min at the start to write down their predictions (describe the four inputs, students write down the best algorithm they expect on each; not submitted).

The lab will be graded on code correctness (i.e. their code compiles and runs, and the sorting algorithms are correct) and quality of their observations (i.e. they write things that are consistent). They are **not** penalized if their implementation is not the most efficient one: so they can expect different results. Better make it clear at the start. Some complexities on some input distributions may even change depending on how they implement their algorithms (e.g. bubble sort on sorted input if they stop after a pass with no swaps, or quicksort depending on partition).


A few details for you:
- `Benchmark` runs on a separate thread with a larger stack (256 MB; the reference solution needs a few MB), because a degenerate quicksort recurses once per element. If a student gets a `StackOverflowError`, they likely did something wrong.
- Students submit the whole lab folder, with `observations.txt` filled in. Their answers should quote their own numbers and explain the result, not just name the complexity.
- We set operations = comparisons + array writes (including writes to merge sort's helper array). A sort that uses `a[i] < a[j]` or `a[i] = v` instead of `less`/`set` still passes `Test` but reports too few operations, unless the student increments `operations` by hand (0 operations: `Benchmark` skips it).