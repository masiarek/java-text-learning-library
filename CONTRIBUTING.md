# Contributing

## The one rule

**No page hand-types what a program prints.**

A lesson marks the spot:

```
<!-- output:my_lesson_java -->
<!-- /output -->
```

and [`tools/run_examples.py`](tools/run_examples.py) fills it from a real run of `my_lesson/examples/my_lesson_java.java`. Inside the markers is generated; outside is yours. There is a second kind, `source:`, which pastes the program itself, for the pages where the code *is* the lesson.

This is not tidiness. A hand-pasted output block is a claim that was true on the day someone ran it, and it silently stops being true when a JDK changes behaviour. A generated one breaks the build instead.

## Adding a lesson

1. `mkdir -p NN_Chapter/my_lesson/examples`
2. Write `examples/my_lesson_java.java`. Single file, no dependencies, `public class Main` is fine — the file name does not have to match. Stems must be unique repo-wide; the convention is a `_java` / `_py` suffix.
3. Write `README.md` with a `**Level:**` line, a `**One line:**` summary, and an `output:` block.
4. `python3 tools/run_examples.py --update` to record the answer key.
5. `python3 tools/run_examples.py --check` to confirm it is reproducible.

## Why the environment is pinned

Examples run under `LC_ALL=C` with `-Duser.language=en -Duser.country=US -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8`. All four are load-bearing, and this repo learned each of them the hard way:

- `Locale.getDefault()` comes from the OS on macOS, not from the environment, so without the two locale flags the answer key depends on whose machine recorded it.
- `System.out` follows the *console* encoding, not `file.encoding`. Under `LC_ALL=C` that is US-ASCII, and Java transliterates anything it cannot encode to a literal `?` **with no error**. The first run of this library recorded `za????` as the correct answer for `zażółć`.

A lesson whose subject is the locale or the charset overrides this inside the program, in view of the reader — never by asking the runner for an exception.

## Examples that fail on purpose

Some lessons are about code that does not compile. The runner records combined stdout and stderr and appends `[exit status: N]` when the exit code is non-zero, so "this is a compile error" is an answer key rather than a claim.

## Prose conventions

- **Don't hard-wrap paragraphs.** One paragraph, one line.
- Link a folder by naming its `README.md` — `[label](some_folder/README.md)`, never `[label](some_folder/)`. MkDocs does not rewrite the bare form and the published link 404s.
- Level tags are `**Level:** <rung> · <audience>` where the rung is 101 / 201 / 301.
- Be fair to Java. The point is to be *accurate*, which means saying plainly where Java is good — it is, in several places — and where a sibling language made a better choice.
