package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class FT_Glyph_Metrics
extends Struct<FT_Glyph_Metrics> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int WIDTH;
    public static final int HEIGHT;
    public static final int HORIBEARINGX;
    public static final int HORIBEARINGY;
    public static final int HORIADVANCE;
    public static final int VERTBEARINGX;
    public static final int VERTBEARINGY;
    public static final int VERTADVANCE;

    protected FT_Glyph_Metrics(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Glyph_Metrics create(long address, @Nullable ByteBuffer container) {
        return new FT_Glyph_Metrics(address, container);
    }

    public FT_Glyph_Metrics(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Glyph_Metrics.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Pos")
    public long width() {
        return FT_Glyph_Metrics.nwidth(this.address());
    }

    @NativeType(value="FT_Pos")
    public long height() {
        return FT_Glyph_Metrics.nheight(this.address());
    }

    @NativeType(value="FT_Pos")
    public long horiBearingX() {
        return FT_Glyph_Metrics.nhoriBearingX(this.address());
    }

    @NativeType(value="FT_Pos")
    public long horiBearingY() {
        return FT_Glyph_Metrics.nhoriBearingY(this.address());
    }

    @NativeType(value="FT_Pos")
    public long horiAdvance() {
        return FT_Glyph_Metrics.nhoriAdvance(this.address());
    }

    @NativeType(value="FT_Pos")
    public long vertBearingX() {
        return FT_Glyph_Metrics.nvertBearingX(this.address());
    }

    @NativeType(value="FT_Pos")
    public long vertBearingY() {
        return FT_Glyph_Metrics.nvertBearingY(this.address());
    }

    @NativeType(value="FT_Pos")
    public long vertAdvance() {
        return FT_Glyph_Metrics.nvertAdvance(this.address());
    }

    public static FT_Glyph_Metrics create(long address) {
        return new FT_Glyph_Metrics(address, null);
    }

    @Nullable
    public static FT_Glyph_Metrics createSafe(long address) {
        return address == 0L ? null : new FT_Glyph_Metrics(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static long nwidth(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)WIDTH);
    }

    public static long nheight(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)HEIGHT);
    }

    public static long nhoriBearingX(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)HORIBEARINGX);
    }

    public static long nhoriBearingY(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)HORIBEARINGY);
    }

    public static long nhoriAdvance(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)HORIADVANCE);
    }

    public static long nvertBearingX(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)VERTBEARINGX);
    }

    public static long nvertBearingY(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)VERTBEARINGY);
    }

    public static long nvertAdvance(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)VERTADVANCE);
    }

    static {
        Struct.Layout layout = FT_Glyph_Metrics.__struct(FT_Glyph_Metrics.__member(CLONG_SIZE), FT_Glyph_Metrics.__member(CLONG_SIZE), FT_Glyph_Metrics.__member(CLONG_SIZE), FT_Glyph_Metrics.__member(CLONG_SIZE), FT_Glyph_Metrics.__member(CLONG_SIZE), FT_Glyph_Metrics.__member(CLONG_SIZE), FT_Glyph_Metrics.__member(CLONG_SIZE), FT_Glyph_Metrics.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        WIDTH = layout.offsetof(0);
        HEIGHT = layout.offsetof(1);
        HORIBEARINGX = layout.offsetof(2);
        HORIBEARINGY = layout.offsetof(3);
        HORIADVANCE = layout.offsetof(4);
        VERTBEARINGX = layout.offsetof(5);
        VERTBEARINGY = layout.offsetof(6);
        VERTADVANCE = layout.offsetof(7);
    }

    public static class Buffer
    extends StructBuffer<FT_Glyph_Metrics, Buffer> {
        private static final FT_Glyph_Metrics ELEMENT_FACTORY = FT_Glyph_Metrics.create(-1L);

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
        protected FT_Glyph_Metrics getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Pos")
        public long width() {
            return FT_Glyph_Metrics.nwidth(this.address());
        }

        @NativeType(value="FT_Pos")
        public long height() {
            return FT_Glyph_Metrics.nheight(this.address());
        }

        @NativeType(value="FT_Pos")
        public long horiBearingX() {
            return FT_Glyph_Metrics.nhoriBearingX(this.address());
        }

        @NativeType(value="FT_Pos")
        public long horiBearingY() {
            return FT_Glyph_Metrics.nhoriBearingY(this.address());
        }

        @NativeType(value="FT_Pos")
        public long horiAdvance() {
            return FT_Glyph_Metrics.nhoriAdvance(this.address());
        }

        @NativeType(value="FT_Pos")
        public long vertBearingX() {
            return FT_Glyph_Metrics.nvertBearingX(this.address());
        }

        @NativeType(value="FT_Pos")
        public long vertBearingY() {
            return FT_Glyph_Metrics.nvertBearingY(this.address());
        }

        @NativeType(value="FT_Pos")
        public long vertAdvance() {
            return FT_Glyph_Metrics.nvertAdvance(this.address());
        }
    }
}
