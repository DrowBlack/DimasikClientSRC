package lombok.core;

import java.util.Collection;

public interface ImportList {
    public String getFullyQualifiedNameForSimpleName(String var1);

    public String getFullyQualifiedNameForSimpleNameNoAliasing(String var1);

    public boolean hasStarImport(String var1);

    public Collection<String> applyNameToStarImports(String var1, String var2);

    public String applyUnqualifiedNameToPackage(String var1);
}
