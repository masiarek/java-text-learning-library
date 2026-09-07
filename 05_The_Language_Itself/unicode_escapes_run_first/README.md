# Unicode escapes run first

**Level:** 301 · a genuine wart, worth knowing once

**One line:** Java translates `\uXXXX` escapes **before** it decides what is a comment, a string, or a line — so an escape inside a comment is still an escape, and `\u000A` inside a comment ends the comment and executes whatever follows.

## Code that runs from inside a comment

<!-- output:unicode_escapes_run_first_java -->

```text
  >> I RAN, FROM INSIDE A COMMENT
  >> the ordinary line
```

<!-- /output -->

The source of that program:

<!-- source:unicode_escapes_run_first_java -->

```java
// A backslash-u escape is translated BEFORE the compiler tokenises the file — before
// it knows what a comment is, or a string, or a line. So an escape inside a
// comment is still an escape, and U+000A is a newline, which ends the comment.
//
// The line below is a comment. Watch what it does anyway.
public class Main {
    public static void main(String[] args) {
        // this text is inside a comment \u000A System.out.println("  >> I RAN, FROM INSIDE A COMMENT");
        System.out.println("  >> the ordinary line");
    }
}
```

<!-- /source -->

There is one statement on that line and it is commented out. It ran anyway, because by the time the compiler looked for comments, the `
` had already become a real newline and the "comment" had already ended.

This is specified behaviour — [JLS §3.3](https://docs.oracle.com/javase/specs/jls/se25/html/jls-3.html#jls-3.3) — and it is the earliest step in translation, before lexing. It exists so that a Java source file can be written in pure ASCII and still contain any character, on any system, including systems that predate reliable Unicode file handling. That was a real problem in 1995.

## The same rule, from its unfriendly side

Because the escape is recognised **anywhere**, a `\u` that is not followed by four hex digits is a *compile error* — even in a comment, where nothing should be able to break:

<!-- source:unicode_escape_in_a_path_java -->

```java
// The same rule, seen from its unfriendly side: a Windows path in a COMMENT
// stops the file from compiling, because the escape begins wherever it
// appears and "sers" is not four hex digits.
public class Main {
    public static void main(String[] args) {
        // the log file is at C:\users\adam\notes.txt
        System.out.println("this line is never reached");
    }
}
```

<!-- /source -->

<!-- output:unicode_escape_in_a_path_java -->

```text
unicode_escape_in_a_path_java.java:6: error: illegal unicode escape
        // the log file is at C:\users\adam\notes.txt
                                  ^
1 error
error: compilation failed
[exit status: 1]
```

<!-- /output -->

A Windows path, in a comment, refuses to compile. `\users` is `\u` followed by `sers`, and `s` is not a hex digit. The error points at a line that is not code and does not appear to contain anything at all.

## What to do about it

Almost nothing, most of the time — but know the shape of it, because when it happens the error message is baffling and searching for it is hard.

- Never write `\u` in a comment. Use "backslash-u", or double it: `\\u`.
- Be suspicious of pasted Windows paths in comments.
- Be aware that `\u0022` is a `"` and can therefore terminate a string literal from inside; this is a known trick for smuggling code past a human reviewer who is reading for meaning rather than for escapes.

The last point is why this is worth a page rather than a footnote: it is the rare Java wart with a **security** face. Source that looks inert can execute, and a code review that reads comments as comments will miss it. It is also why some style checkers flag any `\u` in a source file outside a string literal.

## See also

- [Source-file mode](../source_file_mode/README.md)
