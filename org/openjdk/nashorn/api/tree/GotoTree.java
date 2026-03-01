package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.StatementTree;

public interface GotoTree
extends StatementTree {
    public String getLabel();
}
