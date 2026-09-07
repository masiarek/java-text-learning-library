// The same two invalid bytes, handed to the three ways Java reads text.
// They do not agree, and the disagreement is silent in exactly the place a
// beginner reaches for first.
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws Exception {
        byte[] bad = { (byte) 0xC3, (byte) 0x28 };   // a 2-byte lead with no continuation
        System.out.println("input bytes: C3 28  (a truncated UTF-8 sequence)");
        System.out.println();

        String s = new String(bad, StandardCharsets.UTF_8);
        System.out.printf("new String(bytes, UTF_8)   -> no exception; length %d, first char U+%04X%n",
                s.length(), s.codePointAt(0));
        System.out.println("                              U+FFFD is REPLACEMENT CHARACTER: data lost, quietly");

        try {
            StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bad));
            System.out.println("CharsetDecoder(REPORT)     -> no exception (!)");
        } catch (CharacterCodingException e) {
            System.out.println("CharsetDecoder(REPORT)     -> throws " + e.getClass().getSimpleName());
        }

        Path p = Files.createTempFile("malformed", ".txt");
        try {
            Files.write(p, bad);
            Files.readString(p);
            System.out.println("Files.readString(path)     -> no exception (!)");
        } catch (IOException e) {
            System.out.println("Files.readString(path)     -> throws " + e.getClass().getSimpleName());
        } finally {
            Files.deleteIfExists(p);
        }

        System.out.println();
        System.out.println("Two of the three refuse bad input. The String constructor —");
        System.out.println("the shortest and most obvious one — is the one that hides it.");
    }
}
