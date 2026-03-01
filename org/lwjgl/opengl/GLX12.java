package org.lwjgl.opengl;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLX11;
import org.lwjgl.system.Checks;
import org.lwjgl.system.JNI;
import org.lwjgl.system.NativeType;

public class GLX12
extends GLX11 {
    protected GLX12() {
        throw new UnsupportedOperationException();
    }

    @NativeType(value="Display *")
    public static long glXGetCurrentDisplay() {
        long __functionAddress = GL.getCapabilitiesGLXClient().glXGetCurrentDisplay;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        return JNI.callP(__functionAddress);
    }
}
