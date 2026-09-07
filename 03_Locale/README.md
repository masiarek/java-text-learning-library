# 03 — Locale

Six `String` methods change their behaviour based on where the JVM happens to be running. None of them says so in its name, all of them have a locale-taking overload that is one argument longer, and the short form is the one in every tutorial.

| Lesson | The one thing |
|---|---|
| [Case is locale-sensitive](case_is_locale_sensitive/README.md) | `toLowerCase()` is a different function in Turkey |
| [Format follows the locale](format_follows_the_locale/README.md) | `String.format` writes `3,50` in Poland |
| [Normalization and equality](normalization_and_equality/README.md) | two identical-looking strings, `equals()` false |
