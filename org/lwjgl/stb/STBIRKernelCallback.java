package org.lwjgl.stb;

import javax.annotation.Nullable;
import org.lwjgl.stb.STBIRKernelCallbackI;
import org.lwjgl.system.Callback;

public abstract class STBIRKernelCallback
extends Callback
implements STBIRKernelCallbackI {
    public static STBIRKernelCallback create(long functionPointer) {
        STBIRKernelCallbackI instance = (STBIRKernelCallbackI)Callback.get(functionPointer);
        return instance instanceof STBIRKernelCallback ? (STBIRKernelCallback)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static STBIRKernelCallback createSafe(long functionPointer) {
        return functionPointer == 0L ? null : STBIRKernelCallback.create(functionPointer);
    }

    public static STBIRKernelCallback create(STBIRKernelCallbackI instance) {
        return instance instanceof STBIRKernelCallback ? (STBIRKernelCallback)instance : new Container(instance.address(), instance);
    }

    protected STBIRKernelCallback() {
        super(CIF);
    }

    STBIRKernelCallback(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends STBIRKernelCallback {
        private final STBIRKernelCallbackI delegate;

        Container(long functionPointer, STBIRKernelCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public float invoke(float x, float scale, long user_data) {
            return this.delegate.invoke(x, scale, user_data);
        }
    }
}
