// A UTF-8 file written by Excel, Notepad or many Windows tools starts with
// EF BB BF. Java reads that as a character. Every one of your string
// comparisons then fails on the first line of the file, and only the first.
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Path p = Files.createTempFile("bom", ".csv");
        try {
            Files.write(p, new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF,
                                        'n', 'a', 'm', 'e' });
            String read = Files.readString(p);

            System.out.println("file bytes      : EF BB BF 6E 61 6D 65   (BOM + \"name\")");
            System.out.printf("readString len  : %d   <- 4 would mean the BOM was stripped%n", read.length());
            System.out.printf("first code point: U+%04X  (ZERO WIDTH NO-BREAK SPACE)%n", read.codePointAt(0));
            System.out.println("read.equals(\"name\")        = " + read.equals("name"));
            System.out.println("read.startsWith(\"name\")    = " + read.startsWith("name"));
            System.out.println();
            System.out.println("and it is INVISIBLE when printed: [" + read + "]");
            System.out.println();
            System.out.println("The fix is one line, and there is no library call for it:");
            String fixed = read.startsWith("\uFEFF") ? read.substring(1) : read;
            System.out.println("  fixed.equals(\"name\")     = " + fixed.equals("name"));
        } finally {
            Files.deleteIfExists(p);
        }
    }
}
