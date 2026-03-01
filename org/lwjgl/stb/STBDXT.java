package org.lwjgl.stb;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.lwjgl.stb.LibSTB;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class STBDXT {
    public static final int STB_DXT_NORMAL = 0;
    public static final int STB_DXT_DITHER = 1;
    public static final int STB_DXT_HIGHQUAL = 2;

    protected STBDXT() {
        throw new UnsupportedOperationException();
    }

    public static native void nstb_compress_dxt_block(long var0, long var2, int var4, int var5);

    public static void stb_compress_dxt_block(@NativeType(value="unsigned char *") ByteBuffer dest, @NativeType(value="unsigned char const *") ByteBuffer src_rgba_four_bytes_per_pixel, @NativeType(value="int") boolean alpha, int mode) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)dest, alpha ? 16 : 8);
            Checks.check((Buffer)src_rgba_four_bytes_per_pixel, 64);
        }
        STBDXT.nstb_compress_dxt_block(MemoryUtil.memAddress(dest), MemoryUtil.memAddress(src_rgba_four_bytes_per_pixel), alpha ? 1 : 0, mode);
    }

    public static native void nstb_compress_bc4_block(long var0, long var2);

    public static void stb_compress_bc4_block(@NativeType(value="unsigned char *") ByteBuffer dest, @NativeType(value="unsigned char const *") ByteBuffer src_r_one_byte_per_pixel) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)dest, 8);
            Checks.check((Buffer)src_r_one_byte_per_pixel, 16);
        }
        STBDXT.nstb_compress_bc4_block(MemoryUtil.memAddress(dest), MemoryUtil.memAddress(src_r_one_byte_per_pixel));
    }

    public static native void nstb_compress_bc5_block(long var0, long var2);

    public static void stb_compress_bc5_block(@NativeType(value="unsigned char *") ByteBuffer dest, @NativeType(value="unsigned char const *") ByteBuffer src_rg_two_byte_per_pixel) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)dest, 16);
            Checks.check((Buffer)src_rg_two_byte_per_pixel, 32);
        }
        STBDXT.nstb_compress_bc5_block(MemoryUtil.memAddress(dest), MemoryUtil.memAddress(src_rg_two_byte_per_pixel));
    }

    static {
        LibSTB.initialize();
    }
}
