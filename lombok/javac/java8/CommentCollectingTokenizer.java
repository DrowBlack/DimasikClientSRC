package lombok.javac.java8;

import com.sun.tools.javac.parser.JavaTokenizer;
import com.sun.tools.javac.parser.ScannerFactory;
import com.sun.tools.javac.parser.Tokens;
import com.sun.tools.javac.parser.UnicodeReader;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.nio.CharBuffer;
import lombok.javac.CommentInfo;

class CommentCollectingTokenizer
extends JavaTokenizer {
    private static final boolean tokenizerIsUnicodeReader = JavaTokenizer.class.getSuperclass().getSimpleName().equals("UnicodeReader");
    private int prevEndPosition = 0;
    private final ListBuffer<CommentInfo> comments = new ListBuffer();
    private final ListBuffer<Integer> textBlockStarts;
    private int endComment = 0;

    static CommentCollectingTokenizer create(ScannerFactory fac, char[] buf, int inputLength, boolean findTextBlocks) {
        if (tokenizerIsUnicodeReader) {
            return new CommentCollectingTokenizer(fac, buf, inputLength, findTextBlocks, true);
        }
        return new CommentCollectingTokenizer(fac, buf, inputLength, findTextBlocks);
    }

    private CommentCollectingTokenizer(ScannerFactory fac, char[] buf, int inputLength, boolean findTextBlocks) {
        super(fac, (UnicodeReader)new PositionUnicodeReader(fac, buf, inputLength));
        this.textBlockStarts = findTextBlocks ? new ListBuffer() : null;
    }

    private CommentCollectingTokenizer(ScannerFactory fac, char[] buf, int inputLength, boolean findTextBlocks, boolean java16Signature) {
        super(fac, buf, inputLength);
        this.textBlockStarts = findTextBlocks ? new ListBuffer() : null;
    }

    int pos() {
        if (tokenizerIsUnicodeReader) {
            return this.position();
        }
        return ((PositionUnicodeReader)this.reader).pos();
    }

    @Override
    public Tokens.Token readToken() {
        char[] start;
        Tokens.Token token = super.readToken();
        this.prevEndPosition = this.pos();
        if (this.textBlockStarts != null && this.prevEndPosition - token.pos > 5 && token.getClass().getName().endsWith("$StringToken") && (start = this.reader().getRawCharacters(token.pos, token.pos + 3))[0] == '\"' && start[1] == '\"' && start[2] == '\"') {
            this.textBlockStarts.append(token.pos);
        }
        return token;
    }

    @Override
    protected Tokens.Comment processComment(int pos, int endPos, Tokens.Comment.CommentStyle style) {
        int prevEndPos = Math.max(this.prevEndPosition, this.endComment);
        this.endComment = endPos;
        String content = new String(this.reader().getRawCharacters(pos, endPos));
        CommentInfo.StartConnection start = this.determineStartConnection(prevEndPos, pos);
        CommentInfo.EndConnection end = this.determineEndConnection(endPos);
        CommentInfo comment = new CommentInfo(prevEndPos, pos, endPos, content, start, end);
        this.comments.append(comment);
        return super.processComment(pos, endPos, style);
    }

    private CommentInfo.EndConnection determineEndConnection(int pos) {
        boolean first = true;
        int i = pos;
        while (true) {
            char c;
            try {
                c = this.reader().getRawCharacters(i, i + 1)[0];
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
        char[] between = this.reader().getRawCharacters(from, to);
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

    public List<Integer> getTextBlockStarts() {
        return this.textBlockStarts == null ? List.nil() : this.textBlockStarts.toList();
    }

    private UnicodeReader reader() {
        if (tokenizerIsUnicodeReader) {
            return this;
        }
        return this.reader;
    }

    static class PositionUnicodeReader
    extends UnicodeReader {
        protected PositionUnicodeReader(ScannerFactory sf, char[] input, int inputLength) {
            super(sf, input, inputLength);
        }

        public PositionUnicodeReader(ScannerFactory sf, CharBuffer buffer) {
            super(sf, buffer);
        }

        int pos() {
            return this.bp;
        }
    }
}
