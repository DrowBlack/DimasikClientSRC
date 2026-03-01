package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HandlerUtil;
import lombok.delombok.LombokOptionsFactory;
import lombok.experimental.StandardException;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleStandardException
extends JavacAnnotationHandler<StandardException> {
    @Override
    public void handle(AnnotationValues<StandardException> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.STANDARD_EXCEPTION_FLAG_USAGE, "@StandardException");
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, StandardException.class);
        JavacHandlerUtil.deleteImportFromCompilationUnit(annotationNode, "lombok.AccessLevel");
        JavacNode typeNode = (JavacNode)annotationNode.up();
        if (!JavacHandlerUtil.isClass(typeNode)) {
            annotationNode.addError("@StandardException is only supported on a class");
            return;
        }
        JCTree extending = Javac.getExtendsClause((JCTree.JCClassDecl)typeNode.get());
        if (extending == null) {
            annotationNode.addError("@StandardException requires that you extend a Throwable type");
            return;
        }
        AccessLevel access = annotation.getInstance().access();
        if (access == null) {
            access = AccessLevel.PUBLIC;
        }
        if (access == AccessLevel.NONE) {
            annotationNode.addError("AccessLevel.NONE is not valid here");
            access = AccessLevel.PUBLIC;
        }
        this.generateNoArgsConstructor(typeNode, access, annotationNode);
        this.generateMsgOnlyConstructor(typeNode, access, annotationNode);
        this.generateCauseOnlyConstructor(typeNode, access, annotationNode);
        this.generateFullConstructor(typeNode, access, annotationNode);
    }

    private void generateNoArgsConstructor(JavacNode typeNode, AccessLevel level, JavacNode source) {
        if (HandleStandardException.hasConstructor(typeNode, new Class[0]) != JavacHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            return;
        }
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCTree.JCTypeCast stringArgument = maker.TypeCast(JavacHandlerUtil.genJavaLangTypeRef(typeNode, "String"), maker.Literal(Javac.CTC_BOT, null));
        JCTree.JCTypeCast throwableArgument = maker.TypeCast(JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Throwable"), maker.Literal(Javac.CTC_BOT, null));
        List<JCTree.JCExpression> args = List.of(stringArgument, throwableArgument);
        JCTree.JCExpressionStatement thisCall = maker.Exec(maker.Apply(List.<JCTree.JCExpression>nil(), maker.Ident(typeNode.toName("this")), args));
        JCTree.JCMethodDecl constr = HandleStandardException.createConstructor(level, typeNode, false, false, source, List.of(thisCall));
        JavacHandlerUtil.injectMethod(typeNode, constr);
    }

    private void generateMsgOnlyConstructor(JavacNode typeNode, AccessLevel level, JavacNode source) {
        if (HandleStandardException.hasConstructor(typeNode, String.class) != JavacHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            return;
        }
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCTree.JCIdent stringArgument = maker.Ident(typeNode.toName("message"));
        JCTree.JCTypeCast throwableArgument = maker.TypeCast(JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Throwable"), maker.Literal(Javac.CTC_BOT, null));
        List<JCTree.JCExpression> args = List.of(stringArgument, throwableArgument);
        JCTree.JCExpressionStatement thisCall = maker.Exec(maker.Apply(List.<JCTree.JCExpression>nil(), maker.Ident(typeNode.toName("this")), args));
        JCTree.JCMethodDecl constr = HandleStandardException.createConstructor(level, typeNode, true, false, source, List.of(thisCall));
        JavacHandlerUtil.injectMethod(typeNode, constr);
    }

    private void generateCauseOnlyConstructor(JavacNode typeNode, AccessLevel level, JavacNode source) {
        if (HandleStandardException.hasConstructor(typeNode, Throwable.class) != JavacHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            return;
        }
        JavacTreeMaker maker = typeNode.getTreeMaker();
        Name causeName = typeNode.toName("cause");
        JCTree.JCMethodInvocation causeDotGetMessage = maker.Apply(List.<JCTree.JCExpression>nil(), maker.Select(maker.Ident(causeName), typeNode.toName("getMessage")), List.<JCTree.JCExpression>nil());
        JCTree.JCConditional msgExpression = maker.Conditional(maker.Binary(Javac.CTC_NOT_EQUAL, maker.Ident(causeName), maker.Literal(Javac.CTC_BOT, null)), causeDotGetMessage, maker.Literal(Javac.CTC_BOT, null));
        List<JCTree.JCExpression> args = List.of(msgExpression, maker.Ident(causeName));
        JCTree.JCExpressionStatement thisCall = maker.Exec(maker.Apply(List.<JCTree.JCExpression>nil(), maker.Ident(typeNode.toName("this")), args));
        JCTree.JCMethodDecl constr = HandleStandardException.createConstructor(level, typeNode, false, true, source, List.of(thisCall));
        JavacHandlerUtil.injectMethod(typeNode, constr);
    }

    private void generateFullConstructor(JavacNode typeNode, AccessLevel level, JavacNode source) {
        if (HandleStandardException.hasConstructor(typeNode, String.class, Throwable.class) != JavacHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            return;
        }
        JavacTreeMaker maker = typeNode.getTreeMaker();
        Name causeName = typeNode.toName("cause");
        Name superName = typeNode.toName("super");
        List<JCTree.JCExpression> args = List.of(maker.Ident(typeNode.toName("message")));
        JCTree.JCExpressionStatement superCall = maker.Exec(maker.Apply(List.<JCTree.JCExpression>nil(), maker.Ident(superName), args));
        JCTree.JCBinary causeNotNull = maker.Binary(Javac.CTC_NOT_EQUAL, maker.Ident(causeName), maker.Literal(Javac.CTC_BOT, null));
        JCTree.JCExpressionStatement initCauseCall = maker.Exec(maker.Apply(List.<JCTree.JCExpression>nil(), maker.Select(maker.Ident(superName), typeNode.toName("initCause")), List.of(maker.Ident(causeName))));
        JCTree.JCIf initCause = maker.If(causeNotNull, initCauseCall, null);
        JCTree.JCMethodDecl constr = HandleStandardException.createConstructor(level, typeNode, true, true, source, List.of(superCall, initCause));
        JavacHandlerUtil.injectMethod(typeNode, constr);
    }

    private static JavacHandlerUtil.MemberExistsResult hasConstructor(JavacNode node, Class<?> ... paramTypes) {
        if ((node = JavacHandlerUtil.upToTypeNode(node)) != null && node.get() instanceof JCTree.JCClassDecl) {
            for (JCTree def : ((JCTree.JCClassDecl)node.get()).defs) {
                if (!(def instanceof JCTree.JCMethodDecl)) continue;
                JCTree.JCMethodDecl md = (JCTree.JCMethodDecl)def;
                if (!md.name.contentEquals("<init>") || (md.mods.flags & 0x1000000000L) != 0L || !HandleStandardException.paramsMatch(node, md.params, paramTypes)) continue;
                return JavacHandlerUtil.getGeneratedBy(def) == null ? JavacHandlerUtil.MemberExistsResult.EXISTS_BY_USER : JavacHandlerUtil.MemberExistsResult.EXISTS_BY_LOMBOK;
            }
        }
        return JavacHandlerUtil.MemberExistsResult.NOT_EXISTS;
    }

    private static boolean paramsMatch(JavacNode node, List<JCTree.JCVariableDecl> a, Class<?>[] b) {
        if (a == null) {
            return b == null || b.length == 0;
        }
        if (b == null) {
            return a.size() == 0;
        }
        if (a.size() != b.length) {
            return false;
        }
        int i = 0;
        while (i < a.size()) {
            JCTree.JCVariableDecl param = a.get(i);
            Class<?> c = b[i];
            if (!JavacHandlerUtil.typeMatches(c, node, (JCTree)param.vartype)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    private static void addConstructorProperties(JCTree.JCModifiers mods, JavacNode node, boolean msgParam, boolean causeParam) {
        if (!msgParam && !causeParam) {
            return;
        }
        JavacTreeMaker maker = node.getTreeMaker();
        JCTree.JCExpression constructorPropertiesType = JavacHandlerUtil.chainDots(node, "java", "beans", "ConstructorProperties");
        ListBuffer<JCTree.JCLiteral> fieldNames = new ListBuffer<JCTree.JCLiteral>();
        if (msgParam) {
            fieldNames.append(maker.Literal("message"));
        }
        if (causeParam) {
            fieldNames.append(maker.Literal("cause"));
        }
        JCTree.JCNewArray fieldNamesArray = maker.NewArray(null, List.<JCTree.JCExpression>nil(), fieldNames.toList());
        JCTree.JCAnnotation annotation = maker.Annotation(constructorPropertiesType, List.of(fieldNamesArray));
        mods.annotations = mods.annotations.append(annotation);
    }

    private static JCTree.JCMethodDecl createConstructor(AccessLevel level, JavacNode typeNode, boolean msgParam, boolean causeParam, JavacNode source, List<JCTree.JCStatement> statements) {
        JCTree.JCVariableDecl param;
        JCTree.JCExpression pType;
        long flags;
        Name fieldName;
        Boolean v;
        JavacTreeMaker maker = typeNode.getTreeMaker();
        boolean addConstructorProperties = !msgParam && !causeParam || HandleStandardException.isLocalType(typeNode) || !LombokOptionsFactory.getDelombokOptions(typeNode.getContext()).getFormatPreferences().generateConstructorProperties() ? false : ((v = typeNode.getAst().readConfiguration(ConfigurationKeys.ANY_CONSTRUCTOR_ADD_CONSTRUCTOR_PROPERTIES)) != null ? v.booleanValue() : Boolean.FALSE.equals(typeNode.getAst().readConfiguration(ConfigurationKeys.ANY_CONSTRUCTOR_SUPPRESS_CONSTRUCTOR_PROPERTIES)));
        ListBuffer<JCTree.JCVariableDecl> params = new ListBuffer<JCTree.JCVariableDecl>();
        if (msgParam) {
            fieldName = typeNode.toName("message");
            flags = JavacHandlerUtil.addFinalIfNeeded(0x200000000L, typeNode.getContext());
            pType = JavacHandlerUtil.genJavaLangTypeRef(typeNode, "String");
            param = maker.VarDef(maker.Modifiers(flags), fieldName, pType, null);
            params.append(param);
        }
        if (causeParam) {
            fieldName = typeNode.toName("cause");
            flags = JavacHandlerUtil.addFinalIfNeeded(0x200000000L, typeNode.getContext());
            pType = JavacHandlerUtil.genJavaLangTypeRef(typeNode, "Throwable");
            param = maker.VarDef(maker.Modifiers(flags), fieldName, pType, null);
            params.append(param);
        }
        JCTree.JCModifiers mods = maker.Modifiers(JavacHandlerUtil.toJavacModifier(level), List.<JCTree.JCAnnotation>nil());
        if (addConstructorProperties) {
            HandleStandardException.addConstructorProperties(mods, typeNode, msgParam, causeParam);
        }
        return JavacHandlerUtil.recursiveSetGeneratedBy(maker.MethodDef(mods, typeNode.toName("<init>"), null, List.<JCTree.JCTypeParameter>nil(), params.toList(), List.<JCTree.JCExpression>nil(), maker.Block(0L, statements), null), source);
    }

    public static boolean isLocalType(JavacNode type) {
        AST.Kind kind = ((JavacNode)type.up()).getKind();
        if (kind == AST.Kind.COMPILATION_UNIT) {
            return false;
        }
        if (kind == AST.Kind.TYPE) {
            return HandleStandardException.isLocalType((JavacNode)type.up());
        }
        return true;
    }
}
