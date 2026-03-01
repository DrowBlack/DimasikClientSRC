package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.Diagnostic;
import org.openjdk.nashorn.internal.parser.Token;
import org.openjdk.nashorn.internal.runtime.ParserException;

final class DiagnosticImpl
implements Diagnostic {
    private final ParserException exp;
    private final Diagnostic.Kind kind;

    DiagnosticImpl(ParserException exp, Diagnostic.Kind kind) {
        this.exp = exp;
        this.kind = kind;
    }

    @Override
    public Diagnostic.Kind getKind() {
        return this.kind;
    }

    @Override
    public long getPosition() {
        return this.exp.getPosition();
    }

    @Override
    public String getFileName() {
        return this.exp.getFileName();
    }

    @Override
    public long getLineNumber() {
        return this.exp.getLineNumber();
    }

    @Override
    public long getColumnNumber() {
        return this.exp.getColumnNumber();
    }

    @Override
    public String getCode() {
        long token = this.exp.getToken();
        return token < 0L ? null : Token.toString(null, token, true);
    }

    @Override
    public String getMessage() {
        return this.exp.getMessage();
    }

    public String toString() {
        return this.getMessage();
    }
}
