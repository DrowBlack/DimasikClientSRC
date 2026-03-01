package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.BlockTree;
import org.openjdk.nashorn.api.tree.CatchTree;
import org.openjdk.nashorn.api.tree.StatementTree;

public interface TryTree
extends StatementTree {
    public BlockTree getBlock();

    public List<? extends CatchTree> getCatches();

    public BlockTree getFinallyBlock();
}
