package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.Getter;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HandlerUtil;
import lombok.delombok.LombokOptionsFactory;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacAugments;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleGetter
extends JavacAnnotationHandler<Getter> {
    private static final String GETTER_NODE_NOT_SUPPORTED_ERR = "@Getter is only supported on a class, an enum, or a field.";
    private static final String AR = "java.util.concurrent.atomic.AtomicReference";
    private static final List<JCTree.JCExpression> NIL_EXPRESSION = List.nil();
    public static final Map<JavacTreeMaker.TypeTag, String> TYPE_MAP;

    static {
        HashMap<JavacTreeMaker.TypeTag, String> m = new HashMap<JavacTreeMaker.TypeTag, String>();
        m.put(Javac.CTC_INT, "Integer");
        m.put(Javac.CTC_DOUBLE, "Double");
        m.put(Javac.CTC_FLOAT, "Float");
        m.put(Javac.CTC_SHORT, "Short");
        m.put(Javac.CTC_BYTE, "Byte");
        m.put(Javac.CTC_LONG, "Long");
        m.put(Javac.CTC_BOOLEAN, "Boolean");
        m.put(Javac.CTC_CHAR, "Character");
        TYPE_MAP = Collections.unmodifiableMap(m);
    }

    public void generateGetterForType(JavacNode typeNode, JavacNode errorNode, AccessLevel level, boolean checkForTypeLevelGetter, List<JCTree.JCAnnotation> onMethod) {
        if (checkForTypeLevelGetter && JavacHandlerUtil.hasAnnotation(Getter.class, typeNode)) {
            return;
        }
        if (!JavacHandlerUtil.isClassOrEnum(typeNode)) {
            errorNode.addError(GETTER_NODE_NOT_SUPPORTED_ERR);
            return;
        }
        for (JavacNode field : typeNode.down()) {
            if (!HandleGetter.fieldQualifiesForGetterGeneration(field)) continue;
            this.generateGetterForField(field, (JCDiagnostic.DiagnosticPosition)errorNode.get(), level, false, onMethod);
        }
    }

    public static boolean fieldQualifiesForGetterGeneration(JavacNode field) {
        if (field.getKind() != AST.Kind.FIELD) {
            return false;
        }
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)field.get();
        if (fieldDecl.name.toString().startsWith("$")) {
            return false;
        }
        return (fieldDecl.mods.flags & 8L) == 0L;
    }

    public void generateGetterForField(JavacNode fieldNode, JCDiagnostic.DiagnosticPosition pos, AccessLevel level, boolean lazy, List<JCTree.JCAnnotation> onMethod) {
        if (JavacHandlerUtil.hasAnnotation(Getter.class, fieldNode)) {
            return;
        }
        this.createGetterForField(level, fieldNode, fieldNode, false, lazy, onMethod);
    }

    @Override
    public void handle(AnnotationValues<Getter> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.GETTER_FLAG_USAGE, "@Getter");
        Collection<JavacNode> fields = annotationNode.upFromAnnotationToFields();
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Getter.class);
        JavacHandlerUtil.deleteImportFromCompilationUnit(annotationNode, "lombok.AccessLevel");
        JavacNode node = (JavacNode)annotationNode.up();
        Getter annotationInstance = annotation.getInstance();
        AccessLevel level = annotationInstance.value();
        boolean lazy = annotationInstance.lazy();
        if (lazy) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.GETTER_LAZY_FLAG_USAGE, "@Getter(lazy=true)");
        }
        if (level == AccessLevel.NONE) {
            if (lazy) {
                annotationNode.addWarning("'lazy' does not work with AccessLevel.NONE.");
            }
            return;
        }
        if (node == null) {
            return;
        }
        List<JCTree.JCAnnotation> onMethod = JavacHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onMethod", "@Getter(onMethod", annotationNode);
        if (!onMethod.isEmpty()) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.ON_X_FLAG_USAGE, "@Getter(onMethod=...)");
        }
        switch (node.getKind()) {
            case FIELD: {
                this.createGetterForFields(level, fields, annotationNode, true, lazy, onMethod);
                break;
            }
            case TYPE: {
                if (lazy) {
                    annotationNode.addError("'lazy' is not supported for @Getter on a type.");
                }
                this.generateGetterForType(node, annotationNode, level, false, onMethod);
            }
        }
    }

    public void createGetterForFields(AccessLevel level, Collection<JavacNode> fieldNodes, JavacNode errorNode, boolean whineIfExists, boolean lazy, List<JCTree.JCAnnotation> onMethod) {
        for (JavacNode fieldNode : fieldNodes) {
            this.createGetterForField(level, fieldNode, errorNode, whineIfExists, lazy, onMethod);
        }
    }

    public void createGetterForField(AccessLevel level, JavacNode fieldNode, JavacNode source, boolean whineIfExists, boolean lazy, List<JCTree.JCAnnotation> onMethod) {
        AnnotationValues<Accessors> accessors;
        String methodName;
        if (fieldNode.getKind() != AST.Kind.FIELD || fieldNode.isEnumMember()) {
            source.addError(GETTER_NODE_NOT_SUPPORTED_ERR);
            return;
        }
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)fieldNode.get();
        if (lazy) {
            if ((fieldDecl.mods.flags & 2L) == 0L || (fieldDecl.mods.flags & 0x10L) == 0L) {
                source.addError("'lazy' requires the field to be private and final.");
                return;
            }
            if ((fieldDecl.mods.flags & 0x80L) != 0L) {
                source.addError("'lazy' is not supported on transient fields.");
                return;
            }
            if (fieldDecl.init == null) {
                source.addError("'lazy' requires field initialization.");
                return;
            }
        }
        if ((methodName = JavacHandlerUtil.toGetterName(fieldNode, accessors = JavacHandlerUtil.getAccessorsForField(fieldNode))) == null) {
            source.addWarning("Not generating getter for this field: It does not fit your @Accessors prefix list.");
            return;
        }
        for (String altName : JavacHandlerUtil.toAllGetterNames(fieldNode, accessors)) {
            switch (JavacHandlerUtil.methodExists(altName, fieldNode, false, 0)) {
                case EXISTS_BY_LOMBOK: {
                    return;
                }
                case EXISTS_BY_USER: {
                    if (whineIfExists) {
                        String altNameExpl = "";
                        if (!altName.equals(methodName)) {
                            altNameExpl = String.format(" (%s)", altName);
                        }
                        source.addWarning(String.format("Not generating %s(): A method with that name already exists%s", methodName, altNameExpl));
                    }
                    return;
                }
            }
        }
        long access = (long)JavacHandlerUtil.toJavacModifier(level) | fieldDecl.mods.flags & 8L;
        JavacHandlerUtil.injectMethod((JavacNode)fieldNode.up(), source, this.createGetter(access, fieldNode, fieldNode.getTreeMaker(), source, lazy, onMethod));
    }

    public JCTree.JCMethodDecl createGetter(long access, JavacNode field, JavacTreeMaker treeMaker, JavacNode source, boolean lazy, List<JCTree.JCAnnotation> onMethod) {
        List<JCTree.JCStatement> statements;
        JCTree.JCVariableDecl fieldNode = (JCTree.JCVariableDecl)field.get();
        JCTree.JCExpression methodType = this.copyType(treeMaker, fieldNode, source);
        AnnotationValues<Accessors> accessors = JavacHandlerUtil.getAccessorsForField(field);
        Name methodName = field.toName(JavacHandlerUtil.toGetterName(field, accessors));
        boolean makeFinal = JavacHandlerUtil.shouldMakeFinal(field, accessors);
        boolean addSuppressWarningsUnchecked = false;
        if (lazy && !JavacHandlerUtil.inNetbeansEditor(field)) {
            JavacAugments.JCTree_keepPosition.set(fieldNode.init, true);
            statements = this.createLazyGetterBody(treeMaker, field, source);
            addSuppressWarningsUnchecked = LombokOptionsFactory.getDelombokOptions(field.getContext()).getFormatPreferences().generateSuppressWarnings();
        } else {
            statements = this.createSimpleGetterBody(treeMaker, field);
        }
        JCTree.JCBlock methodBody = treeMaker.Block(0L, statements);
        List<JCTree.JCTypeParameter> methodGenericParams = List.nil();
        List<JCTree.JCVariableDecl> parameters = List.nil();
        List<JCTree.JCExpression> throwsClauses = List.nil();
        JCTree.JCExpression annotationMethodDefaultValue = null;
        List<JCTree.JCAnnotation> copyableAnnotations = JavacHandlerUtil.findCopyableAnnotations(field);
        List<JCTree.JCAnnotation> delegates = HandleGetter.findDelegatesAndRemoveFromField(field);
        List<JCTree.JCAnnotation> annsOnMethod = JavacHandlerUtil.copyAnnotations(onMethod).appendList(copyableAnnotations);
        if (field.isFinal()) {
            if (JavacHandlerUtil.getCheckerFrameworkVersion(field).generatePure()) {
                annsOnMethod = annsOnMethod.prepend(treeMaker.Annotation(JavacHandlerUtil.genTypeRef(field, "org.checkerframework.dataflow.qual.Pure"), List.<JCTree.JCExpression>nil()));
            }
        } else if (JavacHandlerUtil.getCheckerFrameworkVersion(field).generateSideEffectFree()) {
            annsOnMethod = annsOnMethod.prepend(treeMaker.Annotation(JavacHandlerUtil.genTypeRef(field, "org.checkerframework.dataflow.qual.SideEffectFree"), List.<JCTree.JCExpression>nil()));
        }
        if (JavacHandlerUtil.isFieldDeprecated(field)) {
            annsOnMethod = annsOnMethod.prepend(treeMaker.Annotation(JavacHandlerUtil.genJavaLangTypeRef(field, "Deprecated"), List.<JCTree.JCExpression>nil()));
        }
        if (makeFinal) {
            access |= 0x10L;
        }
        JCTree.JCMethodDecl decl = JavacHandlerUtil.recursiveSetGeneratedBy(treeMaker.MethodDef(treeMaker.Modifiers(access, annsOnMethod), methodName, methodType, methodGenericParams, parameters, throwsClauses, methodBody, annotationMethodDefaultValue), source);
        decl.mods.annotations = decl.mods.annotations.appendList(delegates);
        if (addSuppressWarningsUnchecked) {
            ListBuffer<JCTree.JCLiteral> suppressions = new ListBuffer<JCTree.JCLiteral>();
            if (!Boolean.FALSE.equals(field.getAst().readConfiguration(ConfigurationKeys.ADD_SUPPRESSWARNINGS_ANNOTATIONS))) {
                suppressions.append(treeMaker.Literal("all"));
            }
            suppressions.append(treeMaker.Literal("unchecked"));
            JavacHandlerUtil.addAnnotation(decl.mods, field, source, "java.lang.SuppressWarnings", treeMaker.NewArray(null, List.<JCTree.JCExpression>nil(), suppressions.toList()));
        }
        JavacHandlerUtil.copyJavadoc(field, decl, JavacHandlerUtil.CopyJavadoc.GETTER);
        return decl;
    }

    public static List<JCTree.JCAnnotation> findDelegatesAndRemoveFromField(JavacNode field) {
        JCTree.JCVariableDecl fieldNode = (JCTree.JCVariableDecl)field.get();
        List<JCTree.JCAnnotation> delegates = List.nil();
        for (JCTree.JCAnnotation annotation : fieldNode.mods.annotations) {
            if (!JavacHandlerUtil.typeMatches(Delegate.class, field, annotation.annotationType)) continue;
            delegates = delegates.append(annotation);
        }
        if (!delegates.isEmpty()) {
            ListBuffer<JCTree.JCAnnotation> withoutDelegates = new ListBuffer<JCTree.JCAnnotation>();
            for (JCTree.JCAnnotation annotation : fieldNode.mods.annotations) {
                if (delegates.contains(annotation)) continue;
                withoutDelegates.append(annotation);
            }
            fieldNode.mods.annotations = withoutDelegates.toList();
            field.rebuild();
        }
        return delegates;
    }

    public List<JCTree.JCStatement> createSimpleGetterBody(JavacTreeMaker treeMaker, JavacNode field) {
        return List.of(treeMaker.Return(JavacHandlerUtil.createFieldAccessor(treeMaker, field, HandlerUtil.FieldAccess.ALWAYS_FIELD)));
    }

    public List<JCTree.JCStatement> createLazyGetterBody(JavacTreeMaker maker, JavacNode fieldNode, JavacNode source) {
        JCTree.JCStatement statement;
        String boxed;
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<JCTree.JCStatement>();
        JCTree.JCVariableDecl field = (JCTree.JCVariableDecl)fieldNode.get();
        JCTree.JCExpression copyOfRawFieldType = this.copyType(maker, field, source);
        JCTree.JCExpression copyOfBoxedFieldType = null;
        field.type = null;
        boolean isPrimitive = false;
        if (field.vartype instanceof JCTree.JCPrimitiveTypeTree && (boxed = TYPE_MAP.get(JavacTreeMaker.TypeTag.typeTag(field.vartype))) != null) {
            isPrimitive = true;
            field.vartype = JavacHandlerUtil.genJavaLangTypeRef(fieldNode, boxed);
            copyOfBoxedFieldType = JavacHandlerUtil.genJavaLangTypeRef(fieldNode, boxed);
        }
        if (copyOfBoxedFieldType == null) {
            copyOfBoxedFieldType = this.copyType(maker, field, source);
        }
        Name valueName = fieldNode.toName("$value");
        Name actualValueName = fieldNode.toName("actualValue");
        JCTree.JCExpression valueVarType = JavacHandlerUtil.genJavaLangTypeRef(fieldNode, "Object");
        statements.append(maker.VarDef(maker.Modifiers(0L), valueName, valueVarType, this.callGet(fieldNode, JavacHandlerUtil.createFieldAccessor(maker, fieldNode, HandlerUtil.FieldAccess.ALWAYS_FIELD))));
        ListBuffer<JCTree.JCStatement> synchronizedStatements = new ListBuffer<JCTree.JCStatement>();
        JCTree.JCExpressionStatement newAssign = maker.Exec(maker.Assign(maker.Ident(valueName), this.callGet(fieldNode, JavacHandlerUtil.createFieldAccessor(maker, fieldNode, HandlerUtil.FieldAccess.ALWAYS_FIELD))));
        synchronizedStatements.append(newAssign);
        ListBuffer<JCTree.JCStatement> innerIfStatements = new ListBuffer<JCTree.JCStatement>();
        innerIfStatements.append(maker.VarDef(maker.Modifiers(16L), actualValueName, copyOfRawFieldType, field.init));
        if (isPrimitive) {
            statement = maker.Exec(maker.Assign(maker.Ident(valueName), maker.Ident(actualValueName)));
            innerIfStatements.append(statement);
        }
        if (!isPrimitive) {
            JCTree.JCBinary actualValueIsNull = maker.Binary(Javac.CTC_EQUAL, maker.Ident(actualValueName), maker.Literal(Javac.CTC_BOT, null));
            JCTree.JCExpression thisDotFieldName = JavacHandlerUtil.createFieldAccessor(maker, fieldNode, HandlerUtil.FieldAccess.ALWAYS_FIELD);
            JCTree.JCConditional ternary = maker.Conditional(actualValueIsNull, thisDotFieldName, maker.Ident(actualValueName));
            JCTree.JCExpressionStatement statement2 = maker.Exec(maker.Assign(maker.Ident(valueName), ternary));
            innerIfStatements.append(statement2);
        }
        statement = this.callSet(fieldNode, JavacHandlerUtil.createFieldAccessor(maker, fieldNode, HandlerUtil.FieldAccess.ALWAYS_FIELD), maker.Ident(valueName));
        innerIfStatements.append(statement);
        JCTree.JCBinary isNull = maker.Binary(Javac.CTC_EQUAL, maker.Ident(valueName), maker.Literal(Javac.CTC_BOT, null));
        JCTree.JCIf ifStatement = maker.If(isNull, maker.Block(0L, innerIfStatements.toList()), null);
        synchronizedStatements.append(ifStatement);
        JCTree.JCSynchronized synchronizedStatement = maker.Synchronized(JavacHandlerUtil.createFieldAccessor(maker, fieldNode, HandlerUtil.FieldAccess.ALWAYS_FIELD), maker.Block(0L, synchronizedStatements.toList()));
        JCTree.JCBinary isNull2 = maker.Binary(Javac.CTC_EQUAL, maker.Ident(valueName), maker.Literal(Javac.CTC_BOT, null));
        JCTree.JCIf ifStatement2 = maker.If(isNull2, maker.Block(0L, List.of(synchronizedStatement)), null);
        statements.append(ifStatement2);
        if (isPrimitive) {
            statements.append(maker.Return(maker.TypeCast(copyOfBoxedFieldType, maker.Ident(valueName))));
        }
        if (!isPrimitive) {
            JCTree.JCBinary valueEqualsSelf = maker.Binary(Javac.CTC_EQUAL, maker.Ident(valueName), JavacHandlerUtil.createFieldAccessor(maker, fieldNode, HandlerUtil.FieldAccess.ALWAYS_FIELD));
            JCTree.JCConditional ternary = maker.Conditional(valueEqualsSelf, maker.Literal(Javac.CTC_BOT, null), maker.Ident(valueName));
            JCTree.JCTypeCast typeCast = maker.TypeCast(copyOfBoxedFieldType, maker.Parens(ternary));
            statements.append(maker.Return(typeCast));
        }
        field.vartype = JavacHandlerUtil.recursiveSetGeneratedBy(maker.TypeApply(JavacHandlerUtil.chainDotsString(fieldNode, AR), List.of(JavacHandlerUtil.genJavaLangTypeRef(fieldNode, "Object"))), source);
        field.init = JavacHandlerUtil.recursiveSetGeneratedBy(maker.NewClass(null, NIL_EXPRESSION, this.copyType(maker, field, source), NIL_EXPRESSION, null), source);
        return statements.toList();
    }

    public JCTree.JCMethodInvocation callGet(JavacNode source, JCTree.JCExpression receiver) {
        JavacTreeMaker maker = source.getTreeMaker();
        return maker.Apply(NIL_EXPRESSION, maker.Select(receiver, source.toName("get")), NIL_EXPRESSION);
    }

    public JCTree.JCStatement callSet(JavacNode source, JCTree.JCExpression receiver, JCTree.JCExpression value) {
        JavacTreeMaker maker = source.getTreeMaker();
        return maker.Exec(maker.Apply(NIL_EXPRESSION, maker.Select(receiver, source.toName("set")), List.of(value)));
    }

    public JCTree.JCExpression copyType(JavacTreeMaker treeMaker, JCTree.JCVariableDecl fieldNode, JavacNode source) {
        return fieldNode.type != null ? treeMaker.Type(fieldNode.type) : JavacHandlerUtil.cloneType(treeMaker, fieldNode.vartype, source);
    }
}
