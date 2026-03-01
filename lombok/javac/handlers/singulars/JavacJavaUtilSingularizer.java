package lombok.javac.handlers.singulars;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import lombok.javac.Javac;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.javac.handlers.JavacSingularsRecipes;

abstract class JavacJavaUtilSingularizer
extends JavacSingularsRecipes.JavacSingularizer {
    JavacJavaUtilSingularizer() {
    }

    protected List<JCTree.JCStatement> createJavaUtilSetMapInitialCapacitySwitchStatements(JavacTreeMaker maker, JavacSingularsRecipes.SingularData data, JavacNode builderType, boolean mapMode, String emptyCollectionMethod, String singletonCollectionMethod, String targetType, JavacNode source, String builderVariable) {
        JCTree.JCBreak breakStat;
        JCTree.JCExpressionStatement assignStat;
        List<JCTree.JCExpression> jceBlank = List.nil();
        ListBuffer<JCTree.JCCase> cases = new ListBuffer<JCTree.JCCase>();
        if (emptyCollectionMethod != null) {
            JCTree.JCMethodInvocation invoke = maker.Apply(jceBlank, JavacHandlerUtil.chainDots(builderType, "java", "util", "Collections", emptyCollectionMethod), jceBlank);
            assignStat = maker.Exec(maker.Assign(maker.Ident(data.getPluralName()), invoke));
            breakStat = maker.Break(null);
            JCTree.JCCase emptyCase = maker.Case(maker.Literal(Javac.CTC_INT, 0), List.of(assignStat, breakStat));
            cases.append(emptyCase);
        }
        if (singletonCollectionMethod != null) {
            List<JCTree.JCExpression> args;
            JCTree.JCLiteral zeroLiteral = maker.Literal(Javac.CTC_INT, 0);
            JCTree.JCMethodInvocation arg = maker.Apply(jceBlank, JavacHandlerUtil.chainDots(builderType, builderVariable, data.getPluralName() + (mapMode ? "$key" : ""), "get"), List.of(zeroLiteral));
            if (mapMode) {
                JCTree.JCLiteral zeroLiteralClone = maker.Literal(Javac.CTC_INT, 0);
                JCTree.JCMethodInvocation arg2 = maker.Apply(jceBlank, JavacHandlerUtil.chainDots(builderType, builderVariable, data.getPluralName() + (mapMode ? "$value" : ""), "get"), List.of(zeroLiteralClone));
                args = List.of(arg, arg2);
            } else {
                args = List.of(arg);
            }
            JCTree.JCMethodInvocation invoke = maker.Apply(jceBlank, JavacHandlerUtil.chainDots(builderType, "java", "util", "Collections", singletonCollectionMethod), args);
            assignStat = maker.Exec(maker.Assign(maker.Ident(data.getPluralName()), invoke));
            breakStat = maker.Break(null);
            JCTree.JCCase singletonCase = maker.Case(maker.Literal(Javac.CTC_INT, 1), List.of(assignStat, breakStat));
            cases.append(singletonCase);
        }
        List<JCTree.JCStatement> statements = this.createJavaUtilSimpleCreationAndFillStatements(maker, data, builderType, mapMode, false, true, emptyCollectionMethod == null, targetType, source, builderVariable);
        JCTree.JCCase defaultCase = maker.Case(null, statements);
        cases.append(defaultCase);
        JCTree.JCSwitch switchStat = maker.Switch(this.getSize(maker, builderType, mapMode ? builderType.toName(data.getPluralName() + "$key") : data.getPluralName(), true, false, builderVariable), cases.toList());
        JCTree.JCExpression localShadowerType = JavacHandlerUtil.chainDotsString(builderType, data.getTargetFqn());
        localShadowerType = this.addTypeArgs(mapMode ? 2 : 1, false, builderType, localShadowerType, data.getTypeArgs(), source);
        JCTree.JCVariableDecl varDefStat = maker.VarDef(maker.Modifiers(0L), data.getPluralName(), localShadowerType, null);
        return List.of(varDefStat, switchStat);
    }

    protected JCTree.JCStatement createConstructBuilderVarIfNeeded(JavacTreeMaker maker, JavacSingularsRecipes.SingularData data, JavacNode builderType, boolean mapMode, JavacNode source) {
        JCTree.JCStatement thenPart;
        List<JCTree.JCExpression> jceBlank = List.nil();
        Name v1Name = mapMode ? builderType.toName(data.getPluralName() + "$key") : data.getPluralName();
        Name v2Name = mapMode ? builderType.toName(data.getPluralName() + "$value") : null;
        JCTree.JCFieldAccess thisDotField = maker.Select(maker.Ident(builderType.toName("this")), v1Name);
        JCTree.JCBinary cond = maker.Binary(Javac.CTC_EQUAL, thisDotField, maker.Literal(Javac.CTC_BOT, null));
        thisDotField = maker.Select(maker.Ident(builderType.toName("this")), v1Name);
        JCTree.JCExpression v1Type = JavacHandlerUtil.chainDots(builderType, "java", "util", "ArrayList");
        v1Type = this.addTypeArgs(1, false, builderType, v1Type, data.getTypeArgs(), source);
        JCTree.JCNewClass constructArrayList = maker.NewClass(null, jceBlank, v1Type, jceBlank, null);
        JCTree.JCExpressionStatement initV1 = maker.Exec(maker.Assign(thisDotField, constructArrayList));
        if (mapMode) {
            thisDotField = maker.Select(maker.Ident(builderType.toName("this")), v2Name);
            JCTree.JCExpression v2Type = JavacHandlerUtil.chainDots(builderType, "java", "util", "ArrayList");
            List<JCTree.JCExpression> tArgs = data.getTypeArgs();
            tArgs = tArgs != null && tArgs.tail != null ? tArgs.tail : List.nil();
            v2Type = this.addTypeArgs(1, false, builderType, v2Type, tArgs, source);
            constructArrayList = maker.NewClass(null, jceBlank, v2Type, jceBlank, null);
            JCTree.JCExpressionStatement initV2 = maker.Exec(maker.Assign(thisDotField, constructArrayList));
            thenPart = maker.Block(0L, List.of(initV1, initV2));
        } else {
            thenPart = initV1;
        }
        return maker.If(cond, thenPart, null);
    }

    protected List<JCTree.JCStatement> createJavaUtilSimpleCreationAndFillStatements(JavacTreeMaker maker, JavacSingularsRecipes.SingularData data, JavacNode builderType, boolean mapMode, boolean defineVar, boolean addInitialCapacityArg, boolean nullGuard, String targetType, JavacNode source, String builderVariable) {
        JCTree.JCStatement fillStat;
        JCTree.JCStatement createStat;
        List<JCTree.JCExpression> jceBlank = List.nil();
        Name thisName = builderType.toName(builderVariable);
        List<JCTree.JCExpression> constructorArgs = List.nil();
        if (addInitialCapacityArg) {
            Name varName = mapMode ? builderType.toName(data.getPluralName() + "$key") : data.getPluralName();
            JCTree.JCBinary lessThanCutoff = maker.Binary(Javac.CTC_LESS_THAN, this.getSize(maker, builderType, varName, nullGuard, true, builderVariable), maker.Literal(Javac.CTC_INT, 0x40000000));
            JCTree.JCExpression integerMaxValue = JavacHandlerUtil.genJavaLangTypeRef(builderType, "Integer", "MAX_VALUE");
            JCTree.JCBinary sizeFormulaLeft = maker.Binary(Javac.CTC_PLUS, maker.Literal(Javac.CTC_INT, 1), this.getSize(maker, builderType, varName, nullGuard, true, builderVariable));
            JCTree.JCParens sizeFormulaRightLeft = maker.Parens(maker.Binary(Javac.CTC_MINUS, this.getSize(maker, builderType, varName, nullGuard, true, builderVariable), maker.Literal(Javac.CTC_INT, 3)));
            JCTree.JCBinary sizeFormulaRight = maker.Binary(Javac.CTC_DIV, sizeFormulaRightLeft, maker.Literal(Javac.CTC_INT, 3));
            JCTree.JCBinary sizeFormula = maker.Binary(Javac.CTC_PLUS, sizeFormulaLeft, sizeFormulaRight);
            constructorArgs = List.of(maker.Conditional(lessThanCutoff, sizeFormula, integerMaxValue));
        }
        JCTree.JCExpression targetTypeExpr = JavacHandlerUtil.chainDots(builderType, "java", "util", targetType);
        targetTypeExpr = this.addTypeArgs(mapMode ? 2 : 1, false, builderType, targetTypeExpr, data.getTypeArgs(), source);
        JCTree.JCNewClass constructorCall = maker.NewClass(null, jceBlank, targetTypeExpr, constructorArgs, null);
        if (defineVar) {
            JCTree.JCExpression localShadowerType = JavacHandlerUtil.chainDotsString(builderType, data.getTargetFqn());
            localShadowerType = this.addTypeArgs(mapMode ? 2 : 1, false, builderType, localShadowerType, data.getTypeArgs(), source);
            createStat = maker.VarDef(maker.Modifiers(0L), data.getPluralName(), localShadowerType, constructorCall);
        } else {
            createStat = maker.Exec(maker.Assign(maker.Ident(data.getPluralName()), constructorCall));
        }
        if (mapMode) {
            Name ivar = builderType.toName("$i");
            Name keyVarName = builderType.toName(data.getPluralName() + "$key");
            JCTree.JCFieldAccess pluralnameDotPut = maker.Select(maker.Ident(data.getPluralName()), builderType.toName("put"));
            JCTree.JCMethodInvocation arg1 = maker.Apply(jceBlank, JavacHandlerUtil.chainDots(builderType, builderVariable, data.getPluralName() + "$key", "get"), List.of(maker.Ident(ivar)));
            JCTree.JCExpression arg2 = maker.Apply(jceBlank, JavacHandlerUtil.chainDots(builderType, builderVariable, data.getPluralName() + "$value", "get"), List.of(maker.Ident(ivar)));
            arg2 = maker.TypeCast(this.createTypeArgs(2, false, builderType, data.getTypeArgs(), source).get(1), arg2);
            JCTree.JCExpressionStatement putStatement = maker.Exec(maker.Apply(jceBlank, pluralnameDotPut, List.of(arg1, arg2)));
            JCTree.JCVariableDecl forInit = maker.VarDef(maker.Modifiers(0L), ivar, maker.TypeIdent(Javac.CTC_INT), maker.Literal(Javac.CTC_INT, 0));
            JCTree.JCBinary checkExpr = maker.Binary(Javac.CTC_LESS_THAN, maker.Ident(ivar), this.getSize(maker, builderType, keyVarName, nullGuard, true, builderVariable));
            JCTree.JCUnary incrementExpr = maker.Unary(Javac.CTC_POSTINC, maker.Ident(ivar));
            fillStat = maker.ForLoop(List.of(forInit), checkExpr, List.of(maker.Exec(incrementExpr)), putStatement);
        } else {
            JCTree.JCFieldAccess thisDotPluralName = maker.Select(maker.Ident(thisName), data.getPluralName());
            fillStat = maker.Exec(maker.Apply(jceBlank, maker.Select(maker.Ident(data.getPluralName()), builderType.toName("addAll")), List.of(thisDotPluralName)));
        }
        if (nullGuard) {
            JCTree.JCFieldAccess thisDotField = maker.Select(maker.Ident(thisName), mapMode ? builderType.toName(data.getPluralName() + "$key") : data.getPluralName());
            JCTree.JCBinary nullCheck = maker.Binary(Javac.CTC_NOT_EQUAL, thisDotField, maker.Literal(Javac.CTC_BOT, null));
            fillStat = maker.If(nullCheck, fillStat, null);
        }
        JCTree.JCIdent arg = maker.Ident(data.getPluralName());
        JCTree.JCMethodInvocation invoke = maker.Apply(jceBlank, JavacHandlerUtil.chainDots(builderType, "java", "util", "Collections", "unmodifiable" + data.getTargetSimpleType()), List.of(arg));
        JCTree.JCExpressionStatement unmodifiableStat = maker.Exec(maker.Assign(maker.Ident(data.getPluralName()), invoke));
        return List.of(createStat, fillStat, unmodifiableStat);
    }
}
