package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.Tree;

public interface ImportEntryTree
extends Tree {
    public IdentifierTree getModuleRequest();

    public IdentifierTree getImportName();

    public IdentifierTree getLocalName();
}
