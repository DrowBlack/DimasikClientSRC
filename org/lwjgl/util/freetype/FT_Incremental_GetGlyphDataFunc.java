package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Incremental_GetGlyphDataFuncI;

public abstract class FT_Incremental_GetGlyphDataFunc
extends Callback
implements FT_Incremental_GetGlyphDataFuncI {
    public static FT_Incremental_GetGlyphDataFunc create(long functionPointer) {
        FT_Incremental_GetGlyphDataFuncI instance = (FT_Incremental_GetGlyphDataFuncI)Callback.get(functionPointer);
        return instance instanceof FT_Incremental_GetGlyphDataFunc ? (FT_Incremental_GetGlyphDataFunc)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Incremental_GetGlyphDataFunc createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Incremental_GetGlyphDataFunc.create(functionPointer);
    }

    public static FT_Incremental_GetGlyphDataFunc create(FT_Incremental_GetGlyphDataFuncI instance) {
        return instance instanceof FT_Incremental_GetGlyphDataFunc ? (FT_Incremental_GetGlyphDataFunc)instance : new Container(instance.address(), instance);
    }

    protected FT_Incremental_GetGlyphDataFunc() {
        super(CIF);
    }

    FT_Incremental_GetGlyphDataFunc(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Incremental_GetGlyphDataFunc {
        private final FT_Incremental_GetGlyphDataFuncI delegate;

        Container(long functionPointer, FT_Incremental_GetGlyphDataFuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long incremental, int glyph_index, long adata) {
            return this.delegate.invoke(incremental, glyph_index, adata);
        }
    }
}
