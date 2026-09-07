# The same question in Python, for comparison. Python 3 stores code points, not
# UTF-16 units, so len() answers the middle column — but the third column is
# still not in the standard library here either.
samples = [
    "hello",
    "zażółć",
    "\U0001F3AF",
    "\U0001F468‍\U0001F469‍\U0001F467",
    "café",
]
print(f"{'string':<14} {'len()':>8} {'codePoints':>12}")
print("-" * 36)
for s in samples:
    print(f"{s:<14} {len(s):>8} {len(s):>12}")
print()
print("len() IS the code point count — Python never shows you UTF-16 units.")
print("Grapheme clusters need a third-party library (regex, grapheme).")
