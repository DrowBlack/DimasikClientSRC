package org.openjdk.nashorn.internal.ir;

import java.util.List;
import org.openjdk.nashorn.internal.ir.IdentNode;

public final class Module {
    public static final String DEFAULT_EXPORT_BINDING_NAME = "*default*";
    public static final String DEFAULT_NAME = "default";
    public static final String STAR_NAME = "*";
    private final List<String> requestedModules;
    private final List<ImportEntry> importEntries;
    private final List<ExportEntry> localExportEntries;
    private final List<ExportEntry> indirectExportEntries;
    private final List<ExportEntry> starExportEntries;

    public Module(List<String> requestedModules, List<ImportEntry> importEntries, List<ExportEntry> localExportEntries, List<ExportEntry> indirectExportEntries, List<ExportEntry> starExportEntries) {
        this.requestedModules = requestedModules;
        this.importEntries = importEntries;
        this.localExportEntries = localExportEntries;
        this.indirectExportEntries = indirectExportEntries;
        this.starExportEntries = starExportEntries;
    }

    public List<String> getRequestedModules() {
        return this.requestedModules;
    }

    public List<ImportEntry> getImportEntries() {
        return this.importEntries;
    }

    public List<ExportEntry> getLocalExportEntries() {
        return this.localExportEntries;
    }

    public List<ExportEntry> getIndirectExportEntries() {
        return this.indirectExportEntries;
    }

    public List<ExportEntry> getStarExportEntries() {
        return this.starExportEntries;
    }

    public String toString() {
        return "Module [requestedModules=" + this.requestedModules + ", importEntries=" + this.importEntries + ", localExportEntries=" + this.localExportEntries + ", indirectExportEntries=" + this.indirectExportEntries + ", starExportEntries=" + this.starExportEntries + "]";
    }

    public static final class ImportEntry {
        private final IdentNode moduleRequest;
        private final IdentNode importName;
        private final IdentNode localName;
        private final int startPosition;
        private final int endPosition;

        private ImportEntry(IdentNode moduleRequest, IdentNode importName, IdentNode localName, int startPosition, int endPosition) {
            this.moduleRequest = moduleRequest;
            this.importName = importName;
            this.localName = localName;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }

        public static ImportEntry importSpecifier(IdentNode importName, IdentNode localName, int startPosition, int endPosition) {
            return new ImportEntry(null, importName, localName, startPosition, endPosition);
        }

        public static ImportEntry importSpecifier(IdentNode importName, int startPosition, int endPosition) {
            return ImportEntry.importSpecifier(importName, importName, startPosition, endPosition);
        }

        public ImportEntry withFrom(IdentNode moduleRequest, int endPosition) {
            return new ImportEntry(moduleRequest, this.importName, this.localName, this.startPosition, endPosition);
        }

        public IdentNode getModuleRequest() {
            return this.moduleRequest;
        }

        public IdentNode getImportName() {
            return this.importName;
        }

        public IdentNode getLocalName() {
            return this.localName;
        }

        public int getStartPosition() {
            return this.startPosition;
        }

        public int getEndPosition() {
            return this.endPosition;
        }

        public String toString() {
            return "ImportEntry [moduleRequest=" + this.moduleRequest + ", importName=" + this.importName + ", localName=" + this.localName + "]";
        }
    }

    public static final class ExportEntry {
        private final IdentNode exportName;
        private final IdentNode moduleRequest;
        private final IdentNode importName;
        private final IdentNode localName;
        private final int startPosition;
        private final int endPosition;

        private ExportEntry(IdentNode exportName, IdentNode moduleRequest, IdentNode importName, IdentNode localName, int startPosition, int endPosition) {
            this.exportName = exportName;
            this.moduleRequest = moduleRequest;
            this.importName = importName;
            this.localName = localName;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }

        public static ExportEntry exportStarFrom(IdentNode starName, IdentNode moduleRequest, int startPosition, int endPosition) {
            return new ExportEntry(null, moduleRequest, starName, null, startPosition, endPosition);
        }

        public static ExportEntry exportDefault(IdentNode defaultName, IdentNode localName, int startPosition, int endPosition) {
            return new ExportEntry(defaultName, null, null, localName, startPosition, endPosition);
        }

        public static ExportEntry exportSpecifier(IdentNode exportName, IdentNode localName, int startPosition, int endPosition) {
            return new ExportEntry(exportName, null, null, localName, startPosition, endPosition);
        }

        public static ExportEntry exportSpecifier(IdentNode exportName, int startPosition, int endPosition) {
            return ExportEntry.exportSpecifier(exportName, exportName, startPosition, endPosition);
        }

        public ExportEntry withFrom(IdentNode moduleRequest, int endPosition) {
            return new ExportEntry(this.exportName, moduleRequest, this.localName, null, this.startPosition, endPosition);
        }

        public IdentNode getExportName() {
            return this.exportName;
        }

        public IdentNode getModuleRequest() {
            return this.moduleRequest;
        }

        public IdentNode getImportName() {
            return this.importName;
        }

        public IdentNode getLocalName() {
            return this.localName;
        }

        public int getStartPosition() {
            return this.startPosition;
        }

        public int getEndPosition() {
            return this.endPosition;
        }

        public String toString() {
            return "ExportEntry [exportName=" + this.exportName + ", moduleRequest=" + this.moduleRequest + ", importName=" + this.importName + ", localName=" + this.localName + "]";
        }
    }
}
