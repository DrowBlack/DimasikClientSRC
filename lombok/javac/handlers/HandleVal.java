package lombok.javac.handlers;

import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import java.lang.reflect.Field;
import lombok.ConfigurationKeys;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.javac.JavacASTAdapter;
import lombok.javac.JavacNode;
import lombok.javac.JavacResolution;
import lombok.javac.ResolutionResetNeeded;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.permit.Permit;
import lombok.val;
import lombok.var;

@HandlerPriority(value=65636)
@ResolutionResetNeeded
public class HandleVal
extends JavacASTAdapter {
    private static boolean eq(String typeTreeToString, String key) {
        return typeTreeToString.equals(key) || typeTreeToString.equals("lombok." + key) || typeTreeToString.equals("lombok.experimental." + key);
    }

    @Override
    public void endVisitLocal(JavacNode localNode, JCTree.JCVariableDecl local) {
        JCTree.JCEnhancedForLoop efl;
        JCTree var2;
        JCTree.JCExpression typeTree = local.vartype;
        if (typeTree == null) {
            return;
        }
        String typeTreeToString = typeTree.toString();
        JavacNode typeNode = (JavacNode)localNode.getNodeFor(typeTree);
        if (!HandleVal.eq(typeTreeToString, "val") && !HandleVal.eq(typeTreeToString, "var")) {
            return;
        }
        boolean isVal = JavacHandlerUtil.typeMatches(val.class, localNode, (JCTree)typeTree);
        boolean isVar = JavacHandlerUtil.typeMatches(var.class, localNode, (JCTree)typeTree);
        if (!isVal && !isVar) {
            return;
        }
        if (isVal) {
            HandlerUtil.handleFlagUsage(localNode, ConfigurationKeys.VAL_FLAG_USAGE, "val");
        }
        if (isVar) {
            HandlerUtil.handleFlagUsage(localNode, ConfigurationKeys.VAR_FLAG_USAGE, "var");
        }
        JCTree parentRaw = (JCTree)((JavacNode)localNode.directUp()).get();
        if (isVal && parentRaw instanceof JCTree.JCForLoop) {
            localNode.addError("'val' is not allowed in old-style for loops");
            return;
        }
        if (parentRaw instanceof JCTree.JCForLoop && ((List)((JCTree.JCForLoop)parentRaw).getInitializer()).size() > 1) {
            localNode.addError("'var' is not allowed in old-style for loops if there is more than 1 initializer");
            return;
        }
        JCTree.JCExpression rhsOfEnhancedForLoop = null;
        if (local.init == null && parentRaw instanceof JCTree.JCEnhancedForLoop && (var2 = EnhancedForLoopReflect.getVarOrRecordPattern(efl = (JCTree.JCEnhancedForLoop)parentRaw)) == local) {
            rhsOfEnhancedForLoop = efl.expr;
        }
        String annotation = typeTreeToString;
        if (rhsOfEnhancedForLoop == null && local.init == null) {
            localNode.addError("'" + annotation + "' on a local variable requires an initializer expression");
            return;
        }
        if (local.init instanceof JCTree.JCNewArray && ((JCTree.JCNewArray)local.init).elemtype == null) {
            localNode.addError("'" + annotation + "' is not compatible with array initializer expressions. Use the full form (new int[] { ... } instead of just { ... })");
            return;
        }
        if (localNode.shouldDeleteLombokAnnotations()) {
            JavacHandlerUtil.deleteImportFromCompilationUnit(localNode, val.class.getName());
            JavacHandlerUtil.deleteImportFromCompilationUnit(localNode, lombok.experimental.var.class.getName());
            JavacHandlerUtil.deleteImportFromCompilationUnit(localNode, var.class.getName());
        }
        if (isVal) {
            local.mods.flags |= 0x10L;
        }
        if (!localNode.shouldDeleteLombokAnnotations()) {
            JCTree.JCAnnotation valAnnotation = JavacHandlerUtil.recursiveSetGeneratedBy(localNode.getTreeMaker().Annotation(local.vartype, List.<JCTree.JCExpression>nil()), typeNode);
            List<JCTree.JCAnnotation> list = local.mods.annotations = local.mods.annotations == null ? List.of(valAnnotation) : local.mods.annotations.append(valAnnotation);
        }
        if (localNode.getSourceVersion() >= 10) {
            local.vartype = null;
            localNode.getAst().setChanged();
            return;
        }
        local.vartype = JavacResolution.platformHasTargetTyping() ? localNode.getAst().getTreeMaker().Ident(localNode.getAst().toName("___Lombok_VAL_Attrib__")) : JavacResolution.createJavaLangObject(localNode.getAst());
        try {
            try {
                Type type;
                JavacResolution resolver;
                if (rhsOfEnhancedForLoop == null) {
                    if (local.init.type == null) {
                        if (isVar && local.init instanceof JCTree.JCLiteral && ((JCTree.JCLiteral)local.init).value == null) {
                            localNode.addError("variable initializer is 'null'");
                        }
                        resolver = new JavacResolution(localNode.getContext());
                        try {
                            type = ((JCTree.JCExpression)resolver.resolveMethodMember((JavacNode)localNode).get((Object)local.init)).type;
                        }
                        catch (RuntimeException e) {
                            System.err.println("Exception while resolving: " + localNode + "(" + localNode.getFileName() + ")");
                            throw e;
                        }
                    } else {
                        type = local.init.type;
                        if (type.isErroneous()) {
                            try {
                                resolver = new JavacResolution(localNode.getContext());
                                local.type = Symtab.instance((Context)localNode.getContext()).unknownType;
                                type = ((JCTree.JCExpression)resolver.resolveMethodMember((JavacNode)localNode).get((Object)local.init)).type;
                            }
                            catch (RuntimeException e) {
                                System.err.println("Exception while resolving: " + localNode + "(" + localNode.getFileName() + ")");
                                throw e;
                            }
                        }
                    }
                } else if (rhsOfEnhancedForLoop.type == null) {
                    resolver = new JavacResolution(localNode.getContext());
                    type = ((JCTree.JCExpression)resolver.resolveMethodMember((JavacNode)((JavacNode)localNode.directUp())).get((Object)rhsOfEnhancedForLoop)).type;
                } else {
                    type = rhsOfEnhancedForLoop.type;
                }
                try {
                    Type componentType;
                    JCTree.JCExpression replacement = rhsOfEnhancedForLoop != null ? ((componentType = JavacResolution.ifTypeIsIterableToComponent(type, localNode.getAst())) == null ? JavacResolution.createJavaLangObject(localNode.getAst()) : JavacResolution.typeToJCTree(componentType, localNode.getAst(), false)) : JavacResolution.typeToJCTree(type, localNode.getAst(), false);
                    local.vartype = replacement != null ? replacement : JavacResolution.createJavaLangObject(localNode.getAst());
                    localNode.getAst().setChanged();
                }
                catch (JavacResolution.TypeNotConvertibleException e) {
                    localNode.addError("Cannot use '" + annotation + "' here because initializer expression does not have a representable type: " + e.getMessage());
                    local.vartype = JavacResolution.createJavaLangObject(localNode.getAst());
                }
            }
            catch (RuntimeException e) {
                local.vartype = JavacResolution.createJavaLangObject(localNode.getAst());
                throw e;
            }
        }
        finally {
            JavacHandlerUtil.recursiveSetGeneratedBy(local.vartype, typeNode);
        }
    }

    private static class EnhancedForLoopReflect {
        private static final Field varOrRecordPattern = Permit.permissiveGetField(JCTree.JCEnhancedForLoop.class, "varOrRecordPattern");

        private EnhancedForLoopReflect() {
        }

        private static JCTree getVarOrRecordPattern(JCTree.JCEnhancedForLoop loop) {
            if (varOrRecordPattern == null) {
                return loop.var;
            }
            try {
                return (JCTree)varOrRecordPattern.get(loop);
            }
            catch (Exception exception) {
                return null;
            }
        }
    }
}
