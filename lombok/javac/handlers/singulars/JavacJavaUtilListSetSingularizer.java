package lombok.javac.handlers.singulars;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.core.configuration.CheckerFrameworkVersion;
import lombok.javac.Javac;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.JavacSingularsRecipes;
import lombok.javac.handlers.singulars.JavacGuavaSetListSingularizer;
import lombok.javac.handlers.singulars.JavacJavaUtilSingularizer;

abstract class JavacJavaUtilListSetSingularizer
extends JavacJavaUtilSingularizer {
    JavacJavaUtilListSetSingularizer() {
    }

    @Override
    protected JavacSingularsRecipes.JavacSingularizer getGuavaInstead(JavacNode node) {
        return new JavacGuavaSetListSingularizer();
    }

    @Override
    public java.util.List<Name> listFieldsToBeGenerated(JavacSingularsRecipes.SingularData data, JavacNode builderType) {
        return super.listFieldsToBeGenerated(data, builderType);
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
        JCTree.JCVariableDecl buildField = maker.VarDef(maker.Modifiers(2L), data.getPluralName(), type, null);
        return Collections.singletonList(JavacHandlerUtil.injectFieldAndMarkGenerated(builderType, buildField));
    }

    @Override
    public void generateMethods(CheckerFrameworkVersion cfv, JavacSingularsRecipes.SingularData data, boolean deprecate, JavacNode builderType, JavacNode source, boolean fluent, JavacSingularsRecipes.ExpressionMaker returnTypeMaker, JavacSingularsRecipes.StatementMaker returnStatementMaker, AccessLevel access) {
        this.doGenerateMethods(cfv, data, deprecate, builderType, source, fluent, returnTypeMaker, returnStatementMaker, access);
    }

    @Override
    protected JCTree.JCStatement generateClearStatements(JavacTreeMaker maker, JavacSingularsRecipes.SingularData data, JavacNode builderType) {
        List<JCTree.JCExpression> jceBlank = List.nil();
        JCTree.JCFieldAccess thisDotField = maker.Select(maker.Ident(builderType.toName("this")), data.getPluralName());
        JCTree.JCFieldAccess thisDotFieldDotClear = maker.Select(maker.Select(maker.Ident(builderType.toName("this")), data.getPluralName()), builderType.toName("clear"));
        JCTree.JCExpressionStatement clearCall = maker.Exec(maker.Apply(jceBlank, thisDotFieldDotClear, jceBlank));
        JCTree.JCBinary cond = maker.Binary(Javac.CTC_NOT_EQUAL, thisDotField, maker.Literal(Javac.CTC_BOT, null));
        return maker.If(cond, clearCall, null);
    }

    @Override
    protected ListBuffer<JCTree.JCStatement> generateSingularMethodStatements(JavacTreeMaker maker, JavacSingularsRecipes.SingularData data, JavacNode builderType, JavacNode source) {
        return new ListBuffer<JCTree.JCStatement>().append(this.generateSingularMethodAddStatement(maker, builderType, data.getSingularName(), data.getPluralName().toString()));
    }

    @Override
    protected List<JCTree.JCVariableDecl> generateSingularMethodParameters(JavacTreeMaker maker, JavacSingularsRecipes.SingularData data, JavacNode builderType, JavacNode source) {
        JCTree.JCVariableDecl param = this.generateSingularMethodParameter(0, maker, data, builderType, source, data.getSingularName());
        return List.of(param);
    }

    @Override
    protected JCTree.JCExpression getPluralMethodParamType(JavacNode builderType) {
        return JavacHandlerUtil.chainDots(builderType, "java", "util", "Collection");
    }

    @Override
    protected JCTree.JCStatement createConstructBuilderVarIfNeeded(JavacTreeMaker maker, JavacSingularsRecipes.SingularData data, JavacNode builderType, JavacNode source) {
        return this.createConstructBuilderVarIfNeeded(maker, data, builderType, false, source);
    }

    @Override
    protected String getAddMethodName() {
        return "add";
    }

    @Override
    protected int getTypeArgumentsCount() {
        return 1;
    }
}
