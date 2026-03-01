package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Raster_DoneFuncI;

public abstract class FT_Raster_DoneFunc
extends Callback
implements FT_Raster_DoneFuncI {
    public static FT_Raster_DoneFunc create(long functionPointer) {
        FT_Raster_DoneFuncI instance = (FT_Raster_DoneFuncI)Callback.get(functionPointer);
        return instance instanceof FT_Raster_DoneFunc ? (FT_Raster_DoneFunc)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Raster_DoneFunc createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Raster_DoneFunc.create(functionPointer);
    }

    public static FT_Raster_DoneFunc create(FT_Raster_DoneFuncI instance) {
        return instance instanceof FT_Raster_DoneFunc ? (FT_Raster_DoneFunc)instance : new Container(instance.address(), instance);
    }

    protected FT_Raster_DoneFunc() {
        super(CIF);
    }

    FT_Raster_DoneFunc(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Raster_DoneFunc {
        private final FT_Raster_DoneFuncI delegate;

        Container(long functionPointer, FT_Raster_DoneFuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long raster) {
            this.delegate.invoke(raster);
        }
    }
}
