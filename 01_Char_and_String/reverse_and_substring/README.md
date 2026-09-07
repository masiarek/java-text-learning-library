# Reverse and substring cut characters in half

**Level:** 201 · once the model is clear

**One line:** `StringBuilder.reverse()` is careful about surrogate pairs and careless about everything else, and `substring()` is careful about nothing — neither throws, so the damage shows up later, in someone else's data.

## Two different kinds of care

`StringBuilder.reverse()` is *documented* to keep surrogate pairs in order, and it does. So an emoji survives a reverse. That is real work someone did deliberately, and it is worth knowing about — but it protects the UTF-16 layer only. A combining accent belongs to the character *before* it; reverse the sequence and the accent lands on a different letter.

<!-- output:reverse_and_substring_java -->

```text
hello        -> olleh
🎯 ok        -> ko 🎯
café        -> ́efac
👨‍👩‍👧     -> 👧‍👩‍👨

substring(0, 1) of a 2-unit emoji:
  result length = 1, isSurrogate = true
  no exception was thrown — Java let you cut a character in half
```

<!-- /output -->

Look at what happened to each input. The emoji came back intact. `café` came back with its accent moved onto the wrong letter. The ZWJ family came apart into its members. One method, three levels of correctness, no warning at any of them.

## `substring` will cut a character in half

The last block is the one to remember. `"🎯".substring(0, 1)` is a legal call that returns a legal `String` containing a lone high surrogate. No exception, no flag. It is text that cannot be encoded to UTF-8, and the failure surfaces whenever it is written to a file, a socket, or a database — a long way from the line that caused it.

## The rule

Index arithmetic on a `String` is safe only when you already know the content is ASCII. When you don't:

```java
// truncate to N *characters*, not N code units
String cut = s.codePoints().limit(n)
              .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
              .toString();
```

And when the number that matters is what a reader sees — a preview, a column width, a character limit shown in a UI — the unit is the grapheme cluster, so it is `BreakIterator` again, not arithmetic.

## See also

- [Length is three different numbers](../length_is_three_numbers/README.md)
- [A `char` is not a character](../a_char_is_not_a_character/README.md)
