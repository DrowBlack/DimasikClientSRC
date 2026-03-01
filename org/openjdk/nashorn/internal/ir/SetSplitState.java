package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.Statement;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;
import org.openjdk.nashorn.internal.runtime.Scope;

public final class SetSplitState
extends Statement {
    private static final long serialVersionUID = 1L;
    private final int state;

    public SetSplitState(int state, int lineNumber) {
        super(lineNumber, 0L, 0);
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        return visitor.enterSetSplitState(this) ? visitor.leaveSetSplitState(this) : this;
    }

    @Override
    public void toString(StringBuilder sb, boolean printType) {
        sb.append(CompilerConstants.SCOPE.symbolName()).append('.').append(Scope.SET_SPLIT_STATE.name()).append('(').append(this.state).append(");");
    }
}
