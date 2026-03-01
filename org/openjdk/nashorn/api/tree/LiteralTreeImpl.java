package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.LiteralTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.LiteralNode;

final class LiteralTreeImpl
extends ExpressionTreeImpl
implements LiteralTree {
    private final Object value;
    private final Tree.Kind kind;

    LiteralTreeImpl(LiteralNode<?> node) {
        super(node);
        this.kind = LiteralTreeImpl.literalKind(node);
        this.value = node.getValue();
    }

    @Override
    public Tree.Kind getKind() {
        return this.kind;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    private static Tree.Kind literalKind(LiteralNode<?> node) {
        if (node.isBoolean()) {
            return Tree.Kind.BOOLEAN_LITERAL;
        }
        if (node.isNumeric()) {
            return Tree.Kind.NUMBER_LITERAL;
        }
        if (node.isString()) {
            return Tree.Kind.STRING_LITERAL;
        }
        if (node.isNull()) {
            return Tree.Kind.NULL_LITERAL;
        }
        throw new AssertionError((Object)("should not reach here: " + node.getValue()));
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitLiteral(this, data);
    }
}
