package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ForLoopTree;
import org.openjdk.nashorn.api.tree.StatementTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.ForNode;

final class ForLoopTreeImpl
extends StatementTreeImpl
implements ForLoopTree {
    private final ExpressionTree init;
    private final ExpressionTree cond;
    private final ExpressionTree update;
    private final StatementTree stat;

    ForLoopTreeImpl(ForNode node, ExpressionTree init, ExpressionTree cond, ExpressionTree update, StatementTree stat) {
        super(node);
        assert (!node.isForIn()) : "for statement expected";
        this.init = init;
        this.cond = cond;
        this.update = update;
        this.stat = stat;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.FOR_LOOP;
    }

    @Override
    public ExpressionTree getInitializer() {
        return this.init;
    }

    @Override
    public ExpressionTree getCondition() {
        return this.cond;
    }

    @Override
    public ExpressionTree getUpdate() {
        return this.update;
    }

    @Override
    public StatementTree getStatement() {
        return this.stat;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitForLoop(this, data);
    }
}
