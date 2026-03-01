package org.openjdk.nashorn.internal.runtime;

import java.util.concurrent.atomic.LongAdder;
import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.WithObject;

public class Scope
extends ScriptObject {
    private int splitState = -1;
    private static final LongAdder count = Context.DEBUG ? new LongAdder() : null;
    public static final CompilerConstants.Call GET_SPLIT_STATE = CompilerConstants.virtualCallNoLookup(Scope.class, "getSplitState", Integer.TYPE, new Class[0]);
    public static final CompilerConstants.Call SET_SPLIT_STATE = CompilerConstants.virtualCallNoLookup(Scope.class, "setSplitState", Void.TYPE, Integer.TYPE);

    public Scope(PropertyMap map) {
        super(map);
        Scope.incrementCount();
    }

    public Scope(ScriptObject proto, PropertyMap map) {
        super(proto, map);
        Scope.incrementCount();
    }

    public Scope(PropertyMap map, long[] primitiveSpill, Object[] objectSpill) {
        super(map, primitiveSpill, objectSpill);
        Scope.incrementCount();
    }

    @Override
    public boolean isScope() {
        return true;
    }

    @Override
    boolean hasWithScope() {
        for (ScriptObject obj = this; obj != null; obj = obj.getProto()) {
            if (!(obj instanceof WithObject)) continue;
            return true;
        }
        return false;
    }

    public int getSplitState() {
        return this.splitState;
    }

    public void setSplitState(int state) {
        this.splitState = state;
    }

    public static long getScopeCount() {
        return count != null ? count.sum() : 0L;
    }

    private static void incrementCount() {
        if (Context.DEBUG) {
            count.increment();
        }
    }
}
