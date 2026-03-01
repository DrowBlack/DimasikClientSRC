package org.openjdk.nashorn.internal.ir;

import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.codegen.types.Type;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.LexicalContext;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.annotations.Ignore;
import org.openjdk.nashorn.internal.ir.visitor.NodeVisitor;
import org.openjdk.nashorn.internal.runtime.Scope;

public final class GetSplitState
extends Expression {
    private static final long serialVersionUID = 1L;
    @Ignore
    public static final GetSplitState INSTANCE = new GetSplitState();

    private GetSplitState() {
        super(0L, 0);
    }

    @Override
    public Type getType() {
        return Type.INT;
    }

    @Override
    public Node accept(NodeVisitor<? extends LexicalContext> visitor) {
        return visitor.enterGetSplitState(this) ? visitor.leaveGetSplitState(this) : this;
    }

    @Override
    public void toString(StringBuilder sb, boolean printType) {
        if (printType) {
            sb.append("{I}");
        }
        sb.append(CompilerConstants.SCOPE.symbolName()).append('.').append(Scope.GET_SPLIT_STATE.name()).append("()");
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
