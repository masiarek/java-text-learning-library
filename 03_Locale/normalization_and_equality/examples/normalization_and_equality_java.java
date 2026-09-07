// Two strings that look identical, print identically, and are not equal.
// This is not a Java flaw — it is Unicode — but Java gives you no warning and
// equals() is the method everyone reaches for.
import java.text.Collator;
import java.text.Normalizer;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        String composed = "café";        // é as one code point, U+00E9
        String decomposed = "café";     // e + U+0301 COMBINING ACUTE ACCENT

        System.out.println("composed   = " + composed + "   length " + composed.length());
        System.out.println("decomposed = " + decomposed + "   length " + decomposed.length());
        System.out.println("they print the same? look above — yes");
        System.out.println();
        System.out.println("equals()          = " + composed.equals(decomposed));
        System.out.println("compareTo() == 0  = " + (composed.compareTo(decomposed) == 0));
        System.out.println("hashCode equal    = " + (composed.hashCode() == decomposed.hashCode())
                + "   <- so a HashMap keeps them as two different keys");
        System.out.println();
        String nfc = Normalizer.normalize(decomposed, Normalizer.Form.NFC);
        System.out.println("after Normalizer.normalize(.., NFC):");
        System.out.println("  equals()        = " + composed.equals(nfc));
        System.out.println();
        System.out.println("Collator (locale-aware comparison, ignores the difference):");
        System.out.println("  compare() == 0  = " + (Collator.getInstance(Locale.ROOT).compare(composed, decomposed) == 0));
        System.out.println();
        System.out.println("Rule: normalise at the boundary — when text enters your program —");
        System.out.println("      not at the moment you compare it.");
    }
}
