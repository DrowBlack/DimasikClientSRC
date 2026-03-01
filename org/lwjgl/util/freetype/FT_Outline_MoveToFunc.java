package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Outline_MoveToFuncI;

public abstract class FT_Outline_MoveToFunc
extends Callback
implements FT_Outline_MoveToFuncI {
    public static FT_Outline_MoveToFunc create(long functionPointer) {
        FT_Outline_MoveToFuncI instance = (FT_Outline_MoveToFuncI)Callback.get(functionPointer);
        return instance instanceof FT_Outline_MoveToFunc ? (FT_Outline_MoveToFunc)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Outline_MoveToFunc createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Outline_MoveToFunc.create(functionPointer);
    }

    public static FT_Outline_MoveToFunc create(FT_Outline_MoveToFuncI instance) {
        return instance instanceof FT_Outline_MoveToFunc ? (FT_Outline_MoveToFunc)instance : new Container(instance.address(), instance);
    }

    protected FT_Outline_MoveToFunc() {
        super(CIF);
    }

    FT_Outline_MoveToFunc(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Outline_MoveToFunc {
        private final FT_Outline_MoveToFuncI delegate;

        Container(long functionPointer, FT_Outline_MoveToFuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long to, long user) {
            return this.delegate.invoke(to, user);
        }
    }
}
