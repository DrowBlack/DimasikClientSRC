package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.LoopTree;
import org.openjdk.nashorn.api.tree.StatementTree;

public interface ForInLoopTree
extends LoopTree {
    public ExpressionTree getVariable();

    public ExpressionTree getExpression();

    @Override
    public StatementTree getStatement();

    public boolean isForEach();
}
