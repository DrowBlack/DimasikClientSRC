package lombok.javac.handlers;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import lombok.javac.JavacNode;
import lombok.javac.JavacResolution;

public enum JavacResolver {
    CLASS{

        @Override
        public Type resolveMember(JavacNode node, JCTree.JCExpression expr) {
            Type type = expr.type;
            if (type == null) {
                try {
                    new JavacResolution(node.getContext()).resolveClassMember(node);
                    type = expr.type;
                }
                catch (Exception exception) {}
            }
            return type;
        }
    }
    ,
    METHOD{

        @Override
        public Type resolveMember(JavacNode node, JCTree.JCExpression expr) {
            Type type = expr.type;
            if (type == null) {
                try {
                    JCTree.JCExpression resolvedExpression = (JCTree.JCExpression)new JavacResolution(node.getContext()).resolveMethodMember(node).get(expr);
                    if (resolvedExpression != null) {
                        type = resolvedExpression.type;
                    }
                }
                catch (Exception exception) {}
            }
            return type;
        }
    }
    ,
    CLASS_AND_METHOD{

        @Override
        public Type resolveMember(JavacNode node, JCTree.JCExpression expr) {
            Type type = METHOD.resolveMember(node, expr);
            if (type == null) {
                JavacNode classNode = node;
                while (classNode != null && this.noneOf(classNode.get(), JCTree.JCBlock.class, JCTree.JCMethodDecl.class, JCTree.JCVariableDecl.class)) {
                    classNode = (JavacNode)classNode.up();
                }
                if (classNode != null) {
                    type = CLASS.resolveMember(classNode, expr);
                }
            }
            return type;
        }

        private boolean noneOf(Object o, Class<?> ... clazzes) {
            Class<?>[] classArray = clazzes;
            int n = clazzes.length;
            int n2 = 0;
            while (n2 < n) {
                Class<?> clazz = classArray[n2];
                if (clazz.isInstance(o)) {
                    return false;
                }
                ++n2;
            }
            return true;
        }
    };


    private JavacResolver() {
    }

    public abstract Type resolveMember(JavacNode var1, JCTree.JCExpression var2);

    /* synthetic */ JavacResolver(String string, int n, JavacResolver javacResolver) {
        this();
    }
}
