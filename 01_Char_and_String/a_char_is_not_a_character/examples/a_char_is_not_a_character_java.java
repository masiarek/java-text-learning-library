// A `char` is 16 bits. That was enough for Unicode in 1995 and has not been
// enough since 1996, when Unicode grew past U+FFFF.
public class Main {
    public static void main(String[] args) {
        System.out.println("Character.MAX_VALUE  = U+" + hex(Character.MAX_VALUE));
        System.out.println("highest code point   = U+" + Integer.toHexString(Character.MAX_CODE_POINT).toUpperCase());

        String target = "🎯";           // U+1F3AF DIRECT HIT, written as its two halves
        System.out.println();
        System.out.println("the string           = " + target);
        System.out.println("length()             = " + target.length() + "   <- UTF-16 code units, not characters");
        System.out.println("charAt(0)            = U+" + hex(target.charAt(0)) + "  isHighSurrogate=" + Character.isHighSurrogate(target.charAt(0)));
        System.out.println("charAt(1)            = U+" + hex(target.charAt(1)) + "  isLowSurrogate=" + Character.isLowSurrogate(target.charAt(1)));
        System.out.println("codePointAt(0)       = U+" + Integer.toHexString(target.codePointAt(0)).toUpperCase());
        System.out.println("fits in a char?      = " + (target.codePointAt(0) <= Character.MAX_VALUE));

        // Neither half means anything on its own. Printing one is not an error;
        // it is a character the font cannot draw.
        System.out.println();
        System.out.println("charAt(0) printed    = [" + target.charAt(0) + "]  <- a lone surrogate: valid Java, invalid text");
    }
    static String hex(char c) { return String.format("%04X", (int) c); }
}
