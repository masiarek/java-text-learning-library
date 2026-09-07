# 01 — `char` and `String`

Java's text model was designed in 1995, when Unicode fit in 16 bits and a `char` could hold any character. Unicode outgrew that in 1996. Java could not change `char` without breaking every program ever written, so it did the only compatible thing: it kept `char` at 16 bits and let a single character occupy **two** of them.

Everything in this chapter follows from that one decision. It is not a bug, and it is not going to be fixed. It is a thirty-year-old compatibility promise that you have to know about, because the method named `length()` does not return the length.

| Lesson | The one thing |
|---|---|
| [A `char` is not a character](a_char_is_not_a_character/README.md) | 16 bits stopped being enough in 1996 |
| [Length is three different numbers](length_is_three_numbers/README.md) | storage, characters, or what a reader sees — pick |
| [Reverse and substring cut characters in half](reverse_and_substring/README.md) | the standard library will let you, without an error |
