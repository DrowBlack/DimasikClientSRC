package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ExpressionTree;

public interface TemplateLiteralTree
extends ExpressionTree {
    public List<? extends ExpressionTree> getExpressions();
}
