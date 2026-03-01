package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.BreakTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.BreakNode;

final class BreakTreeImpl
extends StatementTreeImpl
implements BreakTree {
    private final String label;

    BreakTreeImpl(BreakNode node) {
        super(node);
        this.label = node.getLabelName();
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.BREAK;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitBreak(this, data);
    }
}
