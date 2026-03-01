package org.lwjgl.opengl;

import org.lwjgl.opengl.GL;

public class INTELFramebufferCMAA {
    protected INTELFramebufferCMAA() {
        throw new UnsupportedOperationException();
    }

    public static native void glApplyFramebufferAttachmentCMAAINTEL();

    static {
        GL.initialize();
    }
}
