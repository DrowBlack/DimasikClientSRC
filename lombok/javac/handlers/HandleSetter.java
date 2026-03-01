package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.Setter;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HandlerUtil;
import lombok.experimental.Accessors;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleSetter
extends JavacAnnotationHandler<Setter> {
    private static final String SETTER_NODE_NOT_SUPPORTED_ERR = "@Setter is only supported on a class or a field.";

    public void generateSetterForType(JavacNode typeNode, JavacNode errorNode, AccessLevel level, boolean checkForTypeLevelSetter, List<JCTree.JCAnnotation> onMethod, List<JCTree.JCAnnotation> onParam) {
        if (checkForTypeLevelSetter && JavacHandlerUtil.hasAnnotation(Setter.class, typeNode)) {
            return;
        }
        if (!JavacHandlerUtil.isClass(typeNode)) {
            errorNode.addError(SETTER_NODE_NOT_SUPPORTED_ERR);
            return;
        }
        for (JavacNode field : typeNode.down()) {
            if (field.getKind() != AST.Kind.FIELD) continue;
            JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)field.get();
            if (fieldDecl.name.toString().startsWith("$") || (fieldDecl.mods.flags & 8L) != 0L || (fieldDecl.mods.flags & 0x10L) != 0L) continue;
            this.generateSetterForField(field, errorNode, level, onMethod, onParam);
        }
    }

    public void generateSetterForField(JavacNode fieldNode, JavacNode sourceNode, AccessLevel level, List<JCTree.JCAnnotation> onMethod, List<JCTree.JCAnnotation> onParam) {
        if (JavacHandlerUtil.hasAnnotation(Setter.class, fieldNode)) {
            return;
        }
        this.createSetterForField(level, fieldNode, sourceNode, false, onMethod, onParam);
    }

    @Override
    public void handle(AnnotationValues<Setter> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        List<JCTree.JCAnnotation> onParam;
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.SETTER_FLAG_USAGE, "@Setter");
        Collection<JavacNode> fields = annotationNode.upFromAnnotationToFields();
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Setter.class);
        JavacHandlerUtil.deleteImportFromCompilationUnit(annotationNode, "lombok.AccessLevel");
        JavacNode node = (JavacNode)annotationNode.up();
        AccessLevel level = annotation.getInstance().value();
        if (level == AccessLevel.NONE || node == null) {
            return;
        }
        List<JCTree.JCAnnotation> onMethod = JavacHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onMethod", "@Setter(onMethod", annotationNode);
        if (!onMethod.isEmpty()) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.ON_X_FLAG_USAGE, "@Setter(onMethod=...)");
        }
        if (!(onParam = JavacHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onParam", "@Setter(onParam", annotationNode)).isEmpty()) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.ON_X_FLAG_USAGE, "@Setter(onParam=...)");
        }
        switch (node.getKind()) {
            case FIELD: {
                this.createSetterForFields(level, fields, annotationNode, true, onMethod, onParam);
                break;
            }
            case TYPE: {
                this.generateSetterForType(node, annotationNode, level, false, onMethod, onParam);
            }
        }
    }

    public void createSetterForFields(AccessLevel level, Collection<JavacNode> fieldNodes, JavacNode errorNode, boolean whineIfExists, List<JCTree.JCAnnotation> onMethod, List<JCTree.JCAnnotation> onParam) {
        for (JavacNode fieldNode : fieldNodes) {
            this.createSetterForField(level, fieldNode, errorNode, whineIfExists, onMethod, onParam);
        }
    }

    public void createSetterForField(AccessLevel level, JavacNode fieldNode, JavacNode sourceNode, boolean whineIfExists, List<JCTree.JCAnnotation> onMethod, List<JCTree.JCAnnotation> onParam) {
        if (fieldNode.getKind() != AST.Kind.FIELD) {
            fieldNode.addError(SETTER_NODE_NOT_SUPPORTED_ERR);
            return;
        }
        AnnotationValues<Accessors> accessors = JavacHandlerUtil.getAccessorsForField(fieldNode);
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)fieldNode.get();
        String methodName = JavacHandlerUtil.toSetterName(fieldNode, accessors);
        if (methodName == null) {
            fieldNode.addWarning("Not generating setter for this field: It does not fit your @Accessors prefix list.");
            return;
        }
        if ((fieldDecl.mods.flags & 0x10L) != 0L) {
            fieldNode.addWarning("Not generating setter for this field: Setters cannot be generated for final fields.");
            return;
        }
        for (String altName : JavacHandlerUtil.toAllSetterNames(fieldNode, accessors)) {
            switch (JavacHandlerUtil.methodExists(altName, fieldNode, false, 1)) {
                case EXISTS_BY_LOMBOK: {
                    return;
                }
                case EXISTS_BY_USER: {
                    if (whineIfExists) {
                        String altNameExpl = "";
                        if (!altName.equals(methodName)) {
                            altNameExpl = String.format(" (%s)", altName);
                        }
                        fieldNode.addWarning(String.format("Not generating %s(): A method with that name already exists%s", methodName, altNameExpl));
                    }
                    return;
                }
            }
        }
        long access = (long)JavacHandlerUtil.toJavacModifier(level) | fieldDecl.mods.flags & 8L;
        JCTree.JCMethodDecl createdSetter = HandleSetter.createSetter(access, fieldNode, fieldNode.getTreeMaker(), sourceNode, onMethod, onParam);
        JavacHandlerUtil.injectMethod((JavacNode)fieldNode.up(), createdSetter);
    }

    public static JCTree.JCMethodDecl createSetter(long access, JavacNode field, JavacTreeMaker treeMaker, JavacNode source, List<JCTree.JCAnnotation> onMethod, List<JCTree.JCAnnotation> onParam) {
        AnnotationValues<Accessors> accessors = JavacHandlerUtil.getAccessorsForField(field);
        String setterName = JavacHandlerUtil.toSetterName(field, accessors);
        boolean returnThis = JavacHandlerUtil.shouldReturnThis(field, accessors);
        JCTree.JCMethodDecl setter = HandleSetter.createSetter(access, false, field, treeMaker, setterName, null, null, returnThis, source, onMethod, onParam);
        return setter;
    }

    public static JCTree.JCMethodDecl createSetter(long access, boolean deprecate, JavacNode field, JavacTreeMaker treeMaker, String setterName, Name paramName, Name booleanFieldToSet, boolean shouldReturnThis, JavacNode source, List<JCTree.JCAnnotation> onMethod, List<JCTree.JCAnnotation> onParam) {
        JCTree.JCExpression returnType = null;
        JCTree.JCReturn returnStatement = null;
        if (shouldReturnThis) {
            returnType = JavacHandlerUtil.cloneSelfType(field);
            returnType = JavacHandlerUtil.addCheckerFrameworkReturnsReceiver(returnType, treeMaker, field, JavacHandlerUtil.getCheckerFrameworkVersion(source));
            returnStatement = treeMaker.Return(treeMaker.Ident(field.toName("this")));
        }
        return HandleSetter.createSetter(access, deprecate, field, treeMaker, setterName, paramName, booleanFieldToSet, returnType, returnStatement, source, onMethod, onParam);
    }

    public static JCTree.JCMethodDecl createSetterWithRecv(long access, boolean deprecate, JavacNode field, JavacTreeMaker treeMaker, String setterName, Name paramName, Name booleanFieldToSet, boolean shouldReturnThis, JavacNode source, List<JCTree.JCAnnotation> onMethod, List<JCTree.JCAnnotation> onParam, JCTree.JCVariableDecl recv) {
        JCTree.JCExpression returnType = null;
        JCTree.JCReturn returnStatement = null;
        if (shouldReturnThis) {
            returnType = JavacHandlerUtil.cloneSelfType(field);
            returnType = JavacHandlerUtil.addCheckerFrameworkReturnsReceiver(returnType, treeMaker, field, JavacHandlerUtil.getCheckerFrameworkVersion(source));
            returnStatement = treeMaker.Return(treeMaker.Ident(field.toName("this")));
        }
        JCTree.JCMethodDecl d = HandleSetter.createSetterWithRecv(access, deprecate, field, treeMaker, setterName, paramName, booleanFieldToSet, returnType, returnStatement, source, onMethod, onParam, recv);
        return d;
    }

    public static JCTree.JCMethodDecl createSetter(long access, boolean deprecate, JavacNode field, JavacTreeMaker treeMaker, String setterName, Name paramName, Name booleanFieldToSet, JCTree.JCExpression methodType, JCTree.JCStatement returnStatement, JavacNode source, List<JCTree.JCAnnotation> onMethod, List<JCTree.JCAnnotation> onParam) {
        return HandleSetter.createSetterWithRecv(access, deprecate, field, treeMaker, setterName, paramName, booleanFieldToSet, methodType, returnStatement, source, onMethod, onParam, null);
    }

    public static JCTree.JCMethodDecl createSetterWithRecv(long access, boolean deprecate, JavacNode field, JavacTreeMaker treeMaker, String setterName, Name paramName, Name booleanFieldToSet, JCTree.JCExpression methodType, JCTree.JCStatement returnStatement, JavacNode source, List<JCTree.JCAnnotation> onMethod, List<JCTree.JCAnnotation> onParam, JCTree.JCVariableDecl recv) {
        AnnotationValues<Accessors> accessors;
        if (setterName == null) {
            return null;
        }
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)field.get();
        if (paramName == null) {
            paramName = fieldDecl.name;
        }
        JCTree.JCExpression fieldRef = JavacHandlerUtil.createFieldAccessor(treeMaker, field, HandlerUtil.FieldAccess.ALWAYS_FIELD);
        JCTree.JCAssign assign = treeMaker.Assign(fieldRef, treeMaker.Ident(paramName));
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<JCTree.JCStatement>();
        List<JCTree.JCAnnotation> copyableAnnotations = JavacHandlerUtil.findCopyableAnnotations(field);
        Name methodName = field.toName(setterName);
        List<JCTree.JCAnnotation> annsOnParam = JavacHandlerUtil.copyAnnotations(onParam).appendList(copyableAnnotations);
        long flags = JavacHandlerUtil.addFinalIfNeeded(0x200000000L, field.getContext());
        JCTree.JCExpression pType = JavacHandlerUtil.cloneType(treeMaker, fieldDecl.vartype, source);
        JCTree.JCVariableDecl param = treeMaker.VarDef(treeMaker.Modifiers(flags, annsOnParam), paramName, pType, null);
        if (!JavacHandlerUtil.hasNonNullAnnotations(field) && !JavacHandlerUtil.hasNonNullAnnotations(field, onParam)) {
            statements.append(treeMaker.Exec(assign));
        } else {
            JCTree.JCStatement nullCheck = JavacHandlerUtil.generateNullCheck(treeMaker, fieldDecl.vartype, paramName, source, null);
            if (nullCheck != null) {
                statements.append(nullCheck);
            }
            statements.append(treeMaker.Exec(assign));
        }
        if (booleanFieldToSet != null) {
            JCTree.JCAssign setBool = treeMaker.Assign(treeMaker.Ident(booleanFieldToSet), treeMaker.Literal(Javac.CTC_BOOLEAN, 1));
            statements.append(treeMaker.Exec(setBool));
        }
        if (methodType == null) {
            methodType = treeMaker.Type(Javac.createVoidType(field.getSymbolTable(), Javac.CTC_VOID));
            returnStatement = null;
        }
        if (returnStatement != null) {
            statements.append(returnStatement);
        }
        JCTree.JCBlock methodBody = treeMaker.Block(0L, statements.toList());
        List<JCTree.JCTypeParameter> methodGenericParams = List.nil();
        List<JCTree.JCVariableDecl> parameters = List.of(param);
        List<JCTree.JCExpression> throwsClauses = List.nil();
        JCTree.JCExpression annotationMethodDefaultValue = null;
        List<JCTree.JCAnnotation> annsOnMethod = JavacHandlerUtil.mergeAnnotations(JavacHandlerUtil.copyAnnotations(onMethod), JavacHandlerUtil.findCopyableToSetterAnnotations(field));
        if (JavacHandlerUtil.isFieldDeprecated(field) || deprecate) {
            annsOnMethod = annsOnMethod.prepend(treeMaker.Annotation(JavacHandlerUtil.genJavaLangTypeRef(field, "Deprecated"), List.<JCTree.JCExpression>nil()));
        }
        if (JavacHandlerUtil.shouldMakeFinal(field, accessors = JavacHandlerUtil.getAccessorsForField(field))) {
            access |= 0x10L;
        }
        JCTree.JCMethodDecl methodDef = recv != null && treeMaker.hasMethodDefWithRecvParam() ? treeMaker.MethodDefWithRecvParam(treeMaker.Modifiers(access, annsOnMethod), methodName, methodType, methodGenericParams, recv, parameters, throwsClauses, methodBody, annotationMethodDefaultValue) : treeMaker.MethodDef(treeMaker.Modifiers(access, annsOnMethod), methodName, methodType, methodGenericParams, parameters, throwsClauses, methodBody, annotationMethodDefaultValue);
        if (returnStatement != null) {
            JavacHandlerUtil.createRelevantNonNullAnnotation(source, methodDef);
        }
        JCTree.JCMethodDecl decl = JavacHandlerUtil.recursiveSetGeneratedBy(methodDef, source);
        JavacHandlerUtil.copyJavadoc(field, decl, JavacHandlerUtil.CopyJavadoc.SETTER, returnStatement != null);
        return decl;
    }
}
