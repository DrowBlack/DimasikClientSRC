package lombok.javac;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import java.util.ArrayList;
import java.util.Collection;
import lombok.core.ImportList;
import lombok.core.LombokInternalAliasing;
import lombok.javac.Javac;
import lombok.javac.PackageName;

public class JavacImportList
implements ImportList {
    private final String pkgStr;
    private final List<JCTree> defs;

    public JavacImportList(JCTree.JCCompilationUnit cud) {
        this.pkgStr = PackageName.getPackageName(cud);
        this.defs = cud.defs;
    }

    @Override
    public String getFullyQualifiedNameForSimpleName(String unqualified) {
        String q = this.getFullyQualifiedNameForSimpleNameNoAliasing(unqualified);
        return q == null ? null : LombokInternalAliasing.processAliases(q);
    }

    @Override
    public String getFullyQualifiedNameForSimpleNameNoAliasing(String unqualified) {
        for (JCTree def : this.defs) {
            String simpleName;
            JCTree qual;
            if (!(def instanceof JCTree.JCImport) || !((qual = Javac.getQualid((JCTree.JCImport)def)) instanceof JCTree.JCFieldAccess) || !(simpleName = ((JCTree.JCFieldAccess)qual).name.toString()).equals(unqualified)) continue;
            return qual.toString();
        }
        return null;
    }

    @Override
    public boolean hasStarImport(String packageName) {
        if (this.pkgStr != null && this.pkgStr.equals(packageName)) {
            return true;
        }
        if ("java.lang".equals(packageName)) {
            return true;
        }
        for (JCTree def : this.defs) {
            String starImport;
            String simpleName;
            JCTree qual;
            if (!(def instanceof JCTree.JCImport) || ((JCTree.JCImport)def).staticImport || !((qual = Javac.getQualid((JCTree.JCImport)def)) instanceof JCTree.JCFieldAccess) || !"*".equals(simpleName = ((JCTree.JCFieldAccess)qual).name.toString()) || !packageName.equals(starImport = ((JCTree.JCFieldAccess)qual).selected.toString())) continue;
            return true;
        }
        return false;
    }

    @Override
    public Collection<String> applyNameToStarImports(String startsWith, String name) {
        ArrayList<String> out = new ArrayList<String>();
        if (this.pkgStr != null && this.topLevelName(this.pkgStr).equals(startsWith)) {
            out.add(String.valueOf(this.pkgStr) + "." + name);
        }
        for (JCTree def : this.defs) {
            String topLevelName;
            String simpleName;
            JCTree qual;
            if (!(def instanceof JCTree.JCImport) || ((JCTree.JCImport)def).staticImport || !((qual = Javac.getQualid((JCTree.JCImport)def)) instanceof JCTree.JCFieldAccess) || !"*".equals(simpleName = ((JCTree.JCFieldAccess)qual).name.toString()) || !(topLevelName = this.topLevelName(qual)).equals(startsWith)) continue;
            out.add(String.valueOf(((JCTree.JCFieldAccess)qual).selected.toString()) + "." + name);
        }
        return out;
    }

    private String topLevelName(JCTree tree) {
        while (tree instanceof JCTree.JCFieldAccess) {
            tree = ((JCTree.JCFieldAccess)tree).selected;
        }
        return tree.toString();
    }

    private String topLevelName(String packageName) {
        int idx = packageName.indexOf(".");
        if (idx == -1) {
            return packageName;
        }
        return packageName.substring(0, idx);
    }

    @Override
    public String applyUnqualifiedNameToPackage(String unqualified) {
        if (this.pkgStr == null) {
            return unqualified;
        }
        return String.valueOf(this.pkgStr) + "." + unqualified;
    }
}
