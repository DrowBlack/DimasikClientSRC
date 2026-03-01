package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.BlockTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.Tree;

public interface CatchTree
extends Tree {
    public ExpressionTree getParameter();

    public BlockTree getBlock();

    public ExpressionTree getCondition();
}
