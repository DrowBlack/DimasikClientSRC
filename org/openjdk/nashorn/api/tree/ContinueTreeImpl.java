package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ContinueTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.ContinueNode;

final class ContinueTreeImpl
extends StatementTreeImpl
implements ContinueTree {
    private final String label;

    ContinueTreeImpl(ContinueNode node) {
        super(node);
        this.label = node.getLabelName();
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.CONTINUE;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitContinue(this, data);
    }
}
