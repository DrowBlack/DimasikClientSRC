package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class FT_Size_Metrics
extends Struct<FT_Size_Metrics> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int X_PPEM;
    public static final int Y_PPEM;
    public static final int X_SCALE;
    public static final int Y_SCALE;
    public static final int ASCENDER;
    public static final int DESCENDER;
    public static final int HEIGHT;
    public static final int MAX_ADVANCE;

    protected FT_Size_Metrics(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Size_Metrics create(long address, @Nullable ByteBuffer container) {
        return new FT_Size_Metrics(address, container);
    }

    public FT_Size_Metrics(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Size_Metrics.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_UShort")
    public short x_ppem() {
        return FT_Size_Metrics.nx_ppem(this.address());
    }

    @NativeType(value="FT_UShort")
    public short y_ppem() {
        return FT_Size_Metrics.ny_ppem(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long x_scale() {
        return FT_Size_Metrics.nx_scale(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long y_scale() {
        return FT_Size_Metrics.ny_scale(this.address());
    }

    @NativeType(value="FT_Pos")
    public long ascender() {
        return FT_Size_Metrics.nascender(this.address());
    }

    @NativeType(value="FT_Pos")
    public long descender() {
        return FT_Size_Metrics.ndescender(this.address());
    }

    @NativeType(value="FT_Pos")
    public long height() {
        return FT_Size_Metrics.nheight(this.address());
    }

    @NativeType(value="FT_Pos")
    public long max_advance() {
        return FT_Size_Metrics.nmax_advance(this.address());
    }

    public static FT_Size_Metrics create(long address) {
        return new FT_Size_Metrics(address, null);
    }

    @Nullable
    public static FT_Size_Metrics createSafe(long address) {
        return address == 0L ? null : new FT_Size_Metrics(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static short nx_ppem(long struct) {
        return UNSAFE.getShort(null, struct + (long)X_PPEM);
    }

    public static short ny_ppem(long struct) {
        return UNSAFE.getShort(null, struct + (long)Y_PPEM);
    }

    public static long nx_scale(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)X_SCALE);
    }

    public static long ny_scale(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)Y_SCALE);
    }

    public static long nascender(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)ASCENDER);
    }

    public static long ndescender(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)DESCENDER);
    }

    public static long nheight(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)HEIGHT);
    }

    public static long nmax_advance(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)MAX_ADVANCE);
    }

    static {
        Struct.Layout layout = FT_Size_Metrics.__struct(FT_Size_Metrics.__member(2), FT_Size_Metrics.__member(2), FT_Size_Metrics.__member(CLONG_SIZE), FT_Size_Metrics.__member(CLONG_SIZE), FT_Size_Metrics.__member(CLONG_SIZE), FT_Size_Metrics.__member(CLONG_SIZE), FT_Size_Metrics.__member(CLONG_SIZE), FT_Size_Metrics.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        X_PPEM = layout.offsetof(0);
        Y_PPEM = layout.offsetof(1);
        X_SCALE = layout.offsetof(2);
        Y_SCALE = layout.offsetof(3);
        ASCENDER = layout.offsetof(4);
        DESCENDER = layout.offsetof(5);
        HEIGHT = layout.offsetof(6);
        MAX_ADVANCE = layout.offsetof(7);
    }

    public static class Buffer
    extends StructBuffer<FT_Size_Metrics, Buffer> {
        private static final FT_Size_Metrics ELEMENT_FACTORY = FT_Size_Metrics.create(-1L);

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
        protected FT_Size_Metrics getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_UShort")
        public short x_ppem() {
            return FT_Size_Metrics.nx_ppem(this.address());
        }

        @NativeType(value="FT_UShort")
        public short y_ppem() {
            return FT_Size_Metrics.ny_ppem(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long x_scale() {
            return FT_Size_Metrics.nx_scale(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long y_scale() {
            return FT_Size_Metrics.ny_scale(this.address());
        }

        @NativeType(value="FT_Pos")
        public long ascender() {
            return FT_Size_Metrics.nascender(this.address());
        }

        @NativeType(value="FT_Pos")
        public long descender() {
            return FT_Size_Metrics.ndescender(this.address());
        }

        @NativeType(value="FT_Pos")
        public long height() {
            return FT_Size_Metrics.nheight(this.address());
        }

        @NativeType(value="FT_Pos")
        public long max_advance() {
            return FT_Size_Metrics.nmax_advance(this.address());
        }
    }
}
