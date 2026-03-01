package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.BlockTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.FunctionDeclarationTree;
import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.IdentifierTreeImpl;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.FunctionNode;
import org.openjdk.nashorn.internal.ir.VarNode;

final class FunctionDeclarationTreeImpl
extends StatementTreeImpl
implements FunctionDeclarationTree {
    private final FunctionNode funcNode;
    private final IdentifierTree funcName;
    private final List<? extends ExpressionTree> params;
    private final BlockTree body;

    FunctionDeclarationTreeImpl(VarNode node, List<? extends ExpressionTree> params, BlockTree body) {
        super(node);
        assert (node.getInit() instanceof FunctionNode) : "function expected";
        this.funcNode = (FunctionNode)node.getInit();
        assert (this.funcNode.isDeclared()) : "function declaration expected";
        this.funcName = this.funcNode.isAnonymous() ? null : new IdentifierTreeImpl(node.getName());
        this.params = params;
        this.body = body;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.FUNCTION;
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
    public BlockTree getBody() {
        return this.body;
    }

    @Override
    public boolean isStrict() {
        return this.funcNode.isStrict();
    }

    @Override
    public boolean isGenerator() {
        return this.funcNode.getKind() == FunctionNode.Kind.GENERATOR;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitFunctionDeclaration(this, data);
    }
}
