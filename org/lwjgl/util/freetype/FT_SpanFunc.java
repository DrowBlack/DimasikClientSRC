package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_SpanFuncI;

public abstract class FT_SpanFunc
extends Callback
implements FT_SpanFuncI {
    public static FT_SpanFunc create(long functionPointer) {
        FT_SpanFuncI instance = (FT_SpanFuncI)Callback.get(functionPointer);
        return instance instanceof FT_SpanFunc ? (FT_SpanFunc)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_SpanFunc createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_SpanFunc.create(functionPointer);
    }

    public static FT_SpanFunc create(FT_SpanFuncI instance) {
        return instance instanceof FT_SpanFunc ? (FT_SpanFunc)instance : new Container(instance.address(), instance);
    }

    protected FT_SpanFunc() {
        super(CIF);
    }

    FT_SpanFunc(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_SpanFunc {
        private final FT_SpanFuncI delegate;

        Container(long functionPointer, FT_SpanFuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(int y, int count, long spans, long user) {
            this.delegate.invoke(y, count, spans, user);
        }
    }
}
