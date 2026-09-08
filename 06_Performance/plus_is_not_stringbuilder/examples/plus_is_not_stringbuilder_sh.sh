#!/usr/bin/env bash
# What `+` actually compiles to on a modern JDK. The answer changed in Java 9
# and most tutorials still give the pre-9 one.
#
# javap output is filtered to the opcodes that carry the lesson: the raw form
# includes constant-pool indexes (#7, #15) that differ between JDK builds and
# would make this an unreproducible answer key.
set -euo pipefail
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT

cat > "$tmp/Demo.java" <<'JAVA'
public class Demo {
    static String constants()            { return "a" + "b" + "c"; }
    static String expression(String s, int n) { return "x" + s + n; }
    static String inALoop(int n) {
        String out = "";
        for (int i = 0; i < n; i++) out += "x";
        return out;
    }
}
JAVA

javac -d "$tmp" "$tmp/Demo.java"

# Strip constant-pool indexes and the trailing comment, keep the opcode.
extract() {
  javap -c -p "$tmp/Demo.class" \
    | sed -n "/ $1(/,/^$/p" \
    | grep -E '^[[:space:]]+[0-9]+: ' \
    | sed -E 's/#[0-9]+,[[:space:]]*[0-9]+//g; s/#[0-9]+//g' \
    | sed -E 's|// InvokeDynamic :|-> InvokeDynamic |; s|// |-> |' \
    | sed -E 's/[[:space:]]+$//; s/[[:space:]]{2,}->/  ->/; s/^[[:space:]]+/    /' \
    | sed -E 's/(invokedynamic|ldc)[[:space:]]+/\1  /'
}

echo "static String constants()  { return \"a\" + \"b\" + \"c\"; }"
extract constants
echo "  ^ folded at COMPILE time into one constant. No concatenation happens at runtime."
echo
echo "static String expression(String s, int n)  { return \"x\" + s + n; }"
extract expression
echo "  ^ ONE invokedynamic. Not a StringBuilder — JEP 280 (Java 9+) hands the job"
echo "    to StringConcatFactory, which builds a tailored method at first call."
echo
echo "static String inALoop(int n)  { out += \"x\"; }"
extract inALoop
echo "  ^ the invokedynamic is INSIDE the loop: one whole new String per iteration."
echo "    Faster per call than the old StringBuilder pattern, still quadratic overall."
