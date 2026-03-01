package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_OpaquePaint;

public class FT_PaintGlyph
extends Struct<FT_PaintGlyph> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int PAINT;
    public static final int GLYPHID;

    protected FT_PaintGlyph(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_PaintGlyph create(long address, @Nullable ByteBuffer container) {
        return new FT_PaintGlyph(address, container);
    }

    public FT_PaintGlyph(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_PaintGlyph.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_OpaquePaintRec")
    public FT_OpaquePaint paint() {
        return FT_PaintGlyph.npaint(this.address());
    }

    @NativeType(value="FT_UInt")
    public int glyphID() {
        return FT_PaintGlyph.nglyphID(this.address());
    }

    public static FT_PaintGlyph create(long address) {
        return new FT_PaintGlyph(address, null);
    }

    @Nullable
    public static FT_PaintGlyph createSafe(long address) {
        return address == 0L ? null : new FT_PaintGlyph(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_OpaquePaint npaint(long struct) {
        return FT_OpaquePaint.create(struct + (long)PAINT);
    }

    public static int nglyphID(long struct) {
        return UNSAFE.getInt(null, struct + (long)GLYPHID);
    }

    static {
        Struct.Layout layout = FT_PaintGlyph.__struct(FT_PaintGlyph.__member(FT_OpaquePaint.SIZEOF, FT_OpaquePaint.ALIGNOF), FT_PaintGlyph.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        PAINT = layout.offsetof(0);
        GLYPHID = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_PaintGlyph, Buffer> {
        private static final FT_PaintGlyph ELEMENT_FACTORY = FT_PaintGlyph.create(-1L);

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
        protected FT_PaintGlyph getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_OpaquePaintRec")
        public FT_OpaquePaint paint() {
            return FT_PaintGlyph.npaint(this.address());
        }

        @NativeType(value="FT_UInt")
        public int glyphID() {
            return FT_PaintGlyph.nglyphID(this.address());
        }
    }
}
