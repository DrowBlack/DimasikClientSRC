package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTree;
import org.openjdk.nashorn.api.tree.Tree;

public interface CaseTree
extends Tree {
    public ExpressionTree getExpression();

    public List<? extends StatementTree> getStatements();
}
