package org.lwjgl.stb;

import javax.annotation.Nullable;
import org.lwjgl.stb.STBIRInputCallbackI;
import org.lwjgl.system.Callback;

public abstract class STBIRInputCallback
extends Callback
implements STBIRInputCallbackI {
    public static STBIRInputCallback create(long functionPointer) {
        STBIRInputCallbackI instance = (STBIRInputCallbackI)Callback.get(functionPointer);
        return instance instanceof STBIRInputCallback ? (STBIRInputCallback)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static STBIRInputCallback createSafe(long functionPointer) {
        return functionPointer == 0L ? null : STBIRInputCallback.create(functionPointer);
    }

    public static STBIRInputCallback create(STBIRInputCallbackI instance) {
        return instance instanceof STBIRInputCallback ? (STBIRInputCallback)instance : new Container(instance.address(), instance);
    }

    protected STBIRInputCallback() {
        super(CIF);
    }

    STBIRInputCallback(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends STBIRInputCallback {
        private final STBIRInputCallbackI delegate;

        Container(long functionPointer, STBIRInputCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long optional_output, long input_ptr, int num_pixels, int x, int y, long context) {
            this.delegate.invoke(optional_output, input_ptr, num_pixels, x, y, context);
        }
    }
}
