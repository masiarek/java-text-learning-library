# 06 — Concatenation and performance

The one piece of Java text advice everybody has heard — *"don't use `+` in a loop, use `StringBuilder`"* — is still correct, and almost every explanation of *why* has been out of date since Java 9.

This chapter separates the part that is true (the loop is quadratic, and always will be) from the part that changed (`+` has not compiled to a `StringBuilder` for years), and is careful about what can honestly be measured without a benchmarking harness. Which is less than you would like.

| Lesson | The one thing |
|---|---|
| [`+` in a loop is quadratic](plus_in_a_loop_is_quadratic/README.md) | proved by counting copies, not by timing |
| [`+` is not `StringBuilder`](plus_is_not_stringbuilder/README.md) | it is `invokedynamic`, and constants fold away entirely |
| [Capacity, and the right tool](capacity_and_the_right_tool/README.md) | the buffer doubles; `String.join` is usually the answer |
