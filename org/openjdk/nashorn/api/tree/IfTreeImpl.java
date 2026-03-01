package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.IfTree;
import org.openjdk.nashorn.api.tree.StatementTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.IfNode;

final class IfTreeImpl
extends StatementTreeImpl
implements IfTree {
    private final ExpressionTree cond;
    private final StatementTree thenStat;
    private final StatementTree elseStat;

    IfTreeImpl(IfNode node, ExpressionTree cond, StatementTree thenStat, StatementTree elseStat) {
        super(node);
        this.cond = cond;
        this.thenStat = thenStat;
        this.elseStat = elseStat;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.IF;
    }

    @Override
    public ExpressionTree getCondition() {
        return this.cond;
    }

    @Override
    public StatementTree getThenStatement() {
        return this.thenStat;
    }

    @Override
    public StatementTree getElseStatement() {
        return this.elseStat;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitIf(this, data);
    }
}
