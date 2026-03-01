package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.BlockTree;
import org.openjdk.nashorn.api.tree.CatchTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeImpl;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.CatchNode;

final class CatchTreeImpl
extends TreeImpl
implements CatchTree {
    private final ExpressionTree param;
    private final BlockTree block;
    private final ExpressionTree condition;

    CatchTreeImpl(CatchNode node, ExpressionTree param, BlockTree block, ExpressionTree condition) {
        super(node);
        this.param = param;
        this.block = block;
        this.condition = condition;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.CATCH;
    }

    @Override
    public ExpressionTree getParameter() {
        return this.param;
    }

    @Override
    public BlockTree getBlock() {
        return this.block;
    }

    @Override
    public ExpressionTree getCondition() {
        return this.condition;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitCatch(this, data);
    }
}
