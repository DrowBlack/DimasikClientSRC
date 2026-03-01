package org.lwjgl.stb;

import org.lwjgl.stb.LibSTB;
import org.lwjgl.stb.STBRPContext;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.stb.STBRPRect;
import org.lwjgl.system.NativeType;

public class STBRectPack {
    public static final int STBRP__MAXVAL = Integer.MAX_VALUE;
    public static final int STBRP_HEURISTIC_Skyline_default = 0;
    public static final int STBRP_HEURISTIC_Skyline_BL_sortHeight = 0;
    public static final int STBRP_HEURISTIC_Skyline_BF_sortHeight = 1;

    protected STBRectPack() {
        throw new UnsupportedOperationException();
    }

    public static native int nstbrp_pack_rects(long var0, long var2, int var4);

    public static int stbrp_pack_rects(@NativeType(value="stbrp_context *") STBRPContext context, @NativeType(value="stbrp_rect *") STBRPRect.Buffer rects) {
        return STBRectPack.nstbrp_pack_rects(context.address(), rects.address(), rects.remaining());
    }

    public static native void nstbrp_init_target(long var0, int var2, int var3, long var4, int var6);

    public static void stbrp_init_target(@NativeType(value="stbrp_context *") STBRPContext context, int width, int height, @NativeType(value="stbrp_node *") STBRPNode.Buffer nodes) {
        STBRectPack.nstbrp_init_target(context.address(), width, height, nodes.address(), nodes.remaining());
    }

    public static native void nstbrp_setup_allow_out_of_mem(long var0, int var2);

    public static void stbrp_setup_allow_out_of_mem(@NativeType(value="stbrp_context *") STBRPContext context, @NativeType(value="int") boolean allow_out_of_mem) {
        STBRectPack.nstbrp_setup_allow_out_of_mem(context.address(), allow_out_of_mem ? 1 : 0);
    }

    public static native void nstbrp_setup_heuristic(long var0, int var2);

    public static void stbrp_setup_heuristic(@NativeType(value="stbrp_context *") STBRPContext context, int heuristic) {
        STBRectPack.nstbrp_setup_heuristic(context.address(), heuristic);
    }

    static {
        LibSTB.initialize();
    }
}
