package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.FunctionExpressionTree;
import org.openjdk.nashorn.api.tree.Tree;

public interface PropertyTree
extends Tree {
    public ExpressionTree getKey();

    public ExpressionTree getValue();

    public FunctionExpressionTree getGetter();

    public FunctionExpressionTree getSetter();

    public boolean isStatic();

    public boolean isComputed();
}
