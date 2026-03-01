package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ExportEntryTree;
import org.openjdk.nashorn.api.tree.ImportEntryTree;
import org.openjdk.nashorn.api.tree.Tree;

public interface ModuleTree
extends Tree {
    public List<? extends ImportEntryTree> getImportEntries();

    public List<? extends ExportEntryTree> getLocalExportEntries();

    public List<? extends ExportEntryTree> getIndirectExportEntries();

    public List<? extends ExportEntryTree> getStarExportEntries();
}
