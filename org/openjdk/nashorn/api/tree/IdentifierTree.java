package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.ExpressionTree;

public interface IdentifierTree
extends ExpressionTree {
    public String getName();

    public boolean isRestParameter();

    public boolean isSuper();

    public boolean isThis();

    public boolean isStar();

    public boolean isDefault();

    public boolean isStarDefaultStar();
}
