// "How long is this string?" has three different right answers in Java, and the
// method named length() gives the one you almost never want.
import java.text.BreakIterator;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        String[] samples = {
            "hello",
            "zażółć",
            "🎯",                                     // one emoji
            "👨‍👩‍👧",  // family, joined by ZWJ
            "café",                                        // e + combining acute
        };
        System.out.printf("%-14s %8s %12s %10s%n", "string", "length()", "codePoints", "graphemes");
        System.out.println("-".repeat(48));
        for (String s : samples) {
            System.out.printf("%-14s %8d %12d %10d%n",
                    s, s.length(), s.codePointCount(0, s.length()), graphemes(s));
        }
        System.out.println();
        System.out.println("length()    counts UTF-16 code units  — storage");
        System.out.println("codePoints  counts Unicode characters — the table");
        System.out.println("graphemes   counts what a reader calls a character — the screen");
    }

    // Java has no String method for this. BreakIterator is where it lives.
    static int graphemes(String s) {
        BreakIterator it = BreakIterator.getCharacterInstance(Locale.ROOT);
        it.setText(s);
        int n = 0;
        while (it.next() != BreakIterator.DONE) n++;
        return n;
    }
}
