# `\w` is ASCII by default

**Level:** 201 · for anyone validating user input

**One line:** In Java, `\w`, `\d`, `\s` and `\b` mean the ASCII versions unless you pass `Pattern.UNICODE_CHARACTER_CLASS` — so a "word character" pattern run over `zażółć` matches `za`, reports success, and throws nothing away that anyone notices.

## Measured

<!-- output:w_is_ascii_by_default_java -->

```text
subject: zażółć gęślą 123

\w+  default                   [za][g][l][123]
\w+  UNICODE_CHARACTER_CLASS   [zażółć][gęślą][123]
\p{L}+  (always Unicode)       [zażółć][gęślą]

Note what the default did: it did not fail, it returned [za].
A validator built on \w+ accepts half a Polish word and calls it clean.

--- and the dot counts differently from length() ---
"🎯".matches(".")   = true   <- regex works in CODE POINTS
"🎯".matches("..")  = false  <- but String.length() is 2
```

<!-- /output -->

The failure mode is the dangerous kind. `\w+` did not fail to match, which would have been caught by the first test with a Polish name in it. It matched a **prefix**, and returned it, and every assertion of the form "did we find something?" passed.

## What each option means

| Pattern | Matches |
|---|---|
| `\w` | `[a-zA-Z0-9_]` — ASCII only |
| `\w` with `UNICODE_CHARACTER_CLASS` | letters, digits and connectors in any script |
| `\p{L}` | a Unicode letter, always — no flag needed |
| `\p{IsLatin}`, `\p{IsCyrillic}` | one script |

`Pattern.UNICODE_CHARACTER_CLASS` has an inline form, `(?U)`, which is useful when the pattern comes from configuration and you cannot reach the `compile` call. Note that it is *not* the same flag as `UNICODE_CASE` (`(?u)`), which only affects case-insensitive matching — the two are one letter apart and do different jobs.

## Why the default is what it is

Perl and POSIX defined `\w` as ASCII, Java copied it in 1999, and changing it later would have silently altered the behaviour of every existing pattern. Python 3 made the opposite choice — `\w` is Unicode-aware by default and `re.ASCII` is the opt-out — because Python 3 was allowed to break compatibility and Java was not. Neither default is indefensible; only one of them matches what a programmer writing `\w` in 2026 expects.

## The dot is not the same unit as `length()`

The last two lines of the output are worth their own note. Java's regex engine works in **code points**, so `.` matches an emoji as one character — while `String.length()` reports it as two. So `s.matches(".{1,10}")` and `s.length() <= 10` disagree about the same string, and both are documented, and neither is wrong.

## See also

- [`split` has sharp edges](../split_has_sharp_edges/README.md)
- [Length is three different numbers](../../01_Char_and_String/length_is_three_numbers/README.md)
- [The Unicode bug](https://masiarek.github.io/perl-learning-library/03_Regex/the_unicode_bug/index.html) — Perl's `\w` today: Unicode under `use v5.12` or later, storage-dependent without it, and `/a` as the opt-out
