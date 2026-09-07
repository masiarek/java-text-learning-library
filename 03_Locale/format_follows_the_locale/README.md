# Format follows the locale

**Level:** 201 · for anyone whose users are not all in one country

**One line:** `String.format("%.2f", 3.5)` produces `3.50` in the US and `3,50` in Poland, Germany and most of Europe — so a number formatted without a locale and then parsed, logged, or written to a file is a bug that only appears on someone else's machine.

## Measured

<!-- output:format_follows_the_locale_java -->

```text
default locale for this run : en_US

Locale.ROOT            1,234.50
en-US                  1,234.50
pl-PL                  1 234,50
de-DE                  1.234,50

Now feed the Polish rendering back to a parser that expects a dot:
  Double.parseDouble("1234,50") throws NumberFormatException

Rule: Locale.ROOT for machines, a real locale for people.
      Never the no-argument overload for either.
```

<!-- /output -->

The last block is the whole problem in two lines: a number rendered by the default `String.format` and then handed to `Double.parseDouble`, which is *always* locale-neutral. Format is locale-sensitive; parse is not. They are not inverses of each other, and the asymmetry is invisible until the program runs somewhere with a comma.

## Where it shows up

- A `%,.2f` amount written into a CSV that a downstream job parses.
- A number interpolated into a JSON body or a URL query. JSON's grammar requires a dot; a Polish JVM writes a comma and produces invalid JSON.
- A `toString()` on a `double` inside a log line that a metrics pipeline scrapes.
- A file name or a cache key built from a formatted number.

The same applies to dates. `SimpleDateFormat` and `DateTimeFormatter` both take a locale, and a pattern like `MMM` is `Jan` in English, `sty` in Polish, and `janv.` in French.

## The rule

**`Locale.ROOT` for machines, an explicit locale for people, never the default.**

For anything that will be read back by a program, prefer a formatter that has no locale at all:

```java
String s = Double.toString(amount);          // always a dot
String s = new BigDecimal("1234.50").toPlainString();
```

`BigDecimal` is the right type for money anyway, for reasons that have nothing to do with locale — but it also sidesteps this entirely.

## See also

- [Case is locale-sensitive](../case_is_locale_sensitive/README.md)
- [The default charset](../../02_Encodings/the_default_charset/README.md) — the other thing the environment used to decide for you
