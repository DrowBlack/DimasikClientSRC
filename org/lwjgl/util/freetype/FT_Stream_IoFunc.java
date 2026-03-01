package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Stream_IoFuncI;

public abstract class FT_Stream_IoFunc
extends Callback
implements FT_Stream_IoFuncI {
    public static FT_Stream_IoFunc create(long functionPointer) {
        FT_Stream_IoFuncI instance = (FT_Stream_IoFuncI)Callback.get(functionPointer);
        return instance instanceof FT_Stream_IoFunc ? (FT_Stream_IoFunc)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Stream_IoFunc createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Stream_IoFunc.create(functionPointer);
    }

    public static FT_Stream_IoFunc create(FT_Stream_IoFuncI instance) {
        return instance instanceof FT_Stream_IoFunc ? (FT_Stream_IoFunc)instance : new Container(instance.address(), instance);
    }

    protected FT_Stream_IoFunc() {
        super(CIF);
    }

    FT_Stream_IoFunc(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Stream_IoFunc {
        private final FT_Stream_IoFuncI delegate;

        Container(long functionPointer, FT_Stream_IoFuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public long invoke(long stream, long offset, long buffer, long count) {
            return this.delegate.invoke(stream, offset, buffer, count);
        }
    }
}
