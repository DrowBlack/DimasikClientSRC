package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class FT_ColorIndex
extends Struct<FT_ColorIndex> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int PALETTE_INDEX;
    public static final int ALPHA;

    protected FT_ColorIndex(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_ColorIndex create(long address, @Nullable ByteBuffer container) {
        return new FT_ColorIndex(address, container);
    }

    public FT_ColorIndex(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_ColorIndex.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_UInt16")
    public short palette_index() {
        return FT_ColorIndex.npalette_index(this.address());
    }

    @NativeType(value="FT_F2Dot14")
    public short alpha() {
        return FT_ColorIndex.nalpha(this.address());
    }

    public static FT_ColorIndex create(long address) {
        return new FT_ColorIndex(address, null);
    }

    @Nullable
    public static FT_ColorIndex createSafe(long address) {
        return address == 0L ? null : new FT_ColorIndex(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static short npalette_index(long struct) {
        return UNSAFE.getShort(null, struct + (long)PALETTE_INDEX);
    }

    public static short nalpha(long struct) {
        return UNSAFE.getShort(null, struct + (long)ALPHA);
    }

    static {
        Struct.Layout layout = FT_ColorIndex.__struct(FT_ColorIndex.__member(2), FT_ColorIndex.__member(2));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        PALETTE_INDEX = layout.offsetof(0);
        ALPHA = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_ColorIndex, Buffer> {
        private static final FT_ColorIndex ELEMENT_FACTORY = FT_ColorIndex.create(-1L);

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
        protected FT_ColorIndex getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_UInt16")
        public short palette_index() {
            return FT_ColorIndex.npalette_index(this.address());
        }

        @NativeType(value="FT_F2Dot14")
        public short alpha() {
            return FT_ColorIndex.nalpha(this.address());
        }
    }
}
