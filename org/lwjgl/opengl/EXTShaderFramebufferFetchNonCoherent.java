package org.lwjgl.opengl;

import org.lwjgl.opengl.GL;

public class EXTShaderFramebufferFetchNonCoherent {
    protected EXTShaderFramebufferFetchNonCoherent() {
        throw new UnsupportedOperationException();
    }

    public static native void glFramebufferFetchBarrierEXT();

    static {
        GL.initialize();
    }
}
