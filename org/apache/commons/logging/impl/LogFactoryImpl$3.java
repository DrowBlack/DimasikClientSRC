package org.apache.commons.logging.impl;

import java.security.PrivilegedAction;

class LogFactoryImpl.3
implements PrivilegedAction {
    private final /* synthetic */ ClassLoader val$cl;

    LogFactoryImpl.3(ClassLoader classLoader) {
        this.val$cl = classLoader;
    }

    public Object run() {
        return this.val$cl.getParent();
    }
}
