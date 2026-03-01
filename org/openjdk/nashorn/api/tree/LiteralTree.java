package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;

public interface LiteralTree
extends ExpressionTree {
    public Object getValue();
}
