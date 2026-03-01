package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.LexicalContextNode;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.Statement;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;

abstract class LexicalContextStatement
extends Statement
implements LexicalContextNode {
    private static final long serialVersionUID = 1L;

    protected LexicalContextStatement(int lineNumber, long token, int finish) {
        super(lineNumber, token, finish);
    }

    protected LexicalContextStatement(LexicalContextStatement node) {
        super(node);
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        return LexicalContextNode.Acceptor.accept(this, visitor);
    }
}
