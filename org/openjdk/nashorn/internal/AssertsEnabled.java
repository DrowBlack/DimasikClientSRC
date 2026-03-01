package org.openjdk.nashorn.internal;

public final class AssertsEnabled {
    private static boolean assertsEnabled = false;

    public static boolean assertsEnabled() {
        return assertsEnabled;
    }

    static {
        if (!$assertionsDisabled) {
            assertsEnabled = true;
            if (!true) {
                throw new AssertionError();
            }
        }
    }
}
