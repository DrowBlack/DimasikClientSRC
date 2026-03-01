package lombok.eclipse.handlers.singulars;

import java.util.List;
import lombok.core.LombokImmutableList;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseSingularsRecipes;
import lombok.eclipse.handlers.singulars.EclipseJavaUtilListSetSingularizer;
import org.eclipse.jdt.internal.compiler.ast.Statement;

public class EclipseJavaUtilSetSingularizer
extends EclipseJavaUtilListSetSingularizer {
    private static final char[] EMPTY_SORTED_SET = new char[]{'e', 'm', 'p', 't', 'y', 'S', 'o', 'r', 't', 'e', 'd', 'S', 'e', 't'};
    private static final char[] EMPTY_NAVIGABLE_SET = new char[]{'e', 'm', 'p', 't', 'y', 'N', 'a', 'v', 'i', 'g', 'a', 'b', 'l', 'e', 'S', 'e', 't'};
    private static final char[] EMPTY_SET = new char[]{'e', 'm', 'p', 't', 'y', 'S', 'e', 't'};

    @Override
    public LombokImmutableList<String> getSupportedTypes() {
        return LombokImmutableList.of("java.util.Set", "java.util.SortedSet", "java.util.NavigableSet");
    }

    @Override
    protected char[][] getEmptyMakerReceiver(String targetFqn) {
        return JAVA_UTIL_COLLECTIONS;
    }

    @Override
    protected char[] getEmptyMakerSelector(String targetFqn) {
        if (targetFqn.endsWith("SortedSet")) {
            return EMPTY_SORTED_SET;
        }
        if (targetFqn.endsWith("NavigableSet")) {
            return EMPTY_NAVIGABLE_SET;
        }
        return EMPTY_SET;
    }

    @Override
    public void appendBuildCode(EclipseSingularsRecipes.SingularData data, EclipseNode builderType, List<Statement> statements, char[] targetVariableName, String builderVariable) {
        if (this.useGuavaInstead(builderType)) {
            this.guavaListSetSingularizer.appendBuildCode(data, builderType, statements, targetVariableName, builderVariable);
            return;
        }
        if (data.getTargetFqn().equals("java.util.Set")) {
            statements.addAll(this.createJavaUtilSetMapInitialCapacitySwitchStatements(data, builderType, false, "emptySet", "singleton", "LinkedHashSet", builderVariable));
        } else {
            statements.addAll(this.createJavaUtilSimpleCreationAndFillStatements(data, builderType, false, true, false, true, "TreeSet", builderVariable));
        }
    }
}
