package lombok.eclipse.handlers.singulars;

import lombok.core.LombokImmutableList;
import lombok.eclipse.handlers.singulars.EclipseGuavaSingularizer;

public class EclipseGuavaTableSingularizer
extends EclipseGuavaSingularizer {
    private static final LombokImmutableList<String> SUFFIXES = LombokImmutableList.of("rowKey", "columnKey", "value");
    private static final LombokImmutableList<String> SUPPORTED_TYPES = LombokImmutableList.of("com.google.common.collect.ImmutableTable");

    @Override
    public LombokImmutableList<String> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    @Override
    protected LombokImmutableList<String> getArgumentSuffixes() {
        return SUFFIXES;
    }

    @Override
    protected String getAddMethodName() {
        return "put";
    }

    @Override
    protected String getAddAllTypeName() {
        return "com.google.common.collect.Table";
    }
}
