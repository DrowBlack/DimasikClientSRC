package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.codegen.Label;
import org.openjdk.nashorn.internal.ir.BreakableNode;
import org.openjdk.nashorn.internal.ir.JumpStatement;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.LocalVariableConversion;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.annotations.Immutable;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;

@Immutable
public final class BreakNode
extends JumpStatement {
    private static final long serialVersionUID = 1L;

    public BreakNode(int lineNumber, long token, int finish, String labelName) {
        super(lineNumber, token, finish, labelName);
    }

    private BreakNode(BreakNode breakNode, LocalVariableConversion conversion) {
        super(breakNode, conversion);
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        if (visitor.enterBreakNode(this)) {
            return visitor.leaveBreakNode(this);
        }
        return this;
    }

    @Override
    JumpStatement createNewJumpStatement(LocalVariableConversion conversion) {
        return new BreakNode(this, conversion);
    }

    @Override
    String getStatementName() {
        return "break";
    }

    @Override
    public BreakableNode getTarget(LexicalContext lc) {
        return lc.getBreakable(this.getLabelName());
    }

    @Override
    Label getTargetLabel(BreakableNode target) {
        return target.getBreakLabel();
    }
}
