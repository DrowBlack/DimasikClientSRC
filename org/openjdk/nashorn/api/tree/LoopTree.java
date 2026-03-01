package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.StatementTree;

public interface LoopTree
extends StatementTree {
    public StatementTree getStatement();
}
