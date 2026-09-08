#!/usr/bin/env python3
"""Run every example, and hold its output to a recorded answer key.

This is the spine of the library. A lesson page never hand-types what a program
prints; it marks the spot and this tool fills it from a real run:

    <!-- output:a_char_is_not_a_character_java -->
    <!-- /output -->

Inside the markers is generated, outside is yours. There is a second kind,
`source:`, which pastes the program itself — for the pages where the code *is*
the lesson and a hand-copied fence could quietly drift from the file CI runs.

Two kinds of example, told apart by extension
---------------------------------------------
    examples/<stem>.java   run as `java <stem>.java` — source-file mode (JEP 330),
                           so there is no javac step, no build tool and no .class
                           artifact to clean up or gitignore
    examples/<stem>.py     stdlib-only Python, run as `python3 -I <stem>.py`,
                           present only to put Java beside a language you know
    examples/<stem>.sh     bash, for the lessons where the point is a tool rather
                           than a program — `javap` on the bytecode, mostly. These
                           MUST filter their output to the lines that carry the
                           lesson: raw javap includes constant-pool indexes that
                           differ between JDK builds and would flake in CI.

Stems are unique repo-wide *across* extensions, because a Markdown block names a
bare stem with no path and no extension. The convention is a language suffix:
`length_is_three_numbers_java`, `length_is_three_numbers_py`.

Why the environment is pinned
-----------------------------
Every example runs under `LC_ALL=C`, `LANG=C`, and the JVM is started with
`-Duser.language=en -Duser.country=US`. Both halves are load-bearing, and this
library is the reason why:

  * `Locale.getDefault()` is read from the OS, not from the environment, on
    macOS — so without the two `-D` flags the answer key would depend on whose
    laptop recorded it.
  * `native.encoding` DOES follow the locale (`US-ASCII` under `LC_ALL=C`,
    `UTF-8` in a normal terminal), while `file.encoding` is UTF-8 regardless
    since Java 18. That difference is the subject of one of the lessons, so the
    runner has to make it reproducible rather than hide it.

A lesson whose subject is the locale or the charset overrides this *inside* the
program, on purpose and in view — never by asking the runner for an exception.

An example that fails on purpose
--------------------------------
Some lessons are about code that does not compile. The runner records combined
stdout+stderr, and appends `[exit status: N]` when the exit code is not zero, so
"this is a compile error" is itself an answer key rather than a claim.

Four modes
----------
    python3 tools/run_examples.py             verify + refill the .md blocks
    python3 tools/run_examples.py --update    accept current output as the key
    python3 tools/run_examples.py --check     write nothing; fail on any drift  (CI)
    python3 tools/run_examples.py --only X    touch example X and nothing else
"""

from __future__ import annotations

import argparse
import os
import re
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent

# The pinned environment. See the module docstring for why each piece is here.
ENV = {**os.environ, "LC_ALL": "C", "LANG": "C"}
JAVA_FLAGS = [
    "-Duser.language=en",
    "-Duser.country=US",
    # Without these two, `System.out` follows the console encoding, which
    # under LC_ALL=C is US-ASCII — and Java then transliterates every
    # non-ASCII character to a literal "?" with no error. The first run of
    # this library recorded "za????" as the answer key for "zażółć".
    "-Dstdout.encoding=UTF-8",
    "-Dstderr.encoding=UTF-8",
]

RUNNERS = {
    ".java": lambda f: ["java", *JAVA_FLAGS, f.name],
    ".py": lambda f: [sys.executable, "-I", f.name],
    ".sh": lambda f: ["bash", f.name],
}
FENCE_LANG = {".java": "java", ".py": "python", ".sh": "bash"}


def discover() -> dict[str, Path]:
    """Every example in the repo, keyed by its stem. Duplicate stems are fatal."""
    found: dict[str, Path] = {}
    for path in sorted(ROOT.glob("*/*/examples/*")):
        if path.suffix not in RUNNERS:
            continue
        if path.stem in found:
            sys.exit(
                f"duplicate stem {path.stem!r}:\n  {found[path.stem].relative_to(ROOT)}"
                f"\n  {path.relative_to(ROOT)}\nStems must be unique repo-wide."
            )
        found[path.stem] = path
    return found


