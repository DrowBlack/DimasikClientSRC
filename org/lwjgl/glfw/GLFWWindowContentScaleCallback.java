package org.lwjgl.glfw;

import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowContentScaleCallbackI;
import org.lwjgl.system.Callback;

public abstract class GLFWWindowContentScaleCallback
extends Callback
implements GLFWWindowContentScaleCallbackI {
    public static GLFWWindowContentScaleCallback create(long functionPointer) {
        GLFWWindowContentScaleCallbackI instance = (GLFWWindowContentScaleCallbackI)Callback.get(functionPointer);
        return instance instanceof GLFWWindowContentScaleCallback ? (GLFWWindowContentScaleCallback)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static GLFWWindowContentScaleCallback createSafe(long functionPointer) {
        return functionPointer == 0L ? null : GLFWWindowContentScaleCallback.create(functionPointer);
    }

    public static GLFWWindowContentScaleCallback create(GLFWWindowContentScaleCallbackI instance) {
        return instance instanceof GLFWWindowContentScaleCallback ? (GLFWWindowContentScaleCallback)instance : new Container(instance.address(), instance);
    }

    protected GLFWWindowContentScaleCallback() {
        super(CIF);
    }

    GLFWWindowContentScaleCallback(long functionPointer) {
        super(functionPointer);
    }

    public GLFWWindowContentScaleCallback set(long window) {
        GLFW.glfwSetWindowContentScaleCallback(window, this);
        return this;
    }

    private static final class Container
    extends GLFWWindowContentScaleCallback {
        private final GLFWWindowContentScaleCallbackI delegate;

        Container(long functionPointer, GLFWWindowContentScaleCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long window, float xscale, float yscale) {
            this.delegate.invoke(window, xscale, yscale);
        }
    }
}
