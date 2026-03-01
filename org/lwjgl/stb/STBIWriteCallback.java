package org.lwjgl.stb;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.stb.STBIWriteCallbackI;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryUtil;

public abstract class STBIWriteCallback
extends Callback
implements STBIWriteCallbackI {
    public static STBIWriteCallback create(long functionPointer) {
        STBIWriteCallbackI instance = (STBIWriteCallbackI)Callback.get(functionPointer);
        return instance instanceof STBIWriteCallback ? (STBIWriteCallback)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static STBIWriteCallback createSafe(long functionPointer) {
        return functionPointer == 0L ? null : STBIWriteCallback.create(functionPointer);
    }

    public static STBIWriteCallback create(STBIWriteCallbackI instance) {
        return instance instanceof STBIWriteCallback ? (STBIWriteCallback)instance : new Container(instance.address(), instance);
    }

    protected STBIWriteCallback() {
        super(CIF);
    }

    STBIWriteCallback(long functionPointer) {
        super(functionPointer);
    }

    public static ByteBuffer getData(long data, int size) {
        return MemoryUtil.memByteBuffer(data, size);
    }

    private static final class Container
    extends STBIWriteCallback {
        private final STBIWriteCallbackI delegate;

        Container(long functionPointer, STBIWriteCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long context, long data, int size) {
            this.delegate.invoke(context, data, size);
        }
    }
}
