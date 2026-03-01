package org.openjdk.nashorn.internal.parser;

import java.util.ArrayList;
import java.util.List;
import org.openjdk.nashorn.internal.ir.Statement;
import org.openjdk.nashorn.internal.parser.ParserContextNode;

abstract class ParserContextBaseNode
implements ParserContextNode {
    protected int flags;
    private List<Statement> statements = new ArrayList<Statement>();

    @Override
    public int getFlags() {
        return this.flags;
    }

    protected int getFlag(int flag) {
        return this.flags & flag;
    }

    @Override
    public int setFlag(int flag) {
        this.flags |= flag;
        return this.flags;
    }

    @Override
    public List<Statement> getStatements() {
        return this.statements;
    }

    @Override
    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public void appendStatement(Statement statement) {
        this.statements.add(statement);
    }

    @Override
    public void prependStatement(Statement statement) {
        this.statements.add(0, statement);
    }
}
