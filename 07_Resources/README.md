# 07 — Resources

## The primary sources

Everything in this library is checkable against these. Where a page makes a claim about *why*, it links the JEP or the spec section rather than paraphrasing it.

- [**JEP 400: UTF-8 by Default**](https://openjdk.org/jeps/400) — the Java 18 change, and an unusually clear write-up of the problem it solved.
- [**JEP 330: Launch Single-File Source-Code Programs**](https://openjdk.org/jeps/330) — why `java Hello.java` works.
- [**JEP 280: Indify String Concatenation**](https://openjdk.org/jeps/280) — why `+` is no longer a `StringBuilder`.
- [**JEP 254: Compact Strings**](https://openjdk.org/jeps/254) — how Java 9 stopped paying two bytes per Latin-1 character.
- [**JLS §3.3, Unicode Escapes**](https://docs.oracle.com/javase/specs/jls/se25/html/jls-3.html#jls-3.3) — the rule behind the comment that executes.
- [**`String` API docs**](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/String.html) — worth reading the class-level notes on the "Unicode Character Representations" section specifically.
- [**The Unicode Standard**](https://www.unicode.org/versions/latest/) — for normalization forms, grapheme cluster boundaries, and case mapping.

## Sibling libraries

Same house style, same answer-key contract:

- [**Encodings**](https://masiarek.github.io/encodings-learning-library/) — bits, bytes, code points and UTF-8 from the bottom up. **Read this one first if the words "code point" are new**; this library assumes them.
- [**Python**](https://masiarek.github.io/python-learning-library/) — `str` vs `bytes`, the same boundary problems in a language that solved them differently.
- [**Rust**](https://masiarek.github.io/rust-learning-library/) — `String` / `&str` / `char`, where `char` really is a code point and the compiler enforces it.

The three-way comparison is the point of having all of them: Java stores UTF-16 and calls a storage unit a `char`, Python stores code points and calls the count a `len`, Rust stores UTF-8 and refuses to index by character at all. Each choice buys something and costs something.

## Tools worth having

- **Error Prone** and **SpotBugs** both catch the locale-less `toLowerCase()` / `String.format` overloads. This is the single highest-value static check for text bugs in Java.
- `hexdump -C` / `xxd` — the only reliable way to see a BOM, a stray CR, or a `U+FFFD` for what it is.
- **[JMH](https://github.com/openjdk/jmh)** — the OpenJDK's own benchmark harness. The only honest way to get a number out of a JVM; `System.nanoTime()` in a loop measures the JIT's warmup schedule as much as your code.
- `javap -c -p` — reads the bytecode. The only way to see what `+` actually compiled to.
- `java -XshowSettings:properties -version` — prints every property this library talks about, without writing a program.
