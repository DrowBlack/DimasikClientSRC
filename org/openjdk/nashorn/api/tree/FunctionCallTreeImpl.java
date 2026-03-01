package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.FunctionCallTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.CallNode;

class FunctionCallTreeImpl
extends ExpressionTreeImpl
implements FunctionCallTree {
    private final List<? extends ExpressionTree> arguments;
    private final ExpressionTree function;

    FunctionCallTreeImpl(CallNode node, ExpressionTree function, List<? extends ExpressionTree> arguments) {
        super(node);
        this.function = function;
        this.arguments = arguments;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.FUNCTION_INVOCATION;
    }

    @Override
    public ExpressionTree getFunctionSelect() {
        return this.function;
    }

    @Override
    public List<? extends ExpressionTree> getArguments() {
        return this.arguments;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitFunctionCall(this, data);
    }
}
