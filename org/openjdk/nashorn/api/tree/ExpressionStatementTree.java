package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTree;

public interface ExpressionStatementTree
extends StatementTree {
    public ExpressionTree getExpression();
}
