package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTree;

public interface VariableTree
extends StatementTree {
    public ExpressionTree getBinding();

    public ExpressionTree getInitializer();

    public boolean isConst();

    public boolean isLet();
}
