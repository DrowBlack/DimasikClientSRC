package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Raster_ResetFuncI;

public abstract class FT_Raster_ResetFunc
extends Callback
implements FT_Raster_ResetFuncI {
    public static FT_Raster_ResetFunc create(long functionPointer) {
        FT_Raster_ResetFuncI instance = (FT_Raster_ResetFuncI)Callback.get(functionPointer);
        return instance instanceof FT_Raster_ResetFunc ? (FT_Raster_ResetFunc)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Raster_ResetFunc createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Raster_ResetFunc.create(functionPointer);
    }

    public static FT_Raster_ResetFunc create(FT_Raster_ResetFuncI instance) {
        return instance instanceof FT_Raster_ResetFunc ? (FT_Raster_ResetFunc)instance : new Container(instance.address(), instance);
    }

    protected FT_Raster_ResetFunc() {
        super(CIF);
    }

    FT_Raster_ResetFunc(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Raster_ResetFunc {
        private final FT_Raster_ResetFuncI delegate;

        Container(long functionPointer, FT_Raster_ResetFuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long raster, long pool_base, long pool_size) {
            this.delegate.invoke(raster, pool_base, pool_size);
        }
    }
}
