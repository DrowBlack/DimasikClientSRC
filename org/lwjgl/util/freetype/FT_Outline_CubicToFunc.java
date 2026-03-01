package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Outline_CubicToFuncI;

public abstract class FT_Outline_CubicToFunc
extends Callback
implements FT_Outline_CubicToFuncI {
    public static FT_Outline_CubicToFunc create(long functionPointer) {
        FT_Outline_CubicToFuncI instance = (FT_Outline_CubicToFuncI)Callback.get(functionPointer);
        return instance instanceof FT_Outline_CubicToFunc ? (FT_Outline_CubicToFunc)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Outline_CubicToFunc createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Outline_CubicToFunc.create(functionPointer);
    }

    public static FT_Outline_CubicToFunc create(FT_Outline_CubicToFuncI instance) {
        return instance instanceof FT_Outline_CubicToFunc ? (FT_Outline_CubicToFunc)instance : new Container(instance.address(), instance);
    }

    protected FT_Outline_CubicToFunc() {
        super(CIF);
    }

    FT_Outline_CubicToFunc(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Outline_CubicToFunc {
        private final FT_Outline_CubicToFuncI delegate;

        Container(long functionPointer, FT_Outline_CubicToFuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long control1, long control2, long to, long user) {
            return this.delegate.invoke(control1, control2, to, user);
        }
    }
}
