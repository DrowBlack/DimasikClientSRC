package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.BinaryTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.BinaryNode;

class BinaryTreeImpl
extends ExpressionTreeImpl
implements BinaryTree {
    private final Tree.Kind kind;
    private final ExpressionTree left;
    private final ExpressionTree right;

    BinaryTreeImpl(BinaryNode node, ExpressionTree left, ExpressionTree right) {
        super(node);
        assert (!node.isAssignment()) : "assignment node";
        this.left = left;
        this.right = right;
        this.kind = BinaryTreeImpl.getOperator(node.tokenType());
    }

    @Override
    public Tree.Kind getKind() {
        return this.kind;
    }

    @Override
    public ExpressionTree getLeftOperand() {
        return this.left;
    }

    @Override
    public ExpressionTree getRightOperand() {
        return this.right;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitBinary(this, data);
    }
}
