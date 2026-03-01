package lombok.javac.handlers;

import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.util.Collection;
import javax.lang.model.type.TypeKind;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.LombokImmutableList;
import lombok.core.configuration.CheckerFrameworkVersion;
import lombok.core.handlers.HandlerUtil;
import lombok.experimental.Accessors;
import lombok.experimental.WithBy;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleWithBy
extends JavacAnnotationHandler<WithBy> {
    private static final LombokImmutableList<String> NAME_JUF_FUNCTION = LombokImmutableList.of("java", "util", "function", "Function");
    private static final LombokImmutableList<String> NAME_JUF_OP = LombokImmutableList.of("java", "util", "function", "UnaryOperator");
    private static final LombokImmutableList<String> NAME_JUF_DOUBLEOP = LombokImmutableList.of("java", "util", "function", "DoubleUnaryOperator");
    private static final LombokImmutableList<String> NAME_JUF_INTOP = LombokImmutableList.of("java", "util", "function", "IntUnaryOperator");
    private static final LombokImmutableList<String> NAME_JUF_LONGOP = LombokImmutableList.of("java", "util", "function", "LongUnaryOperator");

    public void generateWithByForType(JavacNode typeNode, JavacNode errorNode, AccessLevel level, boolean checkForTypeLevelWithBy) {
        boolean notAClass;
        if (checkForTypeLevelWithBy && JavacHandlerUtil.hasAnnotation(WithBy.class, typeNode)) {
            return;
        }
        JCTree.JCClassDecl typeDecl = null;
        if (typeNode.get() instanceof JCTree.JCClassDecl) {
            typeDecl = (JCTree.JCClassDecl)typeNode.get();
        }
        long modifiers = typeDecl == null ? 0L : typeDecl.mods.flags;
        boolean bl = notAClass = (modifiers & 0x6200L) != 0L;
        if (typeDecl == null || notAClass) {
            errorNode.addError("@WithBy is only supported on a class or a field.");
            return;
        }
        for (JavacNode field : typeNode.down()) {
            if (field.getKind() != AST.Kind.FIELD) continue;
            JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)field.get();
            if (fieldDecl.name.toString().startsWith("$") || (fieldDecl.mods.flags & 8L) != 0L || (fieldDecl.mods.flags & 0x10L) != 0L && fieldDecl.init != null) continue;
            this.generateWithByForField(field, (JCDiagnostic.DiagnosticPosition)errorNode.get(), level);
        }
    }

    public void generateWithByForField(JavacNode fieldNode, JCDiagnostic.DiagnosticPosition pos, AccessLevel level) {
        if (JavacHandlerUtil.hasAnnotation(WithBy.class, fieldNode)) {
            return;
        }
        this.createWithByForField(level, fieldNode, fieldNode, false, List.<JCTree.JCAnnotation>nil());
    }

    @Override
    public void handle(AnnotationValues<WithBy> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        HandlerUtil.handleExperimentalFlagUsage(annotationNode, ConfigurationKeys.WITHBY_FLAG_USAGE, "@WithBy");
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, WithBy.class);
        JavacHandlerUtil.deleteImportFromCompilationUnit(annotationNode, "lombok.AccessLevel");
        JavacNode node = (JavacNode)annotationNode.up();
        AccessLevel level = annotation.getInstance().value();
        if (level == AccessLevel.NONE || node == null) {
            return;
        }
        List<JCTree.JCAnnotation> onMethod = JavacHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onMethod", "@WithBy(onMethod", annotationNode);
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

    public void createWithByForFields(AccessLevel level, Collection<JavacNode> fieldNodes, JavacNode errorNode, boolean whineIfExists, List<JCTree.JCAnnotation> onMethod) {
        for (JavacNode fieldNode : fieldNodes) {
            this.createWithByForField(level, fieldNode, errorNode, whineIfExists, onMethod);
        }
    }

    public void createWithByForField(AccessLevel level, JavacNode fieldNode, JavacNode source, boolean strictMode, List<JCTree.JCAnnotation> onMethod) {
        boolean makeAbstract;
        JavacNode typeNode = (JavacNode)fieldNode.up();
        boolean bl = makeAbstract = typeNode != null && typeNode.getKind() == AST.Kind.TYPE && (((JCTree.JCClassDecl)typeNode.get()).mods.flags & 0x400L) != 0L;
        if (fieldNode.getKind() != AST.Kind.FIELD) {
            fieldNode.addError("@WithBy is only supported on a class or a field.");
            return;
        }
        AnnotationValues<Accessors> accessors = JavacHandlerUtil.getAccessorsForField(fieldNode);
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)fieldNode.get();
        String methodName = JavacHandlerUtil.toWithByName(fieldNode, accessors);
        if (methodName == null) {
            fieldNode.addWarning("Not generating a withXBy method for this field: It does not fit your @Accessors prefix list.");
            return;
        }
        if ((fieldDecl.mods.flags & 8L) != 0L) {
            if (strictMode) {
                fieldNode.addWarning("Not generating " + methodName + " for this field: WithBy methods cannot be generated for static fields.");
            }
            return;
        }
        if ((fieldDecl.mods.flags & 0x10L) != 0L && fieldDecl.init != null) {
            if (strictMode) {
                fieldNode.addWarning("Not generating " + methodName + " for this field: WithBy methods cannot be generated for final, initialized fields.");
            }
            return;
        }
        if (fieldDecl.name.toString().startsWith("$")) {
            if (strictMode) {
                fieldNode.addWarning("Not generating " + methodName + " for this field: WithBy methods cannot be generated for fields starting with $.");
            }
            return;
        }
        for (String altName : JavacHandlerUtil.toAllWithByNames(fieldNode, accessors)) {
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
        JCTree.JCMethodDecl createdWithBy = this.createWithBy(access, fieldNode, fieldNode.getTreeMaker(), source, onMethod, makeAbstract);
        JavacHandlerUtil.recursiveSetGeneratedBy(createdWithBy, source);
        JavacHandlerUtil.injectMethod(typeNode, createdWithBy);
    }

    public JCTree.JCMethodDecl createWithBy(long access, JavacNode field, JavacTreeMaker maker, JavacNode source, List<JCTree.JCAnnotation> onMethod, boolean makeAbstract) {
        AnnotationValues<Accessors> accessors;
        boolean makeFinal;
        String withByName = JavacHandlerUtil.toWithByName(field);
        if (withByName == null) {
            return null;
        }
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)field.get();
        Name methodName = field.toName(withByName);
        JCTree.JCExpression returnType = JavacHandlerUtil.cloneSelfType(field);
        JCTree.JCBlock methodBody = null;
        long flags = JavacHandlerUtil.addFinalIfNeeded(0x200000000L, field.getContext());
        LombokImmutableList<String> functionalInterfaceName = null;
        JavacTreeMaker.TypeTag requiredCast = null;
        JCTree.JCExpression parameterizer = null;
        boolean superExtendsStyle = true;
        String applyMethodName = "apply";
        if (fieldDecl.vartype instanceof JCTree.JCPrimitiveTypeTree) {
            TypeKind kind = ((JCTree.JCPrimitiveTypeTree)fieldDecl.vartype).getPrimitiveTypeKind();
            if (kind == TypeKind.CHAR) {
                requiredCast = Javac.CTC_CHAR;
                functionalInterfaceName = NAME_JUF_INTOP;
            } else if (kind == TypeKind.SHORT) {
                requiredCast = Javac.CTC_SHORT;
                functionalInterfaceName = NAME_JUF_INTOP;
            } else if (kind == TypeKind.BYTE) {
                requiredCast = Javac.CTC_BYTE;
                functionalInterfaceName = NAME_JUF_INTOP;
            } else if (kind == TypeKind.INT) {
                functionalInterfaceName = NAME_JUF_INTOP;
            } else if (kind == TypeKind.LONG) {
                functionalInterfaceName = NAME_JUF_LONGOP;
            } else if (kind == TypeKind.FLOAT) {
                functionalInterfaceName = NAME_JUF_DOUBLEOP;
                requiredCast = Javac.CTC_FLOAT;
            } else if (kind == TypeKind.DOUBLE) {
                functionalInterfaceName = NAME_JUF_DOUBLEOP;
            } else if (kind == TypeKind.BOOLEAN) {
                functionalInterfaceName = NAME_JUF_OP;
                parameterizer = JavacHandlerUtil.genJavaLangTypeRef(field, "Boolean");
                superExtendsStyle = false;
            }
        }
        if (functionalInterfaceName == null) {
            functionalInterfaceName = NAME_JUF_FUNCTION;
            parameterizer = JavacHandlerUtil.cloneType(maker, fieldDecl.vartype, source);
        }
        if (functionalInterfaceName == NAME_JUF_INTOP) {
            applyMethodName = "applyAsInt";
        }
        if (functionalInterfaceName == NAME_JUF_LONGOP) {
            applyMethodName = "applyAsLong";
        }
        if (functionalInterfaceName == NAME_JUF_DOUBLEOP) {
            applyMethodName = "applyAsDouble";
        }
        JCTree.JCExpression varType = JavacHandlerUtil.chainDots(field, functionalInterfaceName);
        if (parameterizer != null && superExtendsStyle) {
            JCTree.JCExpression parameterizer1 = parameterizer;
            JCTree.JCExpression parameterizer2 = JavacHandlerUtil.cloneType(maker, parameterizer, source);
            JCTree.JCWildcard arg1 = maker.Wildcard(maker.TypeBoundKind(BoundKind.SUPER), parameterizer1);
            JCTree.JCWildcard arg2 = maker.Wildcard(maker.TypeBoundKind(BoundKind.EXTENDS), parameterizer2);
            varType = maker.TypeApply(varType, List.of(arg1, arg2));
        }
        if (parameterizer != null && !superExtendsStyle) {
            varType = maker.TypeApply(varType, List.of(parameterizer));
        }
        Name paramName = field.toName("transformer");
        JCTree.JCVariableDecl param = maker.VarDef(maker.Modifiers(flags), paramName, varType, null);
        if (!makeAbstract) {
            ListBuffer<JCTree.JCReturn> statements = new ListBuffer<JCTree.JCReturn>();
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
                    JCTree.JCExpression invoke = maker.Apply(List.<JCTree.JCExpression>nil(), maker.Select(maker.Ident(paramName), field.toName(applyMethodName)), List.of(JavacHandlerUtil.createFieldAccessor(maker, child, HandlerUtil.FieldAccess.ALWAYS_FIELD)));
                    if (requiredCast != null) {
                        invoke = maker.TypeCast(maker.TypeIdent(requiredCast), invoke);
                    }
                    args.append(invoke);
                    continue;
                }
                args.append(JavacHandlerUtil.createFieldAccessor(maker, child, HandlerUtil.FieldAccess.ALWAYS_FIELD));
            }
            JCTree.JCNewClass newClass = maker.NewClass(null, List.<JCTree.JCExpression>nil(), selfType, args.toList(), null);
            JCTree.JCReturn returnStatement = maker.Return(newClass);
            statements.append(returnStatement);
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
        JavacHandlerUtil.createRelevantNonNullAnnotation(source, param);
        JCTree.JCMethodDecl decl = JavacHandlerUtil.recursiveSetGeneratedBy(maker.MethodDef(maker.Modifiers(access, annsOnMethod), methodName, returnType, methodGenericParams, parameters, throwsClauses, methodBody, annotationMethodDefaultValue), source);
        JavacHandlerUtil.copyJavadoc(field, decl, JavacHandlerUtil.CopyJavadoc.WITH_BY);
        JavacHandlerUtil.createRelevantNonNullAnnotation(source, decl);
        return decl;
    }
}
