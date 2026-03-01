package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ConditionalLoopTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTree;

public interface DoWhileLoopTree
extends ConditionalLoopTree {
    @Override
    public ExpressionTree getCondition();

    @Override
    public StatementTree getStatement();
}
