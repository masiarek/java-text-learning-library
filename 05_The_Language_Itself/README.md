# 05 — The language itself

Two facts about Java source files, rather than about `String`. One is a genuine design wart with real consequences; the other is a modern convenience that makes this whole library possible without a build tool.

| Lesson | The one thing |
|---|---|
| [Unicode escapes run first](unicode_escapes_run_first/README.md) | a `\u` escape inside a comment is still an escape |
| [Source-file mode](source_file_mode/README.md) | `java Hello.java` — no javac, no build tool, no `.class` |
