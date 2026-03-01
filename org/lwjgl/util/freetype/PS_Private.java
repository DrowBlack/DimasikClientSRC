package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct PS_PrivateRec")
public class PS_Private
extends Struct<PS_Private> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int UNIQUE_ID;
    public static final int LENIV;
    public static final int NUM_BLUE_VALUES;
    public static final int NUM_OTHER_BLUES;
    public static final int NUM_FAMILY_BLUES;
    public static final int NUM_FAMILY_OTHER_BLUES;
    public static final int BLUE_VALUES;
    public static final int OTHER_BLUES;
    public static final int FAMILY_BLUES;
    public static final int FAMILY_OTHER_BLUES;
    public static final int BLUE_SCALE;
    public static final int BLUE_SHIFT;
    public static final int BLUE_FUZZ;
    public static final int STANDARD_WIDTH;
    public static final int STANDARD_HEIGHT;
    public static final int NUM_SNAP_WIDTHS;
    public static final int NUM_SNAP_HEIGHTS;
    public static final int FORCE_BOLD;
    public static final int ROUND_STEM_UP;
    public static final int SNAP_WIDTHS;
    public static final int SNAP_HEIGHTS;
    public static final int EXPANSION_FACTOR;
    public static final int LANGUAGE_GROUP;
    public static final int PASSWORD;
    public static final int MIN_FEATURE;

    protected PS_Private(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected PS_Private create(long address, @Nullable ByteBuffer container) {
        return new PS_Private(address, container);
    }

    public PS_Private(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), PS_Private.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Int")
    public int unique_id() {
        return PS_Private.nunique_id(this.address());
    }

    @NativeType(value="FT_Int")
    public int lenIV() {
        return PS_Private.nlenIV(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte num_blue_values() {
        return PS_Private.nnum_blue_values(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte num_other_blues() {
        return PS_Private.nnum_other_blues(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte num_family_blues() {
        return PS_Private.nnum_family_blues(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte num_family_other_blues() {
        return PS_Private.nnum_family_other_blues(this.address());
    }

    @NativeType(value="FT_Short[14]")
    public ShortBuffer blue_values() {
        return PS_Private.nblue_values(this.address());
    }

    @NativeType(value="FT_Short")
    public short blue_values(int index) {
        return PS_Private.nblue_values(this.address(), index);
    }

    @NativeType(value="FT_Short[10]")
    public ShortBuffer other_blues() {
        return PS_Private.nother_blues(this.address());
    }

    @NativeType(value="FT_Short")
    public short other_blues(int index) {
        return PS_Private.nother_blues(this.address(), index);
    }

    @NativeType(value="FT_Short[14]")
    public ShortBuffer family_blues() {
        return PS_Private.nfamily_blues(this.address());
    }

    @NativeType(value="FT_Short")
    public short family_blues(int index) {
        return PS_Private.nfamily_blues(this.address(), index);
    }

    @NativeType(value="FT_Short[10]")
    public ShortBuffer family_other_blues() {
        return PS_Private.nfamily_other_blues(this.address());
    }

    @NativeType(value="FT_Short")
    public short family_other_blues(int index) {
        return PS_Private.nfamily_other_blues(this.address(), index);
    }

    @NativeType(value="FT_Fixed")
    public long blue_scale() {
        return PS_Private.nblue_scale(this.address());
    }

    @NativeType(value="FT_Int")
    public int blue_shift() {
        return PS_Private.nblue_shift(this.address());
    }

    @NativeType(value="FT_Int")
    public int blue_fuzz() {
        return PS_Private.nblue_fuzz(this.address());
    }

    @NativeType(value="FT_UShort[1]")
    public ShortBuffer standard_width() {
        return PS_Private.nstandard_width(this.address());
    }

    @NativeType(value="FT_UShort")
    public short standard_width(int index) {
        return PS_Private.nstandard_width(this.address(), index);
    }

    @NativeType(value="FT_UShort[1]")
    public ShortBuffer standard_height() {
        return PS_Private.nstandard_height(this.address());
    }

    @NativeType(value="FT_UShort")
    public short standard_height(int index) {
        return PS_Private.nstandard_height(this.address(), index);
    }

    @NativeType(value="FT_Byte")
    public byte num_snap_widths() {
        return PS_Private.nnum_snap_widths(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte num_snap_heights() {
        return PS_Private.nnum_snap_heights(this.address());
    }

    @NativeType(value="FT_Bool")
    public boolean force_bold() {
        return PS_Private.nforce_bold(this.address());
    }

    @NativeType(value="FT_Bool")
    public boolean round_stem_up() {
        return PS_Private.nround_stem_up(this.address());
    }

    @NativeType(value="FT_Short[13]")
    public ShortBuffer snap_widths() {
        return PS_Private.nsnap_widths(this.address());
    }

    @NativeType(value="FT_Short")
    public short snap_widths(int index) {
        return PS_Private.nsnap_widths(this.address(), index);
    }

    @NativeType(value="FT_Short[13]")
    public ShortBuffer snap_heights() {
        return PS_Private.nsnap_heights(this.address());
    }

    @NativeType(value="FT_Short")
    public short snap_heights(int index) {
        return PS_Private.nsnap_heights(this.address(), index);
    }

    @NativeType(value="FT_Fixed")
    public long expansion_factor() {
        return PS_Private.nexpansion_factor(this.address());
    }

    @NativeType(value="FT_Long")
    public long language_group() {
        return PS_Private.nlanguage_group(this.address());
    }

    @NativeType(value="FT_Long")
    public long password() {
        return PS_Private.npassword(this.address());
    }

    @NativeType(value="FT_Short[2]")
    public ShortBuffer min_feature() {
        return PS_Private.nmin_feature(this.address());
    }

    @NativeType(value="FT_Short")
    public short min_feature(int index) {
        return PS_Private.nmin_feature(this.address(), index);
    }

    public static PS_Private create(long address) {
        return new PS_Private(address, null);
    }

    @Nullable
    public static PS_Private createSafe(long address) {
        return address == 0L ? null : new PS_Private(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static int nunique_id(long struct) {
        return UNSAFE.getInt(null, struct + (long)UNIQUE_ID);
    }

    public static int nlenIV(long struct) {
        return UNSAFE.getInt(null, struct + (long)LENIV);
    }

    public static byte nnum_blue_values(long struct) {
        return UNSAFE.getByte(null, struct + (long)NUM_BLUE_VALUES);
    }

    public static byte nnum_other_blues(long struct) {
        return UNSAFE.getByte(null, struct + (long)NUM_OTHER_BLUES);
    }

    public static byte nnum_family_blues(long struct) {
        return UNSAFE.getByte(null, struct + (long)NUM_FAMILY_BLUES);
    }

    public static byte nnum_family_other_blues(long struct) {
        return UNSAFE.getByte(null, struct + (long)NUM_FAMILY_OTHER_BLUES);
    }

    public static ShortBuffer nblue_values(long struct) {
        return MemoryUtil.memShortBuffer(struct + (long)BLUE_VALUES, 14);
    }

    public static short nblue_values(long struct, int index) {
        return UNSAFE.getShort(null, struct + (long)BLUE_VALUES + Checks.check(index, 14) * 2L);
    }

    public static ShortBuffer nother_blues(long struct) {
        return MemoryUtil.memShortBuffer(struct + (long)OTHER_BLUES, 10);
    }

    public static short nother_blues(long struct, int index) {
        return UNSAFE.getShort(null, struct + (long)OTHER_BLUES + Checks.check(index, 10) * 2L);
    }

    public static ShortBuffer nfamily_blues(long struct) {
        return MemoryUtil.memShortBuffer(struct + (long)FAMILY_BLUES, 14);
    }

    public static short nfamily_blues(long struct, int index) {
        return UNSAFE.getShort(null, struct + (long)FAMILY_BLUES + Checks.check(index, 14) * 2L);
    }

    public static ShortBuffer nfamily_other_blues(long struct) {
        return MemoryUtil.memShortBuffer(struct + (long)FAMILY_OTHER_BLUES, 10);
    }

    public static short nfamily_other_blues(long struct, int index) {
        return UNSAFE.getShort(null, struct + (long)FAMILY_OTHER_BLUES + Checks.check(index, 10) * 2L);
    }

    public static long nblue_scale(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)BLUE_SCALE);
    }

    public static int nblue_shift(long struct) {
        return UNSAFE.getInt(null, struct + (long)BLUE_SHIFT);
    }

    public static int nblue_fuzz(long struct) {
        return UNSAFE.getInt(null, struct + (long)BLUE_FUZZ);
    }

    public static ShortBuffer nstandard_width(long struct) {
        return MemoryUtil.memShortBuffer(struct + (long)STANDARD_WIDTH, 1);
    }

    public static short nstandard_width(long struct, int index) {
        return UNSAFE.getShort(null, struct + (long)STANDARD_WIDTH + Checks.check(index, 1) * 2L);
    }

    public static ShortBuffer nstandard_height(long struct) {
        return MemoryUtil.memShortBuffer(struct + (long)STANDARD_HEIGHT, 1);
    }

    public static short nstandard_height(long struct, int index) {
        return UNSAFE.getShort(null, struct + (long)STANDARD_HEIGHT + Checks.check(index, 1) * 2L);
    }

    public static byte nnum_snap_widths(long struct) {
        return UNSAFE.getByte(null, struct + (long)NUM_SNAP_WIDTHS);
    }

    public static byte nnum_snap_heights(long struct) {
        return UNSAFE.getByte(null, struct + (long)NUM_SNAP_HEIGHTS);
    }

    public static boolean nforce_bold(long struct) {
        return UNSAFE.getByte(null, struct + (long)FORCE_BOLD) != 0;
    }

    public static boolean nround_stem_up(long struct) {
        return UNSAFE.getByte(null, struct + (long)ROUND_STEM_UP) != 0;
    }

    public static ShortBuffer nsnap_widths(long struct) {
        return MemoryUtil.memShortBuffer(struct + (long)SNAP_WIDTHS, 13);
    }

    public static short nsnap_widths(long struct, int index) {
        return UNSAFE.getShort(null, struct + (long)SNAP_WIDTHS + Checks.check(index, 13) * 2L);
    }

    public static ShortBuffer nsnap_heights(long struct) {
        return MemoryUtil.memShortBuffer(struct + (long)SNAP_HEIGHTS, 13);
    }

    public static short nsnap_heights(long struct, int index) {
        return UNSAFE.getShort(null, struct + (long)SNAP_HEIGHTS + Checks.check(index, 13) * 2L);
    }

    public static long nexpansion_factor(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)EXPANSION_FACTOR);
    }

    public static long nlanguage_group(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)LANGUAGE_GROUP);
    }

    public static long npassword(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)PASSWORD);
    }

    public static ShortBuffer nmin_feature(long struct) {
        return MemoryUtil.memShortBuffer(struct + (long)MIN_FEATURE, 2);
    }

    public static short nmin_feature(long struct, int index) {
        return UNSAFE.getShort(null, struct + (long)MIN_FEATURE + Checks.check(index, 2) * 2L);
    }

    static {
        Struct.Layout layout = PS_Private.__struct(PS_Private.__member(4), PS_Private.__member(4), PS_Private.__member(1), PS_Private.__member(1), PS_Private.__member(1), PS_Private.__member(1), PS_Private.__array(2, 14), PS_Private.__array(2, 10), PS_Private.__array(2, 14), PS_Private.__array(2, 10), PS_Private.__member(CLONG_SIZE), PS_Private.__member(4), PS_Private.__member(4), PS_Private.__array(2, 1), PS_Private.__array(2, 1), PS_Private.__member(1), PS_Private.__member(1), PS_Private.__member(1), PS_Private.__member(1), PS_Private.__array(2, 13), PS_Private.__array(2, 13), PS_Private.__member(CLONG_SIZE), PS_Private.__member(CLONG_SIZE), PS_Private.__member(CLONG_SIZE), PS_Private.__array(2, 2));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        UNIQUE_ID = layout.offsetof(0);
        LENIV = layout.offsetof(1);
        NUM_BLUE_VALUES = layout.offsetof(2);
        NUM_OTHER_BLUES = layout.offsetof(3);
        NUM_FAMILY_BLUES = layout.offsetof(4);
        NUM_FAMILY_OTHER_BLUES = layout.offsetof(5);
        BLUE_VALUES = layout.offsetof(6);
        OTHER_BLUES = layout.offsetof(7);
        FAMILY_BLUES = layout.offsetof(8);
        FAMILY_OTHER_BLUES = layout.offsetof(9);
        BLUE_SCALE = layout.offsetof(10);
        BLUE_SHIFT = layout.offsetof(11);
        BLUE_FUZZ = layout.offsetof(12);
        STANDARD_WIDTH = layout.offsetof(13);
        STANDARD_HEIGHT = layout.offsetof(14);
        NUM_SNAP_WIDTHS = layout.offsetof(15);
        NUM_SNAP_HEIGHTS = layout.offsetof(16);
        FORCE_BOLD = layout.offsetof(17);
        ROUND_STEM_UP = layout.offsetof(18);
        SNAP_WIDTHS = layout.offsetof(19);
        SNAP_HEIGHTS = layout.offsetof(20);
        EXPANSION_FACTOR = layout.offsetof(21);
        LANGUAGE_GROUP = layout.offsetof(22);
        PASSWORD = layout.offsetof(23);
        MIN_FEATURE = layout.offsetof(24);
    }

    public static class Buffer
    extends StructBuffer<PS_Private, Buffer> {
        private static final PS_Private ELEMENT_FACTORY = PS_Private.create(-1L);

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
        protected PS_Private getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Int")
        public int unique_id() {
            return PS_Private.nunique_id(this.address());
        }

        @NativeType(value="FT_Int")
        public int lenIV() {
            return PS_Private.nlenIV(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte num_blue_values() {
            return PS_Private.nnum_blue_values(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte num_other_blues() {
            return PS_Private.nnum_other_blues(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte num_family_blues() {
            return PS_Private.nnum_family_blues(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte num_family_other_blues() {
            return PS_Private.nnum_family_other_blues(this.address());
        }

        @NativeType(value="FT_Short[14]")
        public ShortBuffer blue_values() {
            return PS_Private.nblue_values(this.address());
        }

        @NativeType(value="FT_Short")
        public short blue_values(int index) {
            return PS_Private.nblue_values(this.address(), index);
        }

        @NativeType(value="FT_Short[10]")
        public ShortBuffer other_blues() {
            return PS_Private.nother_blues(this.address());
        }

        @NativeType(value="FT_Short")
        public short other_blues(int index) {
            return PS_Private.nother_blues(this.address(), index);
        }

        @NativeType(value="FT_Short[14]")
        public ShortBuffer family_blues() {
            return PS_Private.nfamily_blues(this.address());
        }

        @NativeType(value="FT_Short")
        public short family_blues(int index) {
            return PS_Private.nfamily_blues(this.address(), index);
        }

        @NativeType(value="FT_Short[10]")
        public ShortBuffer family_other_blues() {
            return PS_Private.nfamily_other_blues(this.address());
        }

        @NativeType(value="FT_Short")
        public short family_other_blues(int index) {
            return PS_Private.nfamily_other_blues(this.address(), index);
        }

        @NativeType(value="FT_Fixed")
        public long blue_scale() {
            return PS_Private.nblue_scale(this.address());
        }

        @NativeType(value="FT_Int")
        public int blue_shift() {
            return PS_Private.nblue_shift(this.address());
        }

        @NativeType(value="FT_Int")
        public int blue_fuzz() {
            return PS_Private.nblue_fuzz(this.address());
        }

        @NativeType(value="FT_UShort[1]")
        public ShortBuffer standard_width() {
            return PS_Private.nstandard_width(this.address());
        }

        @NativeType(value="FT_UShort")
        public short standard_width(int index) {
            return PS_Private.nstandard_width(this.address(), index);
        }

        @NativeType(value="FT_UShort[1]")
        public ShortBuffer standard_height() {
            return PS_Private.nstandard_height(this.address());
        }

        @NativeType(value="FT_UShort")
        public short standard_height(int index) {
            return PS_Private.nstandard_height(this.address(), index);
        }

        @NativeType(value="FT_Byte")
        public byte num_snap_widths() {
            return PS_Private.nnum_snap_widths(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte num_snap_heights() {
            return PS_Private.nnum_snap_heights(this.address());
        }

        @NativeType(value="FT_Bool")
        public boolean force_bold() {
            return PS_Private.nforce_bold(this.address());
        }

        @NativeType(value="FT_Bool")
        public boolean round_stem_up() {
            return PS_Private.nround_stem_up(this.address());
        }

        @NativeType(value="FT_Short[13]")
        public ShortBuffer snap_widths() {
            return PS_Private.nsnap_widths(this.address());
        }

        @NativeType(value="FT_Short")
        public short snap_widths(int index) {
            return PS_Private.nsnap_widths(this.address(), index);
        }

        @NativeType(value="FT_Short[13]")
        public ShortBuffer snap_heights() {
            return PS_Private.nsnap_heights(this.address());
        }

        @NativeType(value="FT_Short")
        public short snap_heights(int index) {
            return PS_Private.nsnap_heights(this.address(), index);
        }

        @NativeType(value="FT_Fixed")
        public long expansion_factor() {
            return PS_Private.nexpansion_factor(this.address());
        }

        @NativeType(value="FT_Long")
        public long language_group() {
            return PS_Private.nlanguage_group(this.address());
        }

        @NativeType(value="FT_Long")
        public long password() {
            return PS_Private.npassword(this.address());
        }

        @NativeType(value="FT_Short[2]")
        public ShortBuffer min_feature() {
            return PS_Private.nmin_feature(this.address());
        }

        @NativeType(value="FT_Short")
        public short min_feature(int index) {
            return PS_Private.nmin_feature(this.address(), index);
        }
    }
}
