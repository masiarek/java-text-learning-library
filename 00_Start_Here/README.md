# 00 — Start here

**Level:** 101 · read this first

This library is for someone who can already write Java and has been surprised by text: a name that came back with `?` in it, a comparison that failed on two strings that looked the same, a CSV whose first column stopped matching, a regex that worked in testing and not in Warsaw.

## What it assumes

That you know Java syntax. That you have met Unicode as a word. Nothing else — the encoding background each page needs is on that page.

## The four to read first

If you read nothing else, read these, in this order. They are the four that account for most real bugs:

1. [**A `char` is not a character**](../01_Char_and_String/a_char_is_not_a_character/README.md) — the 1995 decision everything else follows from.
2. [**Malformed input has two policies**](../02_Encodings/malformed_input/README.md) — why `new String(bytes)` destroys evidence.
3. [**Case is locale-sensitive**](../03_Locale/case_is_locale_sensitive/README.md) — how `toLowerCase()` breaks a feature in Turkey.
4. [**`split` has sharp edges**](../04_Regex/split_has_sharp_edges/README.md) — the one you will hit this week.

## How to read a page

Each has the same shape: a **one-line** summary, the idea in prose, a **measured** output block from a program in that page's `examples/` folder, and what to do instead. The output blocks are generated, never typed — if a page shows you a number, a real JVM produced it.

## Java's text scorecard, honestly

A summary of the whole library, so you know what you are walking into:

**Genuinely good**

- Unicode support has been there since day one, at a time when C had nothing and Python 2 was a decade from fixing it.
- `String` is immutable and thread-safe, which removes an entire category of bug that C and C++ still have.
- Since Java 18, the default charset is UTF-8 everywhere — the single biggest fix in the language's text history.
- `Files.readString` is strict about malformed input, `Normalizer` and `Collator` and `BreakIterator` are all in the JDK, and the ICU-derived locale data is excellent.
- Compact Strings (Java 9+) store Latin-1 text at one byte per character, so the UTF-16 model does not cost double the memory it used to.
- Since Java 9 the `+` operator compiles to an `invokedynamic` that builds a tailored, often single-allocation concatenation at link time — so straight-line concatenation is *faster* than a hand-written `StringBuilder`. See [Concatenation and performance](../06_Performance/README.md).

**Genuinely bad**

- `char` is 16 bits and named as if it were a character.
- `length()`, `charAt()`, `substring()` all work in storage units and none of them says so.
- The most reachable API for decoding bytes is the one that silently replaces corruption.
- Six or more `String`/`Format` methods depend on an ambient locale, and the locale-taking overload is always the longer one.
- `split` takes a regex and discards trailing empty fields.
- `\u` escapes are processed before lexing, which is a wart with a security face.
- Accumulating with `+=` in a loop is quadratic, and nothing in the language or the JIT will save you from it.

**The pattern:** almost every flaw is a *default* chosen for compatibility, with a correct alternative sitting right next to it, one argument longer. Java rarely makes the right thing impossible. It makes the wrong thing shorter.
