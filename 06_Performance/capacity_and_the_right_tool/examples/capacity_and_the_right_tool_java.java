// StringBuilder's buffer, and the four APIs that are usually better than
// reaching for StringBuilder at all.
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- the buffer ---");
        StringBuilder b = new StringBuilder();
        System.out.println("new StringBuilder().capacity()      = " + b.capacity());
        System.out.println("new StringBuilder(\"abc\").capacity() = "
                + new StringBuilder("abc").capacity() + "   (length + 16)");
        System.out.println("new StringBuilder(1000).capacity()  = "
                + new StringBuilder(1000).capacity() + "  (the int overload is a CAPACITY, not content)");
        System.out.println();
        System.out.println("growth, appending one char at a time:");
        int last = b.capacity();
        for (int i = 0; i < 300; i++) {
            b.append('x');
            if (b.capacity() != last) {
                System.out.printf("  length %3d -> capacity grew %3d to %3d%n", b.length(), last, b.capacity());
                last = b.capacity();
            }
        }
        System.out.println("  the rule is (old * 2) + 2, and each growth copies the whole buffer");
        System.out.println();

        System.out.println("--- the tools that are usually better ---");
        List<String> parts = List.of("alpha", "beta", "gamma");
        System.out.println("String.join(\", \", list)            = " + String.join(", ", parts));
        StringJoiner j = new StringJoiner(", ", "[", "]");
        parts.forEach(j::add);
        System.out.println("StringJoiner(sep, prefix, suffix)  = " + j);
        System.out.println("Collectors.joining(...)            = "
                + parts.stream().collect(Collectors.joining(", ", "[", "]")));
        System.out.println("...and an empty StringJoiner       = "
                + new StringJoiner(",", "[", "]").setEmptyValue("(none)"));
        System.out.println();
        System.out.println("These say what they mean, handle the separator-between-not-after");
        System.out.println("problem for you, and cannot be got wrong at the last element —");
        System.out.println("which is the bug every hand-written StringBuilder join has had once.");
    }
}
