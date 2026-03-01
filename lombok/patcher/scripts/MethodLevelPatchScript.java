package lombok.patcher.scripts;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.patcher.PatchScript;
import lombok.patcher.TargetMatcher;
import lombok.patcher.TransplantMapper;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class MethodLevelPatchScript
extends PatchScript {
    private final Set<String> affectedClasses;
    private final Collection<TargetMatcher> matchers;

    public String describeMatchers() {
        if (this.matchers.size() == 0) {
            return "(No matchers)";
        }
        if (this.matchers.size() == 1) {
            return this.matchers.iterator().next().describe();
        }
        StringBuilder out = new StringBuilder("(");
        for (TargetMatcher tm : this.matchers) {
            out.append(tm.describe()).append(", ");
        }
        out.setLength(out.length() - 2);
        return out.append(")").toString();
    }

    public MethodLevelPatchScript(Collection<TargetMatcher> matchers) {
        this.matchers = matchers;
        HashSet<String> affected = new HashSet<String>();
        for (TargetMatcher t : matchers) {
            affected.addAll(t.getAffectedClasses());
        }
        this.affectedClasses = Collections.unmodifiableSet(affected);
    }

    @Override
    public Collection<String> getClassesToReload() {
        return this.affectedClasses;
    }

    @Override
    public boolean wouldPatch(String className) {
        return MethodLevelPatchScript.classMatches(className, this.affectedClasses);
    }

    @Override
    public byte[] patch(String className, byte[] byteCode, TransplantMapper transplantMapper) {
        if (!MethodLevelPatchScript.classMatches(className, this.affectedClasses)) {
            return null;
        }
        return this.runASM(byteCode, true, transplantMapper);
    }

    @Override
    protected final ClassVisitor createClassVisitor(ClassWriter writer, String classSpec, TransplantMapper transplantMapper) {
        PatchScript.MethodPatcher patcher = this.createPatcher(writer, classSpec, transplantMapper);
        for (TargetMatcher matcher : this.matchers) {
            patcher.addTargetMatcher(matcher);
        }
        return patcher;
    }

    protected abstract PatchScript.MethodPatcher createPatcher(ClassWriter var1, String var2, TransplantMapper var3);
}
