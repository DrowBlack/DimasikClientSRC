package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ExpressionTree;

public interface FunctionCallTree
extends ExpressionTree {
    public ExpressionTree getFunctionSelect();

    public List<? extends ExpressionTree> getArguments();
}
