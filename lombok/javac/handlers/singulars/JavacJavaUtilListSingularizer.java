package lombok.javac.handlers.singulars;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import lombok.core.LombokImmutableList;
import lombok.javac.Javac;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.JavacSingularsRecipes;
import lombok.javac.handlers.singulars.JavacJavaUtilListSetSingularizer;

public class JavacJavaUtilListSingularizer
extends JavacJavaUtilListSetSingularizer {
    @Override
    public LombokImmutableList<String> getSupportedTypes() {
        return LombokImmutableList.of("java.util.List", "java.util.Collection", "java.lang.Iterable");
    }

    @Override
    protected String getEmptyMaker(String target) {
        return "java.util.Collections.emptyList";
    }

    @Override
    public void appendBuildCode(JavacSingularsRecipes.SingularData data, JavacNode builderType, JavacNode source, ListBuffer<JCTree.JCStatement> statements, Name targetVariableName, String builderVariable) {
        JavacTreeMaker maker = builderType.getTreeMaker();
        List<JCTree.JCExpression> jceBlank = List.nil();
        ListBuffer<JCTree.JCCase> cases = new ListBuffer<JCTree.JCCase>();
        JCTree.JCMethodInvocation invoke = maker.Apply(jceBlank, JavacHandlerUtil.chainDots(builderType, "java", "util", "Collections", "emptyList"), jceBlank);
        JCTree.JCExpressionStatement assignStat = maker.Exec(maker.Assign(maker.Ident(data.getPluralName()), invoke));
        JCTree.JCBreak breakStat = maker.Break(null);
        JCTree.JCCase emptyCase = maker.Case(maker.Literal(Javac.CTC_INT, 0), List.of(assignStat, breakStat));
        cases.append(emptyCase);
        JCTree.JCLiteral zeroLiteral = maker.Literal(Javac.CTC_INT, 0);
        JCTree.JCMethodInvocation arg = maker.Apply(jceBlank, JavacHandlerUtil.chainDots(builderType, builderVariable, data.getPluralName().toString(), "get"), List.of(zeroLiteral));
        List<JCTree.JCExpression> args = List.of(arg);
        JCTree.JCMethodInvocation invoke2 = maker.Apply(jceBlank, JavacHandlerUtil.chainDots(builderType, "java", "util", "Collections", "singletonList"), args);
        assignStat = maker.Exec(maker.Assign(maker.Ident(data.getPluralName()), invoke2));
        breakStat = maker.Break(null);
        JCTree.JCCase singletonCase = maker.Case(maker.Literal(Javac.CTC_INT, 1), List.of(assignStat, breakStat));
        cases.append(singletonCase);
        List<JCTree.JCStatement> defStats = this.createListCopy(maker, data, builderType, source, builderVariable);
        JCTree.JCCase defaultCase = maker.Case(null, defStats);
        cases.append(defaultCase);
        JCTree.JCSwitch switchStat = maker.Switch(this.getSize(maker, builderType, data.getPluralName(), true, false, builderVariable), cases.toList());
        JCTree.JCExpression localShadowerType = JavacHandlerUtil.chainDotsString(builderType, data.getTargetFqn());
        localShadowerType = this.addTypeArgs(1, false, builderType, localShadowerType, data.getTypeArgs(), source);
        JCTree.JCVariableDecl varDefStat = maker.VarDef(maker.Modifiers(0L), data.getPluralName(), localShadowerType, null);
        statements.append(varDefStat);
        statements.append(switchStat);
    }

    private List<JCTree.JCStatement> createListCopy(JavacTreeMaker maker, JavacSingularsRecipes.SingularData data, JavacNode builderType, JavacNode source, String builderVariable) {
        List<JCTree.JCExpression> jceBlank = List.nil();
        Name thisName = builderType.toName(builderVariable);
        List<JCTree.JCExpression> constructorArgs = List.nil();
        JCTree.JCFieldAccess thisDotPluralName = maker.Select(maker.Ident(thisName), data.getPluralName());
        constructorArgs = List.of(thisDotPluralName);
        JCTree.JCExpression targetTypeExpr = JavacHandlerUtil.chainDots(builderType, "java", "util", "ArrayList");
        targetTypeExpr = this.addTypeArgs(1, false, builderType, targetTypeExpr, data.getTypeArgs(), source);
        JCTree.JCNewClass argToUnmodifiable = maker.NewClass(null, jceBlank, targetTypeExpr, constructorArgs, null);
        JCTree.JCMethodInvocation invoke = maker.Apply(jceBlank, JavacHandlerUtil.chainDots(builderType, "java", "util", "Collections", "unmodifiableList"), List.of(argToUnmodifiable));
        JCTree.JCExpressionStatement unmodifiableStat = maker.Exec(maker.Assign(maker.Ident(data.getPluralName()), invoke));
        return List.of(unmodifiableStat);
    }
}
