package lombok.javac.handlers.singulars;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.core.LombokImmutableList;
import lombok.core.configuration.CheckerFrameworkVersion;
import lombok.javac.Javac;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.JavacSingularsRecipes;
import lombok.javac.handlers.singulars.JavacGuavaMapSingularizer;
import lombok.javac.handlers.singulars.JavacJavaUtilSingularizer;

public class JavacJavaUtilMapSingularizer
extends JavacJavaUtilSingularizer {
    @Override
    public LombokImmutableList<String> getSupportedTypes() {
        return LombokImmutableList.of("java.util.Map", "java.util.SortedMap", "java.util.NavigableMap");
    }

    @Override
    protected String getEmptyMaker(String target) {
        if (target.endsWith("NavigableMap")) {
            return "java.util.Collections.emptyNavigableMap";
        }
        if (target.endsWith("SortedMap")) {
            return "java.util.Collections.emptySortedMap";
        }
        return "java.util.Collections.emptyMap";
    }

    @Override
    protected JavacSingularsRecipes.JavacSingularizer getGuavaInstead(JavacNode node) {
        return new JavacGuavaMapSingularizer();
    }

    @Override
    public java.util.List<Name> listFieldsToBeGenerated(JavacSingularsRecipes.SingularData data, JavacNode builderType) {
        String p = data.getPluralName().toString();
        return Arrays.asList(builderType.toName(String.valueOf(p) + "$key"), builderType.toName(String.valueOf(p) + "$value"));
    }

    @Override
    public java.util.List<Name> listMethodsToBeGenerated(JavacSingularsRecipes.SingularData data, JavacNode builderType) {
        return super.listMethodsToBeGenerated(data, builderType);
    }

    @Override
    public java.util.List<JavacNode> generateFields(JavacSingularsRecipes.SingularData data, JavacNode builderType, JavacNode source) {
        JavacTreeMaker maker = builderType.getTreeMaker();
        JCTree.JCExpression type = JavacHandlerUtil.chainDots(builderType, "java", "util", "ArrayList");
        type = this.addTypeArgs(1, false, builderType, type, data.getTypeArgs(), source);
        JCTree.JCVariableDecl buildKeyField = maker.VarDef(maker.Modifiers(2L), builderType.toName(data.getPluralName() + "$key"), type, null);
        JCTree.JCExpression type2 = JavacHandlerUtil.chainDots(builderType, "java", "util", "ArrayList");
        List<JCTree.JCExpression> tArgs = data.getTypeArgs();
        tArgs = tArgs != null && tArgs.size() > 1 ? tArgs.tail : List.nil();
        type2 = this.addTypeArgs(1, false, builderType, type2, tArgs, source);
        JCTree.JCVariableDecl buildValueField = maker.VarDef(maker.Modifiers(2L), builderType.toName(data.getPluralName() + "$value"), type2, null);
        JavacNode valueFieldNode = JavacHandlerUtil.injectFieldAndMarkGenerated(builderType, buildValueField);
        JavacNode keyFieldNode = JavacHandlerUtil.injectFieldAndMarkGenerated(builderType, buildKeyField);
        return Arrays.asList(keyFieldNode, valueFieldNode);
    }

    @Override
    public void generateMethods(CheckerFrameworkVersion cfv, JavacSingularsRecipes.SingularData data, boolean deprecate, JavacNode builderType, JavacNode source, boolean fluent, JavacSingularsRecipes.ExpressionMaker returnTypeMaker, JavacSingularsRecipes.StatementMaker returnStatementMaker, AccessLevel access) {
        this.doGenerateMethods(cfv, data, deprecate, builderType, source, fluent, returnTypeMaker, returnStatementMaker, access);
    }

    @Override
    protected JCTree.JCStatement generateClearStatements(JavacTreeMaker maker, JavacSingularsRecipes.SingularData data, JavacNode builderType) {
        List<JCTree.JCExpression> jceBlank = List.nil();
        JCTree.JCExpression thisDotKeyField = JavacHandlerUtil.chainDots(builderType, "this", data.getPluralName() + "$key", new String[0]);
        JCTree.JCExpression thisDotKeyFieldDotClear = JavacHandlerUtil.chainDots(builderType, "this", data.getPluralName() + "$key", "clear");
        JCTree.JCExpression thisDotValueFieldDotClear = JavacHandlerUtil.chainDots(builderType, "this", data.getPluralName() + "$value", "clear");
        JCTree.JCExpressionStatement clearKeyCall = maker.Exec(maker.Apply(jceBlank, thisDotKeyFieldDotClear, jceBlank));
        JCTree.JCExpressionStatement clearValueCall = maker.Exec(maker.Apply(jceBlank, thisDotValueFieldDotClear, jceBlank));
        JCTree.JCBinary cond = maker.Binary(Javac.CTC_NOT_EQUAL, thisDotKeyField, maker.Literal(Javac.CTC_BOT, null));
        JCTree.JCBlock clearCalls = maker.Block(0L, List.of(clearKeyCall, clearValueCall));
        return maker.If(cond, clearCalls, null);
    }

    @Override
    protected ListBuffer<JCTree.JCStatement> generateSingularMethodStatements(JavacTreeMaker maker, JavacSingularsRecipes.SingularData data, JavacNode builderType, JavacNode source) {
        Name keyName = builderType.toName(String.valueOf(data.getSingularName().toString()) + "Key");
        Name valueName = builderType.toName(String.valueOf(data.getSingularName().toString()) + "Value");
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<JCTree.JCStatement>();
        statements.append(this.generateSingularMethodAddStatement(maker, builderType, keyName, data.getPluralName() + "$key"));
        statements.append(this.generateSingularMethodAddStatement(maker, builderType, valueName, data.getPluralName() + "$value"));
        return statements;
    }

    @Override
    protected List<JCTree.JCVariableDecl> generateSingularMethodParameters(JavacTreeMaker maker, JavacSingularsRecipes.SingularData data, JavacNode builderType, JavacNode source) {
        Name keyName = builderType.toName(String.valueOf(data.getSingularName().toString()) + "Key");
        Name valueName = builderType.toName(String.valueOf(data.getSingularName().toString()) + "Value");
        JCTree.JCVariableDecl paramKey = this.generateSingularMethodParameter(0, maker, data, builderType, source, keyName);
        JCTree.JCVariableDecl paramValue = this.generateSingularMethodParameter(1, maker, data, builderType, source, valueName);
        return List.of(paramKey, paramValue);
    }

    @Override
    protected ListBuffer<JCTree.JCStatement> generatePluralMethodStatements(JavacTreeMaker maker, JavacSingularsRecipes.SingularData data, JavacNode builderType, JavacNode source) {
        List<JCTree.JCExpression> jceBlank = List.nil();
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<JCTree.JCStatement>();
        long baseFlags = JavacHandlerUtil.addFinalIfNeeded(0L, builderType.getContext());
        Name entryName = builderType.toName("$lombokEntry");
        JCTree.JCExpression forEachType = JavacHandlerUtil.chainDots(builderType, "java", "util", "Map", "Entry");
        forEachType = this.addTypeArgs(2, true, builderType, forEachType, data.getTypeArgs(), source);
        JCTree.JCMethodInvocation keyArg = maker.Apply(List.<JCTree.JCExpression>nil(), maker.Select(maker.Ident(entryName), builderType.toName("getKey")), List.<JCTree.JCExpression>nil());
        JCTree.JCMethodInvocation valueArg = maker.Apply(List.<JCTree.JCExpression>nil(), maker.Select(maker.Ident(entryName), builderType.toName("getValue")), List.<JCTree.JCExpression>nil());
        JCTree.JCMethodInvocation addKey = maker.Apply(List.<JCTree.JCExpression>nil(), JavacHandlerUtil.chainDots(builderType, "this", data.getPluralName() + "$key", "add"), List.of(keyArg));
        JCTree.JCMethodInvocation addValue = maker.Apply(List.<JCTree.JCExpression>nil(), JavacHandlerUtil.chainDots(builderType, "this", data.getPluralName() + "$value", "add"), List.of(valueArg));
        JCTree.JCBlock forEachBody = maker.Block(0L, List.of(maker.Exec(addKey), maker.Exec(addValue)));
        JCTree.JCMethodInvocation entrySetInvocation = maker.Apply(jceBlank, maker.Select(maker.Ident(data.getPluralName()), builderType.toName("entrySet")), jceBlank);
        JCTree.JCEnhancedForLoop forEach = maker.ForeachLoop(maker.VarDef(maker.Modifiers(baseFlags), entryName, forEachType, null), entrySetInvocation, forEachBody);
        statements.append(forEach);
        return statements;
    }

    @Override
    protected JCTree.JCExpression getPluralMethodParamType(JavacNode builderType) {
        return JavacHandlerUtil.chainDots(builderType, "java", "util", "Map");
    }

    @Override
    protected JCTree.JCStatement createConstructBuilderVarIfNeeded(JavacTreeMaker maker, JavacSingularsRecipes.SingularData data, JavacNode builderType, JavacNode source) {
        return this.createConstructBuilderVarIfNeeded(maker, data, builderType, true, source);
    }

    @Override
    public void appendBuildCode(JavacSingularsRecipes.SingularData data, JavacNode builderType, JavacNode source, ListBuffer<JCTree.JCStatement> statements, Name targetVariableName, String builderVariable) {
        JavacTreeMaker maker = builderType.getTreeMaker();
        if (data.getTargetFqn().equals("java.util.Map")) {
            statements.appendList(this.createJavaUtilSetMapInitialCapacitySwitchStatements(maker, data, builderType, true, "emptyMap", "singletonMap", "LinkedHashMap", source, builderVariable));
        } else {
            statements.appendList(this.createJavaUtilSimpleCreationAndFillStatements(maker, data, builderType, true, true, false, true, "TreeMap", source, builderVariable));
        }
    }

    @Override
    protected String getAddMethodName() {
        return "put";
    }

    @Override
    protected int getTypeArgumentsCount() {
        return 2;
    }
}
