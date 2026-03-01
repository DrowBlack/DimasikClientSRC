package org.lwjgl.stb;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.stb.STBIReadCallbackI;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryUtil;

public abstract class STBIReadCallback
extends Callback
implements STBIReadCallbackI {
    public static STBIReadCallback create(long functionPointer) {
        STBIReadCallbackI instance = (STBIReadCallbackI)Callback.get(functionPointer);
        return instance instanceof STBIReadCallback ? (STBIReadCallback)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static STBIReadCallback createSafe(long functionPointer) {
        return functionPointer == 0L ? null : STBIReadCallback.create(functionPointer);
    }

    public static STBIReadCallback create(STBIReadCallbackI instance) {
        return instance instanceof STBIReadCallback ? (STBIReadCallback)instance : new Container(instance.address(), instance);
    }

    protected STBIReadCallback() {
        super(CIF);
    }

    STBIReadCallback(long functionPointer) {
        super(functionPointer);
    }

    public static ByteBuffer getData(long data, int size) {
        return MemoryUtil.memByteBuffer(data, size);
    }

    private static final class Container
    extends STBIReadCallback {
        private final STBIReadCallbackI delegate;

        Container(long functionPointer, STBIReadCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long user, long data, int size) {
            return this.delegate.invoke(user, data, size);
        }
    }
}
