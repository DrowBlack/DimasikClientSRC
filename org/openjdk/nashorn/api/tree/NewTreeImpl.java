package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.NewTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.UnaryNode;
import org.openjdk.nashorn.internal.parser.TokenType;

final class NewTreeImpl
extends ExpressionTreeImpl
implements NewTree {
    private final ExpressionTree constrExpr;

    NewTreeImpl(UnaryNode node, ExpressionTree constrExpr) {
        super(node);
        assert (node.isTokenType(TokenType.NEW)) : "new expected";
        this.constrExpr = constrExpr;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.NEW;
    }

    @Override
    public ExpressionTree getConstructorExpression() {
        return this.constrExpr;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitNew(this, data);
    }
}
