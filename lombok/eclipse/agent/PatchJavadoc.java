package lombok.eclipse.agent;

import java.lang.reflect.Method;
import java.util.Map;
import lombok.eclipse.EcjAugments;
import lombok.permit.Permit;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2;

public class PatchJavadoc {
    public static String getHTMLContentFromSource(Object instance, String original, Object member) {
        SourceMethod sourceMethod;
        ICompilationUnit iCompilationUnit;
        if (original != null) {
            return original;
        }
        if (member instanceof SourceMethod && (iCompilationUnit = (sourceMethod = (SourceMethod)member).getCompilationUnit()) instanceof CompilationUnit) {
            CompilationUnit compilationUnit = (CompilationUnit)iCompilationUnit;
            Map<String, String> docs = EcjAugments.CompilationUnit_javadoc.get((org.eclipse.jdt.internal.compiler.env.ICompilationUnit)compilationUnit);
            if (docs == null) {
                return null;
            }
            String signature = Signature.getSignature(sourceMethod);
            String rawJavadoc = docs.get(signature);
            if (rawJavadoc == null) {
                return null;
            }
            return Reflection.javadoc2HTML(instance, (IMember)member, (IJavaElement)member, rawJavadoc);
        }
        return null;
    }

    private static class Reflection {
        private static final Method javadoc2HTML;
        private static final Method oldJavadoc2HTML;
        private static final Method reallyOldJavadoc2HTML;
        private static final Method lsJavadoc2HTML;

        static {
            Method a = null;
            Method b = null;
            Method c = null;
            Method d = null;
            try {
                a = Permit.getMethod(JavadocContentAccess2.class, "javadoc2HTML", IMember.class, IJavaElement.class, String.class);
            }
            catch (Throwable throwable) {}
            try {
                b = Permit.getMethod(JavadocContentAccess2.class, "javadoc2HTML", IMember.class, String.class);
            }
            catch (Throwable throwable) {}
            try {
                c = Permit.getMethod(Class.forName("org.eclipse.jdt.ls.core.internal.javadoc.JavadocContentAccess2"), "javadoc2HTML", IMember.class, IJavaElement.class, String.class);
            }
            catch (Throwable throwable) {}
            try {
                d = Permit.getMethod(Class.forName("org.eclipse.jdt.core.manipulation.internal.javadoc.CoreJavadocAccess"), "javadoc2HTML", IMember.class, IJavaElement.class, String.class);
            }
            catch (Throwable throwable) {}
            oldJavadoc2HTML = a;
            reallyOldJavadoc2HTML = b;
            lsJavadoc2HTML = c;
            javadoc2HTML = d;
        }

        private Reflection() {
        }

        private static String javadoc2HTML(Object instance, IMember member, IJavaElement element, String rawJavadoc) {
            if (javadoc2HTML != null) {
                try {
                    return (String)javadoc2HTML.invoke(instance, member, element, rawJavadoc);
                }
                catch (Throwable throwable) {
                    return null;
                }
            }
            if (oldJavadoc2HTML != null) {
                try {
                    return (String)oldJavadoc2HTML.invoke(instance, member, element, rawJavadoc);
                }
                catch (Throwable throwable) {
                    return null;
                }
            }
            if (lsJavadoc2HTML != null) {
                try {
                    return (String)lsJavadoc2HTML.invoke(instance, member, element, rawJavadoc);
                }
                catch (Throwable throwable) {
                    return null;
                }
            }
            if (reallyOldJavadoc2HTML != null) {
                try {
                    return (String)reallyOldJavadoc2HTML.invoke(instance, member, rawJavadoc);
                }
                catch (Throwable throwable) {
                    return null;
                }
            }
            return null;
        }
    }

    private static class Signature {
        private Signature() {
        }

        static final String getSignature(SourceMethod sourceMethod) {
            StringBuilder sb = new StringBuilder();
            sb.append(sourceMethod.getParent().getElementName());
            sb.append(".");
            sb.append(sourceMethod.getElementName());
            sb.append("(");
            String[] stringArray = sourceMethod.getParameterTypes();
            int n = stringArray.length;
            int n2 = 0;
            while (n2 < n) {
                String type = stringArray[n2];
                sb.append(org.eclipse.jdt.core.Signature.toString((String)type));
                ++n2;
            }
            sb.append(")");
            return sb.toString();
        }
    }
}
