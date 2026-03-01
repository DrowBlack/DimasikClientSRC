package org.openjdk.nashorn.internal.runtime;

import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.Scope;
import org.openjdk.nashorn.internal.runtime.ScriptObject;

public class FunctionScope
extends Scope {
    public final ScriptObject arguments;

    public FunctionScope(PropertyMap map, ScriptObject callerScope, ScriptObject arguments) {
        super(callerScope, map);
        this.arguments = arguments;
    }

    public FunctionScope(PropertyMap map, ScriptObject callerScope) {
        super(callerScope, map);
        this.arguments = null;
    }

    public FunctionScope(PropertyMap map, long[] primitiveSpill, Object[] objectSpill) {
        super(map, primitiveSpill, objectSpill);
        this.arguments = null;
    }
}
