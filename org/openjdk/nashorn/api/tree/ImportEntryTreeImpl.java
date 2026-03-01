package org.openjdk.nashorn.api.tree;

import java.util.List;
import java.util.stream.Collectors;
import org.openjdk.nashorn.api.tree.IdentifierTree;
import org.openjdk.nashorn.api.tree.ImportEntryTree;
import org.openjdk.nashorn.api.tree.ModuleTreeImpl;
import org.openjdk.nashorn.api.tree.Tree;
import org.openjdk.nashorn.api.tree.TreeImpl;
import org.openjdk.nashorn.api.tree.TreeVisitor;
import org.openjdk.nashorn.internal.ir.Module;

final class ImportEntryTreeImpl
extends TreeImpl
implements ImportEntryTree {
    private final long startPos;
    private final long endPos;
    private final IdentifierTree moduleRequest;
    private final IdentifierTree importName;
    private final IdentifierTree localName;

    private ImportEntryTreeImpl(long startPos, long endPos, IdentifierTree moduleRequest, IdentifierTree importName, IdentifierTree localName) {
        super(null);
        this.startPos = startPos;
        this.endPos = endPos;
        this.moduleRequest = moduleRequest;
        this.importName = importName;
        this.localName = localName;
    }

    private static ImportEntryTreeImpl createImportEntry(Module.ImportEntry entry) {
        return new ImportEntryTreeImpl(entry.getStartPosition(), entry.getEndPosition(), ModuleTreeImpl.identOrNull(entry.getModuleRequest()), ModuleTreeImpl.identOrNull(entry.getImportName()), ModuleTreeImpl.identOrNull(entry.getLocalName()));
    }

    static List<ImportEntryTreeImpl> createImportList(List<Module.ImportEntry> importList) {
        return importList.stream().map(ImportEntryTreeImpl::createImportEntry).collect(Collectors.toList());
    }

    @Override
    public Tree.Kind getKind() {
        return Tree.Kind.IMPORT_ENTRY;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
        return visitor.visitImportEntry(this, data);
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
