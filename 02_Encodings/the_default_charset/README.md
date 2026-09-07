# The default charset

**Level:** 201 · for anyone who has met mojibake

**One line:** Since Java 18 the default charset is UTF-8 on every platform, which ended a twenty-year class of bug — but the JVM still knows your locale, reports it as `native.encoding`, and the two are now allowed to disagree.

## What JEP 400 changed

Before Java 18, `Charset.defaultCharset()` was derived from the operating system's locale. The same program, the same file, the same JVM version produced different text on a Polish Windows box (`windows-1250`), a US Mac (`UTF-8`), and a Linux server started by cron with no locale (`US-ASCII`). Code that used `new String(bytes)` or `new FileReader(f)` — the overloads with no charset argument — was silently non-portable, and the symptom appeared in production rather than on the developer's machine.

[JEP 400](https://openjdk.org/jeps/400) made UTF-8 the default everywhere, in Java 18. This is the single biggest improvement to Java text handling in the language's history, and it is worth being clear that Java got there *late*: Python 3 had made the same decision a decade earlier.

## What it did not change

<!-- output:the_default_charset_java -->

```text
Charset.defaultCharset() = UTF-8
file.encoding            = UTF-8
native.encoding          = US-ASCII
stdout.encoding          = UTF-8
Locale.getDefault()      = en_US

This run is under LC_ALL=C. file.encoding is UTF-8 anyway;
native.encoding followed the locale. Since Java 18 those two
can disagree, and before Java 18 they could not.
```

<!-- /output -->

That run was under `LC_ALL=C`. `file.encoding` is UTF-8 anyway — that is JEP 400 working. `native.encoding` reported `US-ASCII`, because it is a *new* property whose whole job is to tell you what the environment says, now that `file.encoding` no longer does.

Before Java 18 those two could not disagree. Now they can, and each answers a different question:

| Property | Question it answers |
|---|---|
| `file.encoding` | what does Java use when you don't say? — **UTF-8** |
| `native.encoding` | what does the host environment claim? — locale-derived |
| `stdout.encoding` | what will `System.out` encode to? — console-derived |

## The trap that is left, and it bit this repo

`stdout.encoding` follows the console, not `file.encoding`. In a terminal or a CI job with `LC_ALL=C`, `System.out` encodes to US-ASCII — and Java **transliterates** anything it cannot encode to a literal `?` rather than failing. Your data is fine; your output is not, and nothing says so.

The first run of this library's own test suite recorded `za????` as the correct answer for `zażółć`. The fix is in [`tools/run_examples.py`](../../tools/run_examples.py): every example is launched with `-Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8`.

## Two flags worth knowing

- `-Dfile.encoding=COMPAT` restores the pre-18, locale-derived behaviour. It exists for old code that depended on it, and it is the honest way to reproduce a legacy bug.
- `-Dfile.encoding=ISO-8859-1` and friends still *work* on Java 25, though JEP 400 documents only `UTF-8` and `COMPAT` as supported values. Measured, not assumed — but do not build on it.

## See also

- [Malformed input has two policies](../malformed_input/README.md)
- [The BOM is not stripped](../the_bom_is_not_stripped/README.md)
