# `split` has sharp edges

**Level:** 101 · you will hit this in your first week

**One line:** `String.split()` takes a **regular expression**, not a separator, and it throws away trailing empty fields — so `"a,b,,".split(",")` gives you two fields and `"1.2.3".split(".")` gives you none.

## Measured

<!-- output:split_has_sharp_edges_java -->

```text
--- trailing empty fields vanish ---
"a,b,,".split(",")      = [a, b]   (2 fields, not 4)
"a,b,,".split(",", -1)  = [a, b, , ]   (the limit argument keeps them)

--- an empty string does NOT give an empty array ---
"".split(",").length    = 1   (one element, the empty string)

--- the argument is a regex ---
"1.2.3".split(".")      = []   <- '.' matches everything
"1.2.3".split("\\.")     = [1, 2, 3]
Pattern.quote is the safe form for a literal separator:
"1.2.3".split(quote(".")) = [1, 2, 3]
```

<!-- /output -->

## The three surprises, in the order they bite

**1. `"1.2.3".split(".")` returns an empty array.** `.` is a regex metacharacter meaning "any character", so every character is a separator and every field is empty — and then rule 2 deletes them all. This is the one that gets written into a version-number parser and ships.

**2. Trailing empty fields are discarded.** `"a,b,,"` is four CSV fields, the last two empty. `split(",")` returns two. If you are reading a CSV row into fixed positions, the row with an empty last column silently becomes a shorter array, and the failure is an `ArrayIndexOutOfBoundsException` several lines later — or worse, no exception and a shifted value. `split(",", -1)` keeps them; the limit argument is the fix and it is not optional in real code.

**3. `"".split(",")` has length 1, not 0.** An empty input gives an array containing one empty string. So `split(...).length` is never zero, and a loop over "the fields of this line" processes one phantom field for every blank line in the file.

## What to use instead

```java
String[] parts = s.split(Pattern.quote("."), -1);   // literal separator, keep empties
```

`Pattern.quote` is the general answer whenever the separator is data rather than a pattern you wrote. For a fixed literal, escaping by hand (`"\\."`) is fine and clearer.

And for CSV specifically: do not use `split` at all. A real CSV field can contain a quoted comma, a quoted newline, and a doubled quote, and no regular expression handles the general case. Use a CSV library. The rule of thumb is that `split` is right for data *you* generated with a separator you chose, and wrong for a format someone else defined.

## See also

- [`\w` is ASCII by default](../w_is_ascii_by_default/README.md)
