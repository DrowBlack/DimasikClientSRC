package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_Glyph;
import org.lwjgl.util.freetype.FT_Outline;

@NativeType(value="struct FT_OutlineGlyphRec")
public class FT_OutlineGlyph
extends Struct<FT_OutlineGlyph> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int ROOT;
    public static final int OUTLINE;

    protected FT_OutlineGlyph(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_OutlineGlyph create(long address, @Nullable ByteBuffer container) {
        return new FT_OutlineGlyph(address, container);
    }

    public FT_OutlineGlyph(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_OutlineGlyph.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_GlyphRec")
    public FT_Glyph root() {
        return FT_OutlineGlyph.nroot(this.address());
    }

    public FT_Outline outline() {
        return FT_OutlineGlyph.noutline(this.address());
    }

    public static FT_OutlineGlyph create(long address) {
        return new FT_OutlineGlyph(address, null);
    }

    @Nullable
    public static FT_OutlineGlyph createSafe(long address) {
        return address == 0L ? null : new FT_OutlineGlyph(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Glyph nroot(long struct) {
        return FT_Glyph.create(struct + (long)ROOT);
    }

    public static FT_Outline noutline(long struct) {
        return FT_Outline.create(struct + (long)OUTLINE);
    }

    static {
        Struct.Layout layout = FT_OutlineGlyph.__struct(FT_OutlineGlyph.__member(FT_Glyph.SIZEOF, FT_Glyph.ALIGNOF), FT_OutlineGlyph.__member(FT_Outline.SIZEOF, FT_Outline.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        ROOT = layout.offsetof(0);
        OUTLINE = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_OutlineGlyph, Buffer> {
        private static final FT_OutlineGlyph ELEMENT_FACTORY = FT_OutlineGlyph.create(-1L);

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
        protected FT_OutlineGlyph getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_GlyphRec")
        public FT_Glyph root() {
            return FT_OutlineGlyph.nroot(this.address());
        }

        public FT_Outline outline() {
            return FT_OutlineGlyph.noutline(this.address());
        }
    }
}
