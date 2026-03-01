package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.api.tree.UnaryTree;
import org.openjdk.nashorn.internal.ir.UnaryNode;

class UnaryTreeImpl
extends ExpressionTreeImpl
implements UnaryTree {
    private final ExpressionTree expr;
    private final Tree.Kind kind;

    UnaryTreeImpl(UnaryNode node, ExpressionTree expr) {
        super(node);
        this.expr = expr;
        this.kind = UnaryTreeImpl.getOperator(node.tokenType());
    }

    @Override
    public Tree.Kind getKind() {
        return this.kind;
    }

    @Override
    public ExpressionTree getExpression() {
        return this.expr;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitUnary(this, data);
    }
}
