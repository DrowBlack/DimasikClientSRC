package org.lwjgl.opengl;

import org.lwjgl.opengl.GL;

public class GREMEDYFrameTerminator {
    protected GREMEDYFrameTerminator() {
        throw new UnsupportedOperationException();
    }

    public static native void glFrameTerminatorGREMEDY();

    static {
        GL.initialize();
    }
}
