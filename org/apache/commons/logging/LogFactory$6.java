package org.apache.commons.logging;

import java.security.PrivilegedAction;

static final class LogFactory.6
implements PrivilegedAction {
    private final /* synthetic */ String val$key;
    private final /* synthetic */ String val$def;

    LogFactory.6(String string, String string2) {
        this.val$key = string;
        this.val$def = string2;
    }

    public Object run() {
        return System.getProperty(this.val$key, this.val$def);
    }
}
