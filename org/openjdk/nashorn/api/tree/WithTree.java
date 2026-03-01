package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTree;

public interface WithTree
extends StatementTree {
    public ExpressionTree getScope();

    public StatementTree getStatement();
}
