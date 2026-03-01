package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;

public interface NewTree
extends ExpressionTree {
    public ExpressionTree getConstructorExpression();
}
