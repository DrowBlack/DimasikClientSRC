package org.lwjgl.stb;

import javax.annotation.Nullable;
import org.lwjgl.stb.STBISkipCallbackI;
import org.lwjgl.system.Callback;

public abstract class STBISkipCallback
extends Callback
implements STBISkipCallbackI {
    public static STBISkipCallback create(long functionPointer) {
        STBISkipCallbackI instance = (STBISkipCallbackI)Callback.get(functionPointer);
        return instance instanceof STBISkipCallback ? (STBISkipCallback)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static STBISkipCallback createSafe(long functionPointer) {
        return functionPointer == 0L ? null : STBISkipCallback.create(functionPointer);
    }

    public static STBISkipCallback create(STBISkipCallbackI instance) {
        return instance instanceof STBISkipCallback ? (STBISkipCallback)instance : new Container(instance.address(), instance);
    }

    protected STBISkipCallback() {
        super(CIF);
    }

    STBISkipCallback(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends STBISkipCallback {
        private final STBISkipCallbackI delegate;

        Container(long functionPointer, STBISkipCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long user, int n) {
            this.delegate.invoke(user, n);
        }
    }
}
