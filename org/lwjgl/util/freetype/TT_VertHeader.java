package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class TT_VertHeader
extends Struct<TT_VertHeader> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int VERSION;
    public static final int ASCENDER;
    public static final int DESCENDER;
    public static final int LINE_GAP;
    public static final int ADVANCE_HEIGHT_MAX;
    public static final int MIN_TOP_SIDE_BEARING;
    public static final int MIN_BOTTOM_SIDE_BEARING;
    public static final int YMAX_EXTENT;
    public static final int CARET_SLOPE_RISE;
    public static final int CARET_SLOPE_RUN;
    public static final int CARET_OFFSET;
    public static final int RESERVED;
    public static final int METRIC_DATA_FORMAT;
    public static final int NUMBER_OF_VMETRICS;
    public static final int LONG_METRICS;
    public static final int SHORT_METRICS;

    protected TT_VertHeader(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected TT_VertHeader create(long address, @Nullable ByteBuffer container) {
        return new TT_VertHeader(address, container);
    }

    public TT_VertHeader(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), TT_VertHeader.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Fixed")
    public long Version() {
        return TT_VertHeader.nVersion(this.address());
    }

    @NativeType(value="FT_Short")
    public short Ascender() {
        return TT_VertHeader.nAscender(this.address());
    }

    @NativeType(value="FT_Short")
    public short Descender() {
        return TT_VertHeader.nDescender(this.address());
    }

    @NativeType(value="FT_Short")
    public short Line_Gap() {
        return TT_VertHeader.nLine_Gap(this.address());
    }

    @NativeType(value="FT_UShort")
    public short advance_Height_Max() {
        return TT_VertHeader.nadvance_Height_Max(this.address());
    }

    @NativeType(value="FT_Short")
    public short min_Top_Side_Bearing() {
        return TT_VertHeader.nmin_Top_Side_Bearing(this.address());
    }

    @NativeType(value="FT_Short")
    public short min_Bottom_Side_Bearing() {
        return TT_VertHeader.nmin_Bottom_Side_Bearing(this.address());
    }

    @NativeType(value="FT_Short")
    public short yMax_Extent() {
        return TT_VertHeader.nyMax_Extent(this.address());
    }

    @NativeType(value="FT_Short")
    public short caret_Slope_Rise() {
        return TT_VertHeader.ncaret_Slope_Rise(this.address());
    }

    @NativeType(value="FT_Short")
    public short caret_Slope_Run() {
        return TT_VertHeader.ncaret_Slope_Run(this.address());
    }

    @NativeType(value="FT_Short")
    public short caret_Offset() {
        return TT_VertHeader.ncaret_Offset(this.address());
    }

    @NativeType(value="FT_Short[4]")
    public ShortBuffer Reserved() {
        return TT_VertHeader.nReserved(this.address());
    }

    @NativeType(value="FT_Short")
    public short Reserved(int index) {
        return TT_VertHeader.nReserved(this.address(), index);
    }

    @NativeType(value="FT_Short")
    public short metric_Data_Format() {
        return TT_VertHeader.nmetric_Data_Format(this.address());
    }

    @NativeType(value="FT_UShort")
    public short number_Of_VMetrics() {
        return TT_VertHeader.nnumber_Of_VMetrics(this.address());
    }

    @Nullable
    @NativeType(value="void *")
    public ByteBuffer long_metrics(int capacity) {
        return TT_VertHeader.nlong_metrics(this.address(), capacity);
    }

    @Nullable
    @NativeType(value="void *")
    public ByteBuffer short_metrics(int capacity) {
        return TT_VertHeader.nshort_metrics(this.address(), capacity);
    }

    public static TT_VertHeader create(long address) {
        return new TT_VertHeader(address, null);
    }

    @Nullable
    public static TT_VertHeader createSafe(long address) {
        return address == 0L ? null : new TT_VertHeader(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static long nVersion(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)VERSION);
    }

    public static short nAscender(long struct) {
        return UNSAFE.getShort(null, struct + (long)ASCENDER);
    }

    public static short nDescender(long struct) {
        return UNSAFE.getShort(null, struct + (long)DESCENDER);
    }

    public static short nLine_Gap(long struct) {
        return UNSAFE.getShort(null, struct + (long)LINE_GAP);
    }

    public static short nadvance_Height_Max(long struct) {
        return UNSAFE.getShort(null, struct + (long)ADVANCE_HEIGHT_MAX);
    }

    public static short nmin_Top_Side_Bearing(long struct) {
        return UNSAFE.getShort(null, struct + (long)MIN_TOP_SIDE_BEARING);
    }

    public static short nmin_Bottom_Side_Bearing(long struct) {
        return UNSAFE.getShort(null, struct + (long)MIN_BOTTOM_SIDE_BEARING);
    }

    public static short nyMax_Extent(long struct) {
        return UNSAFE.getShort(null, struct + (long)YMAX_EXTENT);
    }

    public static short ncaret_Slope_Rise(long struct) {
        return UNSAFE.getShort(null, struct + (long)CARET_SLOPE_RISE);
    }

    public static short ncaret_Slope_Run(long struct) {
        return UNSAFE.getShort(null, struct + (long)CARET_SLOPE_RUN);
    }

    public static short ncaret_Offset(long struct) {
        return UNSAFE.getShort(null, struct + (long)CARET_OFFSET);
    }

    public static ShortBuffer nReserved(long struct) {
        return MemoryUtil.memShortBuffer(struct + (long)RESERVED, 4);
    }

    public static short nReserved(long struct, int index) {
        return UNSAFE.getShort(null, struct + (long)RESERVED + Checks.check(index, 4) * 2L);
    }

    public static short nmetric_Data_Format(long struct) {
        return UNSAFE.getShort(null, struct + (long)METRIC_DATA_FORMAT);
    }

    public static short nnumber_Of_VMetrics(long struct) {
        return UNSAFE.getShort(null, struct + (long)NUMBER_OF_VMETRICS);
    }

    @Nullable
    public static ByteBuffer nlong_metrics(long struct, int capacity) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)LONG_METRICS), capacity);
    }

    @Nullable
    public static ByteBuffer nshort_metrics(long struct, int capacity) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)SHORT_METRICS), capacity);
    }

    static {
        Struct.Layout layout = TT_VertHeader.__struct(TT_VertHeader.__member(CLONG_SIZE), TT_VertHeader.__member(2), TT_VertHeader.__member(2), TT_VertHeader.__member(2), TT_VertHeader.__member(2), TT_VertHeader.__member(2), TT_VertHeader.__member(2), TT_VertHeader.__member(2), TT_VertHeader.__member(2), TT_VertHeader.__member(2), TT_VertHeader.__member(2), TT_VertHeader.__array(2, 4), TT_VertHeader.__member(2), TT_VertHeader.__member(2), TT_VertHeader.__member(POINTER_SIZE), TT_VertHeader.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        VERSION = layout.offsetof(0);
        ASCENDER = layout.offsetof(1);
        DESCENDER = layout.offsetof(2);
        LINE_GAP = layout.offsetof(3);
        ADVANCE_HEIGHT_MAX = layout.offsetof(4);
        MIN_TOP_SIDE_BEARING = layout.offsetof(5);
        MIN_BOTTOM_SIDE_BEARING = layout.offsetof(6);
        YMAX_EXTENT = layout.offsetof(7);
        CARET_SLOPE_RISE = layout.offsetof(8);
        CARET_SLOPE_RUN = layout.offsetof(9);
        CARET_OFFSET = layout.offsetof(10);
        RESERVED = layout.offsetof(11);
        METRIC_DATA_FORMAT = layout.offsetof(12);
        NUMBER_OF_VMETRICS = layout.offsetof(13);
        LONG_METRICS = layout.offsetof(14);
        SHORT_METRICS = layout.offsetof(15);
    }

    public static class Buffer
    extends StructBuffer<TT_VertHeader, Buffer> {
        private static final TT_VertHeader ELEMENT_FACTORY = TT_VertHeader.create(-1L);

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
        protected TT_VertHeader getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Fixed")
        public long Version() {
            return TT_VertHeader.nVersion(this.address());
        }

        @NativeType(value="FT_Short")
        public short Ascender() {
            return TT_VertHeader.nAscender(this.address());
        }

        @NativeType(value="FT_Short")
        public short Descender() {
            return TT_VertHeader.nDescender(this.address());
        }

        @NativeType(value="FT_Short")
        public short Line_Gap() {
            return TT_VertHeader.nLine_Gap(this.address());
        }

        @NativeType(value="FT_UShort")
        public short advance_Height_Max() {
            return TT_VertHeader.nadvance_Height_Max(this.address());
        }

        @NativeType(value="FT_Short")
        public short min_Top_Side_Bearing() {
            return TT_VertHeader.nmin_Top_Side_Bearing(this.address());
        }

        @NativeType(value="FT_Short")
        public short min_Bottom_Side_Bearing() {
            return TT_VertHeader.nmin_Bottom_Side_Bearing(this.address());
        }

        @NativeType(value="FT_Short")
        public short yMax_Extent() {
            return TT_VertHeader.nyMax_Extent(this.address());
        }

        @NativeType(value="FT_Short")
        public short caret_Slope_Rise() {
            return TT_VertHeader.ncaret_Slope_Rise(this.address());
        }

        @NativeType(value="FT_Short")
        public short caret_Slope_Run() {
            return TT_VertHeader.ncaret_Slope_Run(this.address());
        }

        @NativeType(value="FT_Short")
        public short caret_Offset() {
            return TT_VertHeader.ncaret_Offset(this.address());
        }

        @NativeType(value="FT_Short[4]")
        public ShortBuffer Reserved() {
            return TT_VertHeader.nReserved(this.address());
        }

        @NativeType(value="FT_Short")
        public short Reserved(int index) {
            return TT_VertHeader.nReserved(this.address(), index);
        }

        @NativeType(value="FT_Short")
        public short metric_Data_Format() {
            return TT_VertHeader.nmetric_Data_Format(this.address());
        }

        @NativeType(value="FT_UShort")
        public short number_Of_VMetrics() {
            return TT_VertHeader.nnumber_Of_VMetrics(this.address());
        }

        @Nullable
        @NativeType(value="void *")
        public ByteBuffer long_metrics(int capacity) {
            return TT_VertHeader.nlong_metrics(this.address(), capacity);
        }

        @Nullable
        @NativeType(value="void *")
        public ByteBuffer short_metrics(int capacity) {
            return TT_VertHeader.nshort_metrics(this.address(), capacity);
        }
    }
}
