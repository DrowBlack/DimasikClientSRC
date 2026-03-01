package org.apache.commons.logging.impl;

import java.security.PrivilegedAction;

static final class LogFactoryImpl.2
implements PrivilegedAction {
    private final /* synthetic */ String val$key;
    private final /* synthetic */ String val$def;

    LogFactoryImpl.2(String string, String string2) {
        this.val$key = string;
        this.val$def = string2;
    }

    public Object run() {
        return System.getProperty(this.val$key, this.val$def);
    }
}
