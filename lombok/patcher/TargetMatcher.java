package lombok.patcher;

import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface TargetMatcher {
    public Collection<String> getAffectedClasses();

    public boolean matches(String var1, String var2, String var3);

    public String describe();
}
