package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_ColorStopIterator;

public class FT_ColorLine
extends Struct<FT_ColorLine> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int EXTEND;
    public static final int COLOR_STOP_ITERATOR;

    protected FT_ColorLine(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_ColorLine create(long address, @Nullable ByteBuffer container) {
        return new FT_ColorLine(address, container);
    }

    public FT_ColorLine(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_ColorLine.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_PaintExtend")
    public int extend() {
        return FT_ColorLine.nextend(this.address());
    }

    public FT_ColorStopIterator color_stop_iterator() {
        return FT_ColorLine.ncolor_stop_iterator(this.address());
    }

    public static FT_ColorLine create(long address) {
        return new FT_ColorLine(address, null);
    }

    @Nullable
    public static FT_ColorLine createSafe(long address) {
        return address == 0L ? null : new FT_ColorLine(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static int nextend(long struct) {
        return UNSAFE.getInt(null, struct + (long)EXTEND);
    }

    public static FT_ColorStopIterator ncolor_stop_iterator(long struct) {
        return FT_ColorStopIterator.create(struct + (long)COLOR_STOP_ITERATOR);
    }

    static {
        Struct.Layout layout = FT_ColorLine.__struct(FT_ColorLine.__member(4), FT_ColorLine.__member(FT_ColorStopIterator.SIZEOF, FT_ColorStopIterator.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        EXTEND = layout.offsetof(0);
        COLOR_STOP_ITERATOR = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_ColorLine, Buffer> {
        private static final FT_ColorLine ELEMENT_FACTORY = FT_ColorLine.create(-1L);

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
        protected FT_ColorLine getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_PaintExtend")
        public int extend() {
            return FT_ColorLine.nextend(this.address());
        }

        public FT_ColorStopIterator color_stop_iterator() {
            return FT_ColorLine.ncolor_stop_iterator(this.address());
        }
    }
}