def run(path: Path) -> str:
    """Run one example and return exactly what a reader would see."""
    proc = subprocess.run(
        RUNNERS[path.suffix](path),
        cwd=path.parent,
        env=ENV,
        capture_output=True,
        timeout=120,
    )
    # Decoded permissively: a byte that is not valid UTF-8 becomes \xNN, which is
    # deterministic and is usually the moment the example wanted a hex dump.
    out = (proc.stdout + proc.stderr).decode("utf-8", "backslashreplace")
    if proc.returncode != 0:
        out += f"[exit status: {proc.returncode}]\n"
    return out


def blocks(text: str, kind: str, stem: str, body: str) -> tuple[str, bool]:
    """Refill every <!-- kind:stem --> block in one page. Returns (text, changed)."""
    pattern = re.compile(
        rf"(<!--\s*{kind}:{re.escape(stem)}\s*-->\n).*?(<!--\s*/{kind}\s*-->)",
        re.DOTALL,
    )
    new, n = pattern.subn(lambda m: m.group(1) + body + m.group(2), text)
    return new, n > 0 and new != text


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--update", action="store_true", help="record current output as the key")
    ap.add_argument("--check", action="store_true", help="write nothing; fail on drift (CI)")
    ap.add_argument("--only", nargs="*", metavar="STEM", help="limit to these stems")
    args = ap.parse_args()

    examples = discover()
    if args.only:
        unknown = set(args.only) - set(examples)
        if unknown:
            sys.exit(f"no such example(s): {', '.join(sorted(unknown))}")
        examples = {k: v for k, v in examples.items() if k in args.only}
    if not examples:
        sys.exit("no examples found")

    failures: list[str] = []
    outputs: dict[str, str] = {}

    for stem, path in examples.items():
        rel = path.relative_to(ROOT)
        actual = run(path)
        outputs[stem] = actual
        key = path.with_suffix(".out")

        if args.update:
            key.write_text(actual, encoding="utf-8")
            print(f"recorded  {rel}")
            continue
        if not key.exists():
            failures.append(f"{rel}: no answer key — run with --update to record one")
            continue
        expected = key.read_text(encoding="utf-8")
        if expected != actual:
            failures.append(
                f"{rel}: output does not match {key.name}\n"
                + _diff(expected, actual)
            )
        else:
            print(f"ok        {rel}")

    # Refill the lesson pages from the runs we just did.
    changed_pages = []
    for md in sorted(ROOT.rglob("*.md")):
        if "/site/" in str(md):
            continue
        text = original = md.read_text(encoding="utf-8")
        for stem, path in examples.items():
            fence = FENCE_LANG[path.suffix]
            text, _ = blocks(text, "output", stem, f"\n```text\n{outputs[stem]}```\n\n")
            src = path.read_text(encoding="utf-8")
            text, _ = blocks(text, "source", stem, f"\n```{fence}\n{src}```\n\n")
        if text != original:
            if args.check:
                failures.append(f"{md.relative_to(ROOT)}: generated block is stale")
            else:
                md.write_text(text, encoding="utf-8")
                changed_pages.append(md.relative_to(ROOT))

    for p in changed_pages:
        print(f"refilled  {p}")

    if failures:
        print("\n" + "\n".join(f"FAIL  {f}" for f in failures), file=sys.stderr)
        return 1
    print(f"\n{len(examples)} example(s) ok")
    return 0


def _diff(expected: str, actual: str) -> str:
    import difflib

    return "".join(
        difflib.unified_diff(
            expected.splitlines(True), actual.splitlines(True),
            fromfile="recorded", tofile="actual", n=2,
        )
    )


if __name__ == "__main__":
    raise SystemExit(main())
