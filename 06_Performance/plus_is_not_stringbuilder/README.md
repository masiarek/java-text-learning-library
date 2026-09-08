# `+` is not `StringBuilder`

**Level:** 301 · for anyone repeating advice they were taught

**One line:** Since Java 9, `+` compiles to an `invokedynamic` call into `StringConcatFactory`, not to a `StringBuilder` — so the standard explanation of string concatenation is a decade out of date, and the standard advice to hand-write a `StringBuilder` for a single expression now makes your code *slower*.

## The bytecode

<!-- output:plus_is_not_stringbuilder_sh -->

```text
static String constants()  { return "a" + "b" + "c"; }
    0: ldc  -> String abc
    2: areturn
  ^ folded at COMPILE time into one constant. No concatenation happens at runtime.

static String expression(String s, int n)  { return "x" + s + n; }
    0: aload_0
    1: iload_1
    2: invokedynamic  -> InvokeDynamic makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;
    7: areturn
  ^ ONE invokedynamic. Not a StringBuilder — JEP 280 (Java 9+) hands the job
    to StringConcatFactory, which builds a tailored method at first call.

static String inALoop(int n)  { out += "x"; }
    0: ldc  -> String
    2: astore_1
    3: iconst_0
    4: istore_2
    5: iload_2
    6: iload_0
    7: if_icmpge     23
    10: aload_1
    11: invokedynamic  -> InvokeDynamic makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;
    16: astore_1
    17: iinc          2, 1
    20: goto          5
    23: aload_1
    24: areturn
  ^ the invokedynamic is INSIDE the loop: one whole new String per iteration.
    Faster per call than the old StringBuilder pattern, still quadratic overall.
```

<!-- /output -->

Three things worth reading off that:

**Constants fold away completely.** `"a" + "b" + "c"` is a single `ldc` of the string `abc`. There is no concatenation at runtime, so "avoid `+` for constants" is not a rule about anything. (The `ldc -> String` with nothing after it, in the third method, is the empty-string constant `""` — javap prints its value, which is empty.)

**One expression is one `invokedynamic`.** Not a `StringBuilder` allocation, not a chain of `append` calls, not a `toString`. [JEP 280](https://openjdk.org/jeps/280) moved the decision from compile time to **link time**: the first time that line runs, `StringConcatFactory` generates a method shaped exactly for those argument types — often computing the total length up front and filling one array with no intermediate objects at all. A hand-written `StringBuilder` cannot do that, because it must be correct for arguments it cannot see.

**The loop still allocates per iteration.** The `invokedynamic` sits *inside* the loop body. Each pass produces one complete new `String`. It is faster per call than the pre-9 code was, and it is still quadratic — the JIT cannot fix this, because each intermediate `String` is a real, immutable object that the program could in principle observe.

## What this changes

| Advice | Status |
|---|---|
| "Don't use `+` in a loop" | **Still right.** Quadratic is quadratic. |
| "`+` creates a StringBuilder each time" | **Wrong since Java 9.** It is `invokedynamic`. |
| "Rewrite `a + b + c` as a StringBuilder" | **Now harmful.** You are replacing a tailored, single-allocation method with a generic, growing buffer. |
| "String concatenation is slow" | **Meaningless** without saying which of the three cases you mean. |

The practical version is short: **use `+` freely for a single expression; never accumulate with it in a loop.**

## Why the change is invisible

This is a nice example of a language change with no syntax. The `+` operator did not move; `javac` simply stopped baking a strategy into the class file and started emitting a request for one. That means a class compiled in 2017 gets a better concatenation strategy when run on a 2026 JVM, with no recompilation — and it also means you cannot tell what your code does by reading it. You have to read the bytecode, which is what this page's example does.

## See also

- [`+` in a loop is quadratic](../plus_in_a_loop_is_quadratic/README.md)
- [Capacity, and the right tool](../capacity_and_the_right_tool/README.md)
