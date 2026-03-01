package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ClassExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.PropertyTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.ClassNode;

final class ClassExpressionTreeImpl
extends ExpressionTreeImpl
implements ClassExpressionTree {
    private final IdentifierTree name;
    private final ExpressionTree classHeritage;
    private final PropertyTree constructor;
    private final List<? extends PropertyTree> classElements;

    ClassExpressionTreeImpl(ClassNode cn, IdentifierTree name, ExpressionTree classHeritage, PropertyTree constructor, List<? extends PropertyTree> classElements) {
        super(cn);
        this.name = name;
        this.classHeritage = classHeritage;
        this.constructor = constructor;
        this.classElements = classElements;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.CLASS_EXPRESSION;
    }

    @Override
    public IdentifierTree getName() {
        return this.name;
    }

    @Override
    public ExpressionTree getClassHeritage() {
        return this.classHeritage;
    }

    @Override
    public PropertyTree getConstructor() {
        return this.constructor;
    }

    @Override
    public List<? extends PropertyTree> getClassElements() {
        return this.classElements;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitClassExpression(this, data);
    }
}
