package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class WGL {
    public static final int WGL_FONT_LINES = 0;
    public static final int WGL_FONT_POLYGONS = 1;
    public static final int WGL_SWAP_MAIN_PLANE = 1;
    public static final int WGL_SWAP_OVERLAY1 = 2;
    public static final int WGL_SWAP_OVERLAY2 = 4;
    public static final int WGL_SWAP_OVERLAY3 = 8;
    public static final int WGL_SWAP_OVERLAY4 = 16;
    public static final int WGL_SWAP_OVERLAY5 = 32;
    public static final int WGL_SWAP_OVERLAY6 = 64;
    public static final int WGL_SWAP_OVERLAY7 = 128;
    public static final int WGL_SWAP_OVERLAY8 = 256;
    public static final int WGL_SWAP_OVERLAY9 = 512;
    public static final int WGL_SWAP_OVERLAY10 = 1024;
    public static final int WGL_SWAP_OVERLAY11 = 2048;
    public static final int WGL_SWAP_OVERLAY12 = 4096;
    public static final int WGL_SWAP_OVERLAY13 = 8192;
    public static final int WGL_SWAP_OVERLAY14 = 16384;
    public static final int WGL_SWAP_OVERLAY15 = 32768;
    public static final int WGL_SWAP_UNDERLAY1 = 65536;
    public static final int WGL_SWAP_UNDERLAY2 = 131072;
    public static final int WGL_SWAP_UNDERLAY3 = 262144;
    public static final int WGL_SWAP_UNDERLAY4 = 524288;
    public static final int WGL_SWAP_UNDERLAY5 = 0x100000;
    public static final int WGL_SWAP_UNDERLAY6 = 0x200000;
    public static final int WGL_SWAP_UNDERLAY7 = 0x400000;
    public static final int WGL_SWAP_UNDERLAY8 = 0x800000;
    public static final int WGL_SWAP_UNDERLAY9 = 0x1000000;
    public static final int WGL_SWAP_UNDERLAY10 = 0x2000000;
    public static final int WGL_SWAP_UNDERLAY11 = 0x4000000;
    public static final int WGL_SWAP_UNDERLAY12 = 0x8000000;
    public static final int WGL_SWAP_UNDERLAY13 = 0x10000000;
    public static final int WGL_SWAP_UNDERLAY14 = 0x20000000;
    public static final int WGL_SWAP_UNDERLAY15 = 0x40000000;

    protected WGL() {
        throw new UnsupportedOperationException();
    }

    public static native long nwglCreateContext(long var0, long var2);

    @NativeType(value="HGLRC")
    public static long wglCreateContext(@NativeType(value="HDC") long hdc) {
        long __functionAddress = Functions.CreateContext;
        if (Checks.CHECKS) {
            Checks.check(hdc);
        }
        return WGL.nwglCreateContext(hdc, __functionAddress);
    }

    public static native long nwglCreateLayerContext(long var0, int var2, long var3);

    @NativeType(value="HGLRC")
    public static long wglCreateLayerContext(@NativeType(value="HDC") long hdc, int layerPlane) {
        long __functionAddress = Functions.CreateLayerContext;
        if (Checks.CHECKS) {
            Checks.check(hdc);
        }
        return WGL.nwglCreateLayerContext(hdc, layerPlane, __functionAddress);
    }

    public static native int nwglCopyContext(long var0, long var2, int var4, long var5);

    @NativeType(value="BOOL")
    public static boolean wglCopyContext(@NativeType(value="HGLRC") long src, @NativeType(value="HGLRC") long dst, @NativeType(value="UINT") int mask) {
        long __functionAddress = Functions.CopyContext;
        if (Checks.CHECKS) {
            Checks.check(src);
            Checks.check(dst);
        }
        return WGL.nwglCopyContext(src, dst, mask, __functionAddress) != 0;
    }

    public static native int nwglDeleteContext(long var0, long var2);

    @NativeType(value="BOOL")
    public static boolean wglDeleteContext(@NativeType(value="HGLRC") long context) {
        long __functionAddress = Functions.DeleteContext;
        if (Checks.CHECKS) {
            Checks.check(context);
        }
        return WGL.nwglDeleteContext(context, __functionAddress) != 0;
    }

    public static native long nwglGetCurrentContext(long var0);

    @NativeType(value="HGLRC")
    public static long wglGetCurrentContext() {
        long __functionAddress = Functions.GetCurrentContext;
        return WGL.nwglGetCurrentContext(__functionAddress);
    }

    public static native long nwglGetCurrentDC(long var0);

    @NativeType(value="HDC")
    public static long wglGetCurrentDC() {
        long __functionAddress = Functions.GetCurrentDC;
        return WGL.nwglGetCurrentDC(__functionAddress);
    }

    public static native long nwglGetProcAddress(long var0, long var2);

    public static long nwglGetProcAddress(long proc) {
        long __functionAddress = Functions.GetProcAddress;
        return WGL.nwglGetProcAddress(proc, __functionAddress);
    }

    @NativeType(value="PROC")
    public static long wglGetProcAddress(@NativeType(value="LPCSTR") ByteBuffer proc) {
        if (Checks.CHECKS) {
            Checks.checkNT1(proc);
        }
        return WGL.nwglGetProcAddress(MemoryUtil.memAddress(proc));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NativeType(value="PROC")
    public static long wglGetProcAddress(@NativeType(value="LPCSTR") CharSequence proc) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nASCII(proc, true);
            long procEncoded = stack.getPointerAddress();
            long l = WGL.nwglGetProcAddress(procEncoded);
            return l;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native int nwglMakeCurrent(long var0, long var2, long var4);

    @NativeType(value="BOOL")
    public static boolean wglMakeCurrent(@NativeType(value="HDC") long hdc, @NativeType(value="HGLRC") long hglrc) {
        long __functionAddress = Functions.MakeCurrent;
        return WGL.nwglMakeCurrent(hdc, hglrc, __functionAddress) != 0;
    }

    public static native int nwglShareLists(long var0, long var2, long var4);

    @NativeType(value="BOOL")
    public static boolean wglShareLists(@NativeType(value="HGLRC") long hglrc1, @NativeType(value="HGLRC") long hglrc2) {
        long __functionAddress = Functions.ShareLists;
        if (Checks.CHECKS) {
            Checks.check(hglrc1);
            Checks.check(hglrc2);
        }
        return WGL.nwglShareLists(hglrc1, hglrc2, __functionAddress) != 0;
    }

    static {
        GL.initialize();
    }

    public static final class Functions {
        public static final long CreateContext = APIUtil.apiGetFunctionAddress(GL.getFunctionProvider(), "wglCreateContext");
        public static final long CreateLayerContext = APIUtil.apiGetFunctionAddress(GL.getFunctionProvider(), "wglCreateLayerContext");
        public static final long CopyContext = APIUtil.apiGetFunctionAddress(GL.getFunctionProvider(), "wglCopyContext");
        public static final long DeleteContext = APIUtil.apiGetFunctionAddress(GL.getFunctionProvider(), "wglDeleteContext");
        public static final long GetCurrentContext = APIUtil.apiGetFunctionAddress(GL.getFunctionProvider(), "wglGetCurrentContext");
        public static final long GetCurrentDC = APIUtil.apiGetFunctionAddress(GL.getFunctionProvider(), "wglGetCurrentDC");
        public static final long GetProcAddress = APIUtil.apiGetFunctionAddress(GL.getFunctionProvider(), "wglGetProcAddress");
        public static final long MakeCurrent = APIUtil.apiGetFunctionAddress(GL.getFunctionProvider(), "wglMakeCurrent");
        public static final long ShareLists = APIUtil.apiGetFunctionAddress(GL.getFunctionProvider(), "wglShareLists");

        private Functions() {
        }
    }
}
