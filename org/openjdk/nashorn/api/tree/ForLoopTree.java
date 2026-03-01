package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ConditionalLoopTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTree;

public interface ForLoopTree
extends ConditionalLoopTree {
    public ExpressionTree getInitializer();

    @Override
    public ExpressionTree getCondition();

    public ExpressionTree getUpdate();

    @Override
    public StatementTree getStatement();
}
