package org.openjdk.nashorn.internal.runtime.logging;

import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.logging.DebugLogger;

public interface Loggable {
    public DebugLogger initLogger(Context var1);

    public DebugLogger getLogger();
}
