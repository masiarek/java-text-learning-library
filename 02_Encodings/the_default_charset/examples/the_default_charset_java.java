// Java 18 (JEP 400) made UTF-8 the default charset everywhere. What it did NOT
// do is make the JVM stop knowing about your locale — and the two properties
// that report those two things are one letter apart in the docs and miles apart
// in behaviour.
import java.nio.charset.Charset;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        System.out.println("Charset.defaultCharset() = " + Charset.defaultCharset());
        System.out.println("file.encoding            = " + System.getProperty("file.encoding"));
        System.out.println("native.encoding          = " + System.getProperty("native.encoding"));
        System.out.println("stdout.encoding          = " + System.getProperty("stdout.encoding"));
        System.out.println("Locale.getDefault()      = " + Locale.getDefault());
        System.out.println();
        System.out.println("This run is under LC_ALL=C. file.encoding is UTF-8 anyway;");
        System.out.println("native.encoding followed the locale. Since Java 18 those two");
        System.out.println("can disagree, and before Java 18 they could not.");
    }
}
