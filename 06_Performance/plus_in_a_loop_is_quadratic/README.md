# `+` in a loop is quadratic

**Level:** 201 · the one performance fact that actually matters

**One line:** `out += "x"` in a loop copies every character accumulated so far, every time — so building a string of length *n* copies **n(n−1)/2** characters instead of *n*, and this is provable by counting rather than by timing.

## Proved by counting

A `String` is immutable. `out += "x"` cannot extend anything; it allocates a new `String` and copies the old contents in. Do that *n* times and you copy 0 + 1 + 2 + … + (n−1) characters. That is arithmetic, not a benchmark, so it holds on every machine, every JDK, and under every JIT:

<!-- output:plus_in_a_loop_is_quadratic_java -->

```text
Characters copied to build a string of length n:
       n          with +=         n(n-1)/2  StringBuilder
----------------------------------------------------------
      10               45               45             10
     100             4950             4950            100
    1000           499500           499500           1000
   10000         49995000         49995000          10000

The += column IS the formula — that is what quadratic means.
StringBuilder copies each character once: it is n, not n squared.
At n = 10,000 that is 49,995,000 copies against 10,000.

--- and now a crude timing, reported only as a band ---
Is += more than 10x slower than StringBuilder at n=20,000?  yes

The exact ratio is deliberately not printed. Measured on one machine
it ranged from 142x to 786x across repetitions of the same run, because
JIT compilation and GC dominate a loop this small. A number that moves
by 5x between runs is not a measurement, and recording one as an answer
key would make this library's own CI flake. See the page for what to
use instead when you genuinely need a number.
```

<!-- /output -->

At n = 10,000 that is **49,995,000** character copies against StringBuilder's 10,000 — a factor of 5,000, and it grows with *n*. This is the whole reason the advice exists, and it is why the advice is not a micro-optimisation: it is an asymptotic difference, the kind that turns a 200 ms request into a 40-second one when the input gets bigger.

## Why there are no timings on this page

There is one measurement in the output above and it is reported as a **band** — "more than 10×" — not a number. That is deliberate.

Running the obvious benchmark on one machine, the `+=`-to-`StringBuilder` ratio came out between **142× and 786× across repetitions of a single run**, and the quadratic-growth ratio itself swung between 2.3 and 7.7 when it should have been a steady 4. Nothing was changing except JIT compilation state and garbage collection.

A JVM microbenchmark written with `System.nanoTime()` and a loop measures the JIT's warmup schedule at least as much as it measures your code. The specific traps:

- **Warmup.** The first thousands of iterations run interpreted, then get compiled, possibly deoptimised, and recompiled. Whichever branch you time first is penalised.
- **Dead-code elimination.** If you do not use the result, the JIT is entitled to delete the work entirely, and will. The examples here assert on `length()` for exactly this reason.
- **Constant folding.** If the input is a compile-time constant, the answer may be computed at compile time, and you time nothing at all.
- **GC.** A quadratic loop generates enormous garbage; whether a collection lands inside your timed region is luck.

If you need a real number, use **[JMH](https://github.com/openjdk/jmh)** (`org.openjdk.jmh`), the OpenJDK's own harness. It handles warmup, forks a fresh JVM per trial, and has a `Blackhole` to defeat dead-code elimination. It is the only answer that is not folklore — and it is a build dependency, which is why this library, which has none, does not print numbers it cannot stand behind.

## What to actually do

```java
StringBuilder sb = new StringBuilder();
for (String part : parts) sb.append(part);
String out = sb.toString();
```

Or better, when you are joining rather than accumulating, don't write the loop at all — see [Capacity, and the right tool](../capacity_and_the_right_tool/README.md).

**And do not "fix" straight-line code.** `"x" + s + n` in a single expression is already optimal and is *faster* than a hand-written `StringBuilder` — the next page shows why. The rule is about the **loop**, not about the operator.

## See also

- [`+` is not `StringBuilder`](../plus_is_not_stringbuilder/README.md)
- [Capacity, and the right tool](../capacity_and_the_right_tool/README.md)
