package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.Collection;
import lombok.ConfigurationKeys;
import lombok.ToString;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.configuration.CallSuperType;
import lombok.core.handlers.HandlerUtil;
import lombok.core.handlers.InclusionExclusionUtils;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleToString
extends JavacAnnotationHandler<ToString> {
    @Override
    public void handle(AnnotationValues<ToString> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.TO_STRING_FLAG_USAGE, "@ToString");
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, ToString.class);
        ToString ann = annotation.getInstance();
        boolean onlyExplicitlyIncluded = annotationNode.getAst().getBooleanAnnotationValue(annotation, "onlyExplicitlyIncluded", ConfigurationKeys.TO_STRING_ONLY_EXPLICITLY_INCLUDED);
        java.util.List<InclusionExclusionUtils.Included<JavacNode, ToString.Include>> members = InclusionExclusionUtils.handleToStringMarking(annotationNode.up(), onlyExplicitlyIncluded, annotation, annotationNode);
        if (members == null) {
            return;
        }
        Boolean callSuper = ann.callSuper();
        if (!annotation.isExplicit("callSuper")) {
            callSuper = null;
        }
        Boolean doNotUseGettersConfiguration = annotationNode.getAst().readConfiguration(ConfigurationKeys.TO_STRING_DO_NOT_USE_GETTERS);
        boolean doNotUseGetters = annotation.isExplicit("doNotUseGetters") || doNotUseGettersConfiguration == null ? ann.doNotUseGetters() : doNotUseGettersConfiguration.booleanValue();
        HandlerUtil.FieldAccess fieldAccess = doNotUseGetters ? HandlerUtil.FieldAccess.PREFER_FIELD : HandlerUtil.FieldAccess.GETTER;
        boolean includeFieldNames = annotationNode.getAst().getBooleanAnnotationValue(annotation, "includeFieldNames", ConfigurationKeys.TO_STRING_INCLUDE_FIELD_NAMES);
        this.generateToString((JavacNode)annotationNode.up(), annotationNode, members, includeFieldNames, callSuper, true, fieldAccess);
    }

    public void generateToStringForType(JavacNode typeNode, JavacNode errorNode) {
        if (JavacHandlerUtil.hasAnnotation(ToString.class, typeNode)) {
            return;
        }
        AnnotationValues<ToString> anno = AnnotationValues.of(ToString.class);
        boolean includeFieldNames = typeNode.getAst().getBooleanAnnotationValue(anno, "includeFieldNames", ConfigurationKeys.TO_STRING_INCLUDE_FIELD_NAMES);
        boolean onlyExplicitlyIncluded = typeNode.getAst().getBooleanAnnotationValue(anno, "onlyExplicitlyIncluded", ConfigurationKeys.TO_STRING_ONLY_EXPLICITLY_INCLUDED);
        Boolean doNotUseGettersConfiguration = typeNode.getAst().readConfiguration(ConfigurationKeys.TO_STRING_DO_NOT_USE_GETTERS);
        HandlerUtil.FieldAccess access = doNotUseGettersConfiguration == null || doNotUseGettersConfiguration == false ? HandlerUtil.FieldAccess.GETTER : HandlerUtil.FieldAccess.PREFER_FIELD;
        java.util.List<InclusionExclusionUtils.Included<JavacNode, ToString.Include>> members = InclusionExclusionUtils.handleToStringMarking(typeNode, onlyExplicitlyIncluded, null, null);
        this.generateToString(typeNode, errorNode, members, includeFieldNames, null, false, access);
    }

    public void generateToString(JavacNode typeNode, JavacNode source, java.util.List<InclusionExclusionUtils.Included<JavacNode, ToString.Include>> members, boolean includeFieldNames, Boolean callSuper, boolean whineIfExists, HandlerUtil.FieldAccess fieldAccess) {
        if (!JavacHandlerUtil.isClassOrEnum(typeNode)) {
            source.addError("@ToString is only supported on a class or enum.");
            return;
        }
        switch (JavacHandlerUtil.methodExists("toString", typeNode, 0)) {
            case NOT_EXISTS: {
                if (callSuper == null) {
                    if (JavacHandlerUtil.isDirectDescendantOfObject(typeNode)) {
                        callSuper = false;
                    } else {
                        CallSuperType cst = typeNode.getAst().readConfiguration(ConfigurationKeys.TO_STRING_CALL_SUPER);
                        if (cst == null) {
                            cst = CallSuperType.SKIP;
                        }
                        switch (cst) {
                            default: {
                                callSuper = false;
                                break;
                            }
                            case WARN: {
                                source.addWarning("Generating toString implementation but without a call to superclass, even though this class does not extend java.lang.Object. If this is intentional, add '@ToString(callSuper=false)' to your type.");
                                callSuper = false;
                                break;
                            }
                            case CALL: {
                                callSuper = true;
                            }
                        }
                    }
                }
                JCTree.JCMethodDecl method = HandleToString.createToString(typeNode, members, includeFieldNames, callSuper, fieldAccess, source);
                JavacHandlerUtil.injectMethod(typeNode, method);
                break;
            }
            case EXISTS_BY_LOMBOK: {
                break;
            }
            default: {
                if (!whineIfExists) break;
                source.addWarning("Not generating toString(): A method with that name already exists");
            }
        }
    }

    static JCTree.JCMethodDecl createToString(JavacNode typeNode, Collection<InclusionExclusionUtils.Included<JavacNode, ToString.Include>> members, boolean includeNames, boolean callSuper, HandlerUtil.FieldAccess fieldAccess, JavacNode source) {
        JCTree.JCExpression current;
        String prefix;
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCTree.JCAnnotation overrideAnnotation = maker.Annotation(JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Override"), List.<JCTree.JCExpression>nil());
        List<JCTree.JCAnnotation> annsOnMethod = List.of(overrideAnnotation);
        if (JavacHandlerUtil.getCheckerFrameworkVersion(typeNode).generateSideEffectFree()) {
            annsOnMethod = annsOnMethod.prepend(maker.Annotation(JavacHandlerUtil.genTypeRef(typeNode, "org.checkerframework.dataflow.qual.SideEffectFree"), List.<JCTree.JCExpression>nil()));
        }
        JCTree.JCModifiers mods = maker.Modifiers(1L, annsOnMethod);
        JCTree.JCExpression returnType = JavacHandlerUtil.genJavaLangTypeRef(typeNode, "String");
        boolean first = true;
        String typeName = HandleToString.getTypeName(typeNode);
        boolean isEnum = typeNode.isEnumType();
        String infix = ", ";
        String suffix = ")";
        if (callSuper) {
            prefix = "(super=";
        } else if (members.isEmpty()) {
            prefix = isEnum ? "" : "()";
        } else if (includeNames) {
            String name;
            InclusionExclusionUtils.Included<JavacNode, ToString.Include> firstMember = members.iterator().next();
            String string = name = firstMember.getInc() == null ? "" : firstMember.getInc().name();
            if (name.isEmpty()) {
                name = firstMember.getNode().getName();
            }
            prefix = "(" + name + "=";
        } else {
            prefix = "(";
        }
        if (!isEnum) {
            current = maker.Literal(String.valueOf(typeName) + prefix);
        } else {
            current = maker.Binary(Javac.CTC_PLUS, maker.Literal(String.valueOf(typeName) + "."), maker.Apply(List.<JCTree.JCExpression>nil(), maker.Select(maker.Ident(typeNode.toName("this")), typeNode.toName("name")), List.<JCTree.JCExpression>nil()));
            if (!prefix.isEmpty()) {
                current = maker.Binary(Javac.CTC_PLUS, current, maker.Literal(prefix));
            }
        }
        if (callSuper) {
            JCTree.JCMethodInvocation callToSuper = maker.Apply(List.<JCTree.JCExpression>nil(), maker.Select(maker.Ident(typeNode.toName("super")), typeNode.toName("toString")), List.<JCTree.JCExpression>nil());
            current = maker.Binary(Javac.CTC_PLUS, current, callToSuper);
            first = false;
        }
        for (InclusionExclusionUtils.Included<JavacNode, ToString.Include> member : members) {
            JCTree.JCExpression expr;
            boolean fieldIsObjectArray;
            JavacNode memberNode = member.getNode();
            JCTree.JCExpression memberAccessor = memberNode.getKind() == AST.Kind.METHOD ? JavacHandlerUtil.createMethodAccessor(maker, memberNode) : JavacHandlerUtil.createFieldAccessor(maker, memberNode, fieldAccess);
            JCTree.JCExpression memberType = JavacHandlerUtil.removeTypeUseAnnotations(JavacHandlerUtil.getFieldType(memberNode, fieldAccess));
            boolean cfr_ignored_0 = memberType instanceof JCTree.JCPrimitiveTypeTree;
            boolean fieldIsPrimitiveArray = memberType instanceof JCTree.JCArrayTypeTree && ((JCTree.JCArrayTypeTree)memberType).elemtype instanceof JCTree.JCPrimitiveTypeTree;
            boolean bl = fieldIsObjectArray = !fieldIsPrimitiveArray && memberType instanceof JCTree.JCArrayTypeTree;
            if (fieldIsPrimitiveArray || fieldIsObjectArray) {
                JCTree.JCExpression tsMethod = JavacHandlerUtil.chainDots(typeNode, "java", "util", "Arrays", fieldIsObjectArray ? "deepToString" : "toString");
                expr = maker.Apply(List.<JCTree.JCExpression>nil(), tsMethod, List.of(memberAccessor));
            } else {
                expr = memberAccessor;
            }
            if (first) {
                current = maker.Binary(Javac.CTC_PLUS, current, expr);
                first = false;
                continue;
            }
            if (includeNames) {
                String n;
                String string = n = member.getInc() == null ? "" : member.getInc().name();
                if (n.isEmpty()) {
                    n = memberNode.getName();
                }
                current = maker.Binary(Javac.CTC_PLUS, current, maker.Literal(String.valueOf(infix) + n + "="));
            } else {
                current = maker.Binary(Javac.CTC_PLUS, current, maker.Literal(infix));
            }
            current = maker.Binary(Javac.CTC_PLUS, current, expr);
        }
        if (!first) {
            current = maker.Binary(Javac.CTC_PLUS, current, maker.Literal(suffix));
        }
        JCTree.JCReturn returnStatement = maker.Return(current);
        JCTree.JCBlock body = maker.Block(0L, List.of(returnStatement));
        JCTree.JCMethodDecl methodDef = maker.MethodDef(mods, typeNode.toName("toString"), returnType, List.<JCTree.JCTypeParameter>nil(), List.<JCTree.JCVariableDecl>nil(), List.<JCTree.JCExpression>nil(), body, null);
        JavacHandlerUtil.createRelevantNonNullAnnotation(typeNode, methodDef);
        return JavacHandlerUtil.recursiveSetGeneratedBy(methodDef, source);
    }

    public static String getTypeName(JavacNode typeNode) {
        String typeName = typeNode.getName();
        JavacNode upType = (JavacNode)typeNode.up();
        while (upType.getKind() == AST.Kind.TYPE && !upType.getName().isEmpty()) {
            typeName = String.valueOf(upType.getName()) + "." + typeName;
            upType = (JavacNode)upType.up();
        }
        return typeName;
    }
}
