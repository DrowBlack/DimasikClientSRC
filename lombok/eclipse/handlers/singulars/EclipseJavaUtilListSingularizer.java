package lombok.eclipse.handlers.singulars;

import java.util.ArrayList;
import java.util.List;
import lombok.core.LombokImmutableList;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.EclipseSingularsRecipes;
import lombok.eclipse.handlers.singulars.EclipseJavaUtilListSetSingularizer;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public class EclipseJavaUtilListSingularizer
extends EclipseJavaUtilListSetSingularizer {
    private static final char[] EMPTY_LIST = new char[]{'e', 'm', 'p', 't', 'y', 'L', 'i', 's', 't'};

    @Override
    public LombokImmutableList<String> getSupportedTypes() {
        return LombokImmutableList.of("java.util.List", "java.util.Collection", "java.lang.Iterable");
    }

    @Override
    protected char[][] getEmptyMakerReceiver(String targetFqn) {
        return JAVA_UTIL_COLLECTIONS;
    }

    @Override
    protected char[] getEmptyMakerSelector(String targetFqn) {
        return EMPTY_LIST;
    }

    @Override
    public void appendBuildCode(EclipseSingularsRecipes.SingularData data, EclipseNode builderType, List<Statement> statements, char[] targetVariableName, String builderVariable) {
        if (this.useGuavaInstead(builderType)) {
            this.guavaListSetSingularizer.appendBuildCode(data, builderType, statements, targetVariableName, builderVariable);
            return;
        }
        ArrayList<Object> switchContents = new ArrayList<Object>();
        switchContents.add(Eclipse.createCaseStatement((Expression)EclipseHandlerUtil.makeIntLiteral(new char[]{'0'}, null)));
        MessageSend invoke = new MessageSend();
        invoke.receiver = new QualifiedNameReference(JAVA_UTIL_COLLECTIONS, NULL_POSS, 0, 0);
        invoke.selector = "emptyList".toCharArray();
        switchContents.add(new Assignment((Expression)new SingleNameReference(data.getPluralName(), 0L), (Expression)invoke, 0));
        switchContents.add(new BreakStatement(null, 0, 0));
        switchContents.add(Eclipse.createCaseStatement((Expression)EclipseHandlerUtil.makeIntLiteral(new char[]{'1'}, null)));
        FieldReference thisDotField = new FieldReference(data.getPluralName(), 0L);
        thisDotField.receiver = EclipseJavaUtilListSingularizer.getBuilderReference(builderVariable);
        MessageSend thisDotFieldGet0 = new MessageSend();
        thisDotFieldGet0.receiver = thisDotField;
        thisDotFieldGet0.selector = new char[]{'g', 'e', 't'};
        thisDotFieldGet0.arguments = new Expression[]{EclipseHandlerUtil.makeIntLiteral(new char[]{'0'}, null)};
        Expression[] args = new Expression[]{thisDotFieldGet0};
        MessageSend invoke2 = new MessageSend();
        invoke2.receiver = new QualifiedNameReference(JAVA_UTIL_COLLECTIONS, NULL_POSS, 0, 0);
        invoke2.selector = "singletonList".toCharArray();
        invoke2.arguments = args;
        switchContents.add(new Assignment((Expression)new SingleNameReference(data.getPluralName(), 0L), (Expression)invoke2, 0));
        switchContents.add(new BreakStatement(null, 0, 0));
        switchContents.add(Eclipse.createCaseStatement(null));
        FieldReference thisDotPluralName = new FieldReference(data.getPluralName(), 0L);
        thisDotPluralName.receiver = EclipseJavaUtilListSingularizer.getBuilderReference(builderVariable);
        QualifiedTypeReference targetTypeExpr = new QualifiedTypeReference(JAVA_UTIL_ARRAYLIST, NULL_POSS);
        targetTypeExpr = this.addTypeArgs(1, false, builderType, (TypeReference)targetTypeExpr, data.getTypeArgs());
        AllocationExpression constructorCall = new AllocationExpression();
        constructorCall.type = targetTypeExpr;
        constructorCall.arguments = new Expression[]{thisDotPluralName};
        AllocationExpression argToUnmodifiable = constructorCall;
        MessageSend unmodInvoke = new MessageSend();
        unmodInvoke.receiver = new QualifiedNameReference(JAVA_UTIL_COLLECTIONS, NULL_POSS, 0, 0);
        unmodInvoke.selector = "unmodifiableList".toCharArray();
        unmodInvoke.arguments = new Expression[]{argToUnmodifiable};
        switchContents.add(new Assignment((Expression)new SingleNameReference(data.getPluralName(), 0L), (Expression)unmodInvoke, 0));
        SwitchStatement switchStat = new SwitchStatement();
        switchStat.statements = switchContents.toArray(new Statement[0]);
        switchStat.expression = this.getSize(builderType, data.getPluralName(), true, builderVariable);
        QualifiedTypeReference localShadowerType = new QualifiedTypeReference(Eclipse.fromQualifiedName(data.getTargetFqn()), NULL_POSS);
        localShadowerType = this.addTypeArgs(1, false, builderType, (TypeReference)localShadowerType, data.getTypeArgs());
        LocalDeclaration varDefStat = new LocalDeclaration(data.getPluralName(), 0, 0);
        varDefStat.type = localShadowerType;
        statements.add((Statement)varDefStat);
        statements.add((Statement)switchStat);
    }
}
