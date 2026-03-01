package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.ThrowTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.ThrowNode;

final class ThrowTreeImpl
extends StatementTreeImpl
implements ThrowTree {
    private final ExpressionTree expr;

    ThrowTreeImpl(ThrowNode node, ExpressionTree expr) {
        super(node);
        this.expr = expr;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.THROW;
    }

    @Override
    public ExpressionTree getExpression() {
        return this.expr;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitThrow(this, data);
    }
}
