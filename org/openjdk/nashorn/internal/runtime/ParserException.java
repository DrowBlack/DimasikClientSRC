package org.openjdk.nashorn.internal.runtime;

import org.openjdk.nashorn.api.scripting.NashornException;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.parser.Token;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.JSErrorType;
import org.openjdk.nashorn.internal.runtime.Source;

public final class ParserException
extends NashornException {
    private final Source source;
    private final long token;
    private final JSErrorType errorType;

    public ParserException(String msg) {
        this(JSErrorType.SYNTAX_ERROR, msg, null, -1, -1, -1L);
    }

    public ParserException(JSErrorType errorType, String msg, Source source, int line, int column, long token) {
        super(msg, source != null ? source.getName() : null, line, column);
        this.source = source;
        this.token = token;
        this.errorType = errorType;
    }

    public Source getSource() {
        return this.source;
    }

    public long getToken() {
        return this.token;
    }

    public int getPosition() {
        return Token.descPosition(this.token);
    }

    public JSErrorType getErrorType() {
        return this.errorType;
    }

    public void throwAsEcmaException() {
        throw ECMAErrors.asEcmaException(this);
    }

    public void throwAsEcmaException(Global global) {
        throw ECMAErrors.asEcmaException(global, this);
    }
}
