# Source-file mode

**Level:** 101 · the reason this library has no build tool

**One line:** Since Java 11, `java Hello.java` compiles and runs a single file in memory — no `javac` step, no `.class` file, and, unlike `javac`, no requirement that the public class name match the file name.

## Measured

<!-- output:source_file_mode_java -->

```text
file name  : source_file_mode_java.java
class name : Main
run with   : java source_file_mode_java.java

Java feature release = 25

javac on this same file says:
  error: class Main is public, should be declared in a file named Main.java

Two tools, one file, opposite answers. The launcher relaxed the rule;
the compiler never did.
```

<!-- /output -->

That file is named `source_file_mode_java.java` and declares `public class Main`. `javac` rejects the identical file with *"class Main is public, should be declared in a file named Main.java"*. The launcher accepts it. Two tools, one file, opposite answers — because [JEP 330](https://openjdk.org/jeps/330) deliberately relaxed the rule for the single-file case, where there is no output artifact for the name to matter to.

## Why it matters here

Every example in this library is a single `.java` file run this way. That buys three things:

- **No build tool.** No Maven, no Gradle, no `pom.xml`, no wrapper script, nothing to install beyond a JDK.
- **No artifacts.** Source-file mode writes no `.class` anywhere, so there is nothing to clean and nothing to `.gitignore`. Verified, not assumed — the runner would have picked them up otherwise.
- **A file name that can carry the lesson.** The examples are named after the page they belong to (`split_has_sharp_edges_java.java`), which is not a legal public class name style, and does not have to be.

This is the same choice the sibling [Rust library](https://masiarek.github.io/rust-learning-library/) makes by compiling with bare `rustc` instead of cargo: the smallest thing that runs, so the lesson is about the language rather than about a toolchain.

## The limits

Source-file mode compiles **one** file. Multiple source files, a dependency, or anything that needs a classpath entry is out of scope — that is what `javac` and a build tool are for. Since Java 22 it can also run a file with several classes in it, as long as the first one has `main`; and Java 21+ lets `main` itself be shorter (no `String[] args`, no `static`) for teaching purposes.

## See also

- [Unicode escapes run first](../unicode_escapes_run_first/README.md)
