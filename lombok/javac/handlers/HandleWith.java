package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.With;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.configuration.CheckerFrameworkVersion;
import lombok.core.handlers.HandlerUtil;
import lombok.experimental.Accessors;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleWith
extends JavacAnnotationHandler<With> {
    public void generateWithForType(JavacNode typeNode, JavacNode errorNode, AccessLevel level, boolean checkForTypeLevelWith) {
        boolean notAClass;
        if (checkForTypeLevelWith && JavacHandlerUtil.hasAnnotation(With.class, typeNode)) {
            return;
        }
        JCTree.JCClassDecl typeDecl = null;
        if (typeNode.get() instanceof JCTree.JCClassDecl) {
            typeDecl = (JCTree.JCClassDecl)typeNode.get();
        }
        long modifiers = typeDecl == null ? 0L : typeDecl.mods.flags;
        boolean bl = notAClass = (modifiers & 0x6200L) != 0L;
        if (typeDecl == null || notAClass) {
            errorNode.addError("@With is only supported on a class or a field.");
            return;
        }
        for (JavacNode field : typeNode.down()) {
            if (field.getKind() != AST.Kind.FIELD) continue;
            JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)field.get();
            if (fieldDecl.name.toString().startsWith("$") || (fieldDecl.mods.flags & 8L) != 0L || (fieldDecl.mods.flags & 0x10L) != 0L && fieldDecl.init != null) continue;
            this.generateWithForField(field, (JCDiagnostic.DiagnosticPosition)errorNode.get(), level);
        }
    }

    public void generateWithForField(JavacNode fieldNode, JCDiagnostic.DiagnosticPosition pos, AccessLevel level) {
        if (JavacHandlerUtil.hasAnnotation(With.class, fieldNode)) {
            return;
        }
        this.createWithForField(level, fieldNode, fieldNode, false, List.<JCTree.JCAnnotation>nil(), List.<JCTree.JCAnnotation>nil());
    }

    @Override
    public void handle(AnnotationValues<With> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.WITH_FLAG_USAGE, "@With");
        Collection<JavacNode> fields = annotationNode.upFromAnnotationToFields();
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, With.class, "lombok.experimental.Wither");
        JavacHandlerUtil.deleteImportFromCompilationUnit(annotationNode, "lombok.AccessLevel");
        JavacNode node = (JavacNode)annotationNode.up();
        AccessLevel level = annotation.getInstance().value();
        if (level == AccessLevel.NONE || node == null) {
            return;
        }
        List<JCTree.JCAnnotation> onMethod = JavacHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onMethod", "@With(onMethod", annotationNode);
        List<JCTree.JCAnnotation> onParam = JavacHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onParam", "@With(onParam", annotationNode);
        switch (node.getKind()) {
            case FIELD: {
                this.createWithForFields(level, fields, annotationNode, true, onMethod, onParam);
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

    public void createWithForFields(AccessLevel level, Collection<JavacNode> fieldNodes, JavacNode errorNode, boolean whineIfExists, List<JCTree.JCAnnotation> onMethod, List<JCTree.JCAnnotation> onParam) {
        for (JavacNode fieldNode : fieldNodes) {
            this.createWithForField(level, fieldNode, errorNode, whineIfExists, onMethod, onParam);
        }
    }

    public void createWithForField(AccessLevel level, JavacNode fieldNode, JavacNode source, boolean strictMode, List<JCTree.JCAnnotation> onMethod, List<JCTree.JCAnnotation> onParam) {
        boolean makeAbstract;
        JavacNode typeNode = (JavacNode)fieldNode.up();
        boolean bl = makeAbstract = typeNode != null && typeNode.getKind() == AST.Kind.TYPE && (((JCTree.JCClassDecl)typeNode.get()).mods.flags & 0x400L) != 0L;
        if (fieldNode.getKind() != AST.Kind.FIELD) {
            fieldNode.addError("@With is only supported on a class or a field.");
            return;
        }
        AnnotationValues<Accessors> accessors = JavacHandlerUtil.getAccessorsForField(fieldNode);
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)fieldNode.get();
        String methodName = JavacHandlerUtil.toWithName(fieldNode, accessors);
        if (methodName == null) {
            fieldNode.addWarning("Not generating a withX method for this field: It does not fit your @Accessors prefix list.");
            return;
        }
        if ((fieldDecl.mods.flags & 8L) != 0L) {
            if (strictMode) {
                fieldNode.addWarning("Not generating " + methodName + " for this field: With methods cannot be generated for static fields.");
            }
            return;
        }
        if ((fieldDecl.mods.flags & 0x10L) != 0L && fieldDecl.init != null) {
            if (strictMode) {
                fieldNode.addWarning("Not generating " + methodName + " for this field: With methods cannot be generated for final, initialized fields.");
            }
            return;
        }
        if (fieldDecl.name.toString().startsWith("$")) {
            if (strictMode) {
                fieldNode.addWarning("Not generating " + methodName + " for this field: With methods cannot be generated for fields starting with $.");
            }
            return;
        }
        for (String altName : JavacHandlerUtil.toAllWithNames(fieldNode, accessors)) {
            switch (JavacHandlerUtil.methodExists(altName, fieldNode, false, 1)) {
                case EXISTS_BY_LOMBOK: {
                    return;
                }
                case EXISTS_BY_USER: {
                    if (strictMode) {
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
        long access = JavacHandlerUtil.toJavacModifier(level);
        JCTree.JCMethodDecl createdWith = this.createWith(access, fieldNode, fieldNode.getTreeMaker(), source, onMethod, onParam, makeAbstract);
        JavacHandlerUtil.createRelevantNonNullAnnotation(fieldNode, createdWith);
        JavacHandlerUtil.recursiveSetGeneratedBy(createdWith, source);
        JavacHandlerUtil.injectMethod(typeNode, createdWith);
    }

    public JCTree.JCMethodDecl createWith(long access, JavacNode field, JavacTreeMaker maker, JavacNode source, List<JCTree.JCAnnotation> onMethod, List<JCTree.JCAnnotation> onParam, boolean makeAbstract) {
        AnnotationValues<Accessors> accessors;
        boolean makeFinal;
        String withName = JavacHandlerUtil.toWithName(field);
        if (withName == null) {
            return null;
        }
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)field.get();
        List<JCTree.JCAnnotation> copyableAnnotations = JavacHandlerUtil.findCopyableAnnotations(field);
        Name methodName = field.toName(withName);
        JCTree.JCExpression returnType = JavacHandlerUtil.cloneSelfType(field);
        JCTree.JCBlock methodBody = null;
        long flags = JavacHandlerUtil.addFinalIfNeeded(0x200000000L, field.getContext());
        List<JCTree.JCAnnotation> annsOnParam = JavacHandlerUtil.copyAnnotations(onParam).appendList(copyableAnnotations);
        JCTree.JCExpression pType = JavacHandlerUtil.cloneType(maker, fieldDecl.vartype, source);
        JCTree.JCVariableDecl param = maker.VarDef(maker.Modifiers(flags, annsOnParam), fieldDecl.name, pType, null);
        if (!makeAbstract) {
            ListBuffer<JCTree.JCStatement> statements = new ListBuffer<JCTree.JCStatement>();
            JCTree.JCExpression selfType = JavacHandlerUtil.cloneSelfType(field);
            if (selfType == null) {
                return null;
            }
            ListBuffer<JCTree.JCExpression> args = new ListBuffer<JCTree.JCExpression>();
            for (JavacNode child : ((JavacNode)field.up()).down()) {
                long fieldFlags;
                if (child.getKind() != AST.Kind.FIELD) continue;
                JCTree.JCVariableDecl childDecl = (JCTree.JCVariableDecl)child.get();
                if (childDecl.name.toString().startsWith("$") || ((fieldFlags = childDecl.mods.flags) & 8L) != 0L || (fieldFlags & 0x10L) != 0L && childDecl.init != null) continue;
                if (child.get() == field.get()) {
                    args.append(maker.Ident(fieldDecl.name));
                    continue;
                }
                args.append(JavacHandlerUtil.createFieldAccessor(maker, child, HandlerUtil.FieldAccess.ALWAYS_FIELD));
            }
            JCTree.JCNewClass newClass = maker.NewClass(null, List.<JCTree.JCExpression>nil(), selfType, args.toList(), null);
            JCTree.JCBinary identityCheck = maker.Binary(Javac.CTC_EQUAL, JavacHandlerUtil.createFieldAccessor(maker, field, HandlerUtil.FieldAccess.ALWAYS_FIELD), maker.Ident(fieldDecl.name));
            JCTree.JCConditional conditional = maker.Conditional(identityCheck, maker.Ident(field.toName("this")), newClass);
            JCTree.JCReturn returnStatement = maker.Return(conditional);
            if (!JavacHandlerUtil.hasNonNullAnnotations(field)) {
                statements.append(returnStatement);
            } else {
                JCTree.JCStatement nullCheck = JavacHandlerUtil.generateNullCheck(maker, field, source);
                if (nullCheck != null) {
                    statements.append(nullCheck);
                }
                statements.append(returnStatement);
            }
            methodBody = maker.Block(0L, statements.toList());
        }
        List<JCTree.JCTypeParameter> methodGenericParams = List.nil();
        List<JCTree.JCVariableDecl> parameters = List.of(param);
        List<JCTree.JCExpression> throwsClauses = List.nil();
        JCTree.JCExpression annotationMethodDefaultValue = null;
        List<JCTree.JCAnnotation> annsOnMethod = JavacHandlerUtil.copyAnnotations(onMethod);
        CheckerFrameworkVersion checkerFramework = JavacHandlerUtil.getCheckerFrameworkVersion(source);
        if (checkerFramework.generateSideEffectFree()) {
            annsOnMethod = annsOnMethod.prepend(maker.Annotation(JavacHandlerUtil.genTypeRef(source, "org.checkerframework.dataflow.qual.SideEffectFree"), List.<JCTree.JCExpression>nil()));
        }
        if (JavacHandlerUtil.isFieldDeprecated(field)) {
            annsOnMethod = annsOnMethod.prepend(maker.Annotation(JavacHandlerUtil.genJavaLangTypeRef(field, "Deprecated"), List.<JCTree.JCExpression>nil()));
        }
        if (makeAbstract) {
            access |= 0x400L;
        }
        if (makeFinal = JavacHandlerUtil.shouldMakeFinal(field, accessors = JavacHandlerUtil.getAccessorsForField(field))) {
            access |= 0x10L;
        }
        JCTree.JCMethodDecl decl = JavacHandlerUtil.recursiveSetGeneratedBy(maker.MethodDef(maker.Modifiers(access, annsOnMethod), methodName, returnType, methodGenericParams, parameters, throwsClauses, methodBody, annotationMethodDefaultValue), source);
        JavacHandlerUtil.copyJavadoc(field, decl, JavacHandlerUtil.CopyJavadoc.WITH);
        return decl;
    }
}
