# Capacity, and the right tool

**Level:** 201 · after the loop rule

**One line:** `StringBuilder` starts with room for 16 characters and grows by `(old × 2) + 2`, copying the whole buffer each time — and for the most common job, joining a collection with a separator, there are three APIs that are clearer than a builder and impossible to get wrong at the last element.

## The buffer

<!-- output:capacity_and_the_right_tool_java -->

```text
--- the buffer ---
new StringBuilder().capacity()      = 16
new StringBuilder("abc").capacity() = 19   (length + 16)
new StringBuilder(1000).capacity()  = 1000  (the int overload is a CAPACITY, not content)

growth, appending one char at a time:
  length  17 -> capacity grew  16 to  34
  length  35 -> capacity grew  34 to  70
  length  71 -> capacity grew  70 to 142
  length 143 -> capacity grew 142 to 286
  length 287 -> capacity grew 286 to 574
  the rule is (old * 2) + 2, and each growth copies the whole buffer

--- the tools that are usually better ---
String.join(", ", list)            = alpha, beta, gamma
StringJoiner(sep, prefix, suffix)  = [alpha, beta, gamma]
Collectors.joining(...)            = [alpha, beta, gamma]
...and an empty StringJoiner       = (none)

These say what they mean, handle the separator-between-not-after
problem for you, and cannot be got wrong at the last element —
which is the bug every hand-written StringBuilder join has had once.
```

<!-- /output -->

Two things to take from the growth trace. Each growth **copies the entire buffer**, so an unsized builder that ends up at 10,000 characters has copied roughly 20,000 characters getting there — still linear overall, and still nothing like the quadratic `+=`, but not free. And `new StringBuilder(1000)` sets a **capacity**, while `new StringBuilder("1000")` sets *contents*; the two constructors sit next to each other and do entirely different things.

Presize when you know the size, and only then:

```java
StringBuilder sb = new StringBuilder(parts.size() * 32);
```

If you don't know, don't guess — the doubling strategy is already good, and a wrong guess wastes memory on every call.

## The tools that are usually better

The output shows all three. Pick by what you have:

| You have | Use |
|---|---|
| a `List<String>` or varargs | `String.join(", ", parts)` |
| items arriving one at a time, with a prefix/suffix | `StringJoiner(", ", "[", "]")` |
| a stream, especially with a `map` before it | `.collect(Collectors.joining(", "))` |
| genuinely incremental building, mixed types | `StringBuilder` |

`String.join` and `StringJoiner` use a `StringBuilder` underneath, so this is not a performance argument — it is a correctness one. Every hand-written join has, at least once, produced `a, b, c, ` with a trailing separator, or needed a `first` boolean, or an `if (i > 0)` inside the loop. `StringJoiner` also has `setEmptyValue`, which handles the empty-collection case that hand-written loops silently get wrong by emitting just the prefix and suffix.

## A note on `+=` inside `StringBuilder` code

The mistake that survives every round of advice:

```java
StringBuilder sb = new StringBuilder();
for (String p : parts) {
    sb.append(p + ", ");     // <- a whole concatenation per iteration
}
```

The builder is doing nothing for you here — you have moved the allocation inside the `append`. It is `sb.append(p).append(", ")`, which is two appends and no intermediate `String` at all.

## See also

- [`+` in a loop is quadratic](../plus_in_a_loop_is_quadratic/README.md)
- [`+` is not `StringBuilder`](../plus_is_not_stringbuilder/README.md)
