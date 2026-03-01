package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.AssignmentTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.BinaryNode;

final class AssignmentTreeImpl
extends ExpressionTreeImpl
implements AssignmentTree {
    private final Tree.Kind kind;
    private final ExpressionTree var;
    private final ExpressionTree expr;

    AssignmentTreeImpl(BinaryNode node, ExpressionTree left, ExpressionTree right) {
        super(node);
        assert (node.isAssignment()) : "assignment node expected";
        this.var = left;
        this.expr = right;
        this.kind = AssignmentTreeImpl.getOperator(node.tokenType());
    }

    @Override
    public Tree.Kind getKind() {
        return this.kind;
    }

    @Override
    public ExpressionTree getVariable() {
        return this.var;
    }

    @Override
    public ExpressionTree getExpression() {
        return this.expr;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitAssignment(this, data);
    }
}
