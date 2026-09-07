// toUpperCase() and toLowerCase() with no argument use the DEFAULT locale.
// That makes them a function of where the JVM is running, which is why they
// must never be used to normalise an identifier, a protocol token, or a key.
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        Locale tr = Locale.of("tr", "TR");

        System.out.println("default locale for this run : " + Locale.getDefault());
        System.out.println();
        System.out.println("\"TITLE\".toLowerCase()        = " + "TITLE".toLowerCase());
        System.out.println("\"TITLE\".toLowerCase(TURKISH) = " + "TITLE".toLowerCase(tr) + "   <- dotless i");
        System.out.println("\"i\".toUpperCase(TURKISH)     = " + "i".toUpperCase(tr) + "    <- dotted capital I");
        System.out.println();
        System.out.println("So this comparison is false in Turkey and true everywhere else:");
        System.out.println("  \"TITLE\".toLowerCase().equals(\"title\") = " + "TITLE".toLowerCase(tr).equals("title"));
        System.out.println("  ...using Locale.ROOT instead          = " + "TITLE".toLowerCase(Locale.ROOT).equals("title"));
        System.out.println();
        System.out.println("Case mapping can also change the LENGTH of a string:");
        System.out.println("  \"ß\".toUpperCase() = " + "ß".toUpperCase()
                + "   (1 char in, " + "ß".toUpperCase().length() + " out)");
        System.out.println();
        System.out.println("Rule: comparing? use equalsIgnoreCase, or toLowerCase(Locale.ROOT).");
        System.out.println("      showing it to a person? use their locale, deliberately.");
    }
}
