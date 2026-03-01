package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.StatementTree;

public interface LabeledStatementTree
extends StatementTree {
    public String getLabel();

    public StatementTree getStatement();
}
