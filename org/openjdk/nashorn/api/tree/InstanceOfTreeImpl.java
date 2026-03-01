package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.BinaryTreeImpl;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.InstanceOfTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.BinaryNode;
import org.openjdk.nashorn.internal.parser.TokenType;

final class InstanceOfTreeImpl
extends BinaryTreeImpl
implements InstanceOfTree {
    InstanceOfTreeImpl(BinaryNode node, ExpressionTree expr, ExpressionTree type) {
        super(node, expr, type);
        assert (node.isTokenType(TokenType.INSTANCEOF)) : "instanceof expected";
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.INSTANCE_OF;
    }

    @Override
    public ExpressionTree getExpression() {
        return this.getLeftOperand();
    }

    @Override
    public Tree getType() {
        return this.getRightOperand();
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitInstanceOf(this, data);
    }
}
