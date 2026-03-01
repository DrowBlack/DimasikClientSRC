package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Free_FuncI;

public abstract class FT_Free_Func
extends Callback
implements FT_Free_FuncI {
    public static FT_Free_Func create(long functionPointer) {
        FT_Free_FuncI instance = (FT_Free_FuncI)Callback.get(functionPointer);
        return instance instanceof FT_Free_Func ? (FT_Free_Func)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Free_Func createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Free_Func.create(functionPointer);
    }

    public static FT_Free_Func create(FT_Free_FuncI instance) {
        return instance instanceof FT_Free_Func ? (FT_Free_Func)instance : new Container(instance.address(), instance);
    }

    protected FT_Free_Func() {
        super(CIF);
    }

    FT_Free_Func(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Free_Func {
        private final FT_Free_FuncI delegate;

        Container(long functionPointer, FT_Free_FuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long memory, long block) {
            this.delegate.invoke(memory, block);
        }
    }
}
