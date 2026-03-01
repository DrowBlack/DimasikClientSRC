package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.api.tree.WithTree;
import org.openjdk.nashorn.internal.ir.WithNode;

final class WithTreeImpl
extends StatementTreeImpl
implements WithTree {
    private final ExpressionTree scope;
    private final StatementTree stat;

    WithTreeImpl(WithNode node, ExpressionTree scope, StatementTree stat) {
        super(node);
        this.scope = scope;
        this.stat = stat;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.WITH;
    }

    @Override
    public ExpressionTree getScope() {
        return this.scope;
    }

    @Override
    public StatementTree getStatement() {
        return this.stat;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitWith(this, data);
    }
}
