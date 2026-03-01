package org.codehaus.plexus.util.cli;

import java.security.AccessControlException;

class ShutdownHookUtils {
    ShutdownHookUtils() {
    }

    public static void addShutDownHook(Thread hook) {
        try {
            Runtime.getRuntime().addShutdownHook(hook);
        }
        catch (IllegalStateException illegalStateException) {
        }
        catch (AccessControlException accessControlException) {
            // empty catch block
        }
    }

    public static void removeShutdownHook(Thread hook) {
        try {
            Runtime.getRuntime().removeShutdownHook(hook);
        }
        catch (IllegalStateException illegalStateException) {
        }
        catch (AccessControlException accessControlException) {
            // empty catch block
        }
    }
}
