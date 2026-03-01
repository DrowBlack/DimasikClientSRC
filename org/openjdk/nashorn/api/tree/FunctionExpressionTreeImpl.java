package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.BlockTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.ExpressionTreeImpl;
import org.openjdk.nashorn.api.tree.FunctionExpressionTree;
import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.IdentifierTreeImpl;
import org.openjdk.nashorn.api.tree.ReturnTree;
import org.openjdk.nashorn.api.tree.StatementTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.FunctionNode;

final class FunctionExpressionTreeImpl
extends ExpressionTreeImpl
implements FunctionExpressionTree {
    private final FunctionNode funcNode;
    private final IdentifierTree funcName;
    private final List<? extends ExpressionTree> params;
    private final Tree body;

    FunctionExpressionTreeImpl(FunctionNode node, List<? extends ExpressionTree> params, BlockTree body) {
        super(node);
        this.funcNode = node;
        assert (!this.funcNode.isDeclared() || this.funcNode.isAnonymous()) : "function expression expected";
        FunctionNode.Kind kind = node.getKind();
        this.funcName = node.isAnonymous() || kind == FunctionNode.Kind.GETTER || kind == FunctionNode.Kind.SETTER ? null : new IdentifierTreeImpl(node.getIdent());
        this.params = params;
        if (node.getFlag(0x4000000)) {
            StatementTree first = body.getStatements().get(0);
            assert (first instanceof ReturnTree) : "consise func. expression should have a return statement";
            this.body = ((ReturnTree)first).getExpression();
        } else {
            this.body = body;
        }
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.FUNCTION_EXPRESSION;
    }

    @Override
    public IdentifierTree getName() {
        return this.funcName;
    }

    @Override
    public List<? extends ExpressionTree> getParameters() {
        return this.params;
    }

    @Override
    public Tree getBody() {
        return this.body;
    }

    @Override
    public boolean isStrict() {
        return this.funcNode.isStrict();
    }

    @Override
    public boolean isArrow() {
        return this.funcNode.getKind() == FunctionNode.Kind.ARROW;
    }

    @Override
    public boolean isGenerator() {
        return this.funcNode.getKind() == FunctionNode.Kind.GENERATOR;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitFunctionExpression(this, data);
    }
}
