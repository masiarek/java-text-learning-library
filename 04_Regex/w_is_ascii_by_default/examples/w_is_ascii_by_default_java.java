// \w, \d, \b and friends are ASCII-only in Java unless you say otherwise.
// On any text that is not English this quietly matches the wrong thing —
// it does not throw, and it does not match nothing, which would at least be
// noticeable. It matches a fragment.
import java.util.regex.*;

public class Main {
    public static void main(String[] args) {
        String subject = "zażółć gęślą 123";
        System.out.println("subject: " + subject);
        System.out.println();
        System.out.println("\\w+  default                   " + matches(Pattern.compile("\\w+"), subject));
        System.out.println("\\w+  UNICODE_CHARACTER_CLASS   " + matches(Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS), subject));
        System.out.println("\\p{L}+  (always Unicode)       " + matches(Pattern.compile("\\p{L}+"), subject));
        System.out.println();
        System.out.println("Note what the default did: it did not fail, it returned [za].");
        System.out.println("A validator built on \\w+ accepts half a Polish word and calls it clean.");
        System.out.println();
        System.out.println("--- and the dot counts differently from length() ---");
        System.out.println("\"🎯\".matches(\".\")   = " + "🎯".matches(".") + "   <- regex works in CODE POINTS");
        System.out.println("\"🎯\".matches(\"..\")  = " + "🎯".matches("..") + "  <- but String.length() is 2");
    }
    static String matches(Pattern p, String s) {
        Matcher m = p.matcher(s);
        StringBuilder b = new StringBuilder();
        while (m.find()) b.append('[').append(m.group()).append(']');
        return b.toString();
    }
}
