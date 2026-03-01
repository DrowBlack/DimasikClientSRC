package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.SpreadTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.Expression;

final class SpreadTreeImpl
extends ExpressionTreeImpl
implements SpreadTree {
    private final ExpressionTree expr;

    SpreadTreeImpl(Expression exprNode, ExpressionTree expr) {
        super(exprNode);
        this.expr = expr;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.SPREAD;
    }

    @Override
    public ExpressionTree getExpression() {
        return this.expr;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitSpread(this, data);
    }
}
