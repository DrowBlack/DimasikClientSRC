package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.Statement;
import org.openjdk.nashorn.internal.ir.annotations.Immutable;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;

@Immutable
public final class DebuggerNode
extends Statement {
    private static final long serialVersionUID = 1L;

    public DebuggerNode(int lineNumber, long token, int finish) {
        super(lineNumber, token, finish);
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        if (visitor.enterDebuggerNode(this)) {
            return visitor.leaveDebuggerNode(this);
        }
        return this;
    }

    @Override
    public void toString(StringBuilder sb, boolean printType) {
        sb.append("debugger");
    }
}
