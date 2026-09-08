// Why `out += "x"` in a loop is quadratic — proved by COUNTING, not by timing.
//
// A String is immutable, so `out += "x"` cannot extend anything. It builds a
// brand-new String and copies every character already accumulated. Do that n
// times and you copy 0 + 1 + 2 + ... + (n-1) characters: n(n-1)/2.
public class Main {
    public static void main(String[] args) {
        System.out.println("Characters copied to build a string of length n:");
        System.out.printf("%8s %16s %16s %14s%n", "n", "with +=", "n(n-1)/2", "StringBuilder");
        System.out.println("-".repeat(58));
        for (int n : new int[] { 10, 100, 1_000, 10_000 }) {
            long counted = countCopiesWithPlus(n);
            System.out.printf("%8d %16d %16d %14d%n", n, counted, (long) n * (n - 1) / 2, n);
        }
        System.out.println();
        System.out.println("The += column IS the formula — that is what quadratic means.");
        System.out.println("StringBuilder copies each character once: it is n, not n squared.");
        System.out.println("At n = 10,000 that is 49,995,000 copies against 10,000.");

        System.out.println();
        System.out.println("--- and now a crude timing, reported only as a band ---");
        long plus = timePlus(20_000);
        long builder = timeBuilder(20_000);
        double ratio = (double) plus / Math.max(builder, 1);
        System.out.println("Is += more than 10x slower than StringBuilder at n=20,000?  "
                + (ratio > 10 ? "yes" : "NO — something is wrong"));
        System.out.println();
        System.out.println("The exact ratio is deliberately not printed. Measured on one machine");
        System.out.println("it ranged from 142x to 786x across repetitions of the same run, because");
        System.out.println("JIT compilation and GC dominate a loop this small. A number that moves");
        System.out.println("by 5x between runs is not a measurement, and recording one as an answer");
        System.out.println("key would make this library's own CI flake. See the page for what to");
        System.out.println("use instead when you genuinely need a number.");
    }

    /** Counts characters copied, exactly as the += form would copy them. */
    static long countCopiesWithPlus(int n) {
        long copied = 0;
        String out = "";
        for (int i = 0; i < n; i++) {
            copied += out.length();   // every existing char is copied into the new String
            out += "x";
        }
        return copied;
    }

    static long timePlus(int n) {
        long start = System.nanoTime();
        String out = "";
        for (int i = 0; i < n; i++) out += "x";
        if (out.length() != n) throw new AssertionError();   // stop the optimiser deleting it
        return System.nanoTime() - start;
    }

    static long timeBuilder(int n) {
        long start = System.nanoTime();
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < n; i++) out.append('x');
        if (out.length() != n) throw new AssertionError();
        return System.nanoTime() - start;
    }
}
