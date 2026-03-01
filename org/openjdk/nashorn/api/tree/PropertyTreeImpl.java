package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.FunctionExpressionTree;
import org.openjdk.nashorn.api.tree.PropertyTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeImpl;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.PropertyNode;

final class PropertyTreeImpl
extends TreeImpl
implements PropertyTree {
    private final ExpressionTree key;
    private final ExpressionTree value;
    private final FunctionExpressionTree getter;
    private final FunctionExpressionTree setter;
    private final boolean isStatic;
    private final boolean isComputed;

    PropertyTreeImpl(PropertyNode node, ExpressionTree key, ExpressionTree value, FunctionExpressionTree getter, FunctionExpressionTree setter) {
        super(node);
        this.key = key;
        this.value = value;
        this.getter = getter;
        this.setter = setter;
        this.isStatic = node.isStatic();
        this.isComputed = node.isComputed();
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.PROPERTY;
    }

    @Override
    public ExpressionTree getKey() {
        return this.key;
    }

    @Override
    public ExpressionTree getValue() {
        return this.value;
    }

    @Override
    public FunctionExpressionTree getGetter() {
        return this.getter;
    }

    @Override
    public FunctionExpressionTree getSetter() {
        return this.setter;
    }

    @Override
    public boolean isStatic() {
        return this.isStatic;
    }

    @Override
    public boolean isComputed() {
        return this.isComputed;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitProperty(this, data);
    }
}
