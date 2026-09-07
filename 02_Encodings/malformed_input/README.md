# Malformed input has two policies

**Level:** 201 · for anyone who has met mojibake

**One line:** Hand the same invalid bytes to Java's three ways of reading text and two of them throw — the third, the shortest and most obvious one, replaces the broken bytes with `U+FFFD` and returns successfully.

## One input, three answers

<!-- output:malformed_input_java -->

```text
input bytes: C3 28  (a truncated UTF-8 sequence)

new String(bytes, UTF_8)   -> no exception; length 2, first char U+FFFD
                              U+FFFD is REPLACEMENT CHARACTER: data lost, quietly
CharsetDecoder(REPORT)     -> throws MalformedInputException
Files.readString(path)     -> throws MalformedInputException

Two of the three refuse bad input. The String constructor —
the shortest and most obvious one — is the one that hides it.
```

<!-- /output -->

`new String(bytes, UTF_8)` cannot report a problem. Its signature has no way to: no checked exception, no status, no partial result. So it substitutes `U+FFFD REPLACEMENT CHARACTER` and hands back a `String` that looks fine, has a plausible length, and has permanently lost the original bytes.

This is a design decision, not an oversight — `String` constructors are used everywhere and were never allowed to fail. But it means **the API a beginner reaches for first is the one that discards evidence**.

## Where it hurts

The replacement is not reversible. Once bytes have become `U+FFFD`, the information about what they were is gone; you cannot recover it later, log it usefully, or ask the sender to resend the bad record, because nothing recorded that a record was bad. A pipeline built on `new String(...)` degrades silently and continuously.

The tell is `U+FFFD` appearing in your data at rest — in a database column, a log file, a rendered page as `�`. By then the boundary that caused it is hours away.

## What to use

```java
// strict: refuse anything that is not valid UTF-8
String s = StandardCharsets.UTF_8.newDecoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT)
        .decode(ByteBuffer.wrap(bytes))
        .toString();
```

Or simply `Files.readString(path)`, which is strict by default — a genuinely good modern API, and a quiet reversal of the older `FileReader` behaviour.

Deliberate replacement is still a valid choice for display-only text where showing something beats showing nothing. The rule is that it should be a choice you can point at in the code, not the default you got by writing the shortest thing.

## Beside Python

<!-- output:malformed_input_py -->

```text
input bytes: c3 28

bytes.decode('utf-8')                 -> raises UnicodeDecodeError
bytes.decode('utf-8', 'replace')      -> '�('

Python's default is STRICT and the lax mode is opt-in.
Java's most obvious API is lax and the strict mode is opt-in.
```

<!-- /output -->

Same two policies, opposite defaults. Python's `bytes.decode()` is strict and `errors="replace"` is opt-in; Java's most reachable API is lax and strictness is opt-in. Neither is wrong in principle — but the default is what most code gets, and most code does not choose.

## See also

- [The default charset](../the_default_charset/README.md)
- [The BOM is not stripped](../the_bom_is_not_stripped/README.md)
