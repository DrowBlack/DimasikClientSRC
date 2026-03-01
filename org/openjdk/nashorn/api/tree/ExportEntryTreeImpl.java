package org.openjdk.nashorn.api.tree;

import java.util.List;
import java.util.stream.Collectors;
import org.openjdk.nashorn.api.tree.ExportEntryTree;
import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.ModuleTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeImpl;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.Module;

final class ExportEntryTreeImpl
extends TreeImpl
implements ExportEntryTree {
    private final long startPos;
    private final long endPos;
    private final IdentifierTree exportName;
    private final IdentifierTree moduleRequest;
    private final IdentifierTree importName;
    private final IdentifierTree localName;

    private ExportEntryTreeImpl(long startPos, long endPos, IdentifierTree exportName, IdentifierTree moduleRequest, IdentifierTree importName, IdentifierTree localName) {
        super(null);
        this.startPos = startPos;
        this.endPos = endPos;
        this.exportName = exportName;
        this.moduleRequest = moduleRequest;
        this.importName = importName;
        this.localName = localName;
    }

    private static ExportEntryTreeImpl createExportEntry(Module.ExportEntry entry) {
        return new ExportEntryTreeImpl(entry.getStartPosition(), entry.getEndPosition(), ModuleTreeImpl.identOrNull(entry.getExportName()), ModuleTreeImpl.identOrNull(entry.getModuleRequest()), ModuleTreeImpl.identOrNull(entry.getImportName()), ModuleTreeImpl.identOrNull(entry.getLocalName()));
    }

    static List<ExportEntryTreeImpl> createExportList(List<Module.ExportEntry> exportList) {
        return exportList.stream().map(ExportEntryTreeImpl::createExportEntry).collect(Collectors.toList());
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.EXPORT_ENTRY;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitExportEntry(this, data);
    }

    @Override
    public long getStartPosition() {
        return this.startPos;
    }

    @Override
    public long getEndPosition() {
        return this.endPos;
    }

    @Override
    public IdentifierTree getExportName() {
        return this.exportName;
    }

    @Override
    public IdentifierTree getModuleRequest() {
        return this.moduleRequest;
    }

    @Override
    public IdentifierTree getImportName() {
        return this.importName;
    }

    @Override
    public IdentifierTree getLocalName() {
        return this.localName;
    }
}
