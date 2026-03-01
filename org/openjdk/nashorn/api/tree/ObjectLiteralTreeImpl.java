package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.ObjectLiteralTree;
import org.openjdk.nashorn.api.tree.PropertyTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.ObjectNode;

final class ObjectLiteralTreeImpl
extends ExpressionTreeImpl
implements ObjectLiteralTree {
    private final List<? extends PropertyTree> props;

    ObjectLiteralTreeImpl(ObjectNode node, List<? extends PropertyTree> props) {
        super(node);
        this.props = props;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.OBJECT_LITERAL;
    }

    @Override
    public List<? extends PropertyTree> getProperties() {
        return this.props;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitObjectLiteral(this, data);
    }
}
