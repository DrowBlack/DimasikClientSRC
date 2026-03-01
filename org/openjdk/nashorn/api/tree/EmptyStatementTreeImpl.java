package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.EmptyStatementTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.EmptyNode;

final class EmptyStatementTreeImpl
extends StatementTreeImpl
implements EmptyStatementTree {
    EmptyStatementTreeImpl(EmptyNode node) {
        super(node);
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.EMPTY_STATEMENT;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitEmptyStatement(this, data);
    }
}
