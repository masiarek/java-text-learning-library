# Case is locale-sensitive

**Level:** 201 · for anyone whose users are not all in one country

**One line:** `"TITLE".toLowerCase()` is not `"title"` everywhere — in Turkish it is `tıtle`, with a dotless ı — so the no-argument overload turns a comparison into a function of where the server is.

## The Turkish I

Turkish has four I's: dotted and dotless, upper and lower. `I` lowercases to `ı`, and `i` uppercases to `İ`. Unicode encodes this correctly, and Java implements it correctly. The bug is not in the mapping; it is that the *default* mapping is chosen by the environment.

<!-- output:case_is_locale_sensitive_java -->

```text
default locale for this run : en_US

"TITLE".toLowerCase()        = title
"TITLE".toLowerCase(TURKISH) = tıtle   <- dotless i
"i".toUpperCase(TURKISH)     = İ    <- dotted capital I

So this comparison is false in Turkey and true everywhere else:
  "TITLE".toLowerCase().equals("title") = false
  ...using Locale.ROOT instead          = true

Case mapping can also change the LENGTH of a string:
  "ß".toUpperCase() = SS   (1 char in, 2 out)

Rule: comparing? use equalsIgnoreCase, or toLowerCase(Locale.ROOT).
      showing it to a person? use their locale, deliberately.
```

<!-- /output -->

## Why this is a real outage and not a curiosity

The classic failure is case-normalising something that is not human language: an HTTP header, a file extension, a database column name, a config key, an enum name, a country code. The code reads

```java
if (header.toLowerCase().equals("content-type")) { ... }
```

and it works on every developer machine, every CI runner and every environment in Europe and the US. Then the same JAR is deployed to a Turkish locale and the branch is never taken — because `"CONTENT-TYPE".toLowerCase()` is `"content-type"` with a dotless ı in the middle. Nothing throws. The feature just quietly stops existing.

## The rules

- **Comparing?** Use `equalsIgnoreCase`, or `toLowerCase(Locale.ROOT)`. `Locale.ROOT` means "locale-neutral", and it exists precisely for machine text.
- **Displaying to a person?** Pass *their* locale, deliberately.
- **Never** the no-argument overload. There is no case where "whatever locale the JVM happens to have" is the right answer; it is either machine text (ROOT) or human text (theirs).

Static analysis catches this: SpotBugs, Error Prone and IntelliJ all have an inspection for the locale-less overloads. It is worth turning on, because the code looks correct and always will.

## And case mapping changes length

`ß`.toUpperCase() is `SS` — one character in, two out. So `s.toUpperCase().length() == s.length()` is not an invariant, and any code that upper-cases in a fixed-width buffer is wrong. Greek final sigma (`ς` vs `σ`) is a second family of this: the correct lowercase of `Σ` depends on whether it ends a word, which is context, not a per-character mapping.

## See also

- [Format follows the locale](../format_follows_the_locale/README.md)
- [Normalization and equality](../normalization_and_equality/README.md)
- [`fc` is how to compare without case](https://masiarek.github.io/perl-learning-library/02_Unicode_Text/fc_for_caseless_comparison/index.html) — the opposite problem in Perl: one case mapping for every language, so Turkish rules never apply
