package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.With;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.SetGeneratedByVisitor;
import lombok.experimental.Accessors;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public class HandleWith
extends EclipseAnnotationHandler<With> {
    public boolean generateWithForType(EclipseNode typeNode, EclipseNode pos, AccessLevel level, boolean checkForTypeLevelWith) {
        boolean notAClass;
        if (checkForTypeLevelWith && EclipseHandlerUtil.hasAnnotation(With.class, typeNode)) {
            return true;
        }
        TypeDeclaration typeDecl = null;
        if (typeNode.get() instanceof TypeDeclaration) {
            typeDecl = (TypeDeclaration)typeNode.get();
        }
        int modifiers = typeDecl == null ? 0 : typeDecl.modifiers;
        boolean bl = notAClass = (modifiers & 0x6200) != 0;
        if (typeDecl == null || notAClass) {
            pos.addError("@With is only supported on a class or a field.");
            return false;
        }
        for (EclipseNode field : typeNode.down()) {
            FieldDeclaration fieldDecl;
            if (field.getKind() != AST.Kind.FIELD || !EclipseHandlerUtil.filterField(fieldDecl = (FieldDeclaration)field.get()) || (fieldDecl.modifiers & 0x10) != 0 && fieldDecl.initialization != null) continue;
            this.generateWithForField(field, pos, level);
        }
        return true;
    }

    public void generateWithForField(EclipseNode fieldNode, EclipseNode sourceNode, AccessLevel level) {
        for (EclipseNode child : fieldNode.down()) {
            if (child.getKind() != AST.Kind.ANNOTATION || !EclipseHandlerUtil.annotationTypeMatches(With.class, child)) continue;
            return;
        }
        List<Annotation> empty = Collections.emptyList();
        this.createWithForField(level, fieldNode, sourceNode, false, empty, empty);
    }

    @Override
    public void handle(AnnotationValues<With> annotation, Annotation ast, EclipseNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.WITH_FLAG_USAGE, "@With");
        EclipseNode node = (EclipseNode)annotationNode.up();
        AccessLevel level = annotation.getInstance().value();
        if (level == AccessLevel.NONE || node == null) {
            return;
        }
        List<Annotation> onMethod = EclipseHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onMethod", "@With(onMethod", annotationNode);
        List<Annotation> onParam = EclipseHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onParam", "@With(onParam", annotationNode);
        switch (node.getKind()) {
            case FIELD: {
                this.createWithForFields(level, annotationNode.upFromAnnotationToFields(), annotationNode, true, onMethod, onParam);
                break;
            }
            case TYPE: {
                if (!onMethod.isEmpty()) {
                    annotationNode.addError("'onMethod' is not supported for @With on a type.");
                }
                if (!onParam.isEmpty()) {
                    annotationNode.addError("'onParam' is not supported for @With on a type.");
                }
                this.generateWithForType(node, annotationNode, level, false);
            }
        }
    }

    public void createWithForFields(AccessLevel level, Collection<EclipseNode> fieldNodes, EclipseNode sourceNode, boolean whineIfExists, List<Annotation> onMethod, List<Annotation> onParam) {
        for (EclipseNode fieldNode : fieldNodes) {
            this.createWithForField(level, fieldNode, sourceNode, whineIfExists, onMethod, onParam);
        }
    }

    public void createWithForField(AccessLevel level, EclipseNode fieldNode, EclipseNode sourceNode, boolean whineIfExists, List<Annotation> onMethod, List<Annotation> onParam) {
        AnnotationValues<Accessors> accessors;
        ASTNode source = (ASTNode)sourceNode.get();
        if (fieldNode.getKind() != AST.Kind.FIELD) {
            sourceNode.addError("@With is only supported on a class or a field.");
            return;
        }
        EclipseNode typeNode = (EclipseNode)fieldNode.up();
        boolean makeAbstract = typeNode != null && typeNode.getKind() == AST.Kind.TYPE && (((TypeDeclaration)typeNode.get()).modifiers & 0x400) != 0;
        FieldDeclaration field = (FieldDeclaration)fieldNode.get();
        TypeReference fieldType = EclipseHandlerUtil.copyType(field.type, source);
        boolean isBoolean = EclipseHandlerUtil.isBoolean(fieldType);
        String withName = EclipseHandlerUtil.toWithName(fieldNode, isBoolean, accessors = EclipseHandlerUtil.getAccessorsForField(fieldNode));
        if (withName == null) {
            fieldNode.addWarning("Not generating a with method for this field: It does not fit your @Accessors prefix list.");
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
        for (String altName : EclipseHandlerUtil.toAllWithNames(fieldNode, isBoolean, accessors)) {
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
        MethodDeclaration method = this.createWith((TypeDeclaration)((EclipseNode)fieldNode.up()).get(), fieldNode, withName, modifier, sourceNode, onMethod, onParam, makeAbstract);
        EclipseHandlerUtil.injectMethod((EclipseNode)fieldNode.up(), (AbstractMethodDeclaration)method);
    }

    public MethodDeclaration createWith(TypeDeclaration parent, EclipseNode fieldNode, String name, int modifier, EclipseNode sourceNode, List<Annotation> onMethod, List<Annotation> onParam, boolean makeAbstract) {
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
        method.annotations = EclipseHandlerUtil.copyAnnotations(source, onMethod.toArray(new Annotation[0]), checkerFramework, deprecated);
        Argument param = new Argument(field.name, p, EclipseHandlerUtil.copyType(field.type, source), 16);
        param.sourceStart = pS;
        param.sourceEnd = pE;
        method.arguments = new Argument[]{param};
        method.selector = name.toCharArray();
        method.binding = null;
        method.thrownExceptions = null;
        method.typeParameters = null;
        method.bits |= 0x800000;
        Annotation[] copyableAnnotations = EclipseHandlerUtil.findCopyableAnnotations(fieldNode);
        if (!makeAbstract) {
            Statement nullCheck;
            ArrayList<Object> args = new ArrayList<Object>();
            for (EclipseNode child : ((EclipseNode)fieldNode.up()).down()) {
                long fieldFlags;
                if (child.getKind() != AST.Kind.FIELD) continue;
                FieldDeclaration childDecl = (FieldDeclaration)child.get();
                if (childDecl.name != null && childDecl.name.length > 0 && childDecl.name[0] == '$' || ((fieldFlags = (long)childDecl.modifiers) & 8L) != 0L || (fieldFlags & 0x10L) != 0L && childDecl.initialization != null) continue;
                if (child.get() == fieldNode.get()) {
                    args.add(new SingleNameReference(field.name, p));
                    continue;
                }
                args.add(EclipseHandlerUtil.createFieldAccessor(child, HandlerUtil.FieldAccess.ALWAYS_FIELD, source));
            }
            AllocationExpression constructorCall = new AllocationExpression();
            constructorCall.arguments = args.toArray(new Expression[0]);
            constructorCall.type = EclipseHandlerUtil.cloneSelfType(fieldNode, source);
            EqualExpression identityCheck = new EqualExpression(EclipseHandlerUtil.createFieldAccessor(fieldNode, HandlerUtil.FieldAccess.ALWAYS_FIELD, source), (Expression)new SingleNameReference(field.name, p), OperatorIds.EQUAL_EQUAL);
            ThisReference thisRef = new ThisReference(pS, pE);
            ConditionalExpression conditional = new ConditionalExpression((Expression)identityCheck, (Expression)thisRef, (Expression)constructorCall);
            ReturnStatement returnStatement = new ReturnStatement((Expression)conditional, pS, pE);
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
        param.annotations = EclipseHandlerUtil.copyAnnotations(source, copyableAnnotations, onParam.toArray(new Annotation[0]));
        EclipseHandlerUtil.createRelevantNonNullAnnotation(fieldNode, method);
        method.traverse((ASTVisitor)new SetGeneratedByVisitor(source), parent.scope);
        EclipseHandlerUtil.copyJavadoc(fieldNode, (ASTNode)method, EclipseHandlerUtil.CopyJavadoc.WITH);
        return method;
    }
}
