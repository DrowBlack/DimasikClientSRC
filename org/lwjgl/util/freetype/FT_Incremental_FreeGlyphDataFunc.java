package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Incremental_FreeGlyphDataFuncI;

public abstract class FT_Incremental_FreeGlyphDataFunc
extends Callback
implements FT_Incremental_FreeGlyphDataFuncI {
    public static FT_Incremental_FreeGlyphDataFunc create(long functionPointer) {
        FT_Incremental_FreeGlyphDataFuncI instance = (FT_Incremental_FreeGlyphDataFuncI)Callback.get(functionPointer);
        return instance instanceof FT_Incremental_FreeGlyphDataFunc ? (FT_Incremental_FreeGlyphDataFunc)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Incremental_FreeGlyphDataFunc createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Incremental_FreeGlyphDataFunc.create(functionPointer);
    }

    public static FT_Incremental_FreeGlyphDataFunc create(FT_Incremental_FreeGlyphDataFuncI instance) {
        return instance instanceof FT_Incremental_FreeGlyphDataFunc ? (FT_Incremental_FreeGlyphDataFunc)instance : new Container(instance.address(), instance);
    }

    protected FT_Incremental_FreeGlyphDataFunc() {
        super(CIF);
    }

    FT_Incremental_FreeGlyphDataFunc(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Incremental_FreeGlyphDataFunc {
        private final FT_Incremental_FreeGlyphDataFuncI delegate;

        Container(long functionPointer, FT_Incremental_FreeGlyphDataFuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long incremental, long data) {
            this.delegate.invoke(incremental, data);
        }
    }
}
