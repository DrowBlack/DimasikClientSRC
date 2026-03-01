package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_OpaquePaint;

public class FT_PaintRotate
extends Struct<FT_PaintRotate> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int PAINT;
    public static final int ANGLE;
    public static final int CENTER_X;
    public static final int CENTER_Y;

    protected FT_PaintRotate(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_PaintRotate create(long address, @Nullable ByteBuffer container) {
        return new FT_PaintRotate(address, container);
    }

    public FT_PaintRotate(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_PaintRotate.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_OpaquePaintRec")
    public FT_OpaquePaint paint() {
        return FT_PaintRotate.npaint(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long angle() {
        return FT_PaintRotate.nangle(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long center_x() {
        return FT_PaintRotate.ncenter_x(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long center_y() {
        return FT_PaintRotate.ncenter_y(this.address());
    }

    public static FT_PaintRotate create(long address) {
        return new FT_PaintRotate(address, null);
    }

    @Nullable
    public static FT_PaintRotate createSafe(long address) {
        return address == 0L ? null : new FT_PaintRotate(address, null);
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

    public static long nangle(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)ANGLE);
    }

    public static long ncenter_x(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)CENTER_X);
    }

    public static long ncenter_y(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)CENTER_Y);
    }

    static {
        Struct.Layout layout = FT_PaintRotate.__struct(FT_PaintRotate.__member(FT_OpaquePaint.SIZEOF, FT_OpaquePaint.ALIGNOF), FT_PaintRotate.__member(CLONG_SIZE), FT_PaintRotate.__member(CLONG_SIZE), FT_PaintRotate.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        PAINT = layout.offsetof(0);
        ANGLE = layout.offsetof(1);
        CENTER_X = layout.offsetof(2);
        CENTER_Y = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FT_PaintRotate, Buffer> {
        private static final FT_PaintRotate ELEMENT_FACTORY = FT_PaintRotate.create(-1L);

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
        protected FT_PaintRotate getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_OpaquePaintRec")
        public FT_OpaquePaint paint() {
            return FT_PaintRotate.npaint(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long angle() {
            return FT_PaintRotate.nangle(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long center_x() {
            return FT_PaintRotate.ncenter_x(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long center_y() {
            return FT_PaintRotate.ncenter_y(this.address());
        }
    }
}
