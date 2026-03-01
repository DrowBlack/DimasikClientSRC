package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.SVG_Lib_Free_FuncI;

public abstract class SVG_Lib_Free_Func
extends Callback
implements SVG_Lib_Free_FuncI {
    public static SVG_Lib_Free_Func create(long functionPointer) {
        SVG_Lib_Free_FuncI instance = (SVG_Lib_Free_FuncI)Callback.get(functionPointer);
        return instance instanceof SVG_Lib_Free_Func ? (SVG_Lib_Free_Func)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static SVG_Lib_Free_Func createSafe(long functionPointer) {
        return functionPointer == 0L ? null : SVG_Lib_Free_Func.create(functionPointer);
    }

    public static SVG_Lib_Free_Func create(SVG_Lib_Free_FuncI instance) {
        return instance instanceof SVG_Lib_Free_Func ? (SVG_Lib_Free_Func)instance : new Container(instance.address(), instance);
    }

    protected SVG_Lib_Free_Func() {
        super(CIF);
    }

    SVG_Lib_Free_Func(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends SVG_Lib_Free_Func {
        private final SVG_Lib_Free_FuncI delegate;

        Container(long functionPointer, SVG_Lib_Free_FuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long data_pointer) {
            this.delegate.invoke(data_pointer);
        }
    }
}
