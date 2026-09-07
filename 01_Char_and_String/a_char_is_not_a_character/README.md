# A `char` is not a character

**Level:** 101 · start here

**One line:** `char` is a 16-bit number, Unicode has characters that need 21 bits, and Java's answer is to store those as **two** `char`s — so `charAt()` can hand you half a character, and will do it without complaining.

## The arithmetic

A `char` holds `0` to `65535`. Unicode's highest code point is `U+10FFFF`, which is 1,114,111. The gap is not small: about 94% of the code space does not fit in a `char`.

Characters above `U+FFFF` — emoji, historic scripts, many CJK extensions, most mathematical alphabets — are stored as a **surrogate pair**: two `char` values from a reserved block, which mean nothing on their own and are only text when read together. That encoding is UTF-16, and in Java it is not a file format you opt into. It is what a `String` *is*, in memory, always.

## What that looks like

<!-- output:a_char_is_not_a_character_java -->

```text
Character.MAX_VALUE  = U+FFFF
highest code point   = U+10FFFF

the string           = 🎯
length()             = 2   <- UTF-16 code units, not characters
charAt(0)            = U+D83C  isHighSurrogate=true
charAt(1)            = U+DFAF  isLowSurrogate=true
codePointAt(0)       = U+1F3AF
fits in a char?      = false

charAt(0) printed    = [?]  <- a lone surrogate: valid Java, invalid text
```

<!-- /output -->

The last line is the important one. `charAt(0)` returned a value, printed without error, and produced something that is not a character in any font. Java has no "half a character" type to refuse you with, because `char` *is* the half.

## What to use instead

| Instead of | Use |
|---|---|
| `s.charAt(i)` | `s.codePointAt(i)` — returns an `int`, the whole character |
| `for (char c : s.toCharArray())` | `s.codePoints().forEach(...)` |
| `(char) n` to build text | `Character.toString(n)` — takes a code point, returns a `String` |
| `s.length()` | see [Length is three different numbers](../length_is_three_numbers/README.md) |

The `int` in `codePointAt` is not a widening for safety's sake. It is the actual type of a Unicode character in Java. There is no 21-bit primitive, so `int` is the character type and `char` is a storage unit with a misleading name.

## The one place `char` is still right

ASCII. If you are parsing a protocol, a number, or a file format whose grammar is defined in ASCII, every character you care about is below 128, a surrogate can never be one of them, and `charAt` is exact and fast. The bug arrives when the same loop meets a user's name.

## See also

- [Length is three different numbers](../length_is_three_numbers/README.md)
- [Reverse and substring cut characters in half](../reverse_and_substring/README.md)
