package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.LineMap;
import org.openjdk.nashorn.api.tree.ModuleTree;
import org.openjdk.nashorn.api.tree.Tree;

public interface CompilationUnitTree
extends Tree {
    public List<? extends Tree> getSourceElements();

    public String getSourceName();

    public boolean isStrict();

    public LineMap getLineMap();

    public ModuleTree getModule();
}
