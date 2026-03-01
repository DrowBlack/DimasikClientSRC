package org.apache.commons.logging;

import java.security.PrivilegedAction;

static final class LogFactory.3
implements PrivilegedAction {
    private final /* synthetic */ ClassLoader val$loader;
    private final /* synthetic */ String val$name;

    LogFactory.3(ClassLoader classLoader, String string) {
        this.val$loader = classLoader;
        this.val$name = string;
    }

    public Object run() {
        if (this.val$loader != null) {
            return this.val$loader.getResourceAsStream(this.val$name);
        }
        return ClassLoader.getSystemResourceAsStream(this.val$name);
    }
}
