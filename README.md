# Java text — a learning library

**Everything Java gets wrong, right, and surprisingly, about text.** One idea per page. Every claim on every page is backed by a program that runs, and whose output is checked against a recorded answer key in CI — so nothing here is quoted from the Javadoc and hoped for.

Java's text model was designed in 1995, when Unicode fit in 16 bits. It hasn't since 1996. Most of what is confusing about Java strings follows from that one fact and the thirty-year compatibility promise built on top of it — plus a set of defaults chosen for a world where everyone's text was ASCII.

## Start here

[**00 — Start here**](00_Start_Here/README.md) — what this library assumes, and the four things to read first.

## The chapters

| | Chapter | What it covers |
|---|---|---|
| 01 | [`char` and `String`](01_Char_and_String/README.md) | why `length()` is not the length, and what to use instead |
| 02 | [Encodings](02_Encodings/README.md) | the default charset, malformed input, the BOM |
| 03 | [Locale](03_Locale/README.md) | the methods that change behaviour depending on where the JVM runs |
| 04 | [Regex and splitting](04_Regex/README.md) | ASCII-only defaults, and `split`'s three surprises |
| 05 | [The language itself](05_The_Language_Itself/README.md) | escapes that run before the compiler; running a file with no build tool |
| 06 | [Concatenation and performance](06_Performance/README.md) | why the loop is quadratic, and what `+` really compiles to |
| 07 | [Resources](07_Resources/README.md) | the JEPs, the specs, and the sibling libraries |

## Running the examples

You need a JDK, version 11 or later, and nothing else — no Maven, no Gradle, no IDE. Every example is a single file:

```bash
java 01_Char_and_String/length_is_three_numbers/examples/length_is_three_numbers_java.java
```

To run all of them and check every recorded output:

```bash
python3 tools/run_examples.py --check
```

Written against **OpenJDK 25**. Where a fact depends on the version, the page says which version.

## The one rule

No page hand-types what a program prints. A lesson marks the spot and the runner fills it from a real run — so an example that changes behaviour on a new JDK breaks the build instead of quietly making a page wrong. See [CONTRIBUTING.md](CONTRIBUTING.md).
