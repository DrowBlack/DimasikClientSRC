package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.api.tree.WhileLoopTree;
import org.openjdk.nashorn.internal.ir.WhileNode;

final class WhileLoopTreeImpl
extends StatementTreeImpl
implements WhileLoopTree {
    private final ExpressionTree cond;
    private final StatementTree stat;

    WhileLoopTreeImpl(WhileNode node, ExpressionTree cond, StatementTree stat) {
        super(node);
        assert (!node.isDoWhile()) : "while expected";
        this.cond = cond;
        this.stat = stat;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.WHILE_LOOP;
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
        return visitor.visitWhileLoop(this, data);
    }
}
