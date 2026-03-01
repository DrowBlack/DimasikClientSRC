package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct PS_FontInfoRec")
public class PS_FontInfo
extends Struct<PS_FontInfo> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int VERSION;
    public static final int NOTICE;
    public static final int FULL_NAME;
    public static final int FAMILY_NAME;
    public static final int WEIGHT;
    public static final int ITALIC_ANGLE;
    public static final int IS_FIXED_PITCH;
    public static final int UNDERLINE_POSITION;
    public static final int UNDERLINE_THICKNESS;

    protected PS_FontInfo(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected PS_FontInfo create(long address, @Nullable ByteBuffer container) {
        return new PS_FontInfo(address, container);
    }

    public PS_FontInfo(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), PS_FontInfo.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_String *")
    public ByteBuffer version() {
        return PS_FontInfo.nversion(this.address());
    }

    @NativeType(value="FT_String *")
    public String versionString() {
        return PS_FontInfo.nversionString(this.address());
    }

    @NativeType(value="FT_String *")
    public ByteBuffer notice() {
        return PS_FontInfo.nnotice(this.address());
    }

    @NativeType(value="FT_String *")
    public String noticeString() {
        return PS_FontInfo.nnoticeString(this.address());
    }

    @NativeType(value="FT_String *")
    public ByteBuffer full_name() {
        return PS_FontInfo.nfull_name(this.address());
    }

    @NativeType(value="FT_String *")
    public String full_nameString() {
        return PS_FontInfo.nfull_nameString(this.address());
    }

    @NativeType(value="FT_String *")
    public ByteBuffer family_name() {
        return PS_FontInfo.nfamily_name(this.address());
    }

    @NativeType(value="FT_String *")
    public String family_nameString() {
        return PS_FontInfo.nfamily_nameString(this.address());
    }

    @NativeType(value="FT_String *")
    public ByteBuffer weight() {
        return PS_FontInfo.nweight(this.address());
    }

    @NativeType(value="FT_String *")
    public String weightString() {
        return PS_FontInfo.nweightString(this.address());
    }

    @NativeType(value="FT_Long")
    public long italic_angle() {
        return PS_FontInfo.nitalic_angle(this.address());
    }

    @NativeType(value="FT_Bool")
    public boolean is_fixed_pitch() {
        return PS_FontInfo.nis_fixed_pitch(this.address());
    }

    @NativeType(value="FT_Short")
    public short underline_position() {
        return PS_FontInfo.nunderline_position(this.address());
    }

    @NativeType(value="FT_UShort")
    public short underline_thickness() {
        return PS_FontInfo.nunderline_thickness(this.address());
    }

    public static PS_FontInfo create(long address) {
        return new PS_FontInfo(address, null);
    }

    @Nullable
    public static PS_FontInfo createSafe(long address) {
        return address == 0L ? null : new PS_FontInfo(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static ByteBuffer nversion(long struct) {
        return MemoryUtil.memByteBufferNT1(MemoryUtil.memGetAddress(struct + (long)VERSION));
    }

    public static String nversionString(long struct) {
        return MemoryUtil.memUTF8(MemoryUtil.memGetAddress(struct + (long)VERSION));
    }

    public static ByteBuffer nnotice(long struct) {
        return MemoryUtil.memByteBufferNT1(MemoryUtil.memGetAddress(struct + (long)NOTICE));
    }

    public static String nnoticeString(long struct) {
        return MemoryUtil.memUTF8(MemoryUtil.memGetAddress(struct + (long)NOTICE));
    }

    public static ByteBuffer nfull_name(long struct) {
        return MemoryUtil.memByteBufferNT1(MemoryUtil.memGetAddress(struct + (long)FULL_NAME));
    }

    public static String nfull_nameString(long struct) {
        return MemoryUtil.memUTF8(MemoryUtil.memGetAddress(struct + (long)FULL_NAME));
    }

    public static ByteBuffer nfamily_name(long struct) {
        return MemoryUtil.memByteBufferNT1(MemoryUtil.memGetAddress(struct + (long)FAMILY_NAME));
    }

    public static String nfamily_nameString(long struct) {
        return MemoryUtil.memUTF8(MemoryUtil.memGetAddress(struct + (long)FAMILY_NAME));
    }

    public static ByteBuffer nweight(long struct) {
        return MemoryUtil.memByteBufferNT1(MemoryUtil.memGetAddress(struct + (long)WEIGHT));
    }

    public static String nweightString(long struct) {
        return MemoryUtil.memUTF8(MemoryUtil.memGetAddress(struct + (long)WEIGHT));
    }

    public static long nitalic_angle(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)ITALIC_ANGLE);
    }

    public static boolean nis_fixed_pitch(long struct) {
        return UNSAFE.getByte(null, struct + (long)IS_FIXED_PITCH) != 0;
    }

    public static short nunderline_position(long struct) {
        return UNSAFE.getShort(null, struct + (long)UNDERLINE_POSITION);
    }

    public static short nunderline_thickness(long struct) {
        return UNSAFE.getShort(null, struct + (long)UNDERLINE_THICKNESS);
    }

    static {
        Struct.Layout layout = PS_FontInfo.__struct(PS_FontInfo.__member(POINTER_SIZE), PS_FontInfo.__member(POINTER_SIZE), PS_FontInfo.__member(POINTER_SIZE), PS_FontInfo.__member(POINTER_SIZE), PS_FontInfo.__member(POINTER_SIZE), PS_FontInfo.__member(CLONG_SIZE), PS_FontInfo.__member(1), PS_FontInfo.__member(2), PS_FontInfo.__member(2));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        VERSION = layout.offsetof(0);
        NOTICE = layout.offsetof(1);
        FULL_NAME = layout.offsetof(2);
        FAMILY_NAME = layout.offsetof(3);
        WEIGHT = layout.offsetof(4);
        ITALIC_ANGLE = layout.offsetof(5);
        IS_FIXED_PITCH = layout.offsetof(6);
        UNDERLINE_POSITION = layout.offsetof(7);
        UNDERLINE_THICKNESS = layout.offsetof(8);
    }

    public static class Buffer
    extends StructBuffer<PS_FontInfo, Buffer> {
        private static final PS_FontInfo ELEMENT_FACTORY = PS_FontInfo.create(-1L);

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
        protected PS_FontInfo getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_String *")
        public ByteBuffer version() {
            return PS_FontInfo.nversion(this.address());
        }

        @NativeType(value="FT_String *")
        public String versionString() {
            return PS_FontInfo.nversionString(this.address());
        }

        @NativeType(value="FT_String *")
        public ByteBuffer notice() {
            return PS_FontInfo.nnotice(this.address());
        }

        @NativeType(value="FT_String *")
        public String noticeString() {
            return PS_FontInfo.nnoticeString(this.address());
        }

        @NativeType(value="FT_String *")
        public ByteBuffer full_name() {
            return PS_FontInfo.nfull_name(this.address());
        }

        @NativeType(value="FT_String *")
        public String full_nameString() {
            return PS_FontInfo.nfull_nameString(this.address());
        }

        @NativeType(value="FT_String *")
        public ByteBuffer family_name() {
            return PS_FontInfo.nfamily_name(this.address());
        }

        @NativeType(value="FT_String *")
        public String family_nameString() {
            return PS_FontInfo.nfamily_nameString(this.address());
        }

        @NativeType(value="FT_String *")
        public ByteBuffer weight() {
            return PS_FontInfo.nweight(this.address());
        }

        @NativeType(value="FT_String *")
        public String weightString() {
            return PS_FontInfo.nweightString(this.address());
        }

        @NativeType(value="FT_Long")
        public long italic_angle() {
            return PS_FontInfo.nitalic_angle(this.address());
        }

        @NativeType(value="FT_Bool")
        public boolean is_fixed_pitch() {
            return PS_FontInfo.nis_fixed_pitch(this.address());
        }

        @NativeType(value="FT_Short")
        public short underline_position() {
            return PS_FontInfo.nunderline_position(this.address());
        }

        @NativeType(value="FT_UShort")
        public short underline_thickness() {
            return PS_FontInfo.nunderline_thickness(this.address());
        }
    }
}
