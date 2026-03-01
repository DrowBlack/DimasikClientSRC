package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Stream_CloseFuncI;

public abstract class FT_Stream_CloseFunc
extends Callback
implements FT_Stream_CloseFuncI {
    public static FT_Stream_CloseFunc create(long functionPointer) {
        FT_Stream_CloseFuncI instance = (FT_Stream_CloseFuncI)Callback.get(functionPointer);
        return instance instanceof FT_Stream_CloseFunc ? (FT_Stream_CloseFunc)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Stream_CloseFunc createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Stream_CloseFunc.create(functionPointer);
    }

    public static FT_Stream_CloseFunc create(FT_Stream_CloseFuncI instance) {
        return instance instanceof FT_Stream_CloseFunc ? (FT_Stream_CloseFunc)instance : new Container(instance.address(), instance);
    }

    protected FT_Stream_CloseFunc() {
        super(CIF);
    }

    FT_Stream_CloseFunc(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Stream_CloseFunc {
        private final FT_Stream_CloseFuncI delegate;

        Container(long functionPointer, FT_Stream_CloseFuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long stream) {
            this.delegate.invoke(stream);
        }
    }
}
