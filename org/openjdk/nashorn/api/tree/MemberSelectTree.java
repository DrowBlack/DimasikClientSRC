package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;

public interface MemberSelectTree
extends ExpressionTree {
    public ExpressionTree getExpression();

    public String getIdentifier();
}
