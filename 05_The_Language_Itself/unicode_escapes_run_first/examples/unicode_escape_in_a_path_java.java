// The same rule, seen from its unfriendly side: a Windows path in a COMMENT
// stops the file from compiling, because the escape begins wherever it
// appears and "sers" is not four hex digits.
public class Main {
    public static void main(String[] args) {
        // the log file is at C:\users\adam\notes.txt
        System.out.println("this line is never reached");
    }
}
