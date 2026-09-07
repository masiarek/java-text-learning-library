// This file is named source_file_mode_java.java and declares `public class Main`.
// `javac` rejects that combination outright. The `java` launcher accepts it,
// because since Java 11 (JEP 330) a single .java file can be run directly:
// it is compiled in memory, and no .class file is written.
//
// That is what lets this library have no build tool, no Maven, no Gradle, and
// nothing to gitignore.
public class Main {
    public static void main(String[] args) {
        System.out.println("file name  : source_file_mode_java.java");
        System.out.println("class name : Main");
        System.out.println("run with   : java source_file_mode_java.java");
        System.out.println();
        // feature(), not java.version: the full string carries the patch level,
        // which differs between JDK builds and would lock this answer key to one.
        System.out.println("Java feature release = " + Runtime.version().feature());
        System.out.println();
        System.out.println("javac on this same file says:");
        System.out.println("  error: class Main is public, should be declared in a file named Main.java");
        System.out.println();
        System.out.println("Two tools, one file, opposite answers. The launcher relaxed the rule;");
        System.out.println("the compiler never did.");
    }
}
