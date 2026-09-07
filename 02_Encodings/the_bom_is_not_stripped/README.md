# The BOM is not stripped

**Level:** 201 · for anyone reading files other people made

**One line:** Excel, Notepad and much of the Windows world start a UTF-8 file with the three bytes `EF BB BF`; Java reads them as a real character, so your first column header stops matching — and only the first.

## Three bytes that are legal and invisible

<!-- output:the_bom_is_not_stripped_java -->

```text
file bytes      : EF BB BF 6E 61 6D 65   (BOM + "name")
readString len  : 5   <- 4 would mean the BOM was stripped
first code point: U+FEFF  (ZERO WIDTH NO-BREAK SPACE)
read.equals("name")        = false
read.startsWith("name")    = false

and it is INVISIBLE when printed: [﻿name]

The fix is one line, and there is no library call for it:
  fixed.equals("name")     = true
```

<!-- /output -->

`Files.readString` is right, by the letter. `EF BB BF` decodes to `U+FEFF`, that is a valid character, so it is in the string. It has zero width, so it does not print. And it sits at index 0, so it breaks exactly the comparisons that run first.

## Why this is such a good bug

Every symptom points away from the cause:

- The file opens correctly in every editor, because editors strip it.
- The string prints correctly in the console and in your debugger.
- `length()` is one more than you expect, which nobody checks.
- Only **row 1, column 1** is affected, so it looks like a header bug or an off-by-one, not an encoding problem.
- Diffing the string against the literal shows two identical-looking values.

The moment it becomes obvious is a hex dump. That is the tool, and it is the first thing to reach for when a string that is visibly correct compares as unequal.

## Java has no built-in fix

There is no `readString(path, stripBom = true)` and no BOM-aware reader in the JDK. Apache Commons IO ships `BOMInputStream` for this, which tells you how common the problem is. Without a dependency it is one line, as in the example:

```java
if (s.startsWith("\uFEFF")) s = s.substring(1);
```

Do it once, at the boundary where the file is read — the same place normalization belongs, and for the same reason.

## Not to be confused with

The BOM's original job is byte-order marking for **UTF-16**, where it genuinely disambiguates big-endian from little-endian. UTF-8 has no byte order to mark, so a UTF-8 BOM carries no information at all: it is used purely as a "this file is UTF-8" signature by tools that had no other way to say so. The Unicode standard neither requires nor recommends it.

## See also

- [Malformed input has two policies](../malformed_input/README.md)
- [Normalization and equality](../../03_Locale/normalization_and_equality/README.md) — the other reason two identical-looking strings differ
