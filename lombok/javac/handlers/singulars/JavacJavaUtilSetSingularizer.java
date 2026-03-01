package lombok.javac.handlers.singulars;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import lombok.core.LombokImmutableList;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacSingularsRecipes;
import lombok.javac.handlers.singulars.JavacJavaUtilListSetSingularizer;

public class JavacJavaUtilSetSingularizer
extends JavacJavaUtilListSetSingularizer {
    @Override
    public LombokImmutableList<String> getSupportedTypes() {
        return LombokImmutableList.of("java.util.Set", "java.util.SortedSet", "java.util.NavigableSet");
    }

    @Override
    protected String getEmptyMaker(String target) {
        if (target.endsWith("SortedSet")) {
            return "java.util.Collections.emptySortedSet";
        }
        if (target.endsWith("NavigableSet")) {
            return "java.util.Collections.emptyNavigableSet";
        }
        return "java.util.Collections.emptySet";
    }

    @Override
    public void appendBuildCode(JavacSingularsRecipes.SingularData data, JavacNode builderType, JavacNode source, ListBuffer<JCTree.JCStatement> statements, Name targetVariableName, String builderVariable) {
        JavacTreeMaker maker = builderType.getTreeMaker();
        if (data.getTargetFqn().equals("java.util.Set")) {
            statements.appendList(this.createJavaUtilSetMapInitialCapacitySwitchStatements(maker, data, builderType, false, "emptySet", "singleton", "LinkedHashSet", source, builderVariable));
        } else {
            statements.appendList(this.createJavaUtilSimpleCreationAndFillStatements(maker, data, builderType, false, true, false, true, "TreeSet", source, builderVariable));
        }
    }
}
