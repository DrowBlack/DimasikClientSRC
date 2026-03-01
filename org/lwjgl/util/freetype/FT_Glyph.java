package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_Vector;

@NativeType(value="struct FT_GlyphRec")
public class FT_Glyph
extends Struct<FT_Glyph> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int LIBRARY;
    public static final int CLAZZ;
    public static final int FORMAT;
    public static final int ADVANCE;

    protected FT_Glyph(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Glyph create(long address, @Nullable ByteBuffer container) {
        return new FT_Glyph(address, container);
    }

    public FT_Glyph(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Glyph.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Library")
    public long library() {
        return FT_Glyph.nlibrary(this.address());
    }

    @NativeType(value="FT_Glyph_Format")
    public int format() {
        return FT_Glyph.nformat(this.address());
    }

    public FT_Vector advance() {
        return FT_Glyph.nadvance(this.address());
    }

    public static FT_Glyph create(long address) {
        return new FT_Glyph(address, null);
    }

    @Nullable
    public static FT_Glyph createSafe(long address) {
        return address == 0L ? null : new FT_Glyph(address, null);
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

    public static long nclazz(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)CLAZZ);
    }

    public static int nformat(long struct) {
        return UNSAFE.getInt(null, struct + (long)FORMAT);
    }

    public static FT_Vector nadvance(long struct) {
        return FT_Vector.create(struct + (long)ADVANCE);
    }

    static {
        Struct.Layout layout = FT_Glyph.__struct(FT_Glyph.__member(POINTER_SIZE), FT_Glyph.__member(POINTER_SIZE), FT_Glyph.__member(4), FT_Glyph.__member(FT_Vector.SIZEOF, FT_Vector.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        LIBRARY = layout.offsetof(0);
        CLAZZ = layout.offsetof(1);
        FORMAT = layout.offsetof(2);
        ADVANCE = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FT_Glyph, Buffer> {
        private static final FT_Glyph ELEMENT_FACTORY = FT_Glyph.create(-1L);

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
        protected FT_Glyph getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Library")
        public long library() {
            return FT_Glyph.nlibrary(this.address());
        }

        @NativeType(value="FT_Glyph_Format")
        public int format() {
            return FT_Glyph.nformat(this.address());
        }

        public FT_Vector advance() {
            return FT_Glyph.nadvance(this.address());
        }
    }
}
