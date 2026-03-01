package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Glyph;

@NativeType(value="struct FT_BitmapGlyphRec")
public class FT_BitmapGlyph
extends Struct<FT_BitmapGlyph> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int ROOT;
    public static final int LEFT;
    public static final int TOP;
    public static final int BITMAP;

    protected FT_BitmapGlyph(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_BitmapGlyph create(long address, @Nullable ByteBuffer container) {
        return new FT_BitmapGlyph(address, container);
    }

    public FT_BitmapGlyph(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_BitmapGlyph.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_GlyphRec")
    public FT_Glyph root() {
        return FT_BitmapGlyph.nroot(this.address());
    }

    @NativeType(value="FT_Int")
    public int left() {
        return FT_BitmapGlyph.nleft(this.address());
    }

    @NativeType(value="FT_Int")
    public int top() {
        return FT_BitmapGlyph.ntop(this.address());
    }

    public FT_Bitmap bitmap() {
        return FT_BitmapGlyph.nbitmap(this.address());
    }

    public static FT_BitmapGlyph create(long address) {
        return new FT_BitmapGlyph(address, null);
    }

    @Nullable
    public static FT_BitmapGlyph createSafe(long address) {
        return address == 0L ? null : new FT_BitmapGlyph(address, null);
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

    public static int nleft(long struct) {
        return UNSAFE.getInt(null, struct + (long)LEFT);
    }

    public static int ntop(long struct) {
        return UNSAFE.getInt(null, struct + (long)TOP);
    }

    public static FT_Bitmap nbitmap(long struct) {
        return FT_Bitmap.create(struct + (long)BITMAP);
    }

    static {
        Struct.Layout layout = FT_BitmapGlyph.__struct(FT_BitmapGlyph.__member(FT_Glyph.SIZEOF, FT_Glyph.ALIGNOF), FT_BitmapGlyph.__member(4), FT_BitmapGlyph.__member(4), FT_BitmapGlyph.__member(FT_Bitmap.SIZEOF, FT_Bitmap.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        ROOT = layout.offsetof(0);
        LEFT = layout.offsetof(1);
        TOP = layout.offsetof(2);
        BITMAP = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FT_BitmapGlyph, Buffer> {
        private static final FT_BitmapGlyph ELEMENT_FACTORY = FT_BitmapGlyph.create(-1L);

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
        protected FT_BitmapGlyph getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_GlyphRec")
        public FT_Glyph root() {
            return FT_BitmapGlyph.nroot(this.address());
        }

        @NativeType(value="FT_Int")
        public int left() {
            return FT_BitmapGlyph.nleft(this.address());
        }

        @NativeType(value="FT_Int")
        public int top() {
            return FT_BitmapGlyph.ntop(this.address());
        }

        public FT_Bitmap bitmap() {
            return FT_BitmapGlyph.nbitmap(this.address());
        }
    }
}
