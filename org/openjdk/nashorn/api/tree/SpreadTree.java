package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;

public interface SpreadTree
extends ExpressionTree {
    public ExpressionTree getExpression();
}
