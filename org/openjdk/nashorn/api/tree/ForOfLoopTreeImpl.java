package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ForOfLoopTree;
import org.openjdk.nashorn.api.tree.StatementTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.ForNode;

final class ForOfLoopTreeImpl
extends StatementTreeImpl
implements ForOfLoopTree {
    private final ExpressionTree lhsExpr;
    private final ExpressionTree expr;
    private final StatementTree stat;

    ForOfLoopTreeImpl(ForNode node, ExpressionTree lhsExpr, ExpressionTree expr, StatementTree stat) {
        super(node);
        assert (node.isForIn()) : "for ..in expected";
        this.lhsExpr = lhsExpr;
        this.expr = expr;
        this.stat = stat;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.FOR_IN_LOOP;
    }

    @Override
    public ExpressionTree getVariable() {
        return this.lhsExpr;
    }

    @Override
    public ExpressionTree getExpression() {
        return this.expr;
    }

    @Override
    public StatementTree getStatement() {
        return this.stat;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitForOfLoop(this, data);
    }
}
