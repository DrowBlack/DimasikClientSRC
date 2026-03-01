package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_ColorIndex;

public class FT_PaintSolid
extends Struct<FT_PaintSolid> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int COLOR;

    protected FT_PaintSolid(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_PaintSolid create(long address, @Nullable ByteBuffer container) {
        return new FT_PaintSolid(address, container);
    }

    public FT_PaintSolid(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_PaintSolid.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public FT_ColorIndex color() {
        return FT_PaintSolid.ncolor(this.address());
    }

    public static FT_PaintSolid create(long address) {
        return new FT_PaintSolid(address, null);
    }

    @Nullable
    public static FT_PaintSolid createSafe(long address) {
        return address == 0L ? null : new FT_PaintSolid(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_ColorIndex ncolor(long struct) {
        return FT_ColorIndex.create(struct + (long)COLOR);
    }

    static {
        Struct.Layout layout = FT_PaintSolid.__struct(FT_PaintSolid.__member(FT_ColorIndex.SIZEOF, FT_ColorIndex.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        COLOR = layout.offsetof(0);
    }

    public static class Buffer
    extends StructBuffer<FT_PaintSolid, Buffer> {
        private static final FT_PaintSolid ELEMENT_FACTORY = FT_PaintSolid.create(-1L);

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
        protected FT_PaintSolid getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public FT_ColorIndex color() {
            return FT_PaintSolid.ncolor(this.address());
        }
    }
}
