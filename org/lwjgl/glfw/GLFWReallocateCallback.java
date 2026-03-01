package org.lwjgl.glfw;

import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFWReallocateCallbackI;
import org.lwjgl.system.Callback;

public abstract class GLFWReallocateCallback
extends Callback
implements GLFWReallocateCallbackI {
    public static GLFWReallocateCallback create(long functionPointer) {
        GLFWReallocateCallbackI instance = (GLFWReallocateCallbackI)Callback.get(functionPointer);
        return instance instanceof GLFWReallocateCallback ? (GLFWReallocateCallback)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static GLFWReallocateCallback createSafe(long functionPointer) {
        return functionPointer == 0L ? null : GLFWReallocateCallback.create(functionPointer);
    }

    public static GLFWReallocateCallback create(GLFWReallocateCallbackI instance) {
        return instance instanceof GLFWReallocateCallback ? (GLFWReallocateCallback)instance : new Container(instance.address(), instance);
    }

    protected GLFWReallocateCallback() {
        super(CIF);
    }

    GLFWReallocateCallback(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends GLFWReallocateCallback {
        private final GLFWReallocateCallbackI delegate;

        Container(long functionPointer, GLFWReallocateCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public long invoke(long block, long size, long user) {
            return this.delegate.invoke(block, size, user);
        }
    }
}
