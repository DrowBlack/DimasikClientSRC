package org.lwjgl.stb;

import javax.annotation.Nullable;
import org.lwjgl.stb.STBIEOFCallbackI;
import org.lwjgl.system.Callback;

public abstract class STBIEOFCallback
extends Callback
implements STBIEOFCallbackI {
    public static STBIEOFCallback create(long functionPointer) {
        STBIEOFCallbackI instance = (STBIEOFCallbackI)Callback.get(functionPointer);
        return instance instanceof STBIEOFCallback ? (STBIEOFCallback)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static STBIEOFCallback createSafe(long functionPointer) {
        return functionPointer == 0L ? null : STBIEOFCallback.create(functionPointer);
    }

    public static STBIEOFCallback create(STBIEOFCallbackI instance) {
        return instance instanceof STBIEOFCallback ? (STBIEOFCallback)instance : new Container(instance.address(), instance);
    }

    protected STBIEOFCallback() {
        super(CIF);
    }

    STBIEOFCallback(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends STBIEOFCallback {
        private final STBIEOFCallbackI delegate;

        Container(long functionPointer, STBIEOFCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long user) {
            return this.delegate.invoke(user);
        }
    }
}
