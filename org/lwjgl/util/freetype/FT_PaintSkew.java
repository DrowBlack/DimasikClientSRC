package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_OpaquePaint;

public class FT_PaintSkew
extends Struct<FT_PaintSkew> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int PAINT;
    public static final int X_SKEW_ANGLE;
    public static final int Y_SKEW_ANGLE;
    public static final int CENTER_X;
    public static final int CENTER_Y;

    protected FT_PaintSkew(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_PaintSkew create(long address, @Nullable ByteBuffer container) {
        return new FT_PaintSkew(address, container);
    }

    public FT_PaintSkew(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_PaintSkew.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_OpaquePaintRec")
    public FT_OpaquePaint paint() {
        return FT_PaintSkew.npaint(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long x_skew_angle() {
        return FT_PaintSkew.nx_skew_angle(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long y_skew_angle() {
        return FT_PaintSkew.ny_skew_angle(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long center_x() {
        return FT_PaintSkew.ncenter_x(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long center_y() {
        return FT_PaintSkew.ncenter_y(this.address());
    }

    public static FT_PaintSkew create(long address) {
        return new FT_PaintSkew(address, null);
    }

    @Nullable
    public static FT_PaintSkew createSafe(long address) {
        return address == 0L ? null : new FT_PaintSkew(address, null);
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

    public static long nx_skew_angle(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)X_SKEW_ANGLE);
    }

    public static long ny_skew_angle(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)Y_SKEW_ANGLE);
    }

    public static long ncenter_x(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)CENTER_X);
    }

    public static long ncenter_y(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)CENTER_Y);
    }

    static {
        Struct.Layout layout = FT_PaintSkew.__struct(FT_PaintSkew.__member(FT_OpaquePaint.SIZEOF, FT_OpaquePaint.ALIGNOF), FT_PaintSkew.__member(CLONG_SIZE), FT_PaintSkew.__member(CLONG_SIZE), FT_PaintSkew.__member(CLONG_SIZE), FT_PaintSkew.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        PAINT = layout.offsetof(0);
        X_SKEW_ANGLE = layout.offsetof(1);
        Y_SKEW_ANGLE = layout.offsetof(2);
        CENTER_X = layout.offsetof(3);
        CENTER_Y = layout.offsetof(4);
    }

    public static class Buffer
    extends StructBuffer<FT_PaintSkew, Buffer> {
        private static final FT_PaintSkew ELEMENT_FACTORY = FT_PaintSkew.create(-1L);

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
        protected FT_PaintSkew getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_OpaquePaintRec")
        public FT_OpaquePaint paint() {
            return FT_PaintSkew.npaint(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long x_skew_angle() {
            return FT_PaintSkew.nx_skew_angle(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long y_skew_angle() {
            return FT_PaintSkew.ny_skew_angle(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long center_x() {
            return FT_PaintSkew.ncenter_x(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long center_y() {
            return FT_PaintSkew.ncenter_y(this.address());
        }
    }
}
