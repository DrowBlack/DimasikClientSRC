package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.codegen.types.Type;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.annotations.Immutable;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;

@Immutable
public final class ErrorNode
extends Expression {
    private static final long serialVersionUID = 1L;

    public ErrorNode(long token, int finish) {
        super(token, finish);
    }

    @Override
    public Type getType() {
        return Type.OBJECT;
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        if (visitor.enterErrorNode(this)) {
            return visitor.leaveErrorNode(this);
        }
        return this;
    }

    @Override
    public void toString(StringBuilder sb, boolean printType) {
        sb.append("<error>");
    }
}
