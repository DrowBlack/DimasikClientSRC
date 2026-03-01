package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.api.tree.YieldTree;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.UnaryNode;
import org.openjdk.nashorn.internal.parser.TokenType;

final class YieldTreeImpl
extends ExpressionTreeImpl
implements YieldTree {
    private final ExpressionTree expr;

    YieldTreeImpl(Expression exprNode, ExpressionTree expr) {
        super(exprNode);
        this.expr = expr;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.YIELD;
    }

    @Override
    public ExpressionTree getExpression() {
        return this.expr;
    }

    @Override
    public boolean isStar() {
        return ((UnaryNode)this.node).isTokenType(TokenType.YIELD_STAR);
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitYield(this, data);
    }
}
