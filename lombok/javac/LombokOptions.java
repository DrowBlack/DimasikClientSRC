package lombok.javac;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Options;
import java.util.HashSet;
import java.util.Set;
import lombok.delombok.FormatPreferences;
import lombok.delombok.LombokOptionsFactory;

public abstract class LombokOptions
extends Options {
    private boolean deleteLombokAnnotations = false;
    private final Set<JCTree.JCCompilationUnit> changed = new HashSet<JCTree.JCCompilationUnit>();
    private FormatPreferences formatPreferences = new FormatPreferences(null);

    public boolean isChanged(JCTree.JCCompilationUnit ast) {
        return this.changed.contains(ast);
    }

    public void setFormatPreferences(FormatPreferences formatPreferences) {
        this.formatPreferences = formatPreferences;
    }

    public FormatPreferences getFormatPreferences() {
        return this.formatPreferences;
    }

    public static void markChanged(Context context, JCTree.JCCompilationUnit ast) {
        LombokOptions options = LombokOptionsFactory.getDelombokOptions(context);
        options.changed.add(ast);
    }

    public static boolean shouldDeleteLombokAnnotations(Context context) {
        LombokOptions options = LombokOptionsFactory.getDelombokOptions(context);
        return options.deleteLombokAnnotations;
    }

    protected LombokOptions(Context context) {
        super(context);
    }

    public abstract void putJavacOption(String var1, String var2);

    public void deleteLombokAnnotations() {
        this.deleteLombokAnnotations = true;
    }
}
