package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.DoWhileLoopTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.WhileNode;

final class DoWhileLoopTreeImpl
extends StatementTreeImpl
implements DoWhileLoopTree {
    private final ExpressionTree cond;
    private final StatementTree stat;

    DoWhileLoopTreeImpl(WhileNode node, ExpressionTree cond, StatementTree stat) {
        super(node);
        assert (node.isDoWhile()) : "do while expected";
        this.cond = cond;
        this.stat = stat;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.DO_WHILE_LOOP;
    }

    @Override
    public ExpressionTree getCondition() {
        return this.cond;
    }

    @Override
    public StatementTree getStatement() {
        return this.stat;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitDoWhileLoop(this, data);
    }
}
