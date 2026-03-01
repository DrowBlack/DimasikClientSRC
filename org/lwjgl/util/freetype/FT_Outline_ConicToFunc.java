package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Outline_ConicToFuncI;

public abstract class FT_Outline_ConicToFunc
extends Callback
implements FT_Outline_ConicToFuncI {
    public static FT_Outline_ConicToFunc create(long functionPointer) {
        FT_Outline_ConicToFuncI instance = (FT_Outline_ConicToFuncI)Callback.get(functionPointer);
        return instance instanceof FT_Outline_ConicToFunc ? (FT_Outline_ConicToFunc)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Outline_ConicToFunc createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Outline_ConicToFunc.create(functionPointer);
    }

    public static FT_Outline_ConicToFunc create(FT_Outline_ConicToFuncI instance) {
        return instance instanceof FT_Outline_ConicToFunc ? (FT_Outline_ConicToFunc)instance : new Container(instance.address(), instance);
    }

    protected FT_Outline_ConicToFunc() {
        super(CIF);
    }

    FT_Outline_ConicToFunc(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Outline_ConicToFunc {
        private final FT_Outline_ConicToFuncI delegate;

        Container(long functionPointer, FT_Outline_ConicToFuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long control, long to, long user) {
            return this.delegate.invoke(control, to, user);
        }
    }
}
