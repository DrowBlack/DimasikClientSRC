package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.MemberSelectTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.AccessNode;

final class MemberSelectTreeImpl
extends ExpressionTreeImpl
implements MemberSelectTree {
    private final String ident;
    private final ExpressionTree expr;

    MemberSelectTreeImpl(AccessNode node, ExpressionTree expr) {
        super(node);
        this.ident = node.getProperty();
        this.expr = expr;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.MEMBER_SELECT;
    }

    @Override
    public ExpressionTree getExpression() {
        return this.expr;
    }

    @Override
    public String getIdentifier() {
        return this.ident;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitMemberSelect(this, data);
    }
}
