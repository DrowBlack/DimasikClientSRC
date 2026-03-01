package lombok.eclipse.handlers.singulars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.core.configuration.CheckerFrameworkVersion;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.EclipseSingularsRecipes;
import lombok.eclipse.handlers.HandleNonNull;
import lombok.eclipse.handlers.singulars.EclipseJavaUtilSingularizer;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

abstract class EclipseJavaUtilListSetSingularizer
extends EclipseJavaUtilSingularizer {
    EclipseJavaUtilListSetSingularizer() {
    }

    @Override
    public List<char[]> listFieldsToBeGenerated(EclipseSingularsRecipes.SingularData data, EclipseNode builderType) {
        if (this.useGuavaInstead(builderType)) {
            return this.guavaListSetSingularizer.listFieldsToBeGenerated(data, builderType);
        }
        return super.listFieldsToBeGenerated(data, builderType);
    }

    @Override
    public List<char[]> listMethodsToBeGenerated(EclipseSingularsRecipes.SingularData data, EclipseNode builderType) {
        if (this.useGuavaInstead(builderType)) {
            return this.guavaListSetSingularizer.listMethodsToBeGenerated(data, builderType);
        }
        return super.listMethodsToBeGenerated(data, builderType);
    }

    @Override
    public List<EclipseNode> generateFields(EclipseSingularsRecipes.SingularData data, EclipseNode builderType) {
        if (this.useGuavaInstead(builderType)) {
            return this.guavaListSetSingularizer.generateFields(data, builderType);
        }
        QualifiedTypeReference type = new QualifiedTypeReference(JAVA_UTIL_ARRAYLIST, NULL_POSS);
        type = this.addTypeArgs(1, false, builderType, (TypeReference)type, data.getTypeArgs());
        FieldDeclaration buildField = new FieldDeclaration(data.getPluralName(), 0, -1);
        buildField.bits |= 0x800000;
        buildField.modifiers = 2;
        buildField.declarationSourceEnd = -1;
        buildField.type = type;
        data.setGeneratedByRecursive((ASTNode)buildField);
        return Collections.singletonList(EclipseHandlerUtil.injectFieldAndMarkGenerated(builderType, buildField));
    }

    @Override
    public void generateMethods(CheckerFrameworkVersion cfv, EclipseSingularsRecipes.SingularData data, boolean deprecate, EclipseNode builderType, boolean fluent, EclipseSingularsRecipes.TypeReferenceMaker returnTypeMaker, EclipseSingularsRecipes.StatementMaker returnStatementMaker, AccessLevel access) {
        if (this.useGuavaInstead(builderType)) {
            this.guavaListSetSingularizer.generateMethods(cfv, data, deprecate, builderType, fluent, returnTypeMaker, returnStatementMaker, access);
            return;
        }
        this.generateSingularMethod(cfv, deprecate, returnTypeMaker.make(), returnStatementMaker.make(), data, builderType, fluent, access);
        this.generatePluralMethod(cfv, deprecate, returnTypeMaker.make(), returnStatementMaker.make(), data, builderType, fluent, access);
        this.generateClearMethod(cfv, deprecate, returnTypeMaker.make(), returnStatementMaker.make(), data, builderType, access);
    }

    private void generateClearMethod(CheckerFrameworkVersion cfv, boolean deprecate, TypeReference returnType, Statement returnStatement, EclipseSingularsRecipes.SingularData data, EclipseNode builderType, AccessLevel access) {
        Statement[] statementArray;
        MethodDeclaration md = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)builderType.top()).get()).compilationResult);
        md.bits |= 0x800000;
        md.modifiers = EclipseHandlerUtil.toEclipseModifier(access);
        FieldReference thisDotField = new FieldReference(data.getPluralName(), 0L);
        thisDotField.receiver = new ThisReference(0, 0);
        FieldReference thisDotField2 = new FieldReference(data.getPluralName(), 0L);
        thisDotField2.receiver = new ThisReference(0, 0);
        md.selector = HandlerUtil.buildAccessorName(builderType, "clear", new String(data.getPluralName())).toCharArray();
        MessageSend clearMsg = new MessageSend();
        clearMsg.receiver = thisDotField2;
        clearMsg.selector = "clear".toCharArray();
        IfStatement clearStatement = new IfStatement((Expression)new EqualExpression((Expression)thisDotField, (Expression)new NullLiteral(0, 0), OperatorIds.NOT_EQUAL), (Statement)clearMsg, 0, 0);
        if (returnStatement != null) {
            Statement[] statementArray2 = new Statement[2];
            statementArray2[0] = clearStatement;
            statementArray = statementArray2;
            statementArray2[1] = returnStatement;
        } else {
            Statement[] statementArray3 = new Statement[1];
            statementArray = statementArray3;
            statementArray3[0] = clearStatement;
        }
        md.statements = statementArray;
        md.returnType = returnType;
        EclipseHandlerUtil.addCheckerFrameworkReturnsReceiver(md.returnType, data.getSource(), cfv);
        md.annotations = this.generateSelfReturnAnnotations(deprecate, data.getSource());
        data.setGeneratedByRecursive((ASTNode)md);
        if (returnStatement != null) {
            EclipseHandlerUtil.createRelevantNonNullAnnotation(builderType, md);
        }
        EclipseHandlerUtil.injectMethod(builderType, (AbstractMethodDeclaration)md);
    }

    void generateSingularMethod(CheckerFrameworkVersion cfv, boolean deprecate, TypeReference returnType, Statement returnStatement, EclipseSingularsRecipes.SingularData data, EclipseNode builderType, boolean fluent, AccessLevel access) {
        MethodDeclaration md = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)builderType.top()).get()).compilationResult);
        md.bits |= 0x800000;
        md.modifiers = EclipseHandlerUtil.toEclipseModifier(access);
        ArrayList<Object> statements = new ArrayList<Object>();
        statements.add(this.createConstructBuilderVarIfNeeded(data, builderType, false));
        FieldReference thisDotField = new FieldReference(data.getPluralName(), 0L);
        thisDotField.receiver = new ThisReference(0, 0);
        MessageSend thisDotFieldDotAdd = new MessageSend();
        thisDotFieldDotAdd.arguments = new Expression[]{new SingleNameReference(data.getSingularName(), 0L)};
        thisDotFieldDotAdd.receiver = thisDotField;
        thisDotFieldDotAdd.selector = "add".toCharArray();
        statements.add(thisDotFieldDotAdd);
        if (returnStatement != null) {
            statements.add(returnStatement);
        }
        md.statements = statements.toArray(new Statement[0]);
        TypeReference paramType = this.cloneParamType(0, data.getTypeArgs(), builderType);
        Annotation[] typeUseAnns = EclipseHandlerUtil.getTypeUseAnnotations(paramType);
        EclipseHandlerUtil.removeTypeUseAnnotations(paramType);
        Argument param = new Argument(data.getSingularName(), 0L, paramType, 16);
        param.annotations = typeUseAnns;
        md.arguments = new Argument[]{param};
        md.returnType = returnType;
        EclipseHandlerUtil.addCheckerFrameworkReturnsReceiver(md.returnType, data.getSource(), cfv);
        char[] prefixedSingularName = data.getSetterPrefix().length == 0 ? data.getSingularName() : HandlerUtil.buildAccessorName(builderType, new String(data.getSetterPrefix()), new String(data.getSingularName())).toCharArray();
        md.selector = fluent ? prefixedSingularName : HandlerUtil.buildAccessorName(builderType, "add", new String(data.getSingularName())).toCharArray();
        Annotation[] selfReturnAnnotations = this.generateSelfReturnAnnotations(deprecate, data.getSource());
        Annotation[] copyToSetterAnnotations = EclipseHandlerUtil.copyAnnotations((ASTNode)md, new Annotation[][]{EclipseHandlerUtil.findCopyableToBuilderSingularSetterAnnotations((EclipseNode)data.getAnnotation().up())});
        md.annotations = EclipseHandlerUtil.concat(selfReturnAnnotations, copyToSetterAnnotations, Annotation.class);
        if (returnStatement != null) {
            EclipseHandlerUtil.createRelevantNonNullAnnotation(builderType, md);
        }
        data.setGeneratedByRecursive((ASTNode)md);
        HandleNonNull.INSTANCE.fix(EclipseHandlerUtil.injectMethod(builderType, (AbstractMethodDeclaration)md));
    }

    void generatePluralMethod(CheckerFrameworkVersion cfv, boolean deprecate, TypeReference returnType, Statement returnStatement, EclipseSingularsRecipes.SingularData data, EclipseNode builderType, boolean fluent, AccessLevel access) {
        MethodDeclaration md = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)builderType.top()).get()).compilationResult);
        md.bits |= 0x800000;
        md.modifiers = EclipseHandlerUtil.toEclipseModifier(access);
        ArrayList<Statement> statements = new ArrayList<Statement>();
        statements.add(this.createConstructBuilderVarIfNeeded(data, builderType, false));
        FieldReference thisDotField = new FieldReference(data.getPluralName(), 0L);
        thisDotField.receiver = new ThisReference(0, 0);
        MessageSend thisDotFieldDotAddAll = new MessageSend();
        thisDotFieldDotAddAll.arguments = new Expression[]{new SingleNameReference(data.getPluralName(), 0L)};
        thisDotFieldDotAddAll.receiver = thisDotField;
        thisDotFieldDotAddAll.selector = "addAll".toCharArray();
        statements.add((Statement)thisDotFieldDotAddAll);
        QualifiedTypeReference paramType = new QualifiedTypeReference(TypeConstants.JAVA_UTIL_COLLECTION, NULL_POSS);
        paramType = this.addTypeArgs(1, true, builderType, (TypeReference)paramType, data.getTypeArgs());
        Argument param = new Argument(data.getPluralName(), 0L, (TypeReference)paramType, 16);
        this.nullBehaviorize(builderType, data, statements, param, md);
        if (returnStatement != null) {
            statements.add(returnStatement);
        }
        md.statements = statements.toArray(new Statement[0]);
        md.arguments = new Argument[]{param};
        md.returnType = returnType;
        EclipseHandlerUtil.addCheckerFrameworkReturnsReceiver(md.returnType, data.getSource(), cfv);
        char[] prefixedSelector = data.getSetterPrefix().length == 0 ? data.getPluralName() : HandlerUtil.buildAccessorName(builderType, new String(data.getSetterPrefix()), new String(data.getPluralName())).toCharArray();
        md.selector = fluent ? prefixedSelector : HandlerUtil.buildAccessorName(builderType, "addAll", new String(data.getPluralName())).toCharArray();
        Annotation[] selfReturnAnnotations = this.generateSelfReturnAnnotations(deprecate, data.getSource());
        Annotation[] copyToSetterAnnotations = EclipseHandlerUtil.copyAnnotations((ASTNode)md, new Annotation[][]{EclipseHandlerUtil.findCopyableToSetterAnnotations((EclipseNode)data.getAnnotation().up())});
        md.annotations = EclipseHandlerUtil.concat(selfReturnAnnotations, copyToSetterAnnotations, Annotation.class);
        if (returnStatement != null) {
            EclipseHandlerUtil.createRelevantNonNullAnnotation(builderType, md);
        }
        data.setGeneratedByRecursive((ASTNode)md);
        EclipseHandlerUtil.injectMethod(builderType, (AbstractMethodDeclaration)md);
    }

    @Override
    protected int getTypeArgumentsCount() {
        return 1;
    }
}
