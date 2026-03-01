package lombok.eclipse.handlers.singulars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.core.GuavaTypeMap;
import lombok.core.LombokImmutableList;
import lombok.core.configuration.CheckerFrameworkVersion;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.EclipseSingularsRecipes;
import lombok.eclipse.handlers.HandleNonNull;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

abstract class EclipseGuavaSingularizer
extends EclipseSingularsRecipes.EclipseSingularizer {
    protected static final char[] OF = new char[]{'o', 'f'};
    protected static final char[][] CGCC = new char[][]{{'c', 'o', 'm'}, {'g', 'o', 'o', 'g', 'l', 'e'}, {'c', 'o', 'm', 'm', 'o', 'n'}, {'c', 'o', 'l', 'l', 'e', 'c', 't'}};

    EclipseGuavaSingularizer() {
    }

    protected String getSimpleTargetTypeName(EclipseSingularsRecipes.SingularData data) {
        return GuavaTypeMap.getGuavaTypeName(data.getTargetFqn());
    }

    protected char[] getBuilderMethodName(EclipseSingularsRecipes.SingularData data) {
        String simpleTypeName = this.getSimpleTargetTypeName(data);
        if ("ImmutableSortedSet".equals(simpleTypeName) || "ImmutableSortedMap".equals(simpleTypeName)) {
            return "naturalOrder".toCharArray();
        }
        return "builder".toCharArray();
    }

    protected char[][] makeGuavaTypeName(String simpleName, boolean addBuilder) {
        char[][] tokenizedName = new char[addBuilder ? 6 : 5][];
        tokenizedName[0] = CGCC[0];
        tokenizedName[1] = CGCC[1];
        tokenizedName[2] = CGCC[2];
        tokenizedName[3] = CGCC[3];
        tokenizedName[4] = simpleName.toCharArray();
        if (addBuilder) {
            tokenizedName[5] = new char[]{'B', 'u', 'i', 'l', 'd', 'e', 'r'};
        }
        return tokenizedName;
    }

    @Override
    protected char[] getEmptyMakerSelector(String targetFqn) {
        return OF;
    }

    @Override
    protected char[][] getEmptyMakerReceiver(String targetFqn) {
        return this.makeGuavaTypeName(GuavaTypeMap.getGuavaTypeName(targetFqn), false);
    }

    @Override
    public List<EclipseNode> generateFields(EclipseSingularsRecipes.SingularData data, EclipseNode builderType) {
        String simpleTypeName = this.getSimpleTargetTypeName(data);
        char[][] tokenizedName = this.makeGuavaTypeName(simpleTypeName, true);
        QualifiedTypeReference type = new QualifiedTypeReference(tokenizedName, NULL_POSS);
        type = this.addTypeArgs(this.getTypeArgumentsCount(), false, builderType, (TypeReference)type, data.getTypeArgs());
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
        this.generateSingularMethod(cfv, deprecate, returnTypeMaker.make(), returnStatementMaker.make(), data, builderType, fluent, access);
        this.generatePluralMethod(cfv, deprecate, returnTypeMaker.make(), returnStatementMaker.make(), data, builderType, fluent, access);
        this.generateClearMethod(cfv, deprecate, returnTypeMaker.make(), returnStatementMaker.make(), data, builderType, access);
    }

    void generateClearMethod(CheckerFrameworkVersion cfv, boolean deprecate, TypeReference returnType, Statement returnStatement, EclipseSingularsRecipes.SingularData data, EclipseNode builderType, AccessLevel access) {
        Statement[] statementArray;
        MethodDeclaration md = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)builderType.top()).get()).compilationResult);
        md.bits |= 0x800000;
        md.modifiers = EclipseHandlerUtil.toEclipseModifier(access);
        FieldReference thisDotField = new FieldReference(data.getPluralName(), 0L);
        thisDotField.receiver = new ThisReference(0, 0);
        Assignment a = new Assignment((Expression)thisDotField, (Expression)new NullLiteral(0, 0), 0);
        md.selector = HandlerUtil.buildAccessorName(builderType, "clear", new String(data.getPluralName())).toCharArray();
        if (returnStatement != null) {
            Statement[] statementArray2 = new Statement[2];
            statementArray2[0] = a;
            statementArray = statementArray2;
            statementArray2[1] = returnStatement;
        } else {
            Statement[] statementArray3 = new Statement[1];
            statementArray = statementArray3;
            statementArray3[0] = a;
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
        LombokImmutableList<String> suffixes = this.getArgumentSuffixes();
        char[][] names = new char[suffixes.size()][];
        int i = 0;
        while (i < suffixes.size()) {
            String s = suffixes.get(i);
            char[] n = data.getSingularName();
            names[i] = s.isEmpty() ? n : s.toCharArray();
            ++i;
        }
        MethodDeclaration md = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)builderType.top()).get()).compilationResult);
        md.bits |= 0x800000;
        md.modifiers = EclipseHandlerUtil.toEclipseModifier(access);
        ArrayList<Object> statements = new ArrayList<Object>();
        statements.add(this.createConstructBuilderVarIfNeeded(data, builderType));
        FieldReference thisDotField = new FieldReference(data.getPluralName(), 0L);
        thisDotField.receiver = new ThisReference(0, 0);
        MessageSend thisDotFieldDotAdd = new MessageSend();
        thisDotFieldDotAdd.arguments = new Expression[suffixes.size()];
        int i2 = 0;
        while (i2 < suffixes.size()) {
            thisDotFieldDotAdd.arguments[i2] = new SingleNameReference(names[i2], 0L);
            ++i2;
        }
        thisDotFieldDotAdd.receiver = thisDotField;
        thisDotFieldDotAdd.selector = this.getAddMethodName().toCharArray();
        statements.add(thisDotFieldDotAdd);
        if (returnStatement != null) {
            statements.add(returnStatement);
        }
        md.statements = statements.toArray(new Statement[0]);
        md.arguments = new Argument[suffixes.size()];
        i2 = 0;
        while (i2 < suffixes.size()) {
            TypeReference tr = this.cloneParamType(i2, data.getTypeArgs(), builderType);
            Annotation[] typeUseAnns = EclipseHandlerUtil.getTypeUseAnnotations(tr);
            EclipseHandlerUtil.removeTypeUseAnnotations(tr);
            md.arguments[i2] = new Argument(names[i2], 0L, tr, 16);
            md.arguments[i2].annotations = typeUseAnns;
            ++i2;
        }
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
        statements.add(this.createConstructBuilderVarIfNeeded(data, builderType));
        FieldReference thisDotField = new FieldReference(data.getPluralName(), 0L);
        thisDotField.receiver = new ThisReference(0, 0);
        MessageSend thisDotFieldDotAddAll = new MessageSend();
        thisDotFieldDotAddAll.arguments = new Expression[]{new SingleNameReference(data.getPluralName(), 0L)};
        thisDotFieldDotAddAll.receiver = thisDotField;
        thisDotFieldDotAddAll.selector = (String.valueOf(this.getAddMethodName()) + "All").toCharArray();
        statements.add((Statement)thisDotFieldDotAddAll);
        QualifiedTypeReference paramType = new QualifiedTypeReference(Eclipse.fromQualifiedName(this.getAddAllTypeName()), NULL_POSS);
        paramType = this.addTypeArgs(this.getTypeArgumentsCount(), true, builderType, (TypeReference)paramType, data.getTypeArgs());
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
    public void appendBuildCode(EclipseSingularsRecipes.SingularData data, EclipseNode builderType, List<Statement> statements, char[] targetVariableName, String builderVariable) {
        QualifiedTypeReference varType = new QualifiedTypeReference(Eclipse.fromQualifiedName(data.getTargetFqn()), NULL_POSS);
        String simpleTypeName = this.getSimpleTargetTypeName(data);
        int agrumentsCount = this.getTypeArgumentsCount();
        varType = this.addTypeArgs(agrumentsCount, false, builderType, (TypeReference)varType, data.getTypeArgs());
        MessageSend emptyInvoke = new MessageSend();
        emptyInvoke.selector = new char[]{'o', 'f'};
        emptyInvoke.receiver = new QualifiedNameReference(this.makeGuavaTypeName(simpleTypeName, false), NULL_POSS, 0, 0);
        emptyInvoke.typeArguments = this.createTypeArgs(agrumentsCount, false, builderType, data.getTypeArgs());
        MessageSend invokeBuild = new MessageSend();
        invokeBuild.selector = new char[]{'b', 'u', 'i', 'l', 'd'};
        FieldReference thisDotField = new FieldReference(data.getPluralName(), 0L);
        thisDotField.receiver = EclipseGuavaSingularizer.getBuilderReference(builderVariable);
        invokeBuild.receiver = thisDotField;
        FieldReference thisDotField2 = new FieldReference(data.getPluralName(), 0L);
        thisDotField2.receiver = EclipseGuavaSingularizer.getBuilderReference(builderVariable);
        EqualExpression isNull = new EqualExpression((Expression)thisDotField2, (Expression)new NullLiteral(0, 0), OperatorIds.EQUAL_EQUAL);
        ConditionalExpression init = new ConditionalExpression((Expression)isNull, (Expression)emptyInvoke, (Expression)invokeBuild);
        LocalDeclaration varDefStat = new LocalDeclaration(data.getPluralName(), 0, 0);
        varDefStat.type = varType;
        varDefStat.initialization = init;
        statements.add((Statement)varDefStat);
    }

    protected Statement createConstructBuilderVarIfNeeded(EclipseSingularsRecipes.SingularData data, EclipseNode builderType) {
        FieldReference thisDotField = new FieldReference(data.getPluralName(), 0L);
        thisDotField.receiver = new ThisReference(0, 0);
        FieldReference thisDotField2 = new FieldReference(data.getPluralName(), 0L);
        thisDotField2.receiver = new ThisReference(0, 0);
        EqualExpression cond = new EqualExpression((Expression)thisDotField, (Expression)new NullLiteral(0, 0), OperatorIds.EQUAL_EQUAL);
        MessageSend createBuilderInvoke = new MessageSend();
        char[][] tokenizedName = this.makeGuavaTypeName(this.getSimpleTargetTypeName(data), false);
        createBuilderInvoke.receiver = new QualifiedNameReference(tokenizedName, NULL_POSS, 0, 0);
        createBuilderInvoke.selector = this.getBuilderMethodName(data);
        return new IfStatement((Expression)cond, (Statement)new Assignment((Expression)thisDotField2, (Expression)createBuilderInvoke, 0), 0, 0);
    }

    protected abstract LombokImmutableList<String> getArgumentSuffixes();

    protected abstract String getAddMethodName();

    protected abstract String getAddAllTypeName();

    @Override
    protected int getTypeArgumentsCount() {
        return this.getArgumentSuffixes().size();
    }
}
