package org.openjdk.nashorn.api.tree;

import java.util.List;
import org.openjdk.nashorn.api.tree.ExportEntryTree;
import org.openjdk.nashorn.api.tree.ExportEntryTreeImpl;
import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.IdentifierTreeImpl;
import org.openjdk.nashorn.api.tree.ImportEntryTree;
import org.openjdk.nashorn.api.tree.ImportEntryTreeImpl;
import org.openjdk.nashorn.api.tree.ModuleTree;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeImpl;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.FunctionNode;
import org.openjdk.nashorn.internal.ir.IdentNode;
import org.openjdk.nashorn.internal.ir.Module;

final class ModuleTreeImpl
extends TreeImpl
implements ModuleTree {
    private final Module mod;
    private final List<? extends ImportEntryTree> imports;
    private final List<? extends ExportEntryTree> localExports;
    private final List<? extends ExportEntryTree> indirectExports;
    private final List<? extends ExportEntryTree> starExports;

    private ModuleTreeImpl(FunctionNode func, List<? extends ImportEntryTree> imports, List<? extends ExportEntryTree> localExports, List<? extends ExportEntryTree> indirectExports, List<? extends ExportEntryTree> starExports) {
        super(func);
        assert (func.getKind() == FunctionNode.Kind.MODULE) : "module function node expected";
        this.mod = func.getModule();
        this.imports = imports;
        this.localExports = localExports;
        this.indirectExports = indirectExports;
        this.starExports = starExports;
    }

    static ModuleTreeImpl create(FunctionNode func) {
        Module mod = func.getModule();
        return new ModuleTreeImpl(func, ImportEntryTreeImpl.createImportList(mod.getImportEntries()), ExportEntryTreeImpl.createExportList(mod.getLocalExportEntries()), ExportEntryTreeImpl.createExportList(mod.getIndirectExportEntries()), ExportEntryTreeImpl.createExportList(mod.getStarExportEntries()));
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.MODULE;
    }

    @Override
    public List<? extends ImportEntryTree> getImportEntries() {
        return this.imports;
    }

    @Override
    public List<? extends ExportEntryTree> getLocalExportEntries() {
        return this.localExports;
    }

    @Override
    public List<? extends ExportEntryTree> getIndirectExportEntries() {
        return this.indirectExports;
    }

    @Override
    public List<? extends ExportEntryTree> getStarExportEntries() {
        return this.starExports;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitModule(this, data);
    }

    static IdentifierTree identOrNull(IdentNode node) {
        return node != null ? new IdentifierTreeImpl(node) : null;
    }
}
