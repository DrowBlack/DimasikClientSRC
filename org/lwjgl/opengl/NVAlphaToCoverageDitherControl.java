package org.lwjgl.opengl;

import org.lwjgl.opengl.GL;
import org.lwjgl.system.NativeType;

public class NVAlphaToCoverageDitherControl {
    public static final int GL_ALPHA_TO_COVERAGE_DITHER_DEFAULT_NV = 37709;
    public static final int GL_ALPHA_TO_COVERAGE_DITHER_ENABLE_NV = 37710;
    public static final int GL_ALPHA_TO_COVERAGE_DITHER_DISABLE_NV = 37711;
    public static final int GL_ALPHA_TO_COVERAGE_DITHER_MODE_NV = 37567;

    protected NVAlphaToCoverageDitherControl() {
        throw new UnsupportedOperationException();
    }

    public static native void glAlphaToCoverageDitherControlNV(@NativeType(value="GLenum") int var0);

    static {
        GL.initialize();
    }
}
