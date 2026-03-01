package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.CompoundAssignmentTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.BinaryNode;

final class CompoundAssignmentTreeImpl
extends ExpressionTreeImpl
implements CompoundAssignmentTree {
    private final ExpressionTree var;
    private final ExpressionTree expr;
    private final Tree.Kind kind;

    CompoundAssignmentTreeImpl(BinaryNode node, ExpressionTree left, ExpressionTree right) {
        super(node);
        assert (node.isAssignment()) : "not an assignment node";
        this.var = left;
        this.expr = right;
        this.kind = CompoundAssignmentTreeImpl.getOperator(node.tokenType());
        assert (this.kind != Tree.Kind.ASSIGNMENT) : "compound assignment expected";
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
        return visitor.visitCompoundAssignment(this, data);
    }
}
