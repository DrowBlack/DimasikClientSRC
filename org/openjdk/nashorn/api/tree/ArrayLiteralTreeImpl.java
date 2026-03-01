package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ArrayLiteralTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.LiteralNode;

final class ArrayLiteralTreeImpl
extends ExpressionTreeImpl
implements ArrayLiteralTree {
    private final List<? extends ExpressionTree> elements;

    ArrayLiteralTreeImpl(LiteralNode<?> node, List<? extends ExpressionTree> elements) {
        super(node);
        this.elements = elements;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.ARRAY_LITERAL;
    }

    @Override
    public List<? extends ExpressionTree> getElements() {
        return this.elements;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitArrayLiteral(this, data);
    }
}
