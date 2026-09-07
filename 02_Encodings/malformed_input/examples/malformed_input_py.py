# Python makes the same choice available, but it makes you name it: the
# errors= argument has no default that silently replaces.
bad = b"\xc3\x28"
print("input bytes: c3 28")
print()
print("bytes.decode('utf-8')                 -> ", end="")
try:
    bad.decode("utf-8")
    print("no exception (!)")
except UnicodeDecodeError as e:
    print(f"raises {type(e).__name__}")
print("bytes.decode('utf-8', 'replace')      -> ", end="")
print(repr(bad.decode("utf-8", "replace")))
print()
print("Python's default is STRICT and the lax mode is opt-in.")
print("Java's most obvious API is lax and the strict mode is opt-in.")
