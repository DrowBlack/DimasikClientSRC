package org.apache.commons.logging;

import java.security.PrivilegedAction;
import org.apache.commons.logging.LogFactory;

static final class LogFactory.1
implements PrivilegedAction {
    LogFactory.1() {
    }

    public Object run() {
        return LogFactory.directGetContextClassLoader();
    }
}
