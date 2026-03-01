package lombok.patcher;

import java.security.ProtectionDomain;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Filter {
    public static final Filter ALWAYS = new Filter(){

        @Override
        public boolean shouldTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            return true;
        }
    };

    public boolean shouldTransform(ClassLoader var1, String var2, Class<?> var3, ProtectionDomain var4, byte[] var5);
}
