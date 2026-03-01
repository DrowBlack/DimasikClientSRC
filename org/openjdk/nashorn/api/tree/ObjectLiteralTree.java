package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.PropertyTree;

public interface ObjectLiteralTree
extends ExpressionTree {
    public List<? extends PropertyTree> getProperties();
}
