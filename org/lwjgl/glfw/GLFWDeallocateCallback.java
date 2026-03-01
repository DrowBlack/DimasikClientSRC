package org.lwjgl.glfw;

import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFWDeallocateCallbackI;
import org.lwjgl.system.Callback;

public abstract class GLFWDeallocateCallback
extends Callback
implements GLFWDeallocateCallbackI {
    public static GLFWDeallocateCallback create(long functionPointer) {
        GLFWDeallocateCallbackI instance = (GLFWDeallocateCallbackI)Callback.get(functionPointer);
        return instance instanceof GLFWDeallocateCallback ? (GLFWDeallocateCallback)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static GLFWDeallocateCallback createSafe(long functionPointer) {
        return functionPointer == 0L ? null : GLFWDeallocateCallback.create(functionPointer);
    }

    public static GLFWDeallocateCallback create(GLFWDeallocateCallbackI instance) {
        return instance instanceof GLFWDeallocateCallback ? (GLFWDeallocateCallback)instance : new Container(instance.address(), instance);
    }

    protected GLFWDeallocateCallback() {
        super(CIF);
    }

    GLFWDeallocateCallback(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends GLFWDeallocateCallback {
        private final GLFWDeallocateCallbackI delegate;

        Container(long functionPointer, GLFWDeallocateCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long block, long user) {
            this.delegate.invoke(block, user);
        }
    }
}
