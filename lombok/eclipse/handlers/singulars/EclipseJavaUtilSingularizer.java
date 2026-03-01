package lombok.eclipse.handlers.singulars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.ConfigurationKeys;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.EclipseSingularsRecipes;
import lombok.eclipse.handlers.singulars.EclipseGuavaMapSingularizer;
import lombok.eclipse.handlers.singulars.EclipseGuavaSetListSingularizer;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

abstract class EclipseJavaUtilSingularizer
extends EclipseSingularsRecipes.EclipseSingularizer {
    protected static final char[][] JAVA_UTIL_ARRAYLIST = new char[][]{{'j', 'a', 'v', 'a'}, {'u', 't', 'i', 'l'}, {'A', 'r', 'r', 'a', 'y', 'L', 'i', 's', 't'}};
    protected static final char[][] JAVA_UTIL_LIST = new char[][]{{'j', 'a', 'v', 'a'}, {'u', 't', 'i', 'l'}, {'L', 'i', 's', 't'}};
    protected static final char[][] JAVA_UTIL_MAP = new char[][]{{'j', 'a', 'v', 'a'}, {'u', 't', 'i', 'l'}, {'M', 'a', 'p'}};
    protected static final char[][] JAVA_UTIL_MAP_ENTRY = new char[][]{{'j', 'a', 'v', 'a'}, {'u', 't', 'i', 'l'}, {'M', 'a', 'p'}, {'E', 'n', 't', 'r', 'y'}};
    protected static final char[][] JAVA_UTIL_COLLECTIONS = new char[][]{{'j', 'a', 'v', 'a'}, {'u', 't', 'i', 'l'}, {'C', 'o', 'l', 'l', 'e', 'c', 't', 'i', 'o', 'n', 's'}};
    protected final EclipseSingularsRecipes.EclipseSingularizer guavaListSetSingularizer = new EclipseGuavaSetListSingularizer();
    protected final EclipseSingularsRecipes.EclipseSingularizer guavaMapSingularizer = new EclipseGuavaMapSingularizer();

    EclipseJavaUtilSingularizer() {
    }

    protected boolean useGuavaInstead(EclipseNode node) {
        return Boolean.TRUE.equals(node.getAst().readConfiguration(ConfigurationKeys.SINGULAR_USE_GUAVA));
    }

    protected List<Statement> createJavaUtilSetMapInitialCapacitySwitchStatements(EclipseSingularsRecipes.SingularData data, EclipseNode builderType, boolean mapMode, String emptyCollectionMethod, String singletonCollectionMethod, String targetType, String builderVariable) {
        char[] keyName;
        ArrayList<Object> switchContents = new ArrayList<Object>();
        char[] cArray = keyName = mapMode ? (String.valueOf(new String(data.getPluralName())) + "$key").toCharArray() : data.getPluralName();
        if (emptyCollectionMethod != null) {
            switchContents.add(Eclipse.createCaseStatement((Expression)EclipseHandlerUtil.makeIntLiteral(new char[]{'0'}, null)));
            MessageSend invoke = new MessageSend();
            invoke.receiver = new QualifiedNameReference(JAVA_UTIL_COLLECTIONS, NULL_POSS, 0, 0);
            invoke.selector = emptyCollectionMethod.toCharArray();
            switchContents.add(new Assignment((Expression)new SingleNameReference(data.getPluralName(), 0L), (Expression)invoke, 0));
            switchContents.add(new BreakStatement(null, 0, 0));
        }
        if (singletonCollectionMethod != null) {
            Expression[] args;
            switchContents.add(Eclipse.createCaseStatement((Expression)EclipseHandlerUtil.makeIntLiteral(new char[]{'1'}, null)));
            FieldReference thisDotKey = new FieldReference(keyName, 0L);
            thisDotKey.receiver = EclipseJavaUtilSingularizer.getBuilderReference(builderVariable);
            MessageSend thisDotKeyGet0 = new MessageSend();
            thisDotKeyGet0.receiver = thisDotKey;
            thisDotKeyGet0.selector = new char[]{'g', 'e', 't'};
            thisDotKeyGet0.arguments = new Expression[]{EclipseHandlerUtil.makeIntLiteral(new char[]{'0'}, null)};
            if (mapMode) {
                char[] valueName = (String.valueOf(new String(data.getPluralName())) + "$value").toCharArray();
                FieldReference thisDotValue = new FieldReference(valueName, 0L);
                thisDotValue.receiver = EclipseJavaUtilSingularizer.getBuilderReference(builderVariable);
                MessageSend thisDotValueGet0 = new MessageSend();
                thisDotValueGet0.receiver = thisDotValue;
                thisDotValueGet0.selector = new char[]{'g', 'e', 't'};
                thisDotValueGet0.arguments = new Expression[]{EclipseHandlerUtil.makeIntLiteral(new char[]{'0'}, null)};
                args = new Expression[]{thisDotKeyGet0, thisDotValueGet0};
            } else {
                args = new Expression[]{thisDotKeyGet0};
            }
            MessageSend invoke = new MessageSend();
            invoke.receiver = new QualifiedNameReference(JAVA_UTIL_COLLECTIONS, NULL_POSS, 0, 0);
            invoke.selector = singletonCollectionMethod.toCharArray();
            invoke.arguments = args;
            switchContents.add(new Assignment((Expression)new SingleNameReference(data.getPluralName(), 0L), (Expression)invoke, 0));
            switchContents.add(new BreakStatement(null, 0, 0));
        }
        switchContents.add(Eclipse.createCaseStatement(null));
        switchContents.addAll(this.createJavaUtilSimpleCreationAndFillStatements(data, builderType, mapMode, false, true, emptyCollectionMethod == null, targetType, builderVariable));
        SwitchStatement switchStat = new SwitchStatement();
        switchStat.statements = switchContents.toArray(new Statement[0]);
        switchStat.expression = this.getSize(builderType, keyName, true, builderVariable);
        QualifiedTypeReference localShadowerType = new QualifiedTypeReference(Eclipse.fromQualifiedName(data.getTargetFqn()), NULL_POSS);
        localShadowerType = this.addTypeArgs(mapMode ? 2 : 1, false, builderType, (TypeReference)localShadowerType, data.getTypeArgs());
        LocalDeclaration varDefStat = new LocalDeclaration(data.getPluralName(), 0, 0);
        varDefStat.type = localShadowerType;
        return Arrays.asList(varDefStat, switchStat);
    }

    protected List<Statement> createJavaUtilSimpleCreationAndFillStatements(EclipseSingularsRecipes.SingularData data, EclipseNode builderType, boolean mapMode, boolean defineVar, boolean addInitialCapacityArg, boolean nullGuard, String targetType, String builderVariable) {
        MessageSend fillStat;
        Assignment createStat;
        char[] varName = mapMode ? (String.valueOf(new String(data.getPluralName())) + "$key").toCharArray() : data.getPluralName();
        Expression[] constructorArgs = null;
        if (addInitialCapacityArg) {
            BinaryExpression lessThanCutoff = new BinaryExpression(this.getSize(builderType, varName, nullGuard, builderVariable), (Expression)EclipseHandlerUtil.makeIntLiteral("0x40000000".toCharArray(), null), 4);
            FieldReference integerMaxValue = new FieldReference("MAX_VALUE".toCharArray(), 0L);
            integerMaxValue.receiver = new QualifiedNameReference(TypeConstants.JAVA_LANG_INTEGER, NULL_POSS, 0, 0);
            BinaryExpression sizeFormulaLeft = new BinaryExpression((Expression)EclipseHandlerUtil.makeIntLiteral(new char[]{'1'}, null), this.getSize(builderType, varName, nullGuard, builderVariable), 14);
            BinaryExpression sizeFormulaRightLeft = new BinaryExpression(this.getSize(builderType, varName, nullGuard, builderVariable), (Expression)EclipseHandlerUtil.makeIntLiteral(new char[]{'3'}, null), 13);
            BinaryExpression sizeFormulaRight = new BinaryExpression((Expression)sizeFormulaRightLeft, (Expression)EclipseHandlerUtil.makeIntLiteral(new char[]{'3'}, null), 9);
            BinaryExpression sizeFormula = new BinaryExpression((Expression)sizeFormulaLeft, (Expression)sizeFormulaRight, 14);
            ConditionalExpression cond = new ConditionalExpression((Expression)lessThanCutoff, (Expression)sizeFormula, (Expression)integerMaxValue);
            constructorArgs = new Expression[]{cond};
        }
        QualifiedTypeReference targetTypeRef = new QualifiedTypeReference((char[][])new char[][]{TypeConstants.JAVA, TypeConstants.UTIL, targetType.toCharArray()}, NULL_POSS);
        targetTypeRef = this.addTypeArgs(mapMode ? 2 : 1, false, builderType, (TypeReference)targetTypeRef, data.getTypeArgs());
        AllocationExpression constructorCall = new AllocationExpression();
        constructorCall.type = targetTypeRef;
        constructorCall.arguments = constructorArgs;
        if (defineVar) {
            QualifiedTypeReference localShadowerType = new QualifiedTypeReference(Eclipse.fromQualifiedName(data.getTargetFqn()), NULL_POSS);
            localShadowerType = this.addTypeArgs(mapMode ? 2 : 1, false, builderType, (TypeReference)localShadowerType, data.getTypeArgs());
            LocalDeclaration localShadowerDecl = new LocalDeclaration(data.getPluralName(), 0, 0);
            localShadowerDecl.type = localShadowerType;
            localShadowerDecl.initialization = constructorCall;
            createStat = localShadowerDecl;
        } else {
            createStat = new Assignment((Expression)new SingleNameReference(data.getPluralName(), 0L), (Expression)constructorCall, 0);
        }
        if (mapMode) {
            char[] iVar = new char[]{'$', 'i'};
            MessageSend pluralnameDotPut = new MessageSend();
            pluralnameDotPut.selector = new char[]{'p', 'u', 't'};
            pluralnameDotPut.receiver = new SingleNameReference(data.getPluralName(), 0L);
            FieldReference thisDotKey = new FieldReference(varName, 0L);
            thisDotKey.receiver = EclipseJavaUtilSingularizer.getBuilderReference(builderVariable);
            FieldReference thisDotValue = new FieldReference((String.valueOf(new String(data.getPluralName())) + "$value").toCharArray(), 0L);
            thisDotValue.receiver = EclipseJavaUtilSingularizer.getBuilderReference(builderVariable);
            MessageSend keyArg = new MessageSend();
            keyArg.receiver = thisDotKey;
            keyArg.arguments = new Expression[]{new SingleNameReference(iVar, 0L)};
            keyArg.selector = new char[]{'g', 'e', 't'};
            MessageSend valueArg = new MessageSend();
            valueArg.receiver = thisDotValue;
            valueArg.arguments = new Expression[]{new SingleNameReference(iVar, 0L)};
            valueArg.selector = new char[]{'g', 'e', 't'};
            pluralnameDotPut.arguments = new Expression[]{keyArg, valueArg};
            LocalDeclaration forInit = new LocalDeclaration(iVar, 0, 0);
            forInit.type = TypeReference.baseTypeReference((int)10, (int)0);
            forInit.initialization = EclipseHandlerUtil.makeIntLiteral(new char[]{'0'}, null);
            BinaryExpression checkExpr = new BinaryExpression((Expression)new SingleNameReference(iVar, 0L), this.getSize(builderType, varName, nullGuard, builderVariable), 4);
            PostfixExpression incrementExpr = new PostfixExpression((Expression)new SingleNameReference(iVar, 0L), (Expression)IntLiteral.One, 14, 0);
            fillStat = new ForStatement(new Statement[]{forInit}, (Expression)checkExpr, new Statement[]{incrementExpr}, (Statement)pluralnameDotPut, true, 0, 0);
        } else {
            MessageSend pluralnameDotAddAll = new MessageSend();
            pluralnameDotAddAll.selector = new char[]{'a', 'd', 'd', 'A', 'l', 'l'};
            pluralnameDotAddAll.receiver = new SingleNameReference(data.getPluralName(), 0L);
            FieldReference thisDotPluralname = new FieldReference(varName, 0L);
            thisDotPluralname.receiver = EclipseJavaUtilSingularizer.getBuilderReference(builderVariable);
            pluralnameDotAddAll.arguments = new Expression[]{thisDotPluralname};
            fillStat = pluralnameDotAddAll;
        }
        if (nullGuard) {
            FieldReference thisDotField = new FieldReference(varName, 0L);
            thisDotField.receiver = EclipseJavaUtilSingularizer.getBuilderReference(builderVariable);
            EqualExpression cond = new EqualExpression((Expression)thisDotField, (Expression)new NullLiteral(0, 0), 29);
            fillStat = new IfStatement((Expression)cond, (Statement)fillStat, 0, 0);
        }
        SingleNameReference arg = new SingleNameReference(data.getPluralName(), 0L);
        MessageSend invoke = new MessageSend();
        invoke.arguments = new Expression[]{arg};
        invoke.selector = ("unmodifiable" + data.getTargetSimpleType()).toCharArray();
        invoke.receiver = new QualifiedNameReference(JAVA_UTIL_COLLECTIONS, NULL_POSS, 0, 0);
        Assignment unmodifiableStat = new Assignment((Expression)new SingleNameReference(data.getPluralName(), 0L), (Expression)invoke, 0);
        return Arrays.asList(createStat, fillStat, unmodifiableStat);
    }

    protected Statement createConstructBuilderVarIfNeeded(EclipseSingularsRecipes.SingularData data, EclipseNode builderType, boolean mapMode) {
        Assignment thenPart;
        char[] v2Name;
        char[] v1Name;
        if (mapMode) {
            String n = new String(data.getPluralName());
            v1Name = (String.valueOf(n) + "$key").toCharArray();
            v2Name = (String.valueOf(n) + "$value").toCharArray();
        } else {
            v1Name = data.getPluralName();
            v2Name = null;
        }
        FieldReference thisDotField = new FieldReference(v1Name, 0L);
        thisDotField.receiver = new ThisReference(0, 0);
        EqualExpression cond = new EqualExpression((Expression)thisDotField, (Expression)new NullLiteral(0, 0), 18);
        thisDotField = new FieldReference(v1Name, 0L);
        thisDotField.receiver = new ThisReference(0, 0);
        QualifiedTypeReference v1Type = new QualifiedTypeReference(JAVA_UTIL_ARRAYLIST, NULL_POSS);
        v1Type = this.addTypeArgs(1, false, builderType, (TypeReference)v1Type, data.getTypeArgs());
        AllocationExpression constructArrayList = new AllocationExpression();
        constructArrayList.type = v1Type;
        Assignment initV1 = new Assignment((Expression)thisDotField, (Expression)constructArrayList, 0);
        if (mapMode) {
            thisDotField = new FieldReference(v2Name, 0L);
            thisDotField.receiver = new ThisReference(0, 0);
            QualifiedTypeReference v2Type = new QualifiedTypeReference(JAVA_UTIL_ARRAYLIST, NULL_POSS);
            List<Object> tArgs = data.getTypeArgs();
            tArgs = tArgs != null && tArgs.size() > 1 ? Collections.singletonList((TypeReference)tArgs.get(1)) : Collections.emptyList();
            v2Type = this.addTypeArgs(1, false, builderType, (TypeReference)v2Type, tArgs);
            constructArrayList = new AllocationExpression();
            constructArrayList.type = v2Type;
            Assignment initV2 = new Assignment((Expression)thisDotField, (Expression)constructArrayList, 0);
            Block b = new Block(0);
            b.statements = new Statement[]{initV1, initV2};
            thenPart = b;
        } else {
            thenPart = initV1;
        }
        return new IfStatement((Expression)cond, (Statement)thenPart, 0, 0);
    }
}
