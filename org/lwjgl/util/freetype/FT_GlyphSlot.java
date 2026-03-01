package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_Generic;
import org.lwjgl.util.freetype.FT_Glyph_Metrics;
import org.lwjgl.util.freetype.FT_Outline;
import org.lwjgl.util.freetype.FT_Vector;

@NativeType(value="struct FT_GlyphSlotRec")
public class FT_GlyphSlot
extends Struct<FT_GlyphSlot> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int LIBRARY;
    public static final int FACE;
    public static final int NEXT;
    public static final int GLYPH_INDEX;
    public static final int GENERIC;
    public static final int METRICS;
    public static final int LINEARHORIADVANCE;
    public static final int LINEARVERTADVANCE;
    public static final int ADVANCE;
    public static final int FORMAT;
    public static final int BITMAP;
    public static final int BITMAP_LEFT;
    public static final int BITMAP_TOP;
    public static final int OUTLINE;
    public static final int NUM_SUBGLYPHS;
    public static final int SUBGLYPHS;
    public static final int CONTROL_DATA;
    public static final int CONTROL_LEN;
    public static final int LSB_DELTA;
    public static final int RSB_DELTA;
    public static final int OTHER;
    public static final int INTERNAL;

    protected FT_GlyphSlot(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_GlyphSlot create(long address, @Nullable ByteBuffer container) {
        return new FT_GlyphSlot(address, container);
    }

    public FT_GlyphSlot(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_GlyphSlot.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Library")
    public long library() {
        return FT_GlyphSlot.nlibrary(this.address());
    }

    public FT_Face face() {
        return FT_GlyphSlot.nface(this.address());
    }

    @Nullable
    public FT_GlyphSlot next() {
        return FT_GlyphSlot.nnext(this.address());
    }

    @NativeType(value="FT_UInt")
    public int glyph_index() {
        return FT_GlyphSlot.nglyph_index(this.address());
    }

    public FT_Generic generic() {
        return FT_GlyphSlot.ngeneric(this.address());
    }

    public FT_Glyph_Metrics metrics() {
        return FT_GlyphSlot.nmetrics(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long linearHoriAdvance() {
        return FT_GlyphSlot.nlinearHoriAdvance(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long linearVertAdvance() {
        return FT_GlyphSlot.nlinearVertAdvance(this.address());
    }

    public FT_Vector advance() {
        return FT_GlyphSlot.nadvance(this.address());
    }

    @NativeType(value="FT_Glyph_Format")
    public int format() {
        return FT_GlyphSlot.nformat(this.address());
    }

    public FT_Bitmap bitmap() {
        return FT_GlyphSlot.nbitmap(this.address());
    }

    @NativeType(value="FT_Int")
    public int bitmap_left() {
        return FT_GlyphSlot.nbitmap_left(this.address());
    }

    @NativeType(value="FT_Int")
    public int bitmap_top() {
        return FT_GlyphSlot.nbitmap_top(this.address());
    }

    public FT_Outline outline() {
        return FT_GlyphSlot.noutline(this.address());
    }

    @NativeType(value="FT_Pos")
    public long lsb_delta() {
        return FT_GlyphSlot.nlsb_delta(this.address());
    }

    @NativeType(value="FT_Pos")
    public long rsb_delta() {
        return FT_GlyphSlot.nrsb_delta(this.address());
    }

    public static FT_GlyphSlot create(long address) {
        return new FT_GlyphSlot(address, null);
    }

    @Nullable
    public static FT_GlyphSlot createSafe(long address) {
        return address == 0L ? null : new FT_GlyphSlot(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static long nlibrary(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)LIBRARY);
    }

    public static FT_Face nface(long struct) {
        return FT_Face.create(MemoryUtil.memGetAddress(struct + (long)FACE));
    }

    @Nullable
    public static FT_GlyphSlot nnext(long struct) {
        return FT_GlyphSlot.createSafe(MemoryUtil.memGetAddress(struct + (long)NEXT));
    }

    public static int nglyph_index(long struct) {
        return UNSAFE.getInt(null, struct + (long)GLYPH_INDEX);
    }

    public static FT_Generic ngeneric(long struct) {
        return FT_Generic.create(struct + (long)GENERIC);
    }

    public static FT_Glyph_Metrics nmetrics(long struct) {
        return FT_Glyph_Metrics.create(struct + (long)METRICS);
    }

    public static long nlinearHoriAdvance(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)LINEARHORIADVANCE);
    }

    public static long nlinearVertAdvance(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)LINEARVERTADVANCE);
    }

    public static FT_Vector nadvance(long struct) {
        return FT_Vector.create(struct + (long)ADVANCE);
    }

    public static int nformat(long struct) {
        return UNSAFE.getInt(null, struct + (long)FORMAT);
    }

    public static FT_Bitmap nbitmap(long struct) {
        return FT_Bitmap.create(struct + (long)BITMAP);
    }

    public static int nbitmap_left(long struct) {
        return UNSAFE.getInt(null, struct + (long)BITMAP_LEFT);
    }

    public static int nbitmap_top(long struct) {
        return UNSAFE.getInt(null, struct + (long)BITMAP_TOP);
    }

    public static FT_Outline noutline(long struct) {
        return FT_Outline.create(struct + (long)OUTLINE);
    }

    public static int nnum_subglyphs(long struct) {
        return UNSAFE.getInt(null, struct + (long)NUM_SUBGLYPHS);
    }

    public static long nsubglyphs(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)SUBGLYPHS);
    }

    @Nullable
    public static ByteBuffer ncontrol_data(long struct) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)CONTROL_DATA), (int)FT_GlyphSlot.ncontrol_len(struct));
    }

    public static long ncontrol_len(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)CONTROL_LEN);
    }

    public static long nlsb_delta(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)LSB_DELTA);
    }

    public static long nrsb_delta(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)RSB_DELTA);
    }

    public static long nother(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)OTHER);
    }

    public static long ninternal(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)INTERNAL);
    }

    static {
        Struct.Layout layout = FT_GlyphSlot.__struct(FT_GlyphSlot.__member(POINTER_SIZE), FT_GlyphSlot.__member(POINTER_SIZE), FT_GlyphSlot.__member(POINTER_SIZE), FT_GlyphSlot.__member(4), FT_GlyphSlot.__member(FT_Generic.SIZEOF, FT_Generic.ALIGNOF), FT_GlyphSlot.__member(FT_Glyph_Metrics.SIZEOF, FT_Glyph_Metrics.ALIGNOF), FT_GlyphSlot.__member(CLONG_SIZE), FT_GlyphSlot.__member(CLONG_SIZE), FT_GlyphSlot.__member(FT_Vector.SIZEOF, FT_Vector.ALIGNOF), FT_GlyphSlot.__member(4), FT_GlyphSlot.__member(FT_Bitmap.SIZEOF, FT_Bitmap.ALIGNOF), FT_GlyphSlot.__member(4), FT_GlyphSlot.__member(4), FT_GlyphSlot.__member(FT_Outline.SIZEOF, FT_Outline.ALIGNOF), FT_GlyphSlot.__member(4), FT_GlyphSlot.__member(POINTER_SIZE), FT_GlyphSlot.__member(POINTER_SIZE), FT_GlyphSlot.__member(CLONG_SIZE), FT_GlyphSlot.__member(CLONG_SIZE), FT_GlyphSlot.__member(CLONG_SIZE), FT_GlyphSlot.__member(POINTER_SIZE), FT_GlyphSlot.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        LIBRARY = layout.offsetof(0);
        FACE = layout.offsetof(1);
        NEXT = layout.offsetof(2);
        GLYPH_INDEX = layout.offsetof(3);
        GENERIC = layout.offsetof(4);
        METRICS = layout.offsetof(5);
        LINEARHORIADVANCE = layout.offsetof(6);
        LINEARVERTADVANCE = layout.offsetof(7);
        ADVANCE = layout.offsetof(8);
        FORMAT = layout.offsetof(9);
        BITMAP = layout.offsetof(10);
        BITMAP_LEFT = layout.offsetof(11);
        BITMAP_TOP = layout.offsetof(12);
        OUTLINE = layout.offsetof(13);
        NUM_SUBGLYPHS = layout.offsetof(14);
        SUBGLYPHS = layout.offsetof(15);
        CONTROL_DATA = layout.offsetof(16);
        CONTROL_LEN = layout.offsetof(17);
        LSB_DELTA = layout.offsetof(18);
        RSB_DELTA = layout.offsetof(19);
        OTHER = layout.offsetof(20);
        INTERNAL = layout.offsetof(21);
    }

    public static class Buffer
    extends StructBuffer<FT_GlyphSlot, Buffer> {
        private static final FT_GlyphSlot ELEMENT_FACTORY = FT_GlyphSlot.create(-1L);

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
        protected FT_GlyphSlot getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Library")
        public long library() {
            return FT_GlyphSlot.nlibrary(this.address());
        }

        public FT_Face face() {
            return FT_GlyphSlot.nface(this.address());
        }

        @Nullable
        public FT_GlyphSlot next() {
            return FT_GlyphSlot.nnext(this.address());
        }

        @NativeType(value="FT_UInt")
        public int glyph_index() {
            return FT_GlyphSlot.nglyph_index(this.address());
        }

        public FT_Generic generic() {
            return FT_GlyphSlot.ngeneric(this.address());
        }

        public FT_Glyph_Metrics metrics() {
            return FT_GlyphSlot.nmetrics(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long linearHoriAdvance() {
            return FT_GlyphSlot.nlinearHoriAdvance(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long linearVertAdvance() {
            return FT_GlyphSlot.nlinearVertAdvance(this.address());
        }

        public FT_Vector advance() {
            return FT_GlyphSlot.nadvance(this.address());
        }

        @NativeType(value="FT_Glyph_Format")
        public int format() {
            return FT_GlyphSlot.nformat(this.address());
        }

        public FT_Bitmap bitmap() {
            return FT_GlyphSlot.nbitmap(this.address());
        }

        @NativeType(value="FT_Int")
        public int bitmap_left() {
            return FT_GlyphSlot.nbitmap_left(this.address());
        }

        @NativeType(value="FT_Int")
        public int bitmap_top() {
            return FT_GlyphSlot.nbitmap_top(this.address());
        }

        public FT_Outline outline() {
            return FT_GlyphSlot.noutline(this.address());
        }

        @NativeType(value="FT_Pos")
        public long lsb_delta() {
            return FT_GlyphSlot.nlsb_delta(this.address());
        }

        @NativeType(value="FT_Pos")
        public long rsb_delta() {
            return FT_GlyphSlot.nrsb_delta(this.address());
        }
    }
}
