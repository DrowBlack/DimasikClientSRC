package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.Tree;

public interface InstanceOfTree
extends ExpressionTree {
    public ExpressionTree getExpression();

    public Tree getType();
}
