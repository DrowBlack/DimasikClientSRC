package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_OpaquePaint;

public class FT_PaintScale
extends Struct<FT_PaintScale> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int PAINT;
    public static final int SCALE_X;
    public static final int SCALE_Y;
    public static final int CENTER_X;
    public static final int CENTER_Y;

    protected FT_PaintScale(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_PaintScale create(long address, @Nullable ByteBuffer container) {
        return new FT_PaintScale(address, container);
    }

    public FT_PaintScale(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_PaintScale.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_OpaquePaintRec")
    public FT_OpaquePaint paint() {
        return FT_PaintScale.npaint(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long scale_x() {
        return FT_PaintScale.nscale_x(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long scale_y() {
        return FT_PaintScale.nscale_y(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long center_x() {
        return FT_PaintScale.ncenter_x(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long center_y() {
        return FT_PaintScale.ncenter_y(this.address());
    }

    public static FT_PaintScale create(long address) {
        return new FT_PaintScale(address, null);
    }

    @Nullable
    public static FT_PaintScale createSafe(long address) {
        return address == 0L ? null : new FT_PaintScale(address, null);
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

    public static long nscale_x(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)SCALE_X);
    }

    public static long nscale_y(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)SCALE_Y);
    }

    public static long ncenter_x(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)CENTER_X);
    }

    public static long ncenter_y(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)CENTER_Y);
    }

    static {
        Struct.Layout layout = FT_PaintScale.__struct(FT_PaintScale.__member(FT_OpaquePaint.SIZEOF, FT_OpaquePaint.ALIGNOF), FT_PaintScale.__member(CLONG_SIZE), FT_PaintScale.__member(CLONG_SIZE), FT_PaintScale.__member(CLONG_SIZE), FT_PaintScale.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        PAINT = layout.offsetof(0);
        SCALE_X = layout.offsetof(1);
        SCALE_Y = layout.offsetof(2);
        CENTER_X = layout.offsetof(3);
        CENTER_Y = layout.offsetof(4);
    }

    public static class Buffer
    extends StructBuffer<FT_PaintScale, Buffer> {
        private static final FT_PaintScale ELEMENT_FACTORY = FT_PaintScale.create(-1L);

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
        protected FT_PaintScale getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_OpaquePaintRec")
        public FT_OpaquePaint paint() {
            return FT_PaintScale.npaint(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long scale_x() {
            return FT_PaintScale.nscale_x(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long scale_y() {
            return FT_PaintScale.nscale_y(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long center_x() {
            return FT_PaintScale.ncenter_x(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long center_y() {
            return FT_PaintScale.ncenter_y(this.address());
        }
    }
}
