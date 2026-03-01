package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.BlockTree;
import org.openjdk.nashorn.api.tree.StatementTree;
import org.openjdk.nashorn.api.tree.StatementTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.Block;
import org.openjdk.nashorn.internal.ir.BlockStatement;

final class BlockTreeImpl
extends StatementTreeImpl
implements BlockTree {
    private final List<? extends StatementTree> statements;

    BlockTreeImpl(BlockStatement node, List<? extends StatementTree> statements) {
        super(node);
        this.statements = statements;
    }

    BlockTreeImpl(Block node, List<? extends StatementTree> statements) {
        super(node);
        this.statements = statements;
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.BLOCK;
    }

    @Override
    public List<? extends StatementTree> getStatements() {
        return this.statements;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitBlock(this, data);
    }
}
