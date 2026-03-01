package org.lwjgl.glfw;

import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWPreeditCallbackI;
import org.lwjgl.system.Callback;

public abstract class GLFWPreeditCallback
extends Callback
implements GLFWPreeditCallbackI {
    public static GLFWPreeditCallback create(long functionPointer) {
        GLFWPreeditCallbackI instance = (GLFWPreeditCallbackI)Callback.get(functionPointer);
        return instance instanceof GLFWPreeditCallback ? (GLFWPreeditCallback)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static GLFWPreeditCallback createSafe(long functionPointer) {
        return functionPointer == 0L ? null : GLFWPreeditCallback.create(functionPointer);
    }

    public static GLFWPreeditCallback create(GLFWPreeditCallbackI instance) {
        return instance instanceof GLFWPreeditCallback ? (GLFWPreeditCallback)instance : new Container(instance.address(), instance);
    }

    protected GLFWPreeditCallback() {
        super(CIF);
    }

    GLFWPreeditCallback(long functionPointer) {
        super(functionPointer);
    }

    public GLFWPreeditCallback set(long window) {
        GLFW.glfwSetPreeditCallback(window, this);
        return this;
    }

    private static final class Container
    extends GLFWPreeditCallback {
        private final GLFWPreeditCallbackI delegate;

        Container(long functionPointer, GLFWPreeditCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long window, int preedit_count, long preedit_string, int block_count, long block_sizes, int focused_block, int caret) {
            this.delegate.invoke(window, preedit_count, preedit_string, block_count, block_sizes, focused_block, caret);
        }
    }
}
