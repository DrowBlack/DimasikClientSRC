package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.SetGeneratedByVisitor;
import lombok.experimental.Accessors;
import lombok.experimental.WithBy;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;

public class HandleWithBy
extends EclipseAnnotationHandler<WithBy> {
    private static final char[][] NAME_JUF_FUNCTION = Eclipse.fromQualifiedName("java.util.function.Function");
    private static final char[][] NAME_JUF_OP = Eclipse.fromQualifiedName("java.util.function.UnaryOperator");
    private static final char[][] NAME_JUF_DOUBLEOP = Eclipse.fromQualifiedName("java.util.function.DoubleUnaryOperator");
    private static final char[][] NAME_JUF_INTOP = Eclipse.fromQualifiedName("java.util.function.IntUnaryOperator");
    private static final char[][] NAME_JUF_LONGOP = Eclipse.fromQualifiedName("java.util.function.LongUnaryOperator");
    private static final char[] NAME_CHAR = new char[]{'c', 'h', 'a', 'r'};
    private static final char[] NAME_SHORT = new char[]{'s', 'h', 'o', 'r', 't'};
    private static final char[] NAME_BYTE = new char[]{'b', 'y', 't', 'e'};
    private static final char[] NAME_INT = new char[]{'i', 'n', 't'};
    private static final char[] NAME_LONG = new char[]{'l', 'o', 'n', 'g'};
    private static final char[] NAME_DOUBLE = new char[]{'d', 'o', 'u', 'b', 'l', 'e'};
    private static final char[] NAME_FLOAT = new char[]{'f', 'l', 'o', 'a', 't'};
    private static final char[] NAME_BOOLEAN = new char[]{'b', 'o', 'o', 'l', 'e', 'a', 'n'};
    private static final char[][] NAME_JAVA_LANG_BOOLEAN = Eclipse.fromQualifiedName("java.lang.Boolean");
    private static final char[] NAME_APPLY = new char[]{'a', 'p', 'p', 'l', 'y'};
    private static final char[] NAME_APPLY_AS_INT = new char[]{'a', 'p', 'p', 'l', 'y', 'A', 's', 'I', 'n', 't'};
    private static final char[] NAME_APPLY_AS_LONG = new char[]{'a', 'p', 'p', 'l', 'y', 'A', 's', 'L', 'o', 'n', 'g'};
    private static final char[] NAME_APPLY_AS_DOUBLE = new char[]{'a', 'p', 'p', 'l', 'y', 'A', 's', 'D', 'o', 'u', 'b', 'l', 'e'};
    private static final char[] NAME_TRANSFORMER = new char[]{'t', 'r', 'a', 'n', 's', 'f', 'o', 'r', 'm', 'e', 'r'};

    public boolean generateWithByForType(EclipseNode typeNode, EclipseNode pos, AccessLevel level, boolean checkForTypeLevelWithBy) {
        boolean notAClass;
        if (checkForTypeLevelWithBy && EclipseHandlerUtil.hasAnnotation(WithBy.class, typeNode)) {
            return true;
        }
        TypeDeclaration typeDecl = null;
        if (typeNode.get() instanceof TypeDeclaration) {
            typeDecl = (TypeDeclaration)typeNode.get();
        }
        int modifiers = typeDecl == null ? 0 : typeDecl.modifiers;
        boolean bl = notAClass = (modifiers & 0x6200) != 0;
        if (typeDecl == null || notAClass) {
            pos.addError("@WithBy is only supported on a class or a field.");
            return false;
        }
        for (EclipseNode field : typeNode.down()) {
            FieldDeclaration fieldDecl;
            if (field.getKind() != AST.Kind.FIELD || !EclipseHandlerUtil.filterField(fieldDecl = (FieldDeclaration)field.get()) || (fieldDecl.modifiers & 0x10) != 0 && fieldDecl.initialization != null) continue;
            this.generateWithByForField(field, pos, level);
        }
        return true;
    }

    public void generateWithByForField(EclipseNode fieldNode, EclipseNode sourceNode, AccessLevel level) {
        for (EclipseNode child : fieldNode.down()) {
            if (child.getKind() != AST.Kind.ANNOTATION || !EclipseHandlerUtil.annotationTypeMatches(WithBy.class, child)) continue;
            return;
        }
        List<Annotation> empty = Collections.emptyList();
        this.createWithByForField(level, fieldNode, sourceNode, false, empty);
    }

    @Override
    public void handle(AnnotationValues<WithBy> annotation, Annotation ast, EclipseNode annotationNode) {
        HandlerUtil.handleExperimentalFlagUsage(annotationNode, ConfigurationKeys.WITHBY_FLAG_USAGE, "@WithBy");
        EclipseNode node = (EclipseNode)annotationNode.up();
        AccessLevel level = annotation.getInstance().value();
        if (level == AccessLevel.NONE || node == null) {
            return;
        }
        List<Annotation> onMethod = EclipseHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onMethod", "@WithBy(onMethod", annotationNode);
        switch (node.getKind()) {
            case FIELD: {
                this.createWithByForFields(level, annotationNode.upFromAnnotationToFields(), annotationNode, true, onMethod);
                break;
            }
            case TYPE: {
                if (!onMethod.isEmpty()) {
                    annotationNode.addError("'onMethod' is not supported for @WithBy on a type.");
                }
                this.generateWithByForType(node, annotationNode, level, false);
            }
        }
    }

    public void createWithByForFields(AccessLevel level, Collection<EclipseNode> fieldNodes, EclipseNode sourceNode, boolean whineIfExists, List<Annotation> onMethod) {
        for (EclipseNode fieldNode : fieldNodes) {
            this.createWithByForField(level, fieldNode, sourceNode, whineIfExists, onMethod);
        }
    }

    public void createWithByForField(AccessLevel level, EclipseNode fieldNode, EclipseNode sourceNode, boolean whineIfExists, List<Annotation> onMethod) {
        AnnotationValues<Accessors> accessors;
        ASTNode source = (ASTNode)sourceNode.get();
        if (fieldNode.getKind() != AST.Kind.FIELD) {
            sourceNode.addError("@WithBy is only supported on a class or a field.");
            return;
        }
        EclipseNode typeNode = (EclipseNode)fieldNode.up();
        boolean makeAbstract = typeNode != null && typeNode.getKind() == AST.Kind.TYPE && (((TypeDeclaration)typeNode.get()).modifiers & 0x400) != 0;
        FieldDeclaration field = (FieldDeclaration)fieldNode.get();
        TypeReference fieldType = EclipseHandlerUtil.copyType(field.type, source);
        boolean isBoolean = EclipseHandlerUtil.isBoolean(fieldType);
        String withName = EclipseHandlerUtil.toWithByName(fieldNode, isBoolean, accessors = EclipseHandlerUtil.getAccessorsForField(fieldNode));
        if (withName == null) {
            fieldNode.addWarning("Not generating a withXBy method for this field: It does not fit your @Accessors prefix list.");
            return;
        }
        if ((field.modifiers & 8) != 0) {
            fieldNode.addWarning("Not generating " + withName + " for this field: With methods cannot be generated for static fields.");
            return;
        }
        if ((field.modifiers & 0x10) != 0 && field.initialization != null) {
            fieldNode.addWarning("Not generating " + withName + " for this field: With methods cannot be generated for final, initialized fields.");
            return;
        }
        if (field.name != null && field.name.length > 0 && field.name[0] == '$') {
            fieldNode.addWarning("Not generating " + withName + " for this field: With methods cannot be generated for fields starting with $.");
            return;
        }
        for (String altName : EclipseHandlerUtil.toAllWithByNames(fieldNode, isBoolean, accessors)) {
            switch (EclipseHandlerUtil.methodExists(altName, fieldNode, false, 1)) {
                case EXISTS_BY_LOMBOK: {
                    return;
                }
                case EXISTS_BY_USER: {
                    if (whineIfExists) {
                        String altNameExpl = "";
                        if (!altName.equals(withName)) {
                            altNameExpl = String.format(" (%s)", altName);
                        }
                        fieldNode.addWarning(String.format("Not generating %s(): A method with that name already exists%s", withName, altNameExpl));
                    }
                    return;
                }
            }
        }
        int modifier = EclipseHandlerUtil.toEclipseModifier(level);
        MethodDeclaration method = this.createWithBy((TypeDeclaration)((EclipseNode)fieldNode.up()).get(), fieldNode, withName, modifier, sourceNode, onMethod, makeAbstract);
        EclipseHandlerUtil.injectMethod((EclipseNode)fieldNode.up(), (AbstractMethodDeclaration)method);
    }

    public MethodDeclaration createWithBy(TypeDeclaration parent, EclipseNode fieldNode, String name, int modifier, EclipseNode sourceNode, List<Annotation> onMethod, boolean makeAbstract) {
        Object ps;
        ASTNode source = (ASTNode)sourceNode.get();
        if (name == null) {
            return null;
        }
        FieldDeclaration field = (FieldDeclaration)fieldNode.get();
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        MethodDeclaration method = new MethodDeclaration(parent.compilationResult);
        AnnotationValues<Accessors> accessors = EclipseHandlerUtil.getAccessorsForField(fieldNode);
        if (makeAbstract) {
            modifier |= 0x1000400;
        }
        if (EclipseHandlerUtil.shouldMakeFinal(fieldNode, accessors)) {
            modifier |= 0x10;
        }
        method.modifiers = modifier;
        method.returnType = EclipseHandlerUtil.cloneSelfType(fieldNode, source);
        if (method.returnType == null) {
            return null;
        }
        Annotation[] deprecated = null;
        Annotation[] checkerFramework = null;
        if (EclipseHandlerUtil.isFieldDeprecated(fieldNode)) {
            deprecated = new Annotation[]{EclipseHandlerUtil.generateDeprecatedAnnotation(source)};
        }
        if (EclipseHandlerUtil.getCheckerFrameworkVersion(fieldNode).generateSideEffectFree()) {
            checkerFramework = new Annotation[]{EclipseHandlerUtil.generateNamedAnnotation(source, "org.checkerframework.dataflow.qual.SideEffectFree")};
        }
        char[][] functionalInterfaceName = null;
        int requiredCast = -1;
        TypeReference parameterizer = null;
        boolean superExtendsStyle = true;
        char[] applyMethodName = NAME_APPLY;
        if (field.type instanceof SingleTypeReference) {
            char[] token = ((SingleTypeReference)field.type).token;
            if (Arrays.equals(token, NAME_CHAR)) {
                requiredCast = 2;
                functionalInterfaceName = NAME_JUF_INTOP;
            } else if (Arrays.equals(token, NAME_SHORT)) {
                requiredCast = 4;
                functionalInterfaceName = NAME_JUF_INTOP;
            } else if (Arrays.equals(token, NAME_BYTE)) {
                requiredCast = 3;
                functionalInterfaceName = NAME_JUF_INTOP;
            } else if (Arrays.equals(token, NAME_INT)) {
                functionalInterfaceName = NAME_JUF_INTOP;
            } else if (Arrays.equals(token, NAME_LONG)) {
                functionalInterfaceName = NAME_JUF_LONGOP;
            } else if (Arrays.equals(token, NAME_FLOAT)) {
                requiredCast = 9;
                functionalInterfaceName = NAME_JUF_DOUBLEOP;
            } else if (Arrays.equals(token, NAME_DOUBLE)) {
                functionalInterfaceName = NAME_JUF_DOUBLEOP;
            } else if (Arrays.equals(token, NAME_BOOLEAN)) {
                functionalInterfaceName = NAME_JUF_OP;
                parameterizer = new QualifiedTypeReference(NAME_JAVA_LANG_BOOLEAN, new long[3]);
                superExtendsStyle = false;
            }
        }
        if (functionalInterfaceName == NAME_JUF_INTOP) {
            applyMethodName = NAME_APPLY_AS_INT;
        }
        if (functionalInterfaceName == NAME_JUF_LONGOP) {
            applyMethodName = NAME_APPLY_AS_LONG;
        }
        if (functionalInterfaceName == NAME_JUF_DOUBLEOP) {
            applyMethodName = NAME_APPLY_AS_DOUBLE;
        }
        if (functionalInterfaceName == null) {
            functionalInterfaceName = NAME_JUF_FUNCTION;
            parameterizer = EclipseHandlerUtil.copyType(field.type, source);
        }
        method.annotations = EclipseHandlerUtil.copyAnnotations(source, onMethod.toArray(new Annotation[0]), checkerFramework, deprecated);
        QualifiedTypeReference fType = null;
        if (parameterizer != null && superExtendsStyle) {
            Wildcard w1 = new Wildcard(2);
            w1.bound = parameterizer;
            Wildcard w2 = new Wildcard(1);
            w2.bound = EclipseHandlerUtil.copyType(field.type, source);
            TypeReference[][] ta = new TypeReference[functionalInterfaceName.length][];
            ta[functionalInterfaceName.length - 1] = new TypeReference[]{w1, w2};
            ps = new long[functionalInterfaceName.length];
            fType = new ParameterizedQualifiedTypeReference(functionalInterfaceName, (TypeReference[][])ta, 0, (long[])ps);
        }
        if (parameterizer != null && !superExtendsStyle) {
            TypeReference[][] ta = new TypeReference[functionalInterfaceName.length][];
            ta[functionalInterfaceName.length - 1] = new TypeReference[]{parameterizer};
            long[] ps2 = new long[functionalInterfaceName.length];
            fType = new ParameterizedQualifiedTypeReference(functionalInterfaceName, (TypeReference[][])ta, 0, ps2);
        }
        if (parameterizer == null) {
            long[] ps3 = new long[functionalInterfaceName.length];
            fType = new QualifiedTypeReference(functionalInterfaceName, ps3);
        }
        Argument param = new Argument(NAME_TRANSFORMER, p, fType, 16);
        param.sourceStart = pS;
        param.sourceEnd = pE;
        method.arguments = new Argument[]{param};
        method.selector = name.toCharArray();
        method.binding = null;
        method.thrownExceptions = null;
        method.typeParameters = null;
        method.bits |= 0x800000;
        if (!makeAbstract) {
            Statement nullCheck;
            ArrayList<Object> args = new ArrayList<Object>();
            ps = ((EclipseNode)fieldNode.up()).down().iterator();
            while (ps.hasNext()) {
                long fieldFlags;
                EclipseNode child = (EclipseNode)ps.next();
                if (child.getKind() != AST.Kind.FIELD) continue;
                FieldDeclaration childDecl = (FieldDeclaration)child.get();
                if (childDecl.name != null && childDecl.name.length > 0 && childDecl.name[0] == '$' || ((fieldFlags = (long)childDecl.modifiers) & 8L) != 0L || (fieldFlags & 0x10L) != 0L && childDecl.initialization != null) continue;
                if (child.get() == fieldNode.get()) {
                    MessageSend ms = new MessageSend();
                    ms.receiver = new SingleNameReference(NAME_TRANSFORMER, 0L);
                    ms.selector = applyMethodName;
                    ms.arguments = new Expression[]{EclipseHandlerUtil.createFieldAccessor(child, HandlerUtil.FieldAccess.ALWAYS_FIELD, source)};
                    if (requiredCast != -1) {
                        args.add(EclipseHandlerUtil.makeCastExpression((Expression)ms, TypeReference.baseTypeReference((int)requiredCast, (int)0), source));
                        continue;
                    }
                    args.add(ms);
                    continue;
                }
                args.add(EclipseHandlerUtil.createFieldAccessor(child, HandlerUtil.FieldAccess.ALWAYS_FIELD, source));
            }
            AllocationExpression constructorCall = new AllocationExpression();
            constructorCall.arguments = args.toArray(new Expression[0]);
            constructorCall.type = EclipseHandlerUtil.cloneSelfType(fieldNode, source);
            ReturnStatement returnStatement = new ReturnStatement((Expression)constructorCall, pS, pE);
            method.declarationSourceStart = method.sourceStart = source.sourceStart;
            method.bodyStart = method.sourceStart;
            method.declarationSourceEnd = method.sourceEnd = source.sourceEnd;
            method.bodyEnd = method.sourceEnd;
            ArrayList<Object> statements = new ArrayList<Object>(5);
            if (EclipseHandlerUtil.hasNonNullAnnotations(fieldNode) && (nullCheck = EclipseHandlerUtil.generateNullCheck((AbstractVariableDeclaration)field, sourceNode, null)) != null) {
                statements.add(nullCheck);
            }
            statements.add(returnStatement);
            method.statements = statements.toArray(new Statement[0]);
        }
        EclipseHandlerUtil.createRelevantNonNullAnnotation(sourceNode, param, method);
        EclipseHandlerUtil.createRelevantNonNullAnnotation(fieldNode, method);
        method.traverse((ASTVisitor)new SetGeneratedByVisitor(source), parent.scope);
        EclipseHandlerUtil.copyJavadoc(fieldNode, (ASTNode)method, EclipseHandlerUtil.CopyJavadoc.WITH_BY);
        return method;
    }
}
