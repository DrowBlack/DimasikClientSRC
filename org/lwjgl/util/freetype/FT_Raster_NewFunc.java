package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Raster_NewFuncI;

public abstract class FT_Raster_NewFunc
extends Callback
implements FT_Raster_NewFuncI {
    public static FT_Raster_NewFunc create(long functionPointer) {
        FT_Raster_NewFuncI instance = (FT_Raster_NewFuncI)Callback.get(functionPointer);
        return instance instanceof FT_Raster_NewFunc ? (FT_Raster_NewFunc)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Raster_NewFunc createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Raster_NewFunc.create(functionPointer);
    }

    public static FT_Raster_NewFunc create(FT_Raster_NewFuncI instance) {
        return instance instanceof FT_Raster_NewFunc ? (FT_Raster_NewFunc)instance : new Container(instance.address(), instance);
    }

    protected FT_Raster_NewFunc() {
        super(CIF);
    }

    FT_Raster_NewFunc(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Raster_NewFunc {
        private final FT_Raster_NewFuncI delegate;

        Container(long functionPointer, FT_Raster_NewFuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long memory, long raster) {
            return this.delegate.invoke(memory, raster);
        }
    }
}
