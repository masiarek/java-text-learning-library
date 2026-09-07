// String.split() takes a REGULAR EXPRESSION, not a separator, and it silently
// discards trailing empty fields. Both surprises bite hardest on CSV.
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- trailing empty fields vanish ---");
        System.out.println("\"a,b,,\".split(\",\")      = " + Arrays.toString("a,b,,".split(",")) + "   (2 fields, not 4)");
        System.out.println("\"a,b,,\".split(\",\", -1)  = " + Arrays.toString("a,b,,".split(",", -1)) + "   (the limit argument keeps them)");
        System.out.println();
        System.out.println("--- an empty string does NOT give an empty array ---");
        System.out.println("\"\".split(\",\").length    = " + "".split(",").length + "   (one element, the empty string)");
        System.out.println();
        System.out.println("--- the argument is a regex ---");
        System.out.println("\"1.2.3\".split(\".\")      = " + Arrays.toString("1.2.3".split(".")) + "   <- '.' matches everything");
        System.out.println("\"1.2.3\".split(\"\\\\.\")     = " + Arrays.toString("1.2.3".split("\\.")));
        System.out.println("Pattern.quote is the safe form for a literal separator:");
        System.out.println("\"1.2.3\".split(quote(\".\")) = " + Arrays.toString("1.2.3".split(java.util.regex.Pattern.quote("."))));
    }
}
