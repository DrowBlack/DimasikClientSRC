package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.Setter;
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
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public class HandleSetter
extends EclipseAnnotationHandler<Setter> {
    private static final String SETTER_NODE_NOT_SUPPORTED_ERR = "@Setter is only supported on a class or a field.";

    public boolean generateSetterForType(EclipseNode typeNode, EclipseNode pos, AccessLevel level, boolean checkForTypeLevelSetter, List<Annotation> onMethod, List<Annotation> onParam) {
        if (checkForTypeLevelSetter && EclipseHandlerUtil.hasAnnotation(Setter.class, typeNode)) {
            return true;
        }
        if (!EclipseHandlerUtil.isClass(typeNode)) {
            pos.addError(SETTER_NODE_NOT_SUPPORTED_ERR);
            return false;
        }
        for (EclipseNode field : typeNode.down()) {
            FieldDeclaration fieldDecl;
            if (field.getKind() != AST.Kind.FIELD || !EclipseHandlerUtil.filterField(fieldDecl = (FieldDeclaration)field.get()) || (fieldDecl.modifiers & 0x10) != 0) continue;
            this.generateSetterForField(field, pos, level, onMethod, onParam);
        }
        return true;
    }

    public void generateSetterForField(EclipseNode fieldNode, EclipseNode sourceNode, AccessLevel level, List<Annotation> onMethod, List<Annotation> onParam) {
        if (EclipseHandlerUtil.hasAnnotation(Setter.class, fieldNode)) {
            return;
        }
        this.createSetterForField(level, fieldNode, sourceNode, false, onMethod, onParam);
    }

    @Override
    public void handle(AnnotationValues<Setter> annotation, Annotation ast, EclipseNode annotationNode) {
        List<Annotation> onParam;
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.SETTER_FLAG_USAGE, "@Setter");
        EclipseNode node = (EclipseNode)annotationNode.up();
        AccessLevel level = annotation.getInstance().value();
        if (level == AccessLevel.NONE || node == null) {
            return;
        }
        List<Annotation> onMethod = EclipseHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onMethod", "@Setter(onMethod", annotationNode);
        if (!onMethod.isEmpty()) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.ON_X_FLAG_USAGE, "@Setter(onMethod=...)");
        }
        if (!(onParam = EclipseHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onParam", "@Setter(onParam", annotationNode)).isEmpty()) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.ON_X_FLAG_USAGE, "@Setter(onParam=...)");
        }
        switch (node.getKind()) {
            case FIELD: {
                this.createSetterForFields(level, annotationNode.upFromAnnotationToFields(), annotationNode, true, onMethod, onParam);
                break;
            }
            case TYPE: {
                this.generateSetterForType(node, annotationNode, level, false, onMethod, onParam);
            }
        }
    }

    public void createSetterForFields(AccessLevel level, Collection<EclipseNode> fieldNodes, EclipseNode sourceNode, boolean whineIfExists, List<Annotation> onMethod, List<Annotation> onParam) {
        for (EclipseNode fieldNode : fieldNodes) {
            this.createSetterForField(level, fieldNode, sourceNode, whineIfExists, onMethod, onParam);
        }
    }

    public void createSetterForField(AccessLevel level, EclipseNode fieldNode, EclipseNode sourceNode, boolean whineIfExists, List<Annotation> onMethod, List<Annotation> onParam) {
        ASTNode source = (ASTNode)sourceNode.get();
        if (fieldNode.getKind() != AST.Kind.FIELD) {
            sourceNode.addError(SETTER_NODE_NOT_SUPPORTED_ERR);
            return;
        }
        FieldDeclaration field = (FieldDeclaration)fieldNode.get();
        TypeReference fieldType = EclipseHandlerUtil.copyType(field.type, source);
        boolean isBoolean = EclipseHandlerUtil.isBoolean(fieldType);
        AnnotationValues<Accessors> accessors = EclipseHandlerUtil.getAccessorsForField(fieldNode);
        String setterName = EclipseHandlerUtil.toSetterName(fieldNode, isBoolean, accessors);
        boolean shouldReturnThis = EclipseHandlerUtil.shouldReturnThis(fieldNode, accessors);
        if (setterName == null) {
            fieldNode.addWarning("Not generating setter for this field: It does not fit your @Accessors prefix list.");
            return;
        }
        int modifier = EclipseHandlerUtil.toEclipseModifier(level) | field.modifiers & 8;
        for (String altName : EclipseHandlerUtil.toAllSetterNames(fieldNode, isBoolean, accessors)) {
            switch (EclipseHandlerUtil.methodExists(altName, fieldNode, false, 1)) {
                case EXISTS_BY_LOMBOK: {
                    return;
                }
                case EXISTS_BY_USER: {
                    if (whineIfExists) {
                        String altNameExpl = "";
                        if (!altName.equals(setterName)) {
                            altNameExpl = String.format(" (%s)", altName);
                        }
                        fieldNode.addWarning(String.format("Not generating %s(): A method with that name already exists%s", setterName, altNameExpl));
                    }
                    return;
                }
            }
        }
        MethodDeclaration method = HandleSetter.createSetter((TypeDeclaration)((EclipseNode)fieldNode.up()).get(), false, fieldNode, setterName, null, null, shouldReturnThis, modifier, sourceNode, onMethod, onParam);
        EclipseHandlerUtil.injectMethod((EclipseNode)fieldNode.up(), (AbstractMethodDeclaration)method);
    }

    static MethodDeclaration createSetter(TypeDeclaration parent, boolean deprecate, EclipseNode fieldNode, String name, char[] paramName, char[] booleanFieldToSet, boolean shouldReturnThis, int modifier, EclipseNode sourceNode, List<Annotation> onMethod, List<Annotation> onParam) {
        ASTNode source = (ASTNode)sourceNode.get();
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        TypeReference returnType = null;
        ReturnStatement returnThis = null;
        if (shouldReturnThis) {
            returnType = EclipseHandlerUtil.cloneSelfType(fieldNode, source);
            EclipseHandlerUtil.addCheckerFrameworkReturnsReceiver(returnType, source, EclipseHandlerUtil.getCheckerFrameworkVersion(sourceNode));
            ThisReference thisRef = new ThisReference(pS, pE);
            returnThis = new ReturnStatement((Expression)thisRef, pS, pE);
        }
        MethodDeclaration d = HandleSetter.createSetter(parent, deprecate, fieldNode, name, paramName, booleanFieldToSet, returnType, returnThis, modifier, sourceNode, onMethod, onParam);
        return d;
    }

    static MethodDeclaration createSetter(TypeDeclaration parent, boolean deprecate, EclipseNode fieldNode, String name, char[] paramName, char[] booleanFieldToSet, TypeReference returnType, Statement returnStatement, int modifier, EclipseNode sourceNode, List<Annotation> onMethod, List<Annotation> onParam) {
        FieldDeclaration field = (FieldDeclaration)fieldNode.get();
        if (paramName == null) {
            paramName = field.name;
        }
        ASTNode source = (ASTNode)sourceNode.get();
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        MethodDeclaration method = new MethodDeclaration(parent.compilationResult);
        AnnotationValues<Accessors> accessors = EclipseHandlerUtil.getAccessorsForField(fieldNode);
        if (EclipseHandlerUtil.shouldMakeFinal(fieldNode, accessors)) {
            modifier |= 0x10;
        }
        method.modifiers = modifier;
        if (returnType != null) {
            method.returnType = returnType;
        } else {
            method.returnType = TypeReference.baseTypeReference((int)6, (int)0);
            method.returnType.sourceStart = pS;
            method.returnType.sourceEnd = pE;
        }
        Annotation[] deprecated = null;
        if (EclipseHandlerUtil.isFieldDeprecated(fieldNode) || deprecate) {
            deprecated = new Annotation[]{EclipseHandlerUtil.generateDeprecatedAnnotation(source)};
        }
        method.annotations = EclipseHandlerUtil.copyAnnotations(source, onMethod.toArray(new Annotation[0]), deprecated, EclipseHandlerUtil.findCopyableToSetterAnnotations(fieldNode));
        Argument param = new Argument(paramName, p, EclipseHandlerUtil.copyType(field.type, source), 16);
        param.sourceStart = pS;
        param.sourceEnd = pE;
        method.arguments = new Argument[]{param};
        method.selector = name.toCharArray();
        method.binding = null;
        method.thrownExceptions = null;
        method.typeParameters = null;
        method.bits |= 0x800000;
        Expression fieldRef = EclipseHandlerUtil.createFieldAccessor(fieldNode, HandlerUtil.FieldAccess.ALWAYS_FIELD, source);
        SingleNameReference fieldNameRef = new SingleNameReference(paramName, p);
        Assignment assignment = new Assignment(fieldRef, (Expression)fieldNameRef, (int)p);
        assignment.sourceStart = pS;
        assignment.sourceEnd = assignment.statementEnd = pE;
        method.declarationSourceStart = method.sourceStart = source.sourceStart;
        method.bodyStart = method.sourceStart;
        method.declarationSourceEnd = method.sourceEnd = source.sourceEnd;
        method.bodyEnd = method.sourceEnd;
        Annotation[] copyableAnnotations = EclipseHandlerUtil.findCopyableAnnotations(fieldNode);
        ArrayList<Object> statements = new ArrayList<Object>(5);
        if (!EclipseHandlerUtil.hasNonNullAnnotations(fieldNode) && !EclipseHandlerUtil.hasNonNullAnnotations(fieldNode, onParam)) {
            statements.add(assignment);
        } else {
            Statement nullCheck = EclipseHandlerUtil.generateNullCheck(field.type, paramName, sourceNode, null);
            if (nullCheck != null) {
                statements.add(nullCheck);
            }
            statements.add(assignment);
        }
        if (booleanFieldToSet != null) {
            statements.add(new Assignment((Expression)new SingleNameReference(booleanFieldToSet, p), (Expression)new TrueLiteral(pS, pE), pE));
        }
        if (returnType != null && returnStatement != null) {
            statements.add(returnStatement);
        }
        method.statements = statements.toArray(new Statement[0]);
        param.annotations = EclipseHandlerUtil.copyAnnotations(source, copyableAnnotations, onParam.toArray(new Annotation[0]));
        if (param.annotations != null) {
            param.bits |= 0x100000;
            method.bits |= 0x100000;
        }
        if (returnType != null && returnStatement != null) {
            EclipseHandlerUtil.createRelevantNonNullAnnotation(sourceNode, method);
        }
        method.traverse((ASTVisitor)new SetGeneratedByVisitor(source), parent.scope);
        EclipseHandlerUtil.copyJavadoc(fieldNode, (ASTNode)method, EclipseHandlerUtil.CopyJavadoc.SETTER, returnStatement != null);
        return method;
    }
}
