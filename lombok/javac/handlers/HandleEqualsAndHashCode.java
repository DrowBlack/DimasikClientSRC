package lombok.javac.handlers;

import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import lombok.ConfigurationKeys;
import lombok.EqualsAndHashCode;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.configuration.CallSuperType;
import lombok.core.configuration.CheckerFrameworkVersion;
import lombok.core.handlers.HandlerUtil;
import lombok.core.handlers.InclusionExclusionUtils;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleEqualsAndHashCode
extends JavacAnnotationHandler<EqualsAndHashCode> {
    private static final String RESULT_NAME = "result";
    private static final String PRIME_NAME = "PRIME";
    private static final String HASH_CODE_CACHE_NAME = "$hashCodeCache";

    @Override
    public void handle(AnnotationValues<EqualsAndHashCode> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.EQUALS_AND_HASH_CODE_FLAG_USAGE, "@EqualsAndHashCode");
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, EqualsAndHashCode.class);
        JavacHandlerUtil.deleteImportFromCompilationUnit(annotationNode, EqualsAndHashCode.CacheStrategy.class.getName());
        EqualsAndHashCode ann = annotation.getInstance();
        java.util.List<InclusionExclusionUtils.Included<JavacNode, EqualsAndHashCode.Include>> members = InclusionExclusionUtils.handleEqualsAndHashCodeMarking(annotationNode.up(), annotation, annotationNode);
        JavacNode typeNode = (JavacNode)annotationNode.up();
        List<JCTree.JCAnnotation> onParam = JavacHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onParam", "@EqualsAndHashCode(onParam", annotationNode);
        Boolean callSuper = ann.callSuper();
        if (!annotation.isExplicit("callSuper")) {
            callSuper = null;
        }
        Boolean doNotUseGettersConfiguration = annotationNode.getAst().readConfiguration(ConfigurationKeys.EQUALS_AND_HASH_CODE_DO_NOT_USE_GETTERS);
        boolean doNotUseGetters = annotation.isExplicit("doNotUseGetters") || doNotUseGettersConfiguration == null ? ann.doNotUseGetters() : doNotUseGettersConfiguration.booleanValue();
        HandlerUtil.FieldAccess fieldAccess = doNotUseGetters ? HandlerUtil.FieldAccess.PREFER_FIELD : HandlerUtil.FieldAccess.GETTER;
        boolean cacheHashCode = ann.cacheStrategy() == EqualsAndHashCode.CacheStrategy.LAZY;
        this.generateMethods(typeNode, annotationNode, members, callSuper, true, cacheHashCode, fieldAccess, onParam);
    }

    public void generateEqualsAndHashCodeForType(JavacNode typeNode, JavacNode source) {
        if (JavacHandlerUtil.hasAnnotation(EqualsAndHashCode.class, typeNode)) {
            return;
        }
        Boolean doNotUseGettersConfiguration = typeNode.getAst().readConfiguration(ConfigurationKeys.EQUALS_AND_HASH_CODE_DO_NOT_USE_GETTERS);
        HandlerUtil.FieldAccess access = doNotUseGettersConfiguration == null || doNotUseGettersConfiguration == false ? HandlerUtil.FieldAccess.GETTER : HandlerUtil.FieldAccess.PREFER_FIELD;
        java.util.List<InclusionExclusionUtils.Included<JavacNode, EqualsAndHashCode.Include>> members = InclusionExclusionUtils.handleEqualsAndHashCodeMarking(typeNode, null, null);
        this.generateMethods(typeNode, source, members, null, false, false, access, List.<JCTree.JCAnnotation>nil());
    }

    public void generateMethods(JavacNode typeNode, JavacNode source, java.util.List<InclusionExclusionUtils.Included<JavacNode, EqualsAndHashCode.Include>> members, Boolean callSuper, boolean whineIfExists, boolean cacheHashCode, HandlerUtil.FieldAccess fieldAccess, List<JCTree.JCAnnotation> onParam) {
        boolean implicitCallSuper;
        if (!JavacHandlerUtil.isClass(typeNode)) {
            source.addError("@EqualsAndHashCode is only supported on a class.");
            return;
        }
        boolean bl = implicitCallSuper = callSuper == null;
        if (callSuper == null) {
            try {
                callSuper = (boolean)((Boolean)EqualsAndHashCode.class.getMethod("callSuper", new Class[0]).getDefaultValue());
            }
            catch (Exception exception) {
                throw new InternalError("Lombok bug - this cannot happen - can't find callSuper field in EqualsAndHashCode annotation.");
            }
        }
        boolean isDirectDescendantOfObject = JavacHandlerUtil.isDirectDescendantOfObject(typeNode);
        boolean isFinal = (((JCTree.JCClassDecl)typeNode.get()).mods.flags & 0x10L) != 0L;
        boolean needsCanEqual = !isFinal || !isDirectDescendantOfObject;
        JavacHandlerUtil.MemberExistsResult equalsExists = JavacHandlerUtil.methodExists("equals", typeNode, 1);
        JavacHandlerUtil.MemberExistsResult hashCodeExists = JavacHandlerUtil.methodExists("hashCode", typeNode, 0);
        JavacHandlerUtil.MemberExistsResult canEqualExists = JavacHandlerUtil.methodExists("canEqual", typeNode, 1);
        switch (Collections.max(Arrays.asList(equalsExists, hashCodeExists))) {
            case EXISTS_BY_LOMBOK: {
                return;
            }
            case EXISTS_BY_USER: {
                if (whineIfExists) {
                    String msg = "Not generating equals and hashCode: A method with one of those names already exists. (Either both or none of these methods will be generated).";
                    source.addWarning(msg);
                } else if (equalsExists == JavacHandlerUtil.MemberExistsResult.NOT_EXISTS || hashCodeExists == JavacHandlerUtil.MemberExistsResult.NOT_EXISTS) {
                    String msg = String.format("Not generating %s: One of equals or hashCode exists. You should either write both of these or none of these (in the latter case, lombok generates them).", equalsExists == JavacHandlerUtil.MemberExistsResult.NOT_EXISTS ? "equals" : "hashCode");
                    source.addWarning(msg);
                }
                return;
            }
        }
        if (isDirectDescendantOfObject && callSuper.booleanValue()) {
            source.addError("Generating equals/hashCode with a supercall to java.lang.Object is pointless.");
            return;
        }
        if (implicitCallSuper && !isDirectDescendantOfObject) {
            CallSuperType cst = typeNode.getAst().readConfiguration(ConfigurationKeys.EQUALS_AND_HASH_CODE_CALL_SUPER);
            if (cst == null) {
                cst = CallSuperType.WARN;
            }
            switch (cst) {
                default: {
                    source.addWarning("Generating equals/hashCode implementation but without a call to superclass, even though this class does not extend java.lang.Object. If this is intentional, add '@EqualsAndHashCode(callSuper=false)' to your type.");
                    callSuper = false;
                    break;
                }
                case SKIP: {
                    callSuper = false;
                    break;
                }
                case CALL: {
                    callSuper = true;
                }
            }
        }
        JCTree.JCMethodDecl equalsMethod = this.createEquals(typeNode, members, callSuper, fieldAccess, needsCanEqual, source, onParam);
        JavacHandlerUtil.injectMethod(typeNode, equalsMethod);
        if (needsCanEqual && canEqualExists == JavacHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            JCTree.JCMethodDecl canEqualMethod = this.createCanEqual(typeNode, source, JavacHandlerUtil.copyAnnotations(onParam));
            JavacHandlerUtil.injectMethod(typeNode, canEqualMethod);
        }
        if (cacheHashCode) {
            if (JavacHandlerUtil.fieldExists(HASH_CODE_CACHE_NAME, typeNode) != JavacHandlerUtil.MemberExistsResult.NOT_EXISTS) {
                String msg = String.format("Not caching the result of hashCode: A field named %s already exists.", HASH_CODE_CACHE_NAME);
                source.addWarning(msg);
                cacheHashCode = false;
            } else {
                this.createHashCodeCacheField(typeNode, source);
            }
        }
        JCTree.JCMethodDecl hashCodeMethod = this.createHashCode(typeNode, members, callSuper, cacheHashCode, fieldAccess, source);
        JavacHandlerUtil.injectMethod(typeNode, hashCodeMethod);
    }

    private void createHashCodeCacheField(JavacNode typeNode, JavacNode source) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCTree.JCModifiers mods = maker.Modifiers(130L);
        JCTree.JCVariableDecl hashCodeCacheField = maker.VarDef(mods, typeNode.toName(HASH_CODE_CACHE_NAME), maker.TypeIdent(Javac.CTC_INT), null);
        JavacHandlerUtil.injectFieldAndMarkGenerated(typeNode, hashCodeCacheField);
        JavacHandlerUtil.recursiveSetGeneratedBy(hashCodeCacheField, source);
    }

    public JCTree.JCMethodDecl createHashCode(JavacNode typeNode, java.util.List<InclusionExclusionUtils.Included<JavacNode, EqualsAndHashCode.Include>> members, boolean callSuper, boolean cacheHashCode, HandlerUtil.FieldAccess fieldAccess, JavacNode source) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCTree.JCAnnotation overrideAnnotation = maker.Annotation(JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Override"), List.<JCTree.JCExpression>nil());
        List<JCTree.JCAnnotation> annsOnMethod = List.of(overrideAnnotation);
        CheckerFrameworkVersion checkerFramework = JavacHandlerUtil.getCheckerFrameworkVersion(typeNode);
        if (cacheHashCode && checkerFramework.generatePure()) {
            annsOnMethod = annsOnMethod.prepend(maker.Annotation(JavacHandlerUtil.genTypeRef(typeNode, "org.checkerframework.dataflow.qual.Pure"), List.<JCTree.JCExpression>nil()));
        } else if (checkerFramework.generateSideEffectFree()) {
            annsOnMethod = annsOnMethod.prepend(maker.Annotation(JavacHandlerUtil.genTypeRef(typeNode, "org.checkerframework.dataflow.qual.SideEffectFree"), List.<JCTree.JCExpression>nil()));
        }
        JCTree.JCModifiers mods = maker.Modifiers(1L, annsOnMethod);
        JCTree.JCPrimitiveTypeTree returnType = maker.TypeIdent(Javac.CTC_INT);
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<JCTree.JCStatement>();
        Name primeName = typeNode.toName(PRIME_NAME);
        Name resultName = typeNode.toName(RESULT_NAME);
        long finalFlag = JavacHandlerUtil.addFinalIfNeeded(0L, typeNode.getContext());
        boolean isEmpty = members.isEmpty();
        if (cacheHashCode) {
            JCTree.JCFieldAccess hashCodeCacheFieldAccess = this.createHashCodeCacheFieldAccess(typeNode, maker);
            JCTree.JCBinary cacheNotZero = maker.Binary(Javac.CTC_NOT_EQUAL, hashCodeCacheFieldAccess, maker.Literal(Javac.CTC_INT, 0));
            hashCodeCacheFieldAccess = this.createHashCodeCacheFieldAccess(typeNode, maker);
            statements.append(maker.If(cacheNotZero, maker.Return(hashCodeCacheFieldAccess), null));
        }
        if (!isEmpty) {
            statements.append(maker.VarDef(maker.Modifiers(finalFlag), primeName, maker.TypeIdent(Javac.CTC_INT), maker.Literal(HandlerUtil.primeForHashcode())));
        }
        JCTree.JCExpression init = callSuper ? maker.Apply(List.<JCTree.JCExpression>nil(), maker.Select(maker.Ident(typeNode.toName("super")), typeNode.toName("hashCode")), List.<JCTree.JCExpression>nil()) : maker.Literal(1);
        statements.append(maker.VarDef(maker.Modifiers(isEmpty && !cacheHashCode ? finalFlag : 0L), resultName, maker.TypeIdent(Javac.CTC_INT), init));
        for (InclusionExclusionUtils.Included<JavacNode, EqualsAndHashCode.Include> member : members) {
            Name dollarFieldName;
            JCTree.JCExpression fieldAccessor;
            JavacNode memberNode = member.getNode();
            JCTree.JCExpression fType = JavacHandlerUtil.removeTypeUseAnnotations(JavacHandlerUtil.getFieldType(memberNode, fieldAccess));
            boolean isMethod = memberNode.getKind() == AST.Kind.METHOD;
            JCTree.JCExpression jCExpression = fieldAccessor = isMethod ? JavacHandlerUtil.createMethodAccessor(maker, memberNode) : JavacHandlerUtil.createFieldAccessor(maker, memberNode, fieldAccess);
            if (fType instanceof JCTree.JCPrimitiveTypeTree) {
                switch (((JCTree.JCPrimitiveTypeTree)fType).getPrimitiveTypeKind()) {
                    case BOOLEAN: {
                        statements.append(this.createResultCalculation(typeNode, maker.Parens(maker.Conditional(fieldAccessor, maker.Literal(HandlerUtil.primeForTrue()), maker.Literal(HandlerUtil.primeForFalse())))));
                        break;
                    }
                    case LONG: {
                        dollarFieldName = memberNode.toName(String.valueOf(isMethod ? "$$" : "$") + memberNode.getName());
                        statements.append(maker.VarDef(maker.Modifiers(finalFlag), dollarFieldName, maker.TypeIdent(Javac.CTC_LONG), fieldAccessor));
                        statements.append(this.createResultCalculation(typeNode, this.longToIntForHashCode(maker, maker.Ident(dollarFieldName), maker.Ident(dollarFieldName))));
                        break;
                    }
                    case FLOAT: {
                        statements.append(this.createResultCalculation(typeNode, maker.Apply(List.<JCTree.JCExpression>nil(), JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Float", "floatToIntBits"), List.of(fieldAccessor))));
                        break;
                    }
                    case DOUBLE: {
                        dollarFieldName = memberNode.toName(String.valueOf(isMethod ? "$$" : "$") + memberNode.getName());
                        JCTree.JCMethodInvocation init2 = maker.Apply(List.<JCTree.JCExpression>nil(), JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Double", "doubleToLongBits"), List.of(fieldAccessor));
                        statements.append(maker.VarDef(maker.Modifiers(finalFlag), dollarFieldName, maker.TypeIdent(Javac.CTC_LONG), init2));
                        statements.append(this.createResultCalculation(typeNode, this.longToIntForHashCode(maker, maker.Ident(dollarFieldName), maker.Ident(dollarFieldName))));
                        break;
                    }
                    default: {
                        statements.append(this.createResultCalculation(typeNode, fieldAccessor));
                        break;
                    }
                }
                continue;
            }
            if (fType instanceof JCTree.JCArrayTypeTree) {
                JCTree.JCArrayTypeTree array = (JCTree.JCArrayTypeTree)fType;
                boolean multiDim = JavacHandlerUtil.removeTypeUseAnnotations(array.elemtype) instanceof JCTree.JCArrayTypeTree;
                boolean primitiveArray = JavacHandlerUtil.removeTypeUseAnnotations(array.elemtype) instanceof JCTree.JCPrimitiveTypeTree;
                boolean useDeepHC = multiDim || !primitiveArray;
                JCTree.JCExpression hcMethod = JavacHandlerUtil.chainDots(typeNode, "java", "util", "Arrays", useDeepHC ? "deepHashCode" : "hashCode");
                statements.append(this.createResultCalculation(typeNode, maker.Apply(List.<JCTree.JCExpression>nil(), hcMethod, List.of(fieldAccessor))));
                continue;
            }
            dollarFieldName = memberNode.toName(String.valueOf(isMethod ? "$$" : "$") + memberNode.getName());
            statements.append(maker.VarDef(maker.Modifiers(finalFlag), dollarFieldName, JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Object"), fieldAccessor));
            JCTree.JCMethodInvocation hcCall = maker.Apply(List.<JCTree.JCExpression>nil(), maker.Select(maker.Ident(dollarFieldName), typeNode.toName("hashCode")), List.<JCTree.JCExpression>nil());
            JCTree.JCBinary thisEqualsNull = maker.Binary(Javac.CTC_EQUAL, maker.Ident(dollarFieldName), maker.Literal(Javac.CTC_BOT, null));
            statements.append(this.createResultCalculation(typeNode, maker.Parens(maker.Conditional(thisEqualsNull, maker.Literal(HandlerUtil.primeForNull()), hcCall))));
        }
        if (cacheHashCode) {
            statements.append(maker.If(maker.Binary(Javac.CTC_EQUAL, maker.Ident(resultName), maker.Literal(Javac.CTC_INT, 0)), maker.Exec(maker.Assign(maker.Ident(resultName), JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Integer", "MIN_VALUE"))), null));
            JCTree.JCFieldAccess cacheHashCodeFieldAccess = this.createHashCodeCacheFieldAccess(typeNode, maker);
            statements.append(maker.Exec(maker.Assign(cacheHashCodeFieldAccess, maker.Ident(resultName))));
        }
        statements.append(maker.Return(maker.Ident(resultName)));
        JCTree.JCBlock body = maker.Block(0L, statements.toList());
        return JavacHandlerUtil.recursiveSetGeneratedBy(maker.MethodDef(mods, typeNode.toName("hashCode"), returnType, List.<JCTree.JCTypeParameter>nil(), List.<JCTree.JCVariableDecl>nil(), List.<JCTree.JCExpression>nil(), body, null), source);
    }

    private JCTree.JCFieldAccess createHashCodeCacheFieldAccess(JavacNode typeNode, JavacTreeMaker maker) {
        JCTree.JCIdent receiver = maker.Ident(typeNode.toName("this"));
        JCTree.JCFieldAccess cacheHashCodeFieldAccess = maker.Select(receiver, typeNode.toName(HASH_CODE_CACHE_NAME));
        return cacheHashCodeFieldAccess;
    }

    public JCTree.JCExpressionStatement createResultCalculation(JavacNode typeNode, JCTree.JCExpression expr) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        Name resultName = typeNode.toName(RESULT_NAME);
        JCTree.JCBinary mult = maker.Binary(Javac.CTC_MUL, maker.Ident(resultName), maker.Ident(typeNode.toName(PRIME_NAME)));
        JCTree.JCBinary add = maker.Binary(Javac.CTC_PLUS, mult, expr);
        return maker.Exec(maker.Assign(maker.Ident(resultName), add));
    }

    public JCTree.JCExpression longToIntForHashCode(JavacTreeMaker maker, JCTree.JCExpression ref1, JCTree.JCExpression ref2) {
        JCTree.JCBinary shift = maker.Binary(Javac.CTC_UNSIGNED_SHIFT_RIGHT, ref1, maker.Literal(32));
        JCTree.JCBinary xorBits = maker.Binary(Javac.CTC_BITXOR, shift, ref2);
        return maker.TypeCast(maker.TypeIdent(Javac.CTC_INT), maker.Parens(xorBits));
    }

    public JCTree.JCExpression createTypeReference(JavacNode type, boolean addWildcards) {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<Integer> genericsCount = addWildcards ? new ArrayList<Integer>() : null;
        list.add(type.getName());
        if (addWildcards) {
            genericsCount.add(((JCTree.JCClassDecl)type.get()).typarams.size());
        }
        boolean staticContext = (((JCTree.JCClassDecl)type.get()).getModifiers().flags & 8L) != 0L;
        JavacNode tNode = (JavacNode)type.up();
        while (tNode != null && tNode.getKind() == AST.Kind.TYPE && !tNode.getName().isEmpty()) {
            list.add(tNode.getName());
            if (addWildcards) {
                genericsCount.add(staticContext ? 0 : ((JCTree.JCClassDecl)tNode.get()).typarams.size());
            }
            if (!staticContext) {
                staticContext = (((JCTree.JCClassDecl)tNode.get()).getModifiers().flags & 8L) != 0L;
            }
            tNode = (JavacNode)tNode.up();
        }
        Collections.reverse(list);
        if (addWildcards) {
            Collections.reverse(genericsCount);
        }
        JavacTreeMaker maker = type.getTreeMaker();
        JCTree.JCExpression chain = maker.Ident(type.toName((String)list.get(0)));
        if (addWildcards) {
            chain = this.wildcardify(maker, chain, (Integer)genericsCount.get(0));
        }
        int i = 1;
        while (i < list.size()) {
            chain = maker.Select(chain, type.toName((String)list.get(i)));
            if (addWildcards) {
                chain = this.wildcardify(maker, chain, (Integer)genericsCount.get(i));
            }
            ++i;
        }
        return chain;
    }

    private JCTree.JCExpression wildcardify(JavacTreeMaker maker, JCTree.JCExpression expr, int count) {
        if (count == 0) {
            return expr;
        }
        ListBuffer<JCTree.JCWildcard> wildcards = new ListBuffer<JCTree.JCWildcard>();
        int i = 0;
        while (i < count) {
            wildcards.append(maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null));
            ++i;
        }
        return maker.TypeApply(expr, wildcards.toList());
    }

    public JCTree.JCMethodDecl createEquals(JavacNode typeNode, java.util.List<InclusionExclusionUtils.Included<JavacNode, EqualsAndHashCode.Include>> members, boolean callSuper, HandlerUtil.FieldAccess fieldAccess, boolean needsCanEqual, JavacNode source, List<JCTree.JCAnnotation> onParam) {
        JCTree.JCExpression objectType;
        JavacTreeMaker maker = typeNode.getTreeMaker();
        Name oName = typeNode.toName("o");
        Name otherName = typeNode.toName("other");
        Name thisName = typeNode.toName("this");
        List<JCTree.JCAnnotation> annsOnParamOnMethod = List.nil();
        JCTree.JCAnnotation overrideAnnotation = maker.Annotation(JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Override"), List.<JCTree.JCExpression>nil());
        List<JCTree.JCAnnotation> annsOnMethod = List.of(overrideAnnotation);
        CheckerFrameworkVersion checkerFramework = JavacHandlerUtil.getCheckerFrameworkVersion(typeNode);
        if (checkerFramework.generateSideEffectFree()) {
            annsOnMethod = annsOnMethod.prepend(maker.Annotation(JavacHandlerUtil.genTypeRef(typeNode, "org.checkerframework.dataflow.qual.SideEffectFree"), List.<JCTree.JCExpression>nil()));
        }
        JCTree.JCModifiers mods = maker.Modifiers(1L, annsOnMethod);
        if (annsOnParamOnMethod.isEmpty()) {
            objectType = JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Object");
        } else {
            objectType = JavacHandlerUtil.chainDots(typeNode, "java", "lang", "Object");
            objectType = maker.AnnotatedType(annsOnParamOnMethod, objectType);
        }
        JCTree.JCPrimitiveTypeTree returnType = maker.TypeIdent(Javac.CTC_BOOLEAN);
        long finalFlag = JavacHandlerUtil.addFinalIfNeeded(0L, typeNode.getContext());
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<JCTree.JCStatement>();
        JCTree.JCVariableDecl param = maker.VarDef(maker.Modifiers(finalFlag | 0x200000000L, onParam), oName, objectType, null);
        JavacHandlerUtil.createRelevantNullableAnnotation(typeNode, param);
        List<JCTree.JCVariableDecl> params = List.of(param);
        statements.append(maker.If(maker.Binary(Javac.CTC_EQUAL, maker.Ident(oName), maker.Ident(thisName)), this.returnBool(maker, true), null));
        JCTree.JCUnary notInstanceOf = maker.Unary(Javac.CTC_NOT, maker.Parens(maker.TypeTest(maker.Ident(oName), this.createTypeReference(typeNode, false))));
        statements.append(maker.If(notInstanceOf, this.returnBool(maker, false), null));
        if (!members.isEmpty() || needsCanEqual) {
            JCTree.JCExpression selfType1 = this.createTypeReference(typeNode, true);
            JCTree.JCExpression selfType2 = this.createTypeReference(typeNode, true);
            statements.append(maker.VarDef(maker.Modifiers(finalFlag), otherName, selfType1, maker.TypeCast(selfType2, maker.Ident(oName))));
        }
        if (needsCanEqual) {
            List<JCTree.JCExpression> exprNil = List.nil();
            JCTree.JCIdent thisRef = maker.Ident(thisName);
            JCTree.JCTypeCast castThisRef = maker.TypeCast(JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Object"), thisRef);
            JCTree.JCMethodInvocation equalityCheck = maker.Apply(exprNil, maker.Select(maker.Ident(otherName), typeNode.toName("canEqual")), List.of(castThisRef));
            statements.append(maker.If(maker.Unary(Javac.CTC_NOT, equalityCheck), this.returnBool(maker, false), null));
        }
        if (callSuper) {
            JCTree.JCMethodInvocation callToSuper = maker.Apply(List.<JCTree.JCExpression>nil(), maker.Select(maker.Ident(typeNode.toName("super")), typeNode.toName("equals")), List.of(maker.Ident(oName)));
            JCTree.JCUnary superNotEqual = maker.Unary(Javac.CTC_NOT, callToSuper);
            statements.append(maker.If(superNotEqual, this.returnBool(maker, false), null));
        }
        for (InclusionExclusionUtils.Included<JavacNode, EqualsAndHashCode.Include> member : members) {
            JCTree.JCExpression otherFieldAccessor;
            JavacNode memberNode = member.getNode();
            boolean isMethod = memberNode.getKind() == AST.Kind.METHOD;
            JCTree.JCExpression fType = JavacHandlerUtil.removeTypeUseAnnotations(JavacHandlerUtil.getFieldType(memberNode, fieldAccess));
            JCTree.JCExpression thisFieldAccessor = isMethod ? JavacHandlerUtil.createMethodAccessor(maker, memberNode) : JavacHandlerUtil.createFieldAccessor(maker, memberNode, fieldAccess);
            JCTree.JCExpression jCExpression = otherFieldAccessor = isMethod ? JavacHandlerUtil.createMethodAccessor(maker, memberNode, maker.Ident(otherName)) : JavacHandlerUtil.createFieldAccessor(maker, memberNode, fieldAccess, maker.Ident(otherName));
            if (fType instanceof JCTree.JCPrimitiveTypeTree) {
                switch (((JCTree.JCPrimitiveTypeTree)fType).getPrimitiveTypeKind()) {
                    case FLOAT: {
                        statements.append(this.generateCompareFloatOrDouble(thisFieldAccessor, otherFieldAccessor, maker, typeNode, false));
                        break;
                    }
                    case DOUBLE: {
                        statements.append(this.generateCompareFloatOrDouble(thisFieldAccessor, otherFieldAccessor, maker, typeNode, true));
                        break;
                    }
                    default: {
                        statements.append(maker.If(maker.Binary(Javac.CTC_NOT_EQUAL, thisFieldAccessor, otherFieldAccessor), this.returnBool(maker, false), null));
                        break;
                    }
                }
                continue;
            }
            if (fType instanceof JCTree.JCArrayTypeTree) {
                JCTree.JCArrayTypeTree array = (JCTree.JCArrayTypeTree)fType;
                boolean multiDim = JavacHandlerUtil.removeTypeUseAnnotations(array.elemtype) instanceof JCTree.JCArrayTypeTree;
                boolean primitiveArray = JavacHandlerUtil.removeTypeUseAnnotations(array.elemtype) instanceof JCTree.JCPrimitiveTypeTree;
                boolean useDeepEquals = multiDim || !primitiveArray;
                JCTree.JCExpression eqMethod = JavacHandlerUtil.chainDots(typeNode, "java", "util", "Arrays", useDeepEquals ? "deepEquals" : "equals");
                List<JCTree.JCExpression> args = List.of(thisFieldAccessor, otherFieldAccessor);
                statements.append(maker.If(maker.Unary(Javac.CTC_NOT, maker.Apply(List.<JCTree.JCExpression>nil(), eqMethod, args)), this.returnBool(maker, false), null));
                continue;
            }
            Name thisDollarFieldName = memberNode.toName("this" + (isMethod ? "$$" : "$") + memberNode.getName());
            Name otherDollarFieldName = memberNode.toName("other" + (isMethod ? "$$" : "$") + memberNode.getName());
            statements.append(maker.VarDef(maker.Modifiers(finalFlag), thisDollarFieldName, JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Object"), thisFieldAccessor));
            statements.append(maker.VarDef(maker.Modifiers(finalFlag), otherDollarFieldName, JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Object"), otherFieldAccessor));
            JCTree.JCBinary thisEqualsNull = maker.Binary(Javac.CTC_EQUAL, maker.Ident(thisDollarFieldName), maker.Literal(Javac.CTC_BOT, null));
            JCTree.JCBinary otherNotEqualsNull = maker.Binary(Javac.CTC_NOT_EQUAL, maker.Ident(otherDollarFieldName), maker.Literal(Javac.CTC_BOT, null));
            JCTree.JCMethodInvocation thisEqualsThat = maker.Apply(List.<JCTree.JCExpression>nil(), maker.Select(maker.Ident(thisDollarFieldName), typeNode.toName("equals")), List.of(maker.Ident(otherDollarFieldName)));
            JCTree.JCConditional fieldsAreNotEqual = maker.Conditional(thisEqualsNull, otherNotEqualsNull, maker.Unary(Javac.CTC_NOT, thisEqualsThat));
            statements.append(maker.If(fieldsAreNotEqual, this.returnBool(maker, false), null));
        }
        statements.append(this.returnBool(maker, true));
        JCTree.JCBlock body = maker.Block(0L, statements.toList());
        return JavacHandlerUtil.recursiveSetGeneratedBy(maker.MethodDef(mods, typeNode.toName("equals"), returnType, List.<JCTree.JCTypeParameter>nil(), params, List.<JCTree.JCExpression>nil(), body, null), source);
    }

    public JCTree.JCMethodDecl createCanEqual(JavacNode typeNode, JavacNode source, List<JCTree.JCAnnotation> onParam) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        List<JCTree.JCAnnotation> annsOnMethod = List.nil();
        CheckerFrameworkVersion checkerFramework = JavacHandlerUtil.getCheckerFrameworkVersion(typeNode);
        if (checkerFramework.generatePure()) {
            annsOnMethod = annsOnMethod.prepend(maker.Annotation(JavacHandlerUtil.genTypeRef(typeNode, "org.checkerframework.dataflow.qual.Pure"), List.<JCTree.JCExpression>nil()));
        }
        JCTree.JCModifiers mods = maker.Modifiers(4L, annsOnMethod);
        JCTree.JCPrimitiveTypeTree returnType = maker.TypeIdent(Javac.CTC_BOOLEAN);
        Name canEqualName = typeNode.toName("canEqual");
        JCTree.JCExpression objectType = JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Object");
        Name otherName = typeNode.toName("other");
        long flags = JavacHandlerUtil.addFinalIfNeeded(0x200000000L, typeNode.getContext());
        JCTree.JCVariableDecl param = maker.VarDef(maker.Modifiers(flags, onParam), otherName, objectType, null);
        JavacHandlerUtil.createRelevantNullableAnnotation(typeNode, param);
        List<JCTree.JCVariableDecl> params = List.of(param);
        JCTree.JCBlock body = maker.Block(0L, List.of(maker.Return(maker.TypeTest(maker.Ident(otherName), this.createTypeReference(typeNode, false)))));
        return JavacHandlerUtil.recursiveSetGeneratedBy(maker.MethodDef(mods, canEqualName, returnType, List.<JCTree.JCTypeParameter>nil(), params, List.<JCTree.JCExpression>nil(), body, null), source);
    }

    public JCTree.JCStatement generateCompareFloatOrDouble(JCTree.JCExpression thisDotField, JCTree.JCExpression otherDotField, JavacTreeMaker maker, JavacNode node, boolean isDouble) {
        JCTree.JCExpression clazz = JavacHandlerUtil.genJavaLangTypeRef(node, isDouble ? "Double" : "Float");
        List<JCTree.JCExpression> args = List.of(thisDotField, otherDotField);
        JCTree.JCBinary compareCallEquals0 = maker.Binary(Javac.CTC_NOT_EQUAL, maker.Apply(List.<JCTree.JCExpression>nil(), maker.Select(clazz, node.toName("compare")), args), maker.Literal(0));
        return maker.If(compareCallEquals0, this.returnBool(maker, false), null);
    }

    public JCTree.JCStatement returnBool(JavacTreeMaker maker, boolean bool) {
        return maker.Return(maker.Literal(Javac.CTC_BOOLEAN, bool ? 1 : 0));
    }
}
