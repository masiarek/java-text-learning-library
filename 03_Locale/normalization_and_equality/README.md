# Normalization and equality

**Level:** 201 · for anyone storing text other people typed

**One line:** `café` can be written two ways in Unicode — one code point for `é`, or `e` plus a combining accent — and they print identically, compare unequal, and hash differently, so a `HashMap` keeps them as two separate keys.

## Measured

<!-- output:normalization_and_equality_java -->

```text
composed   = café   length 4
decomposed = café   length 5
they print the same? look above — yes

equals()          = false
compareTo() == 0  = false
hashCode equal    = false   <- so a HashMap keeps them as two different keys

after Normalizer.normalize(.., NFC):
  equals()        = true

Collator (locale-aware comparison, ignores the difference):
  compare() == 0  = true

Rule: normalise at the boundary — when text enters your program —
      not at the moment you compare it.
```

<!-- /output -->

Nothing here is a Java bug. `equals()` compares code units and the code units genuinely differ; that is Unicode's design, and every language has it. What Java gives you is `java.text.Normalizer`, and what it does not give you is any hint that you need it.

## Where the two forms come from

They are not exotic. They arrive from real places, in the same system:

- **macOS filesystems** hand back decomposed (NFD) filenames. Linux and Windows hand back composed (NFC). A file synced between them has two spellings of the same name.
- **Web forms** submit whatever the user's input method produced. iOS and Android keyboards differ.
- **Copy-paste** from a PDF, Word, or a database export preserves whatever that tool used.

So a user registers as `café` from a Mac, logs in as `café` from a phone, and is told the account does not exist.

## The four forms, briefly

| Form | What it does |
|---|---|
| **NFC** | compose — use the single code point where one exists. The web's default; what you almost always want. |
| **NFD** | decompose — always base letter + marks |
| **NFKC** / **NFKD** | as above, plus *compatibility* folding: `ﬁ` → `fi`, `①` → `1`, full-width → ASCII |

The K forms are lossy and destroy real distinctions. They are right for a search index or a fuzzy match, and wrong for anything you will store and hand back.

## The rule

**Normalize at the boundary, once — when text enters the program — not at the moment you compare it.**

```java
String clean = Normalizer.normalize(input, Normalizer.Form.NFC);
```

Normalizing at comparison time means every comparison has to remember, and one that forgets is a bug you will not find by reading the diff. Normalizing at entry means the invariant holds everywhere after it.

For *comparing* rather than storing — sorting a list for a person to read, or matching case-insensitively across accents — `java.text.Collator` is the locale-aware tool, and as the output shows, it already treats the two forms as equal.

## See also

- [The BOM is not stripped](../../02_Encodings/the_bom_is_not_stripped/README.md) — the other invisible-character bug
- [Length is three different numbers](../../01_Char_and_String/length_is_three_numbers/README.md)
- [`length` counts code points](https://masiarek.github.io/perl-learning-library/02_Unicode_Text/length_counts_code_points/index.html) — the same two spellings of `café` in Perl, and `Unicode::Normalize`
