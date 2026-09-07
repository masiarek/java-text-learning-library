"""Build-time fixes that would otherwise cost a plugin and a pinned dependency.

Right now there is one job: sidebar section labels. MkDocs derives them from
folder names, so `01_Char_and_String` renders as "01 Char And String" — the
underscores survive and the title-casing mangles `String` into a word it isn't.

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
                item.title = _label(parts[-2] if parts[-1] != "README.md" else parts[0])
        for child in item.children:
            _fix(child)
