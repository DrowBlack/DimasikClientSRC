package org.lwjgl.opengl;

import java.nio.Buffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Checks;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class NVTimelineSemaphore {
    public static final int GL_SEMAPHORE_TYPE_NV = 38323;
    public static final int GL_SEMAPHORE_TYPE_BINARY_NV = 38324;
    public static final int GL_SEMAPHORE_TYPE_TIMELINE_NV = 38325;
    public static final int GL_TIMELINE_SEMAPHORE_VALUE_NV = 38293;
    public static final int GL_MAX_TIMELINE_SEMAPHORE_VALUE_DIFFERENCE_NV = 38326;

    protected NVTimelineSemaphore() {
        throw new UnsupportedOperationException();
    }

    public static native void nglCreateSemaphoresNV(int var0, long var1);

    public static void glCreateSemaphoresNV(@NativeType(value="GLuint *") IntBuffer semaphores) {
        NVTimelineSemaphore.nglCreateSemaphoresNV(semaphores.remaining(), MemoryUtil.memAddress(semaphores));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NativeType(value="void")
    public static int glCreateSemaphoresNV() {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            IntBuffer semaphores = stack.callocInt(1);
            NVTimelineSemaphore.nglCreateSemaphoresNV(1, MemoryUtil.memAddress(semaphores));
            int n = semaphores.get(0);
            return n;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nglSemaphoreParameterivNV(int var0, int var1, long var2);

    public static void glSemaphoreParameterivNV(@NativeType(value="GLuint") int semaphore, @NativeType(value="GLenum") int pname, @NativeType(value="GLint const *") IntBuffer params) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)params, 1);
        }
        NVTimelineSemaphore.nglSemaphoreParameterivNV(semaphore, pname, MemoryUtil.memAddress(params));
    }

    public static native void nglGetSemaphoreParameterivNV(int var0, int var1, long var2);

    public static void glGetSemaphoreParameterivNV(@NativeType(value="GLuint") int semaphore, @NativeType(value="GLenum") int pname, @NativeType(value="GLint *") IntBuffer params) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)params, 1);
        }
        NVTimelineSemaphore.nglGetSemaphoreParameterivNV(semaphore, pname, MemoryUtil.memAddress(params));
    }

    public static void glCreateSemaphoresNV(@NativeType(value="GLuint *") int[] semaphores) {
        long __functionAddress = GL.getICD().glCreateSemaphoresNV;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.callPV(semaphores.length, semaphores, __functionAddress);
    }

    public static void glSemaphoreParameterivNV(@NativeType(value="GLuint") int semaphore, @NativeType(value="GLenum") int pname, @NativeType(value="GLint const *") int[] params) {
        long __functionAddress = GL.getICD().glSemaphoreParameterivNV;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
            Checks.check(params, 1);
        }
        JNI.callPV(semaphore, pname, params, __functionAddress);
    }

    public static void glGetSemaphoreParameterivNV(@NativeType(value="GLuint") int semaphore, @NativeType(value="GLenum") int pname, @NativeType(value="GLint *") int[] params) {
        long __functionAddress = GL.getICD().glGetSemaphoreParameterivNV;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
            Checks.check(params, 1);
        }
        JNI.callPV(semaphore, pname, params, __functionAddress);
    }

    static {
        GL.initialize();
    }
}
