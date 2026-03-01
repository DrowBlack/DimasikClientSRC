package org.lwjgl.opengl;

import org.lwjgl.opengl.GL;
import org.lwjgl.system.Checks;
import org.lwjgl.system.JNI;
import org.lwjgl.system.NativeType;

public class GLXSGIMakeCurrentRead {
    protected GLXSGIMakeCurrentRead() {
        throw new UnsupportedOperationException();
    }

    @NativeType(value="Bool")
    public static boolean glXMakeCurrentReadSGI(@NativeType(value="Display *") long display, @NativeType(value="GLXDrawable") long draw, @NativeType(value="GLXDrawable") long read, @NativeType(value="GLXContext") long ctx) {
        long __functionAddress = GL.getCapabilitiesGLXClient().glXMakeCurrentReadSGI;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
            Checks.check(display);
        }
        return JNI.callPPPPI(display, draw, read, ctx, __functionAddress) != 0;
    }

    @NativeType(value="GLXDrawable")
    public static long glXGetCurrentReadDrawableSGI() {
        long __functionAddress = GL.getCapabilitiesGLXClient().glXGetCurrentReadDrawableSGI;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        return JNI.callP(__functionAddress);
    }
}
