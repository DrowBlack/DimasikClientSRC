package org.openjdk.nashorn.internal.parser;

import java.util.List;
import org.openjdk.nashorn.internal.ir.Statement;

interface ParserContextNode {
    public int getFlags();

    public int setFlag(int var1);

    public List<Statement> getStatements();

    public void setStatements(List<Statement> var1);

    public void appendStatement(Statement var1);

    public void prependStatement(Statement var1);
}
