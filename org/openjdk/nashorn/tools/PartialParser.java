package org.openjdk.nashorn.tools;

import org.openjdk.nashorn.internal.runtime.Context;

public interface PartialParser {
    public int getLastExpressionStart(Context var1, String var2);
}
