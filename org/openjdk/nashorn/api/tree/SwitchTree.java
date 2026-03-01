package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.CaseTree;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.StatementTree;

public interface SwitchTree
extends StatementTree {
    public ExpressionTree getExpression();

    public List<? extends CaseTree> getCases();
}
