package lombok.eclipse.handlers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.ToString;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.configuration.CallSuperType;
import lombok.core.handlers.HandlerUtil;
import lombok.core.handlers.InclusionExclusionUtils;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class HandleToString
extends EclipseAnnotationHandler<ToString> {
    private static final Set<String> BUILT_IN_TYPES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("byte", "short", "int", "long", "char", "boolean", "double", "float")));

    @Override
    public void handle(AnnotationValues<ToString> annotation, Annotation ast, EclipseNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.TO_STRING_FLAG_USAGE, "@ToString");
        ToString ann = annotation.getInstance();
        boolean onlyExplicitlyIncluded = annotationNode.getAst().getBooleanAnnotationValue(annotation, "onlyExplicitlyIncluded", ConfigurationKeys.TO_STRING_ONLY_EXPLICITLY_INCLUDED);
        List<InclusionExclusionUtils.Included<EclipseNode, ToString.Include>> members = InclusionExclusionUtils.handleToStringMarking(annotationNode.up(), onlyExplicitlyIncluded, annotation, annotationNode);
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
        Boolean fieldNamesConfiguration = annotationNode.getAst().readConfiguration(ConfigurationKeys.TO_STRING_INCLUDE_FIELD_NAMES);
        boolean includeFieldNames = annotation.isExplicit("includeFieldNames") || fieldNamesConfiguration == null ? ann.includeFieldNames() : fieldNamesConfiguration.booleanValue();
        this.generateToString((EclipseNode)annotationNode.up(), annotationNode, members, includeFieldNames, callSuper, true, fieldAccess);
    }

    public void generateToStringForType(EclipseNode typeNode, EclipseNode errorNode) {
        if (EclipseHandlerUtil.hasAnnotation(ToString.class, typeNode)) {
            return;
        }
        AnnotationValues<ToString> anno = AnnotationValues.of(ToString.class);
        boolean includeFieldNames = typeNode.getAst().getBooleanAnnotationValue(anno, "includeFieldNames", ConfigurationKeys.TO_STRING_INCLUDE_FIELD_NAMES);
        boolean onlyExplicitlyIncluded = typeNode.getAst().getBooleanAnnotationValue(anno, "onlyExplicitlyIncluded", ConfigurationKeys.TO_STRING_ONLY_EXPLICITLY_INCLUDED);
        Boolean doNotUseGettersConfiguration = typeNode.getAst().readConfiguration(ConfigurationKeys.TO_STRING_DO_NOT_USE_GETTERS);
        HandlerUtil.FieldAccess access = doNotUseGettersConfiguration == null || doNotUseGettersConfiguration == false ? HandlerUtil.FieldAccess.GETTER : HandlerUtil.FieldAccess.PREFER_FIELD;
        List<InclusionExclusionUtils.Included<EclipseNode, ToString.Include>> members = InclusionExclusionUtils.handleToStringMarking(typeNode, onlyExplicitlyIncluded, null, null);
        this.generateToString(typeNode, errorNode, members, includeFieldNames, null, false, access);
    }

    public void generateToString(EclipseNode typeNode, EclipseNode errorNode, List<InclusionExclusionUtils.Included<EclipseNode, ToString.Include>> members, boolean includeFieldNames, Boolean callSuper, boolean whineIfExists, HandlerUtil.FieldAccess fieldAccess) {
        if (!EclipseHandlerUtil.isClassOrEnum(typeNode)) {
            errorNode.addError("@ToString is only supported on a class or enum.");
            return;
        }
        switch (EclipseHandlerUtil.methodExists("toString", typeNode, 0)) {
            case NOT_EXISTS: {
                if (callSuper == null) {
                    if (EclipseHandlerUtil.isDirectDescendantOfObject(typeNode)) {
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
                                errorNode.addWarning("Generating toString implementation but without a call to superclass, even though this class does not extend java.lang.Object. If this intentional, add '@ToString(callSuper=false)' to your type.");
                                callSuper = false;
                                break;
                            }
                            case CALL: {
                                callSuper = true;
                            }
                        }
                    }
                }
                MethodDeclaration toString = HandleToString.createToString(typeNode, members, includeFieldNames, callSuper, (ASTNode)errorNode.get(), fieldAccess);
                EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)toString);
                break;
            }
            case EXISTS_BY_LOMBOK: {
                break;
            }
            default: {
                if (!whineIfExists) break;
                errorNode.addWarning("Not generating toString(): A method with that name already exists");
            }
        }
    }

    public static MethodDeclaration createToString(EclipseNode type, Collection<InclusionExclusionUtils.Included<EclipseNode, ToString.Include>> members, boolean includeNames, boolean callSuper, ASTNode source, HandlerUtil.FieldAccess fieldAccess) {
        StringLiteral current;
        String prefix;
        String typeName = HandleToString.getTypeName(type);
        boolean isEnum = type.isEnumType();
        char[] suffix = ")".toCharArray();
        String infixS = ", ";
        char[] infix = infixS.toCharArray();
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        int PLUS = OperatorIds.PLUS;
        if (callSuper) {
            prefix = "(super=";
        } else if (members.isEmpty()) {
            prefix = isEnum ? "" : "()";
        } else if (includeNames) {
            String name;
            InclusionExclusionUtils.Included<EclipseNode, ToString.Include> firstMember = members.iterator().next();
            String string = name = firstMember.getInc() == null ? "" : firstMember.getInc().name();
            if (name.isEmpty()) {
                name = firstMember.getNode().getName();
            }
            prefix = "(" + name + "=";
        } else {
            prefix = "(";
        }
        boolean first = true;
        if (!isEnum) {
            current = new StringLiteral((String.valueOf(typeName) + prefix).toCharArray(), pS, pE, 0);
            EclipseHandlerUtil.setGeneratedBy(current, source);
        } else {
            current = new StringLiteral((String.valueOf(typeName) + ".").toCharArray(), pS, pE, 0);
            EclipseHandlerUtil.setGeneratedBy(current, source);
            MessageSend thisName = new MessageSend();
            thisName.sourceStart = pS;
            thisName.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy(thisName, source);
            thisName.receiver = new ThisReference(pS, pE);
            EclipseHandlerUtil.setGeneratedBy(thisName.receiver, source);
            thisName.selector = "name".toCharArray();
            current = new BinaryExpression((Expression)current, (Expression)thisName, PLUS);
            EclipseHandlerUtil.setGeneratedBy(current, source);
            if (!prefix.isEmpty()) {
                StringLiteral px = new StringLiteral(prefix.toCharArray(), pS, pE, 0);
                EclipseHandlerUtil.setGeneratedBy(px, source);
                current = new BinaryExpression((Expression)current, (Expression)px, PLUS);
                current.sourceStart = pS;
                current.sourceEnd = pE;
                EclipseHandlerUtil.setGeneratedBy(current, source);
            }
        }
        if (callSuper) {
            MessageSend callToSuper = new MessageSend();
            callToSuper.sourceStart = pS;
            callToSuper.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy(callToSuper, source);
            callToSuper.receiver = new SuperReference(pS, pE);
            EclipseHandlerUtil.setGeneratedBy(callToSuper.receiver, source);
            callToSuper.selector = "toString".toCharArray();
            current = new BinaryExpression((Expression)current, (Expression)callToSuper, PLUS);
            EclipseHandlerUtil.setGeneratedBy(current, source);
            first = false;
        }
        for (InclusionExclusionUtils.Included<EclipseNode, ToString.Include> member : members) {
            StringLiteral fieldNameLiteral;
            Expression ex;
            boolean fieldIsObjectArray;
            EclipseNode memberNode = member.getNode();
            TypeReference fieldType = EclipseHandlerUtil.getFieldType(memberNode, fieldAccess);
            Expression memberAccessor = memberNode.getKind() == AST.Kind.METHOD ? EclipseHandlerUtil.createMethodAccessor(memberNode, source) : EclipseHandlerUtil.createFieldAccessor(memberNode, fieldAccess, source);
            boolean fieldBaseTypeIsPrimitive = BUILT_IN_TYPES.contains(new String(fieldType.getLastToken()));
            if (fieldType.dimensions() == 0) {
                // empty if block
            }
            boolean fieldIsPrimitiveArray = fieldType.dimensions() == 1 && fieldBaseTypeIsPrimitive;
            boolean bl = fieldIsObjectArray = fieldType.dimensions() > 0 && !fieldIsPrimitiveArray;
            if (fieldIsPrimitiveArray || fieldIsObjectArray) {
                MessageSend arrayToString = new MessageSend();
                arrayToString.sourceStart = pS;
                arrayToString.sourceEnd = pE;
                arrayToString.receiver = HandleToString.generateQualifiedNameRef(source, TypeConstants.JAVA, TypeConstants.UTIL, "Arrays".toCharArray());
                arrayToString.arguments = new Expression[]{memberAccessor};
                EclipseHandlerUtil.setGeneratedBy(arrayToString.arguments[0], source);
                arrayToString.selector = (fieldIsObjectArray ? "deepToString" : "toString").toCharArray();
                ex = arrayToString;
            } else {
                ex = memberAccessor;
            }
            EclipseHandlerUtil.setGeneratedBy(ex, source);
            if (first) {
                current = new BinaryExpression((Expression)current, ex, PLUS);
                current.sourceStart = pS;
                current.sourceEnd = pE;
                EclipseHandlerUtil.setGeneratedBy(current, source);
                first = false;
                continue;
            }
            if (includeNames) {
                String n;
                String string = n = member.getInc() == null ? "" : member.getInc().name();
                if (n.isEmpty()) {
                    n = memberNode.getName();
                }
                char[] namePlusEqualsSign = (String.valueOf(infixS) + n + "=").toCharArray();
                fieldNameLiteral = new StringLiteral(namePlusEqualsSign, pS, pE, 0);
            } else {
                fieldNameLiteral = new StringLiteral(infix, pS, pE, 0);
            }
            EclipseHandlerUtil.setGeneratedBy(fieldNameLiteral, source);
            current = new BinaryExpression((Expression)current, (Expression)fieldNameLiteral, PLUS);
            EclipseHandlerUtil.setGeneratedBy(current, source);
            current = new BinaryExpression((Expression)current, ex, PLUS);
            EclipseHandlerUtil.setGeneratedBy(current, source);
        }
        if (!first) {
            StringLiteral suffixLiteral = new StringLiteral(suffix, pS, pE, 0);
            EclipseHandlerUtil.setGeneratedBy(suffixLiteral, source);
            current = new BinaryExpression((Expression)current, (Expression)suffixLiteral, PLUS);
            EclipseHandlerUtil.setGeneratedBy(current, source);
        }
        ReturnStatement returnStatement = new ReturnStatement((Expression)current, pS, pE);
        EclipseHandlerUtil.setGeneratedBy(returnStatement, source);
        MethodDeclaration method = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)type.top()).get()).compilationResult);
        EclipseHandlerUtil.setGeneratedBy(method, source);
        method.modifiers = EclipseHandlerUtil.toEclipseModifier(AccessLevel.PUBLIC);
        method.returnType = new QualifiedTypeReference(TypeConstants.JAVA_LANG_STRING, new long[]{p, p, p});
        EclipseHandlerUtil.setGeneratedBy(method.returnType, source);
        MarkerAnnotation overrideAnnotation = EclipseHandlerUtil.makeMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, source);
        method.annotations = EclipseHandlerUtil.getCheckerFrameworkVersion(type).generateSideEffectFree() ? new Annotation[]{overrideAnnotation, EclipseHandlerUtil.generateNamedAnnotation(source, "org.checkerframework.dataflow.qual.SideEffectFree")} : new Annotation[]{overrideAnnotation};
        method.arguments = null;
        method.selector = "toString".toCharArray();
        method.thrownExceptions = null;
        method.typeParameters = null;
        method.bits |= 0x800000;
        method.declarationSourceStart = method.sourceStart = source.sourceStart;
        method.bodyStart = method.sourceStart;
        method.declarationSourceEnd = method.sourceEnd = source.sourceEnd;
        method.bodyEnd = method.sourceEnd;
        method.statements = new Statement[]{returnStatement};
        EclipseHandlerUtil.createRelevantNonNullAnnotation(type, method);
        return method;
    }

    public static String getTypeName(EclipseNode type) {
        String typeName = HandleToString.getSingleTypeName(type);
        EclipseNode upType = (EclipseNode)type.up();
        while (upType.getKind() == AST.Kind.TYPE) {
            String upTypeName = HandleToString.getSingleTypeName(upType);
            if (upTypeName.isEmpty()) break;
            typeName = String.valueOf(upTypeName) + "." + typeName;
            upType = (EclipseNode)upType.up();
        }
        return typeName;
    }

    public static String getSingleTypeName(EclipseNode type) {
        TypeDeclaration typeDeclaration = (TypeDeclaration)type.get();
        char[] rawTypeName = typeDeclaration.name;
        return rawTypeName == null ? "" : new String(rawTypeName);
    }

    public static NameReference generateQualifiedNameRef(ASTNode source, char[] ... varNames) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        Object ref = varNames.length > 1 ? new QualifiedNameReference(varNames, new long[varNames.length], pS, pE) : new SingleNameReference(varNames[0], p);
        EclipseHandlerUtil.setGeneratedBy(ref, source);
        return ref;
    }
}
