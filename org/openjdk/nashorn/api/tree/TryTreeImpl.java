package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.BlockTree;
import org.openjdk.nashorn.api.tree.CatchTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.api.tree.TryTree;
import org.openjdk.nashorn.internal.ir.TryNode;

final class TryTreeImpl
extends StatementTreeImpl
implements TryTree {
    private final BlockTree block;
    private final List<? extends CatchTree> catches;
    private final BlockTree finallyBlock;

    TryTreeImpl(TryNode node, BlockTree block, List<? extends CatchTree> catches, BlockTree finallyBlock) {
        super(node);
        this.block = block;
        this.catches = catches;
        this.finallyBlock = finallyBlock;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.TRY;
    }

    @Override
    public BlockTree getBlock() {
        return this.block;
    }

    @Override
    public List<? extends CatchTree> getCatches() {
        return this.catches;
    }

    @Override
    public BlockTree getFinallyBlock() {
        return this.finallyBlock;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitTry(this, data);
    }
}
