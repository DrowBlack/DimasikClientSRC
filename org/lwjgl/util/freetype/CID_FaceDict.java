package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_Matrix;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.PS_Private;

@NativeType(value="struct CID_FaceDictRec")
public class CID_FaceDict
extends Struct<CID_FaceDict> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int PRIVATE_DICT;
    public static final int LEN_BUILDCHAR;
    public static final int FORCEBOLD_THRESHOLD;
    public static final int STROKE_WIDTH;
    public static final int EXPANSION_FACTOR;
    public static final int PAINT_TYPE;
    public static final int FONT_TYPE;
    public static final int FONT_MATRIX;
    public static final int FONT_OFFSET;
    public static final int NUM_SUBRS;
    public static final int SUBRMAP_OFFSET;
    public static final int SD_BYTES;

    protected CID_FaceDict(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected CID_FaceDict create(long address, @Nullable ByteBuffer container) {
        return new CID_FaceDict(address, container);
    }

    public CID_FaceDict(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), CID_FaceDict.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="PS_PrivateRec")
    public PS_Private private_dict() {
        return CID_FaceDict.nprivate_dict(this.address());
    }

    @NativeType(value="FT_UInt")
    public int len_buildchar() {
        return CID_FaceDict.nlen_buildchar(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long forcebold_threshold() {
        return CID_FaceDict.nforcebold_threshold(this.address());
    }

    @NativeType(value="FT_Pos")
    public long stroke_width() {
        return CID_FaceDict.nstroke_width(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long expansion_factor() {
        return CID_FaceDict.nexpansion_factor(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte paint_type() {
        return CID_FaceDict.npaint_type(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte font_type() {
        return CID_FaceDict.nfont_type(this.address());
    }

    public FT_Matrix font_matrix() {
        return CID_FaceDict.nfont_matrix(this.address());
    }

    public FT_Vector font_offset() {
        return CID_FaceDict.nfont_offset(this.address());
    }

    @NativeType(value="FT_UInt")
    public int num_subrs() {
        return CID_FaceDict.nnum_subrs(this.address());
    }

    @NativeType(value="FT_ULong")
    public long subrmap_offset() {
        return CID_FaceDict.nsubrmap_offset(this.address());
    }

    @NativeType(value="FT_UInt")
    public int sd_bytes() {
        return CID_FaceDict.nsd_bytes(this.address());
    }

    public static CID_FaceDict create(long address) {
        return new CID_FaceDict(address, null);
    }

    @Nullable
    public static CID_FaceDict createSafe(long address) {
        return address == 0L ? null : new CID_FaceDict(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static PS_Private nprivate_dict(long struct) {
        return PS_Private.create(struct + (long)PRIVATE_DICT);
    }

    public static int nlen_buildchar(long struct) {
        return UNSAFE.getInt(null, struct + (long)LEN_BUILDCHAR);
    }

    public static long nforcebold_threshold(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)FORCEBOLD_THRESHOLD);
    }

    public static long nstroke_width(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)STROKE_WIDTH);
    }

    public static long nexpansion_factor(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)EXPANSION_FACTOR);
    }

    public static byte npaint_type(long struct) {
        return UNSAFE.getByte(null, struct + (long)PAINT_TYPE);
    }

    public static byte nfont_type(long struct) {
        return UNSAFE.getByte(null, struct + (long)FONT_TYPE);
    }

    public static FT_Matrix nfont_matrix(long struct) {
        return FT_Matrix.create(struct + (long)FONT_MATRIX);
    }

    public static FT_Vector nfont_offset(long struct) {
        return FT_Vector.create(struct + (long)FONT_OFFSET);
    }

    public static int nnum_subrs(long struct) {
        return UNSAFE.getInt(null, struct + (long)NUM_SUBRS);
    }

    public static long nsubrmap_offset(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)SUBRMAP_OFFSET);
    }

    public static int nsd_bytes(long struct) {
        return UNSAFE.getInt(null, struct + (long)SD_BYTES);
    }

    static {
        Struct.Layout layout = CID_FaceDict.__struct(CID_FaceDict.__member(PS_Private.SIZEOF, PS_Private.ALIGNOF), CID_FaceDict.__member(4), CID_FaceDict.__member(CLONG_SIZE), CID_FaceDict.__member(CLONG_SIZE), CID_FaceDict.__member(CLONG_SIZE), CID_FaceDict.__member(1), CID_FaceDict.__member(1), CID_FaceDict.__member(FT_Matrix.SIZEOF, FT_Matrix.ALIGNOF), CID_FaceDict.__member(FT_Vector.SIZEOF, FT_Vector.ALIGNOF), CID_FaceDict.__member(4), CID_FaceDict.__member(CLONG_SIZE), CID_FaceDict.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        PRIVATE_DICT = layout.offsetof(0);
        LEN_BUILDCHAR = layout.offsetof(1);
        FORCEBOLD_THRESHOLD = layout.offsetof(2);
        STROKE_WIDTH = layout.offsetof(3);
        EXPANSION_FACTOR = layout.offsetof(4);
        PAINT_TYPE = layout.offsetof(5);
        FONT_TYPE = layout.offsetof(6);
        FONT_MATRIX = layout.offsetof(7);
        FONT_OFFSET = layout.offsetof(8);
        NUM_SUBRS = layout.offsetof(9);
        SUBRMAP_OFFSET = layout.offsetof(10);
        SD_BYTES = layout.offsetof(11);
    }

    public static class Buffer
    extends StructBuffer<CID_FaceDict, Buffer> {
        private static final CID_FaceDict ELEMENT_FACTORY = CID_FaceDict.create(-1L);

        public Buffer(ByteBuffer container) {
            super(container, container.remaining() / SIZEOF);
        }

        public Buffer(long address, int cap) {
            super(address, null, -1, 0, cap, cap);
        }

        Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
            super(address, container, mark, pos, lim, cap);
        }

        @Override
        protected Buffer self() {
            return this;
        }

        @Override
        protected CID_FaceDict getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="PS_PrivateRec")
        public PS_Private private_dict() {
            return CID_FaceDict.nprivate_dict(this.address());
        }

        @NativeType(value="FT_UInt")
        public int len_buildchar() {
            return CID_FaceDict.nlen_buildchar(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long forcebold_threshold() {
            return CID_FaceDict.nforcebold_threshold(this.address());
        }

        @NativeType(value="FT_Pos")
        public long stroke_width() {
            return CID_FaceDict.nstroke_width(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long expansion_factor() {
            return CID_FaceDict.nexpansion_factor(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte paint_type() {
            return CID_FaceDict.npaint_type(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte font_type() {
            return CID_FaceDict.nfont_type(this.address());
        }

        public FT_Matrix font_matrix() {
            return CID_FaceDict.nfont_matrix(this.address());
        }

        public FT_Vector font_offset() {
            return CID_FaceDict.nfont_offset(this.address());
        }

        @NativeType(value="FT_UInt")
        public int num_subrs() {
            return CID_FaceDict.nnum_subrs(this.address());
        }

        @NativeType(value="FT_ULong")
        public long subrmap_offset() {
            return CID_FaceDict.nsubrmap_offset(this.address());
        }

        @NativeType(value="FT_UInt")
        public int sd_bytes() {
            return CID_FaceDict.nsd_bytes(this.address());
        }
    }
}
