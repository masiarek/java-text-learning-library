"""Build-time fixes that would otherwise cost a plugin and a pinned dependency.

Right now there is one job: sidebar section labels. MkDocs derives them from
folder names, so `01_Char_and_String` renders as "01 Char And String" — the
underscores survive and the title-casing mangles `String` into a word it isn't.

So every section is labelled with its own README's `# H1`, which already reads
the way it should ("01 — `char` and `String`", "A `char` is not a character"),
with the backticks dropped. The H1 is read from disk because MkDocs fills in a
page's title only when it renders the page, long after this hook runs. A folder
with no README H1 falls back to a label built from its name.

Until 2026-09-12 that fallback was the only rule, and for a lesson it read the
wrong folder: a lesson's README sits two levels down, and the hook took the
first segment of its path, so all sixteen lessons carried their chapter's name —
"01 — char and String", three times, under "01 — char and String".

This is fixed at build time rather than by renaming the folders, because a
folder name is a permanent part of every published URL and a reader may have
bookmarked it.
"""

from __future__ import annotations

import re

# Words the default title-casing gets wrong, whatever position they are in.
# `char` stays lower-case because it is a Java keyword, not a noun.
EXACT = {
    "char": "char",
    "string": "String",
    "bom": "BOM",
    "java": "Java",
    "utf": "UTF",
}

# Lower-cased only when they are NOT the first word — otherwise "05_The_Language"
# renders as "the Language", which reads as a typo.
SMALL = {"and", "the", "is", "of", "a", "in", "to"}


def _label(folder: str) -> str:
    """`01_Char_and_String` -> `01 — char and String`."""
    m = re.match(r"^(\d+)_(.*)$", folder)
    if not m:
        return folder.replace("_", " ")
    number, rest = m.groups()
    out = []
    for i, word in enumerate(rest.split("_")):
        low = word.lower()
        if low in EXACT:
            out.append(EXACT[low])
        elif i > 0 and low in SMALL:
            out.append(low)
        else:
            out.append(word[:1].upper() + word[1:])
    return f"{number} — " + " ".join(out)


def _readme_h1(section) -> str:
    """The `# H1` of a section's own README.md, backticks dropped ("" if none)."""
    for child in section.children:
        page_file = getattr(child, "file", None)
        if page_file is None or page_file.src_path.rsplit("/", 1)[-1] != "README.md":
            continue
        with open(page_file.abs_src_path, encoding="utf-8") as fh:
            for line in fh:
                if line.startswith("# "):
                    return line[2:].strip().replace("`", "")
    return ""


def on_nav(nav, config, files):
    for item in nav:
        _fix(item)
    return nav


def _fix(item) -> None:
    if getattr(item, "is_section", False):
        src = None
        # Find the folder this section came from, via its first child's path.
        for child in item.children:
            if getattr(child, "file", None) is not None:
                src = child.file.src_path
                break
        if src:
            parts = src.split("/")
            if len(parts) > 1:
                item.title = _readme_h1(item) or _label(parts[-2])
        for child in item.children:
            _fix(child)
