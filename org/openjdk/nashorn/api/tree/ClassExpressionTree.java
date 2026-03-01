package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ExpressionTree;
import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.PropertyTree;

public interface ClassExpressionTree
extends ExpressionTree {
    public IdentifierTree getName();

    public ExpressionTree getClassHeritage();

    public PropertyTree getConstructor();

    public List<? extends PropertyTree> getClassElements();
}
