package org.lwjgl.stb;

import javax.annotation.Nullable;
import org.lwjgl.stb.STBIROutputCallbackI;
import org.lwjgl.system.Callback;

public abstract class STBIROutputCallback
extends Callback
implements STBIROutputCallbackI {
    public static STBIROutputCallback create(long functionPointer) {
        STBIROutputCallbackI instance = (STBIROutputCallbackI)Callback.get(functionPointer);
        return instance instanceof STBIROutputCallback ? (STBIROutputCallback)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static STBIROutputCallback createSafe(long functionPointer) {
        return functionPointer == 0L ? null : STBIROutputCallback.create(functionPointer);
    }

    public static STBIROutputCallback create(STBIROutputCallbackI instance) {
        return instance instanceof STBIROutputCallback ? (STBIROutputCallback)instance : new Container(instance.address(), instance);
    }

    protected STBIROutputCallback() {
        super(CIF);
    }

    STBIROutputCallback(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends STBIROutputCallback {
        private final STBIROutputCallbackI delegate;

        Container(long functionPointer, STBIROutputCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long output_ptr, int num_pixels, int x, int y, long context) {
            this.delegate.invoke(output_ptr, num_pixels, x, y, context);
        }
    }
}
