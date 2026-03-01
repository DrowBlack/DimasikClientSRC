package lombok.eclipse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.core.ImportList;
import lombok.core.LombokInternalAliasing;
import lombok.eclipse.Eclipse;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;

public class EclipseImportList
implements ImportList {
    private ImportReference[] imports;
    private ImportReference pkg;

    public EclipseImportList(CompilationUnitDeclaration cud) {
        this.pkg = cud.currentPackage;
        this.imports = cud.imports;
    }

    @Override
    public String getFullyQualifiedNameForSimpleName(String unqualified) {
        String q = this.getFullyQualifiedNameForSimpleNameNoAliasing(unqualified);
        return q == null ? null : LombokInternalAliasing.processAliases(q);
    }

    @Override
    public String getFullyQualifiedNameForSimpleNameNoAliasing(String unqualified) {
        if (this.imports != null) {
            ImportReference[] importReferenceArray = this.imports;
            int n = this.imports.length;
            int n2 = 0;
            while (n2 < n) {
                block5: {
                    char[][] tokens;
                    char[] token;
                    int len;
                    ImportReference imp = importReferenceArray[n2];
                    if ((imp.bits & 0x20000) == 0 && (len = (token = (tokens = imp.tokens).length == 0 ? new char[]{} : tokens[tokens.length - 1]).length) == unqualified.length()) {
                        int i = 0;
                        while (i < len) {
                            if (token[i] == unqualified.charAt(i)) {
                                ++i;
                                continue;
                            }
                            break block5;
                        }
                        return Eclipse.toQualifiedName(tokens);
                    }
                }
                ++n2;
            }
        }
        return null;
    }

    @Override
    public boolean hasStarImport(String packageName) {
        if (EclipseImportList.isEqual(packageName, this.pkg)) {
            return true;
        }
        if ("java.lang".equals(packageName)) {
            return true;
        }
        if (this.imports != null) {
            ImportReference[] importReferenceArray = this.imports;
            int n = this.imports.length;
            int n2 = 0;
            while (n2 < n) {
                ImportReference imp = importReferenceArray[n2];
                if ((imp.bits & 0x20000) != 0 && !imp.isStatic() && EclipseImportList.isEqual(packageName, imp)) {
                    return true;
                }
                ++n2;
            }
        }
        return false;
    }

    private static boolean isEqual(String packageName, ImportReference pkgOrStarImport) {
        if (pkgOrStarImport == null || pkgOrStarImport.tokens == null || pkgOrStarImport.tokens.length == 0) {
            return packageName.isEmpty();
        }
        int pos = 0;
        int len = packageName.length();
        int i = 0;
        while (i < pkgOrStarImport.tokens.length) {
            if (i != 0) {
                if (pos >= len) {
                    return false;
                }
                if (packageName.charAt(pos++) != '.') {
                    return false;
                }
            }
            int j = 0;
            while (j < pkgOrStarImport.tokens[i].length) {
                if (pos >= len) {
                    return false;
                }
                if (packageName.charAt(pos++) != pkgOrStarImport.tokens[i][j]) {
                    return false;
                }
                ++j;
            }
            ++i;
        }
        return true;
    }

    @Override
    public Collection<String> applyNameToStarImports(String startsWith, String name) {
        List<String> out = Collections.emptyList();
        if (this.pkg != null && this.pkg.tokens != null && this.pkg.tokens.length != 0) {
            char[] first = this.pkg.tokens[0];
            int len = first.length;
            boolean match = true;
            if (startsWith.length() == len) {
                int i = 0;
                while (match && i < len) {
                    if (startsWith.charAt(i) != first[i]) {
                        match = false;
                    }
                    ++i;
                }
                if (match) {
                    out.add(String.valueOf(Eclipse.toQualifiedName(this.pkg.tokens)) + "." + name);
                }
            }
        }
        if (this.imports != null) {
            ImportReference[] importReferenceArray = this.imports;
            int n = this.imports.length;
            int n2 = 0;
            while (n2 < n) {
                block14: {
                    char[] firstToken;
                    ImportReference imp = importReferenceArray[n2];
                    if ((imp.bits & 0x20000) != 0 && !imp.isStatic() && imp.tokens != null && imp.tokens.length != 0 && (firstToken = imp.tokens[0]).length == startsWith.length()) {
                        int i = 0;
                        while (i < firstToken.length) {
                            if (startsWith.charAt(i) == firstToken[i]) {
                                ++i;
                                continue;
                            }
                            break block14;
                        }
                        String fqn = String.valueOf(Eclipse.toQualifiedName(imp.tokens)) + "." + name;
                        if (out.isEmpty()) {
                            out = Collections.singletonList(fqn);
                        } else if (out.size() == 1) {
                            out = new ArrayList<String>(out);
                            out.add(fqn);
                        } else {
                            out.add(fqn);
                        }
                    }
                }
                ++n2;
            }
        }
        return out;
    }

    @Override
    public String applyUnqualifiedNameToPackage(String unqualified) {
        if (this.pkg == null || this.pkg.tokens == null || this.pkg.tokens.length == 0) {
            return unqualified;
        }
        return String.valueOf(Eclipse.toQualifiedName(this.pkg.tokens)) + "." + unqualified;
    }
}
