package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionStatementTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.ExpressionStatement;

final class ExpressionStatementTreeImpl
extends StatementTreeImpl
implements ExpressionStatementTree {
    private final ExpressionTree expr;

    ExpressionStatementTreeImpl(ExpressionStatement es, ExpressionTree expr) {
        super(es);
        this.expr = expr;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.EXPRESSION_STATEMENT;
    }

    @Override
    public ExpressionTree getExpression() {
        return this.expr;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitExpressionStatement(this, data);
    }
}
