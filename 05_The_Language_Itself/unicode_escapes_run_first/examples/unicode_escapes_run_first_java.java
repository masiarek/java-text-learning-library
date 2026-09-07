// A backslash-u escape is translated BEFORE the compiler tokenises the file — before
// it knows what a comment is, or a string, or a line. So an escape inside a
// comment is still an escape, and U+000A is a newline, which ends the comment.
//
// The line below is a comment. Watch what it does anyway.
public class Main {
    public static void main(String[] args) {
        // this text is inside a comment \u000A System.out.println("  >> I RAN, FROM INSIDE A COMMENT");
        System.out.println("  >> the ordinary line");
    }
}
