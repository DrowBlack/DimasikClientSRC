package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ExpressionTree;

public interface ArrayLiteralTree
extends ExpressionTree {
    public List<? extends ExpressionTree> getElements();
}
