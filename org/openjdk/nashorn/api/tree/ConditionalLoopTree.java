package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.LoopTree;

public interface ConditionalLoopTree
extends LoopTree {
    public ExpressionTree getCondition();
}
