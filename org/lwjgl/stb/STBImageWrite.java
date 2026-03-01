package org.lwjgl.stb;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.LibSTB;
import org.lwjgl.stb.STBIWriteCallbackI;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class STBImageWrite {
    public static final IntBuffer stbi_write_png_compression_level;
    public static final IntBuffer stbi_write_force_png_filter;
    public static final PointerBuffer stbi_zlib_compress;
    public static final IntBuffer stbi_write_tga_with_rle;

    protected STBImageWrite() {
        throw new UnsupportedOperationException();
    }

    public static native int nstbi_write_png(long var0, int var2, int var3, int var4, long var5, int var7);

    @NativeType(value="int")
    public static boolean stbi_write_png(@NativeType(value="char const *") ByteBuffer filename, int w, int h, int comp, @NativeType(value="void const *") ByteBuffer data, int stride_in_bytes) {
        if (Checks.CHECKS) {
            Checks.checkNT1(filename);
            Checks.check((Buffer)data, (stride_in_bytes != 0 ? stride_in_bytes : w * comp) * h);
        }
        return STBImageWrite.nstbi_write_png(MemoryUtil.memAddress(filename), w, h, comp, MemoryUtil.memAddress(data), stride_in_bytes) != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NativeType(value="int")
    public static boolean stbi_write_png(@NativeType(value="char const *") CharSequence filename, int w, int h, int comp, @NativeType(value="void const *") ByteBuffer data, int stride_in_bytes) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)data, (stride_in_bytes != 0 ? stride_in_bytes : w * comp) * h);
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(filename, true);
            long filenameEncoded = stack.getPointerAddress();
            boolean bl = STBImageWrite.nstbi_write_png(filenameEncoded, w, h, comp, MemoryUtil.memAddress(data), stride_in_bytes) != 0;
            return bl;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    private static native long nstbi_write_png_compression_level();

    @NativeType(value="int *")
    private static IntBuffer stbi_write_png_compression_level() {
        long __result = STBImageWrite.nstbi_write_png_compression_level();
        return MemoryUtil.memIntBuffer(__result, 1);
    }

    private static native long nstbi_write_force_png_filter();

    @NativeType(value="int *")
    private static IntBuffer stbi_write_force_png_filter() {
        long __result = STBImageWrite.nstbi_write_force_png_filter();
        return MemoryUtil.memIntBuffer(__result, 1);
    }

    private static native long nstbi_zlib_compress();

    @NativeType(value="unsigned char * (*) (unsigned char *, int, int *, int) *")
    private static PointerBuffer stbi_zlib_compress() {
        long __result = STBImageWrite.nstbi_zlib_compress();
        return MemoryUtil.memPointerBuffer(__result, 1);
    }

    public static native int nstbi_write_bmp(long var0, int var2, int var3, int var4, long var5);

    @NativeType(value="int")
    public static boolean stbi_write_bmp(@NativeType(value="char const *") ByteBuffer filename, int w, int h, int comp, @NativeType(value="void const *") ByteBuffer data) {
        if (Checks.CHECKS) {
            Checks.checkNT1(filename);
            Checks.check((Buffer)data, w * h * comp);
        }
        return STBImageWrite.nstbi_write_bmp(MemoryUtil.memAddress(filename), w, h, comp, MemoryUtil.memAddress(data)) != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NativeType(value="int")
    public static boolean stbi_write_bmp(@NativeType(value="char const *") CharSequence filename, int w, int h, int comp, @NativeType(value="void const *") ByteBuffer data) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)data, w * h * comp);
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(filename, true);
            long filenameEncoded = stack.getPointerAddress();
            boolean bl = STBImageWrite.nstbi_write_bmp(filenameEncoded, w, h, comp, MemoryUtil.memAddress(data)) != 0;
            return bl;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native int nstbi_write_tga(long var0, int var2, int var3, int var4, long var5);

    @NativeType(value="int")
    public static boolean stbi_write_tga(@NativeType(value="char const *") ByteBuffer filename, int w, int h, int comp, @NativeType(value="void const *") ByteBuffer data) {
        if (Checks.CHECKS) {
            Checks.checkNT1(filename);
            Checks.check((Buffer)data, w * h * comp);
        }
        return STBImageWrite.nstbi_write_tga(MemoryUtil.memAddress(filename), w, h, comp, MemoryUtil.memAddress(data)) != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NativeType(value="int")
    public static boolean stbi_write_tga(@NativeType(value="char const *") CharSequence filename, int w, int h, int comp, @NativeType(value="void const *") ByteBuffer data) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)data, w * h * comp);
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(filename, true);
            long filenameEncoded = stack.getPointerAddress();
            boolean bl = STBImageWrite.nstbi_write_tga(filenameEncoded, w, h, comp, MemoryUtil.memAddress(data)) != 0;
            return bl;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    private static native long nstbi_write_tga_with_rle();

    @NativeType(value="int *")
    private static IntBuffer stbi_write_tga_with_rle() {
        long __result = STBImageWrite.nstbi_write_tga_with_rle();
        return MemoryUtil.memIntBuffer(__result, 1);
    }

    public static native int nstbi_write_hdr(long var0, int var2, int var3, int var4, long var5);

    @NativeType(value="int")
    public static boolean stbi_write_hdr(@NativeType(value="char const *") ByteBuffer filename, int w, int h, int comp, @NativeType(value="float const *") FloatBuffer data) {
        if (Checks.CHECKS) {
            Checks.checkNT1(filename);
            Checks.check((Buffer)data, w * h * comp);
        }
        return STBImageWrite.nstbi_write_hdr(MemoryUtil.memAddress(filename), w, h, comp, MemoryUtil.memAddress(data)) != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NativeType(value="int")
    public static boolean stbi_write_hdr(@NativeType(value="char const *") CharSequence filename, int w, int h, int comp, @NativeType(value="float const *") FloatBuffer data) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)data, w * h * comp);
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(filename, true);
            long filenameEncoded = stack.getPointerAddress();
            boolean bl = STBImageWrite.nstbi_write_hdr(filenameEncoded, w, h, comp, MemoryUtil.memAddress(data)) != 0;
            return bl;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native int nstbi_write_jpg(long var0, int var2, int var3, int var4, long var5, int var7);

    @NativeType(value="int")
    public static boolean stbi_write_jpg(@NativeType(value="char const *") ByteBuffer filename, int w, int h, int comp, @NativeType(value="void const *") ByteBuffer data, int quality) {
        if (Checks.CHECKS) {
            Checks.checkNT1(filename);
            Checks.check((Buffer)data, w * h * comp);
        }
        return STBImageWrite.nstbi_write_jpg(MemoryUtil.memAddress(filename), w, h, comp, MemoryUtil.memAddress(data), quality) != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NativeType(value="int")
    public static boolean stbi_write_jpg(@NativeType(value="char const *") CharSequence filename, int w, int h, int comp, @NativeType(value="void const *") ByteBuffer data, int quality) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)data, w * h * comp);
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(filename, true);
            long filenameEncoded = stack.getPointerAddress();
            boolean bl = STBImageWrite.nstbi_write_jpg(filenameEncoded, w, h, comp, MemoryUtil.memAddress(data), quality) != 0;
            return bl;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native int nstbi_write_png_to_func(long var0, long var2, int var4, int var5, int var6, long var7, int var9);

    @NativeType(value="int")
    public static boolean stbi_write_png_to_func(@NativeType(value="stbi_write_func *") STBIWriteCallbackI func, @NativeType(value="void *") long context, int w, int h, int comp, @NativeType(value="void const *") ByteBuffer data, int stride_in_bytes) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)data, (stride_in_bytes != 0 ? stride_in_bytes : w * comp) * h);
        }
        return STBImageWrite.nstbi_write_png_to_func(func.address(), context, w, h, comp, MemoryUtil.memAddress(data), stride_in_bytes) != 0;
    }

    public static native int nstbi_write_bmp_to_func(long var0, long var2, int var4, int var5, int var6, long var7);

    @NativeType(value="int")
    public static boolean stbi_write_bmp_to_func(@NativeType(value="stbi_write_func *") STBIWriteCallbackI func, @NativeType(value="void *") long context, int w, int h, int comp, @NativeType(value="void const *") ByteBuffer data) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)data, w * h * comp);
        }
        return STBImageWrite.nstbi_write_bmp_to_func(func.address(), context, w, h, comp, MemoryUtil.memAddress(data)) != 0;
    }

    public static native int nstbi_write_tga_to_func(long var0, long var2, int var4, int var5, int var6, long var7);

    @NativeType(value="int")
    public static boolean stbi_write_tga_to_func(@NativeType(value="stbi_write_func *") STBIWriteCallbackI func, @NativeType(value="void *") long context, int w, int h, int comp, @NativeType(value="void const *") ByteBuffer data) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)data, w * h * comp);
        }
        return STBImageWrite.nstbi_write_tga_to_func(func.address(), context, w, h, comp, MemoryUtil.memAddress(data)) != 0;
    }

    public static native int nstbi_write_hdr_to_func(long var0, long var2, int var4, int var5, int var6, long var7);

    @NativeType(value="int")
    public static boolean stbi_write_hdr_to_func(@NativeType(value="stbi_write_func *") STBIWriteCallbackI func, @NativeType(value="void *") long context, int w, int h, int comp, @NativeType(value="float const *") FloatBuffer data) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)data, w * h * comp);
        }
        return STBImageWrite.nstbi_write_hdr_to_func(func.address(), context, w, h, comp, MemoryUtil.memAddress(data)) != 0;
    }

    public static native int nstbi_write_jpg_to_func(long var0, long var2, int var4, int var5, int var6, long var7, int var9);

    public static int stbi_write_jpg_to_func(@NativeType(value="stbi_write_func *") STBIWriteCallbackI func, @NativeType(value="void *") long context, int w, int h, int comp, @NativeType(value="void const *") ByteBuffer data, int quality) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)data, w * h * comp);
        }
        return STBImageWrite.nstbi_write_jpg_to_func(func.address(), context, w, h, comp, MemoryUtil.memAddress(data), quality);
    }

    public static native void nstbi_flip_vertically_on_write(int var0);

    public static void stbi_flip_vertically_on_write(@NativeType(value="int") boolean flip_boolean) {
        STBImageWrite.nstbi_flip_vertically_on_write(flip_boolean ? 1 : 0);
    }

    public static native int nstbi_write_hdr(long var0, int var2, int var3, int var4, float[] var5);

    @NativeType(value="int")
    public static boolean stbi_write_hdr(@NativeType(value="char const *") ByteBuffer filename, int w, int h, int comp, @NativeType(value="float const *") float[] data) {
        if (Checks.CHECKS) {
            Checks.checkNT1(filename);
            Checks.check(data, w * h * comp);
        }
        return STBImageWrite.nstbi_write_hdr(MemoryUtil.memAddress(filename), w, h, comp, data) != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NativeType(value="int")
    public static boolean stbi_write_hdr(@NativeType(value="char const *") CharSequence filename, int w, int h, int comp, @NativeType(value="float const *") float[] data) {
        if (Checks.CHECKS) {
            Checks.check(data, w * h * comp);
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(filename, true);
            long filenameEncoded = stack.getPointerAddress();
            boolean bl = STBImageWrite.nstbi_write_hdr(filenameEncoded, w, h, comp, data) != 0;
            return bl;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native int nstbi_write_hdr_to_func(long var0, long var2, int var4, int var5, int var6, float[] var7);

    @NativeType(value="int")
    public static boolean stbi_write_hdr_to_func(@NativeType(value="stbi_write_func *") STBIWriteCallbackI func, @NativeType(value="void *") long context, int w, int h, int comp, @NativeType(value="float const *") float[] data) {
        if (Checks.CHECKS) {
            Checks.check(data, w * h * comp);
        }
        return STBImageWrite.nstbi_write_hdr_to_func(func.address(), context, w, h, comp, data) != 0;
    }

    static {
        LibSTB.initialize();
        stbi_write_png_compression_level = STBImageWrite.stbi_write_png_compression_level();
        stbi_write_force_png_filter = STBImageWrite.stbi_write_force_png_filter();
        stbi_zlib_compress = STBImageWrite.stbi_zlib_compress();
        stbi_write_tga_with_rle = STBImageWrite.stbi_write_tga_with_rle();
    }
}
