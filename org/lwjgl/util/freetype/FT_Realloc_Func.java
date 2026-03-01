package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Realloc_FuncI;

public abstract class FT_Realloc_Func
extends Callback
implements FT_Realloc_FuncI {
    public static FT_Realloc_Func create(long functionPointer) {
        FT_Realloc_FuncI instance = (FT_Realloc_FuncI)Callback.get(functionPointer);
        return instance instanceof FT_Realloc_Func ? (FT_Realloc_Func)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Realloc_Func createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Realloc_Func.create(functionPointer);
    }

    public static FT_Realloc_Func create(FT_Realloc_FuncI instance) {
        return instance instanceof FT_Realloc_Func ? (FT_Realloc_Func)instance : new Container(instance.address(), instance);
    }

    protected FT_Realloc_Func() {
        super(CIF);
    }

    FT_Realloc_Func(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Realloc_Func {
        private final FT_Realloc_FuncI delegate;

        Container(long functionPointer, FT_Realloc_FuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public long invoke(long memory, long cur_size, long new_size, long block) {
            return this.delegate.invoke(memory, cur_size, new_size, block);
        }
    }
}
