# 02 — Encodings

A `String` in memory has no encoding question to answer — it is UTF-16, always. The question arrives at the two boundaries: bytes coming in, bytes going out. Java's history at those boundaries is one long correction, finished in Java 18 and still visible in the API.

| Lesson | The one thing |
|---|---|
| [The default charset](the_default_charset/README.md) | UTF-8 since Java 18 — but `native.encoding` still follows your locale |
| [Malformed input has two policies](malformed_input/README.md) | the most obvious API is the one that hides corruption |
| [The BOM is not stripped](the_bom_is_not_stripped/README.md) | three invisible bytes that break only your first line |
