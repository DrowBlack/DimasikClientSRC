package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_DebugHook_FuncI;

public abstract class FT_DebugHook_Func
extends Callback
implements FT_DebugHook_FuncI {
    public static FT_DebugHook_Func create(long functionPointer) {
        FT_DebugHook_FuncI instance = (FT_DebugHook_FuncI)Callback.get(functionPointer);
        return instance instanceof FT_DebugHook_Func ? (FT_DebugHook_Func)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_DebugHook_Func createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_DebugHook_Func.create(functionPointer);
    }

    public static FT_DebugHook_Func create(FT_DebugHook_FuncI instance) {
        return instance instanceof FT_DebugHook_Func ? (FT_DebugHook_Func)instance : new Container(instance.address(), instance);
    }

    protected FT_DebugHook_Func() {
        super(CIF);
    }

    FT_DebugHook_Func(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_DebugHook_Func {
        private final FT_DebugHook_FuncI delegate;

        Container(long functionPointer, FT_DebugHook_FuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long arg) {
            return this.delegate.invoke(arg);
        }
    }
}
