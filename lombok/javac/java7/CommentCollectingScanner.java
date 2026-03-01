package lombok.javac.java7;

import com.sun.tools.javac.parser.Scanner;
import com.sun.tools.javac.parser.ScannerFactory;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.nio.CharBuffer;
import lombok.javac.CommentInfo;
import lombok.javac.java7.CommentCollectingScannerFactory;

public class CommentCollectingScanner
extends Scanner {
    private final ListBuffer<CommentInfo> comments = new ListBuffer();
    private int endComment = 0;

    public CommentCollectingScanner(CommentCollectingScannerFactory factory, CharBuffer charBuffer) {
        super((ScannerFactory)factory, charBuffer);
    }

    public CommentCollectingScanner(CommentCollectingScannerFactory factory, char[] input, int inputLength) {
        super(factory, input, inputLength);
    }

    protected void processComment(Scanner.CommentStyle style) {
        int endPos;
        int prevEndPos = Math.max(this.prevEndPos(), this.endComment);
        int pos = this.pos();
        this.endComment = endPos = this.endPos();
        String content = new String(this.getRawCharacters(pos, endPos));
        CommentInfo.StartConnection start = this.determineStartConnection(prevEndPos, pos);
        CommentInfo.EndConnection end = this.determineEndConnection(endPos);
        CommentInfo comment = new CommentInfo(prevEndPos, pos, endPos, content, start, end);
        this.comments.append(comment);
        super.processComment(style);
    }

    private CommentInfo.EndConnection determineEndConnection(int pos) {
        boolean first = true;
        int i = pos;
        while (true) {
            char c;
            try {
                c = this.getRawCharacters(i, i + 1)[0];
            }
            catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                c = '\n';
            }
            if (this.isNewLine(c)) {
                return CommentInfo.EndConnection.ON_NEXT_LINE;
            }
            if (!Character.isWhitespace(c)) {
                return first ? CommentInfo.EndConnection.DIRECT_AFTER_COMMENT : CommentInfo.EndConnection.AFTER_COMMENT;
            }
            first = false;
            ++i;
        }
    }

    private CommentInfo.StartConnection determineStartConnection(int from, int to) {
        if (from == to) {
            return CommentInfo.StartConnection.DIRECT_AFTER_PREVIOUS;
        }
        char[] between = this.getRawCharacters(from, to);
        if (this.isNewLine(between[between.length - 1])) {
            return CommentInfo.StartConnection.START_OF_LINE;
        }
        char[] cArray = between;
        int n = between.length;
        int n2 = 0;
        while (n2 < n) {
            char c = cArray[n2];
            if (this.isNewLine(c)) {
                return CommentInfo.StartConnection.ON_NEXT_LINE;
            }
            ++n2;
        }
        return CommentInfo.StartConnection.AFTER_PREVIOUS;
    }

    private boolean isNewLine(char c) {
        return c == '\n' || c == '\r';
    }

    public List<CommentInfo> getComments() {
        return this.comments.toList();
    }
}
