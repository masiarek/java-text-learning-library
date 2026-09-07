# 04 — Regex and splitting

Java's regex engine is good, fast, and older than the Unicode rules most people assume it follows. Two defaults in it are set for compatibility with 1999 and will quietly do the wrong thing to any text that is not English.

| Lesson | The one thing |
|---|---|
| [`\w` is ASCII by default](w_is_ascii_by_default/README.md) | it matches half a Polish word and reports success |
| [`split` has sharp edges](split_has_sharp_edges/README.md) | trailing empty fields vanish; the argument is a regex |
