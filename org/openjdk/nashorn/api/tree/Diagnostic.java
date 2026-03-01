package org.openjdk.nashorn.api.tree;

public interface Diagnostic {
    public static final long NOPOS = -1L;

    public Kind getKind();

    public long getPosition();

    public String getFileName();

    public long getLineNumber();

    public long getColumnNumber();

    public String getCode();

    public String getMessage();

    public static enum Kind {
        ERROR,
        WARNING,
        MANDATORY_WARNING,
        NOTE,
        OTHER;

    }
}
