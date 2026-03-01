package org.apache.commons.logging;

import java.io.IOException;
import java.security.PrivilegedAction;
import org.apache.commons.logging.LogFactory;

static final class LogFactory.4
implements PrivilegedAction {
    private final /* synthetic */ ClassLoader val$loader;
    private final /* synthetic */ String val$name;

    LogFactory.4(ClassLoader classLoader, String string) {
        this.val$loader = classLoader;
        this.val$name = string;
    }

    public Object run() {
        try {
            if (this.val$loader != null) {
                return this.val$loader.getResources(this.val$name);
            }
            return ClassLoader.getSystemResources(this.val$name);
        }
        catch (IOException e) {
            if (LogFactory.isDiagnosticsEnabled()) {
                LogFactory.logDiagnostic("Exception while trying to find configuration file " + this.val$name + ":" + e.getMessage());
            }
            return null;
        }
        catch (NoSuchMethodError e) {
            return null;
        }
    }
}
