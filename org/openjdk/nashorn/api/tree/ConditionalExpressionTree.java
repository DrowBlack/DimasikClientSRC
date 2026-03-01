package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;

public interface ConditionalExpressionTree
extends ExpressionTree {
    public ExpressionTree getCondition();

    public ExpressionTree getTrueExpression();

    public ExpressionTree getFalseExpression();
}
