package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.api.tree.VariableTree;
import org.openjdk.nashorn.internal.ir.VarNode;

final class VariableTreeImpl
extends StatementTreeImpl
implements VariableTree {
    private final IdentifierTree ident;
    private final ExpressionTree init;

    VariableTreeImpl(VarNode node, IdentifierTree ident, ExpressionTree init) {
        super(node);
        this.ident = ident;
        this.init = init;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.VARIABLE;
    }

    @Override
    public ExpressionTree getBinding() {
        return this.ident;
    }

    @Override
    public ExpressionTree getInitializer() {
        return this.init;
    }

    @Override
    public boolean isConst() {
        return ((VarNode)this.node).isConst();
    }

    @Override
    public boolean isLet() {
        return ((VarNode)this.node).isLet();
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitVariable(this, data);
    }
}
