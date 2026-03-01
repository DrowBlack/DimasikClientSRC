package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.Tree;

public interface FunctionExpressionTree
extends ExpressionTree {
    public IdentifierTree getName();

    public List<? extends ExpressionTree> getParameters();

    public Tree getBody();

    public boolean isStrict();

    public boolean isArrow();

    public boolean isGenerator();
}
