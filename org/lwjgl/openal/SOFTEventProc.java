package org.lwjgl.openal;

import javax.annotation.Nullable;
import org.lwjgl.openal.SOFTEventProcI;
import org.lwjgl.system.Callback;

public abstract class SOFTEventProc
extends Callback
implements SOFTEventProcI {
    public static SOFTEventProc create(long functionPointer) {
        SOFTEventProcI instance = (SOFTEventProcI)Callback.get(functionPointer);
        return instance instanceof SOFTEventProc ? (SOFTEventProc)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static SOFTEventProc createSafe(long functionPointer) {
        return functionPointer == 0L ? null : SOFTEventProc.create(functionPointer);
    }

    public static SOFTEventProc create(SOFTEventProcI instance) {
        return instance instanceof SOFTEventProc ? (SOFTEventProc)instance : new Container(instance.address(), instance);
    }

    protected SOFTEventProc() {
        super(CIF);
    }

    SOFTEventProc(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends SOFTEventProc {
        private final SOFTEventProcI delegate;

        Container(long functionPointer, SOFTEventProcI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(int eventType, int object, int param, int length, long message, long userParam) {
            this.delegate.invoke(eventType, object, param, length, message, userParam);
        }
    }
}
