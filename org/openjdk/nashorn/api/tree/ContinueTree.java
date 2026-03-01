package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.GotoTree;

public interface ContinueTree
extends GotoTree {
    @Override
    public String getLabel();
}
