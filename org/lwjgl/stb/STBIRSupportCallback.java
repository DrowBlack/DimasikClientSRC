package org.lwjgl.stb;

import javax.annotation.Nullable;
import org.lwjgl.stb.STBIRSupportCallbackI;
import org.lwjgl.system.Callback;

public abstract class STBIRSupportCallback
extends Callback
implements STBIRSupportCallbackI {
    public static STBIRSupportCallback create(long functionPointer) {
        STBIRSupportCallbackI instance = (STBIRSupportCallbackI)Callback.get(functionPointer);
        return instance instanceof STBIRSupportCallback ? (STBIRSupportCallback)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static STBIRSupportCallback createSafe(long functionPointer) {
        return functionPointer == 0L ? null : STBIRSupportCallback.create(functionPointer);
    }

    public static STBIRSupportCallback create(STBIRSupportCallbackI instance) {
        return instance instanceof STBIRSupportCallback ? (STBIRSupportCallback)instance : new Container(instance.address(), instance);
    }

    protected STBIRSupportCallback() {
        super(CIF);
    }

    STBIRSupportCallback(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends STBIRSupportCallback {
        private final STBIRSupportCallbackI delegate;

        Container(long functionPointer, STBIRSupportCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public float invoke(float scale, long user_data) {
            return this.delegate.invoke(scale, user_data);
        }
    }
}
