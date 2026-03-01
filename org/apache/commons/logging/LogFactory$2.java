package org.apache.commons.logging;

import java.security.PrivilegedAction;
import org.apache.commons.logging.LogFactory;

static final class LogFactory.2
implements PrivilegedAction {
    private final /* synthetic */ String val$factoryClass;
    private final /* synthetic */ ClassLoader val$classLoader;

    LogFactory.2(String string, ClassLoader classLoader) {
        this.val$factoryClass = string;
        this.val$classLoader = classLoader;
    }

    public Object run() {
        return LogFactory.createFactory(this.val$factoryClass, this.val$classLoader);
    }
}
