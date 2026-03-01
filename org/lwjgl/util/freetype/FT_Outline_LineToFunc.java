package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Outline_LineToFuncI;

public abstract class FT_Outline_LineToFunc
extends Callback
implements FT_Outline_LineToFuncI {
    public static FT_Outline_LineToFunc create(long functionPointer) {
        FT_Outline_LineToFuncI instance = (FT_Outline_LineToFuncI)Callback.get(functionPointer);
        return instance instanceof FT_Outline_LineToFunc ? (FT_Outline_LineToFunc)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Outline_LineToFunc createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Outline_LineToFunc.create(functionPointer);
    }

    public static FT_Outline_LineToFunc create(FT_Outline_LineToFuncI instance) {
        return instance instanceof FT_Outline_LineToFunc ? (FT_Outline_LineToFunc)instance : new Container(instance.address(), instance);
    }

    protected FT_Outline_LineToFunc() {
        super(CIF);
    }

    FT_Outline_LineToFunc(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Outline_LineToFunc {
        private final FT_Outline_LineToFuncI delegate;

        Container(long functionPointer, FT_Outline_LineToFuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long to, long user) {
            return this.delegate.invoke(to, user);
        }
    }
}
