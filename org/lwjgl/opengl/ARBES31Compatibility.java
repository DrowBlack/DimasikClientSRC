package org.lwjgl.opengl;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL45C;
import org.lwjgl.system.NativeType;

public class ARBES31Compatibility {
    protected ARBES31Compatibility() {
        throw new UnsupportedOperationException();
    }

    public static void glMemoryBarrierByRegion(@NativeType(value="GLbitfield") int barriers) {
        GL45C.glMemoryBarrierByRegion(barriers);
    }

    static {
        GL.initialize();
    }
}
