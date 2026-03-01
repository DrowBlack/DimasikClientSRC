package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_BBox;
import org.lwjgl.util.freetype.FT_Bitmap_Size;
import org.lwjgl.util.freetype.FT_CharMap;
import org.lwjgl.util.freetype.FT_Generic;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FT_List;
import org.lwjgl.util.freetype.FT_Memory;
import org.lwjgl.util.freetype.FT_Size;
import org.lwjgl.util.freetype.FT_Stream;

@NativeType(value="struct FT_FaceRec")
public class FT_Face
extends Struct<FT_Face> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int NUM_FACES;
    public static final int FACE_INDEX;
    public static final int FACE_FLAGS;
    public static final int STYLE_FLAGS;
    public static final int NUM_GLYPHS;
    public static final int FAMILY_NAME;
    public static final int STYLE_NAME;
    public static final int NUM_FIXED_SIZES;
    public static final int AVAILABLE_SIZES;
    public static final int NUM_CHARMAPS;
    public static final int CHARMAPS;
    public static final int GENERIC;
    public static final int BBOX;
    public static final int UNITS_PER_EM;
    public static final int ASCENDER;
    public static final int DESCENDER;
    public static final int HEIGHT;
    public static final int MAX_ADVANCE_WIDTH;
    public static final int MAX_ADVANCE_HEIGHT;
    public static final int UNDERLINE_POSITION;
    public static final int UNDERLINE_THICKNESS;
    public static final int GLYPH;
    public static final int SIZE;
    public static final int CHARMAP;
    public static final int DRIVER;
    public static final int MEMORY;
    public static final int STREAM;
    public static final int SIZES_LIST;
    public static final int AUTOHINT;
    public static final int EXTENSIONS;
    public static final int INTERNAL;

    protected FT_Face(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Face create(long address, @Nullable ByteBuffer container) {
        return new FT_Face(address, container);
    }

    public FT_Face(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Face.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Long")
    public long num_faces() {
        return FT_Face.nnum_faces(this.address());
    }

    @NativeType(value="FT_Long")
    public long face_index() {
        return FT_Face.nface_index(this.address());
    }

    @NativeType(value="FT_Long")
    public long face_flags() {
        return FT_Face.nface_flags(this.address());
    }

    @NativeType(value="FT_Long")
    public long style_flags() {
        return FT_Face.nstyle_flags(this.address());
    }

    @NativeType(value="FT_Long")
    public long num_glyphs() {
        return FT_Face.nnum_glyphs(this.address());
    }

    @NativeType(value="FT_String *")
    public ByteBuffer family_name() {
        return FT_Face.nfamily_name(this.address());
    }

    @NativeType(value="FT_String *")
    public String family_nameString() {
        return FT_Face.nfamily_nameString(this.address());
    }

    @NativeType(value="FT_String *")
    public ByteBuffer style_name() {
        return FT_Face.nstyle_name(this.address());
    }

    @NativeType(value="FT_String *")
    public String style_nameString() {
        return FT_Face.nstyle_nameString(this.address());
    }

    @NativeType(value="FT_Int")
    public int num_fixed_sizes() {
        return FT_Face.nnum_fixed_sizes(this.address());
    }

    @Nullable
    @NativeType(value="FT_Bitmap_Size *")
    public FT_Bitmap_Size.Buffer available_sizes() {
        return FT_Face.navailable_sizes(this.address());
    }

    @NativeType(value="FT_Int")
    public int num_charmaps() {
        return FT_Face.nnum_charmaps(this.address());
    }

    @NativeType(value="FT_CharMap *")
    public PointerBuffer charmaps() {
        return FT_Face.ncharmaps(this.address());
    }

    public FT_Generic generic() {
        return FT_Face.ngeneric(this.address());
    }

    public FT_BBox bbox() {
        return FT_Face.nbbox(this.address());
    }

    @NativeType(value="FT_UShort")
    public short units_per_EM() {
        return FT_Face.nunits_per_EM(this.address());
    }

    @NativeType(value="FT_Short")
    public short ascender() {
        return FT_Face.nascender(this.address());
    }

    @NativeType(value="FT_Short")
    public short descender() {
        return FT_Face.ndescender(this.address());
    }

    @NativeType(value="FT_Short")
    public short height() {
        return FT_Face.nheight(this.address());
    }

    @NativeType(value="FT_Short")
    public short max_advance_width() {
        return FT_Face.nmax_advance_width(this.address());
    }

    @NativeType(value="FT_Short")
    public short max_advance_height() {
        return FT_Face.nmax_advance_height(this.address());
    }

    @NativeType(value="FT_Short")
    public short underline_position() {
        return FT_Face.nunderline_position(this.address());
    }

    @NativeType(value="FT_Short")
    public short underline_thickness() {
        return FT_Face.nunderline_thickness(this.address());
    }

    @Nullable
    public FT_GlyphSlot glyph() {
        return FT_Face.nglyph(this.address());
    }

    @Nullable
    public FT_Size size() {
        return FT_Face.nsize(this.address());
    }

    @Nullable
    public FT_CharMap charmap() {
        return FT_Face.ncharmap(this.address());
    }

    public static FT_Face create(long address) {
        return new FT_Face(address, null);
    }

    @Nullable
    public static FT_Face createSafe(long address) {
        return address == 0L ? null : new FT_Face(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static long nnum_faces(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)NUM_FACES);
    }

    public static long nface_index(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)FACE_INDEX);
    }

    public static long nface_flags(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)FACE_FLAGS);
    }

    public static long nstyle_flags(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)STYLE_FLAGS);
    }

    public static long nnum_glyphs(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)NUM_GLYPHS);
    }

    public static ByteBuffer nfamily_name(long struct) {
        return MemoryUtil.memByteBufferNT1(MemoryUtil.memGetAddress(struct + (long)FAMILY_NAME));
    }

    public static String nfamily_nameString(long struct) {
        return MemoryUtil.memUTF8(MemoryUtil.memGetAddress(struct + (long)FAMILY_NAME));
    }

    public static ByteBuffer nstyle_name(long struct) {
        return MemoryUtil.memByteBufferNT1(MemoryUtil.memGetAddress(struct + (long)STYLE_NAME));
    }

    public static String nstyle_nameString(long struct) {
        return MemoryUtil.memUTF8(MemoryUtil.memGetAddress(struct + (long)STYLE_NAME));
    }

    public static int nnum_fixed_sizes(long struct) {
        return UNSAFE.getInt(null, struct + (long)NUM_FIXED_SIZES);
    }

    @Nullable
    public static FT_Bitmap_Size.Buffer navailable_sizes(long struct) {
        return FT_Bitmap_Size.createSafe(MemoryUtil.memGetAddress(struct + (long)AVAILABLE_SIZES), FT_Face.nnum_fixed_sizes(struct));
    }

    public static int nnum_charmaps(long struct) {
        return UNSAFE.getInt(null, struct + (long)NUM_CHARMAPS);
    }

    public static PointerBuffer ncharmaps(long struct) {
        return MemoryUtil.memPointerBuffer(MemoryUtil.memGetAddress(struct + (long)CHARMAPS), FT_Face.nnum_charmaps(struct));
    }

    public static FT_Generic ngeneric(long struct) {
        return FT_Generic.create(struct + (long)GENERIC);
    }

    public static FT_BBox nbbox(long struct) {
        return FT_BBox.create(struct + (long)BBOX);
    }

    public static short nunits_per_EM(long struct) {
        return UNSAFE.getShort(null, struct + (long)UNITS_PER_EM);
    }

    public static short nascender(long struct) {
        return UNSAFE.getShort(null, struct + (long)ASCENDER);
    }

    public static short ndescender(long struct) {
        return UNSAFE.getShort(null, struct + (long)DESCENDER);
    }

    public static short nheight(long struct) {
        return UNSAFE.getShort(null, struct + (long)HEIGHT);
    }

    public static short nmax_advance_width(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAX_ADVANCE_WIDTH);
    }

    public static short nmax_advance_height(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAX_ADVANCE_HEIGHT);
    }

    public static short nunderline_position(long struct) {
        return UNSAFE.getShort(null, struct + (long)UNDERLINE_POSITION);
    }

    public static short nunderline_thickness(long struct) {
        return UNSAFE.getShort(null, struct + (long)UNDERLINE_THICKNESS);
    }

    @Nullable
    public static FT_GlyphSlot nglyph(long struct) {
        return FT_GlyphSlot.createSafe(MemoryUtil.memGetAddress(struct + (long)GLYPH));
    }

    @Nullable
    public static FT_Size nsize(long struct) {
        return FT_Size.createSafe(MemoryUtil.memGetAddress(struct + (long)SIZE));
    }

    @Nullable
    public static FT_CharMap ncharmap(long struct) {
        return FT_CharMap.createSafe(MemoryUtil.memGetAddress(struct + (long)CHARMAP));
    }

    public static long ndriver(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)DRIVER);
    }

    @Nullable
    public static FT_Memory nmemory(long struct) {
        return FT_Memory.createSafe(MemoryUtil.memGetAddress(struct + (long)MEMORY));
    }

    @Nullable
    public static FT_Stream nstream$(long struct) {
        return FT_Stream.createSafe(MemoryUtil.memGetAddress(struct + (long)STREAM));
    }

    public static FT_List nsizes_list(long struct) {
        return FT_List.create(struct + (long)SIZES_LIST);
    }

    public static FT_Generic nautohint(long struct) {
        return FT_Generic.create(struct + (long)AUTOHINT);
    }

    public static long nextensions(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)EXTENSIONS);
    }

    public static long ninternal(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)INTERNAL);
    }

    static {
        Struct.Layout layout = FT_Face.__struct(FT_Face.__member(CLONG_SIZE), FT_Face.__member(CLONG_SIZE), FT_Face.__member(CLONG_SIZE), FT_Face.__member(CLONG_SIZE), FT_Face.__member(CLONG_SIZE), FT_Face.__member(POINTER_SIZE), FT_Face.__member(POINTER_SIZE), FT_Face.__member(4), FT_Face.__member(POINTER_SIZE), FT_Face.__member(4), FT_Face.__member(POINTER_SIZE), FT_Face.__member(FT_Generic.SIZEOF, FT_Generic.ALIGNOF), FT_Face.__member(FT_BBox.SIZEOF, FT_BBox.ALIGNOF), FT_Face.__member(2), FT_Face.__member(2), FT_Face.__member(2), FT_Face.__member(2), FT_Face.__member(2), FT_Face.__member(2), FT_Face.__member(2), FT_Face.__member(2), FT_Face.__member(POINTER_SIZE), FT_Face.__member(POINTER_SIZE), FT_Face.__member(POINTER_SIZE), FT_Face.__member(POINTER_SIZE), FT_Face.__member(POINTER_SIZE), FT_Face.__member(POINTER_SIZE), FT_Face.__member(FT_List.SIZEOF, FT_List.ALIGNOF), FT_Face.__member(FT_Generic.SIZEOF, FT_Generic.ALIGNOF), FT_Face.__member(POINTER_SIZE), FT_Face.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        NUM_FACES = layout.offsetof(0);
        FACE_INDEX = layout.offsetof(1);
        FACE_FLAGS = layout.offsetof(2);
        STYLE_FLAGS = layout.offsetof(3);
        NUM_GLYPHS = layout.offsetof(4);
        FAMILY_NAME = layout.offsetof(5);
        STYLE_NAME = layout.offsetof(6);
        NUM_FIXED_SIZES = layout.offsetof(7);
        AVAILABLE_SIZES = layout.offsetof(8);
        NUM_CHARMAPS = layout.offsetof(9);
        CHARMAPS = layout.offsetof(10);
        GENERIC = layout.offsetof(11);
        BBOX = layout.offsetof(12);
        UNITS_PER_EM = layout.offsetof(13);
        ASCENDER = layout.offsetof(14);
        DESCENDER = layout.offsetof(15);
        HEIGHT = layout.offsetof(16);
        MAX_ADVANCE_WIDTH = layout.offsetof(17);
        MAX_ADVANCE_HEIGHT = layout.offsetof(18);
        UNDERLINE_POSITION = layout.offsetof(19);
        UNDERLINE_THICKNESS = layout.offsetof(20);
        GLYPH = layout.offsetof(21);
        SIZE = layout.offsetof(22);
        CHARMAP = layout.offsetof(23);
        DRIVER = layout.offsetof(24);
        MEMORY = layout.offsetof(25);
        STREAM = layout.offsetof(26);
        SIZES_LIST = layout.offsetof(27);
        AUTOHINT = layout.offsetof(28);
        EXTENSIONS = layout.offsetof(29);
        INTERNAL = layout.offsetof(30);
    }

    public static class Buffer
    extends StructBuffer<FT_Face, Buffer> {
        private static final FT_Face ELEMENT_FACTORY = FT_Face.create(-1L);

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
        protected FT_Face getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Long")
        public long num_faces() {
            return FT_Face.nnum_faces(this.address());
        }

        @NativeType(value="FT_Long")
        public long face_index() {
            return FT_Face.nface_index(this.address());
        }

        @NativeType(value="FT_Long")
        public long face_flags() {
            return FT_Face.nface_flags(this.address());
        }

        @NativeType(value="FT_Long")
        public long style_flags() {
            return FT_Face.nstyle_flags(this.address());
        }

        @NativeType(value="FT_Long")
        public long num_glyphs() {
            return FT_Face.nnum_glyphs(this.address());
        }

        @NativeType(value="FT_String *")
        public ByteBuffer family_name() {
            return FT_Face.nfamily_name(this.address());
        }

        @NativeType(value="FT_String *")
        public String family_nameString() {
            return FT_Face.nfamily_nameString(this.address());
        }

        @NativeType(value="FT_String *")
        public ByteBuffer style_name() {
            return FT_Face.nstyle_name(this.address());
        }

        @NativeType(value="FT_String *")
        public String style_nameString() {
            return FT_Face.nstyle_nameString(this.address());
        }

        @NativeType(value="FT_Int")
        public int num_fixed_sizes() {
            return FT_Face.nnum_fixed_sizes(this.address());
        }

        @Nullable
        @NativeType(value="FT_Bitmap_Size *")
        public FT_Bitmap_Size.Buffer available_sizes() {
            return FT_Face.navailable_sizes(this.address());
        }

        @NativeType(value="FT_Int")
        public int num_charmaps() {
            return FT_Face.nnum_charmaps(this.address());
        }

        @NativeType(value="FT_CharMap *")
        public PointerBuffer charmaps() {
            return FT_Face.ncharmaps(this.address());
        }

        public FT_Generic generic() {
            return FT_Face.ngeneric(this.address());
        }

        public FT_BBox bbox() {
            return FT_Face.nbbox(this.address());
        }

        @NativeType(value="FT_UShort")
        public short units_per_EM() {
            return FT_Face.nunits_per_EM(this.address());
        }

        @NativeType(value="FT_Short")
        public short ascender() {
            return FT_Face.nascender(this.address());
        }

        @NativeType(value="FT_Short")
        public short descender() {
            return FT_Face.ndescender(this.address());
        }

        @NativeType(value="FT_Short")
        public short height() {
            return FT_Face.nheight(this.address());
        }

        @NativeType(value="FT_Short")
        public short max_advance_width() {
            return FT_Face.nmax_advance_width(this.address());
        }

        @NativeType(value="FT_Short")
        public short max_advance_height() {
            return FT_Face.nmax_advance_height(this.address());
        }

        @NativeType(value="FT_Short")
        public short underline_position() {
            return FT_Face.nunderline_position(this.address());
        }

        @NativeType(value="FT_Short")
        public short underline_thickness() {
            return FT_Face.nunderline_thickness(this.address());
        }

        @Nullable
        public FT_GlyphSlot glyph() {
            return FT_Face.nglyph(this.address());
        }

        @Nullable
        public FT_Size size() {
            return FT_Face.nsize(this.address());
        }

        @Nullable
        public FT_CharMap charmap() {
            return FT_Face.ncharmap(this.address());
        }
    }
}
