package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTree;

public interface IfTree
extends StatementTree {
    public ExpressionTree getCondition();

    public StatementTree getThenStatement();

    public StatementTree getElseStatement();
}
