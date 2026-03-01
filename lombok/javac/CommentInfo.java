package lombok.javac;

public final class CommentInfo {
    public final int pos;
    public final int prevEndPos;
    public final String content;
    public final int endPos;
    public final StartConnection start;
    public final EndConnection end;

    public CommentInfo(int prevEndPos, int pos, int endPos, String content, StartConnection start, EndConnection end) {
        this.pos = pos;
        this.prevEndPos = prevEndPos;
        this.endPos = endPos;
        this.content = content;
        this.start = start;
        this.end = end;
    }

    public boolean isJavadoc() {
        return this.content.startsWith("/**") && this.content.length() > 4;
    }

    public String toString() {
        return String.format("%d: %s (%s,%s)", new Object[]{this.pos, this.content, this.start, this.end});
    }

    public static enum EndConnection {
        DIRECT_AFTER_COMMENT,
        AFTER_COMMENT,
        ON_NEXT_LINE;

    }

    public static enum StartConnection {
        START_OF_LINE,
        ON_NEXT_LINE,
        DIRECT_AFTER_PREVIOUS,
        AFTER_PREVIOUS;

    }
}
