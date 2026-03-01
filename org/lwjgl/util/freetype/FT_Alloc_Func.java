package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Alloc_FuncI;

public abstract class FT_Alloc_Func
extends Callback
implements FT_Alloc_FuncI {
    public static FT_Alloc_Func create(long functionPointer) {
        FT_Alloc_FuncI instance = (FT_Alloc_FuncI)Callback.get(functionPointer);
        return instance instanceof FT_Alloc_Func ? (FT_Alloc_Func)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Alloc_Func createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Alloc_Func.create(functionPointer);
    }

    public static FT_Alloc_Func create(FT_Alloc_FuncI instance) {
        return instance instanceof FT_Alloc_Func ? (FT_Alloc_Func)instance : new Container(instance.address(), instance);
    }

    protected FT_Alloc_Func() {
        super(CIF);
    }

    FT_Alloc_Func(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Alloc_Func {
        private final FT_Alloc_FuncI delegate;

        Container(long functionPointer, FT_Alloc_FuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public long invoke(long memory, long size) {
            return this.delegate.invoke(memory, size);
        }
    }
}
