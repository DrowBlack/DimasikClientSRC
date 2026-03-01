package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.TemplateLiteralTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.Expression;

final class TemplateLiteralTreeImpl
extends ExpressionTreeImpl
implements TemplateLiteralTree {
    private final List<? extends ExpressionTree> expressions;

    TemplateLiteralTreeImpl(Expression node, List<? extends ExpressionTree> expressions) {
        super(node);
        this.expressions = expressions;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.TEMPLATE_LITERAL;
    }

    @Override
    public List<? extends ExpressionTree> getExpressions() {
        return this.expressions;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitTemplateLiteral(this, data);
    }
}
