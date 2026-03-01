package lombok.javac;

import com.sun.tools.javac.tree.JCTree;
import java.lang.reflect.Method;
import lombok.permit.Permit;

public class PackageName {
    private static final Method packageNameMethod = PackageName.getPackageNameMethod();

    private static Method getPackageNameMethod() {
        try {
            return Permit.getMethod(JCTree.JCCompilationUnit.class, "getPackageName", new Class[0]);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static String getPackageName(JCTree.JCCompilationUnit cu) {
        JCTree t = PackageName.getPackageNode(cu);
        return t != null ? t.toString() : null;
    }

    public static JCTree getPackageNode(JCTree.JCCompilationUnit cu) {
        if (packageNameMethod != null) {
            try {
                Object pkg = packageNameMethod.invoke((Object)cu, new Object[0]);
                return pkg instanceof JCTree.JCFieldAccess || pkg instanceof JCTree.JCIdent ? (JCTree)pkg : null;
            }
            catch (Exception exception) {}
        }
        return cu.pid instanceof JCTree.JCFieldAccess || cu.pid instanceof JCTree.JCIdent ? cu.pid : null;
    }
}
