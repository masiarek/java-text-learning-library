// StringBuilder.reverse() is documented to keep surrogate pairs together, and it
// does. That is not the same as being correct about text.
public class Main {
    public static void main(String[] args) {
        show("hello");
        show("🎯 ok");        // emoji: the pair survives
        show("café");             // e + combining acute: the mark moves
        show("👨‍👩‍👧");  // ZWJ family: shatters

        String target = "🎯";
        System.out.println();
        System.out.println("substring(0, 1) of a 2-unit emoji:");
        String half = target.substring(0, 1);
        System.out.println("  result length = " + half.length() + ", isSurrogate = " + Character.isSurrogate(half.charAt(0)));
        System.out.println("  no exception was thrown — Java let you cut a character in half");
    }
    static void show(String s) {
        System.out.println(pad(s) + " -> " + new StringBuilder(s).reverse());
    }
    static String pad(String s) {
        return s + " ".repeat(Math.max(0, 12 - s.length()));
    }
}
