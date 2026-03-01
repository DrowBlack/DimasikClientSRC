package org.lwjgl.opengl;

import java.nio.Buffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Checks;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class MESAFramebufferFlipY {
    public static final int GL_FRAMEBUFFER_FLIP_Y_MESA = 35771;

    protected MESAFramebufferFlipY() {
        throw new UnsupportedOperationException();
    }

    public static native void glFramebufferParameteriMESA(@NativeType(value="GLenum") int var0, @NativeType(value="GLenum") int var1, @NativeType(value="GLint") int var2);

    public static native void nglGetFramebufferParameterivMESA(int var0, int var1, long var2);

    public static void glGetFramebufferParameterivMESA(@NativeType(value="GLenum") int target, @NativeType(value="GLenum") int pname, @NativeType(value="GLint *") IntBuffer params) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)params, 1);
        }
        MESAFramebufferFlipY.nglGetFramebufferParameterivMESA(target, pname, MemoryUtil.memAddress(params));
    }

    public static void glGetFramebufferParameterivMESA(@NativeType(value="GLenum") int target, @NativeType(value="GLenum") int pname, @NativeType(value="GLint *") int[] params) {
        long __functionAddress = GL.getICD().glGetFramebufferParameterivMESA;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
            Checks.check(params, 1);
        }
        JNI.callPV(target, pname, params, __functionAddress);
    }

    static {
        GL.initialize();
    }
}
