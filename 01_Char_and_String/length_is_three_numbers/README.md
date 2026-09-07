# Length is three different numbers

**Level:** 101 · start here

**One line:** "How long is this string?" has three correct answers in Java — UTF-16 units, code points, and grapheme clusters — and `length()` returns the one that is almost never the one you meant.

## The three questions

They are genuinely different questions, and each has a real use:

- **`length()` — UTF-16 code units.** How much memory, how big a `char[]`, what index `substring` wants. A storage number.
- **`codePointCount()` — Unicode characters.** How many entries from the Unicode table. The number a specification means by "character".
- **Grapheme clusters** — how many things a *reader* would count. `é` written as `e` + a combining accent is one. A family emoji built from four people and three joiners is one.

## Measured

<!-- output:length_is_three_numbers_java -->

```text
string         length()   codePoints  graphemes
------------------------------------------------
hello                 5            5          5
zażółć                6            6          6
🎯                    2            1          1
👨‍👩‍👧              8            5          1
café                 5            5          4

length()    counts UTF-16 code units  — storage
codePoints  counts Unicode characters — the table
graphemes   counts what a reader calls a character — the screen
```

<!-- /output -->

Read the last two rows. The family emoji is **8** by `length()`, **5** by code points, and **1** to a human being. `café` written in decomposed form is **5** and **5** and **4**. No single number is wrong; they answer different questions.

## Where Java leaves you

There is no `String` method for the third column. Grapheme segmentation lives in `java.text.BreakIterator`, which is a 1996-era API with a cursor and a `DONE` sentinel — you write a loop, as the example does. It is correct and it is in the JDK, but nothing about `String`'s own surface suggests you need it.

That gap is why "truncate this to 20 characters for the preview" is a bug factory: `substring(0, 20)` is a *storage* operation, and it will split a surrogate pair or orphan a combining accent without a word.

## Beside Python

Python 3 stores code points, not UTF-16 units, so its `len()` answers the middle column and the first column does not exist for a user to trip over:

<!-- output:length_is_three_numbers_py -->

```text
string            len()   codePoints
------------------------------------
hello                 5            5
zażółć                6            6
🎯                     1            1
👨‍👩‍👧                 5            5
café                 5            5

len() IS the code point count — Python never shows you UTF-16 units.
Grapheme clusters need a third-party library (regex, grapheme).
```

<!-- /output -->

Worth being fair about: Python removes one of the three traps, not all three. Grapheme clusters need a third-party library there too. What Python does not have is a `length()` that silently means something else.

## See also

- [A `char` is not a character](../a_char_is_not_a_character/README.md)
- [Reverse and substring cut characters in half](../reverse_and_substring/README.md)
- [Normalization and equality](../../03_Locale/normalization_and_equality/README.md) — why `café` had two spellings above
