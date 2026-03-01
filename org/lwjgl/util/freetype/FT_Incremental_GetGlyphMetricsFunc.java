package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Incremental_GetGlyphMetricsFuncI;

public abstract class FT_Incremental_GetGlyphMetricsFunc
extends Callback
implements FT_Incremental_GetGlyphMetricsFuncI {
    public static FT_Incremental_GetGlyphMetricsFunc create(long functionPointer) {
        FT_Incremental_GetGlyphMetricsFuncI instance = (FT_Incremental_GetGlyphMetricsFuncI)Callback.get(functionPointer);
        return instance instanceof FT_Incremental_GetGlyphMetricsFunc ? (FT_Incremental_GetGlyphMetricsFunc)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Incremental_GetGlyphMetricsFunc createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Incremental_GetGlyphMetricsFunc.create(functionPointer);
    }

    public static FT_Incremental_GetGlyphMetricsFunc create(FT_Incremental_GetGlyphMetricsFuncI instance) {
        return instance instanceof FT_Incremental_GetGlyphMetricsFunc ? (FT_Incremental_GetGlyphMetricsFunc)instance : new Container(instance.address(), instance);
    }

    protected FT_Incremental_GetGlyphMetricsFunc() {
        super(CIF);
    }

    FT_Incremental_GetGlyphMetricsFunc(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Incremental_GetGlyphMetricsFunc {
        private final FT_Incremental_GetGlyphMetricsFuncI delegate;

        Container(long functionPointer, FT_Incremental_GetGlyphMetricsFuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long incremental, int glyph_index, boolean vertical, long ametrics) {
            return this.delegate.invoke(incremental, glyph_index, vertical, ametrics);
        }
    }
}
