package lombok.javac.handlers;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.experimental.UtilityClass;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

@HandlerPriority(value=-4096)
public class HandleUtilityClass
extends JavacAnnotationHandler<UtilityClass> {
    @Override
    public void handle(AnnotationValues<UtilityClass> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        HandlerUtil.handleExperimentalFlagUsage(annotationNode, ConfigurationKeys.UTILITY_CLASS_FLAG_USAGE, "@UtilityClass");
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, UtilityClass.class);
        JavacNode typeNode = (JavacNode)annotationNode.up();
        if (!HandleUtilityClass.checkLegality(typeNode, annotationNode)) {
            return;
        }
        this.changeModifiersAndGenerateConstructor((JavacNode)annotationNode.up(), annotationNode);
    }

    private static boolean checkLegality(JavacNode typeNode, JavacNode errorNode) {
        if (!JavacHandlerUtil.isClass(typeNode)) {
            errorNode.addError("@UtilityClass is only supported on a class.");
            return false;
        }
        JavacNode typeWalk = typeNode;
        block4: while (true) {
            typeWalk = (JavacNode)typeWalk.up();
            switch (typeWalk.getKind()) {
                case TYPE: {
                    JCTree.JCClassDecl typeDef = (JCTree.JCClassDecl)typeWalk.get();
                    if ((typeDef.mods.flags & 0x6208L) != 0L) continue block4;
                    if (((JavacNode)typeWalk.up()).getKind() == AST.Kind.COMPILATION_UNIT) {
                        return true;
                    }
                    errorNode.addError("@UtilityClass automatically makes the class static, however, this class cannot be made static.");
                    return false;
                }
                case COMPILATION_UNIT: {
                    return true;
                }
            }
            break;
        }
        errorNode.addError("@UtilityClass cannot be placed on a method local or anonymous inner class, or any class nested in such a class.");
        return false;
    }

    private void changeModifiersAndGenerateConstructor(JavacNode typeNode, JavacNode errorNode) {
        JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl)typeNode.get();
        boolean makeConstructor = true;
        classDecl.mods.flags |= 0x10L;
        boolean markStatic = true;
        if (((JavacNode)typeNode.up()).getKind() == AST.Kind.COMPILATION_UNIT) {
            markStatic = false;
        }
        if (markStatic && ((JavacNode)typeNode.up()).getKind() == AST.Kind.TYPE) {
            JCTree.JCClassDecl typeDecl = (JCTree.JCClassDecl)((JavacNode)typeNode.up()).get();
            if ((typeDecl.mods.flags & 0x2200L) != 0L) {
                markStatic = false;
            }
        }
        if (markStatic) {
            classDecl.mods.flags |= 8L;
        }
        for (JavacNode element : typeNode.down()) {
            if (element.getKind() == AST.Kind.FIELD) {
                JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)element.get();
                fieldDecl.mods.flags |= 8L;
                continue;
            }
            if (element.getKind() == AST.Kind.METHOD) {
                JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl)element.get();
                if (methodDecl.name.contentEquals("<init>") && JavacHandlerUtil.getGeneratedBy(methodDecl) == null && (methodDecl.mods.flags & 0x1000000000L) == 0L) {
                    element.addError("@UtilityClasses cannot have declared constructors.");
                    makeConstructor = false;
                    continue;
                }
                methodDecl.mods.flags |= 8L;
                continue;
            }
            if (element.getKind() != AST.Kind.TYPE) continue;
            JCTree.JCClassDecl innerClassDecl = (JCTree.JCClassDecl)element.get();
            innerClassDecl.mods.flags |= 8L;
            Symbol.ClassSymbol innerClassSymbol = innerClassDecl.sym;
            if (innerClassSymbol == null) continue;
            if (innerClassSymbol.type instanceof Type.ClassType) {
                ((Type.ClassType)innerClassSymbol.type).setEnclosingType(Type.noType);
            }
            innerClassSymbol.erasure_field = null;
        }
        if (makeConstructor) {
            this.createPrivateDefaultConstructor(typeNode);
        }
    }

    private void createPrivateDefaultConstructor(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCTree.JCModifiers mods = maker.Modifiers(2L, List.<JCTree.JCAnnotation>nil());
        Name name = typeNode.toName("<init>");
        JCTree.JCBlock block = maker.Block(0L, this.createThrowStatement(typeNode, maker));
        JCTree.JCMethodDecl methodDef = maker.MethodDef(mods, name, null, List.<JCTree.JCTypeParameter>nil(), List.<JCTree.JCVariableDecl>nil(), List.<JCTree.JCExpression>nil(), block, null);
        JCTree.JCMethodDecl constructor = JavacHandlerUtil.recursiveSetGeneratedBy(methodDef, typeNode);
        JavacHandlerUtil.injectMethod(typeNode, constructor);
    }

    private List<JCTree.JCStatement> createThrowStatement(JavacNode typeNode, JavacTreeMaker maker) {
        JCTree.JCExpression exceptionType = JavacHandlerUtil.genJavaLangTypeRef(typeNode, "UnsupportedOperationException");
        List<JCTree.JCExpression> jceBlank = List.nil();
        JCTree.JCLiteral message = maker.Literal("This is a utility class and cannot be instantiated");
        JCTree.JCNewClass exceptionInstance = maker.NewClass(null, jceBlank, exceptionType, List.of(message), null);
        JCTree.JCThrow throwStatement = maker.Throw(exceptionInstance);
        return List.of(throwStatement);
    }
}
