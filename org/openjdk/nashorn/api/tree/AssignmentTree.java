package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;

public interface AssignmentTree
extends ExpressionTree {
    public ExpressionTree getVariable();

    public ExpressionTree getExpression();
}
