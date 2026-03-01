package org.lwjgl.openal;

import javax.annotation.Nullable;
import org.lwjgl.openal.SOFTCallbackBufferTypeI;
import org.lwjgl.system.Callback;

public abstract class SOFTCallbackBufferType
extends Callback
implements SOFTCallbackBufferTypeI {
    public static SOFTCallbackBufferType create(long functionPointer) {
        SOFTCallbackBufferTypeI instance = (SOFTCallbackBufferTypeI)Callback.get(functionPointer);
        return instance instanceof SOFTCallbackBufferType ? (SOFTCallbackBufferType)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static SOFTCallbackBufferType createSafe(long functionPointer) {
        return functionPointer == 0L ? null : SOFTCallbackBufferType.create(functionPointer);
    }

    public static SOFTCallbackBufferType create(SOFTCallbackBufferTypeI instance) {
        return instance instanceof SOFTCallbackBufferType ? (SOFTCallbackBufferType)instance : new Container(instance.address(), instance);
    }

    protected SOFTCallbackBufferType() {
        super(CIF);
    }

    SOFTCallbackBufferType(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends SOFTCallbackBufferType {
        private final SOFTCallbackBufferTypeI delegate;

        Container(long functionPointer, SOFTCallbackBufferTypeI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public long invoke(long userptr, long sampledata, int numbytes) {
            return this.delegate.invoke(userptr, sampledata, numbytes);
        }
    }
}
