package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;

public interface YieldTree
extends ExpressionTree {
    public ExpressionTree getExpression();

    public boolean isStar();
}
