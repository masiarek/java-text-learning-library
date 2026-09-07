// String.format without a Locale uses the default one. In Poland, Germany,
// France and most of Europe the decimal separator is a comma — so a number
// formatted for display and then parsed back, or written into a CSV or a JSON
// body, is a bug that only appears on someone else's machine.
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        Locale pl = Locale.of("pl", "PL");
        Locale de = Locale.of("de", "DE");
        double amount = 1234.5;

        System.out.println("default locale for this run : " + Locale.getDefault());
        System.out.println();
        System.out.printf("%-22s %s%n", "Locale.ROOT", String.format(Locale.ROOT, "%,.2f", amount));
        System.out.printf("%-22s %s%n", "en-US", String.format(Locale.US, "%,.2f", amount));
        System.out.printf("%-22s %s%n", "pl-PL", String.format(pl, "%,.2f", amount));
        System.out.printf("%-22s %s%n", "de-DE", String.format(de, "%,.2f", amount));
        System.out.println();
        System.out.println("Now feed the Polish rendering back to a parser that expects a dot:");
        String polish = String.format(pl, "%.2f", amount);
        try {
            System.out.println("  Double.parseDouble(\"" + polish + "\") = " + Double.parseDouble(polish));
        } catch (NumberFormatException e) {
            System.out.println("  Double.parseDouble(\"" + polish + "\") throws " + e.getClass().getSimpleName());
        }
        System.out.println();
        System.out.println("Rule: Locale.ROOT for machines, a real locale for people.");
        System.out.println("      Never the no-argument overload for either.");
    }
}
