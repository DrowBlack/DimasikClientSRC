package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ErroneousTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.ErrorNode;

final class ErroneousTreeImpl
extends ExpressionTreeImpl
implements ErroneousTree {
    ErroneousTreeImpl(ErrorNode errorNode) {
        super(errorNode);
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.ERROR;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitErroneous(this, data);
    }
}
