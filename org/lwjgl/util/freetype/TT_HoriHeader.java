package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class TT_HoriHeader
extends Struct<TT_HoriHeader> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int VERSION;
    public static final int ASCENDER;
    public static final int DESCENDER;
    public static final int LINE_GAP;
    public static final int ADVANCE_WIDTH_MAX;
    public static final int MIN_LEFT_SIDE_BEARING;
    public static final int MIN_RIGHT_SIDE_BEARING;
    public static final int XMAX_EXTENT;
    public static final int CARET_SLOPE_RISE;
    public static final int CARET_SLOPE_RUN;
    public static final int CARET_OFFSET;
    public static final int RESERVED;
    public static final int METRIC_DATA_FORMAT;
    public static final int NUMBER_OF_HMETRICS;
    public static final int LONG_METRICS;
    public static final int SHORT_METRICS;

    protected TT_HoriHeader(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected TT_HoriHeader create(long address, @Nullable ByteBuffer container) {
        return new TT_HoriHeader(address, container);
    }

    public TT_HoriHeader(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), TT_HoriHeader.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Fixed")
    public long Version() {
        return TT_HoriHeader.nVersion(this.address());
    }

    @NativeType(value="FT_Short")
    public short Ascender() {
        return TT_HoriHeader.nAscender(this.address());
    }

    @NativeType(value="FT_Short")
    public short Descender() {
        return TT_HoriHeader.nDescender(this.address());
    }

    @NativeType(value="FT_Short")
    public short Line_Gap() {
        return TT_HoriHeader.nLine_Gap(this.address());
    }

    @NativeType(value="FT_UShort")
    public short advance_Width_Max() {
        return TT_HoriHeader.nadvance_Width_Max(this.address());
    }

    @NativeType(value="FT_Short")
    public short min_Left_Side_Bearing() {
        return TT_HoriHeader.nmin_Left_Side_Bearing(this.address());
    }

    @NativeType(value="FT_Short")
    public short min_Right_Side_Bearing() {
        return TT_HoriHeader.nmin_Right_Side_Bearing(this.address());
    }

    @NativeType(value="FT_Short")
    public short xMax_Extent() {
        return TT_HoriHeader.nxMax_Extent(this.address());
    }

    @NativeType(value="FT_Short")
    public short caret_Slope_Rise() {
        return TT_HoriHeader.ncaret_Slope_Rise(this.address());
    }

    @NativeType(value="FT_Short")
    public short caret_Slope_Run() {
        return TT_HoriHeader.ncaret_Slope_Run(this.address());
    }

    @NativeType(value="FT_Short")
    public short caret_Offset() {
        return TT_HoriHeader.ncaret_Offset(this.address());
    }

    @NativeType(value="FT_Short[4]")
    public ShortBuffer Reserved() {
        return TT_HoriHeader.nReserved(this.address());
    }

    @NativeType(value="FT_Short")
    public short Reserved(int index) {
        return TT_HoriHeader.nReserved(this.address(), index);
    }

    @NativeType(value="FT_Short")
    public short metric_Data_Format() {
        return TT_HoriHeader.nmetric_Data_Format(this.address());
    }

    @NativeType(value="FT_UShort")
    public short number_Of_HMetrics() {
        return TT_HoriHeader.nnumber_Of_HMetrics(this.address());
    }

    @Nullable
    @NativeType(value="void *")
    public ByteBuffer long_metrics(int capacity) {
        return TT_HoriHeader.nlong_metrics(this.address(), capacity);
    }

    @Nullable
    @NativeType(value="void *")
    public ByteBuffer short_metrics(int capacity) {
        return TT_HoriHeader.nshort_metrics(this.address(), capacity);
    }

    public static TT_HoriHeader create(long address) {
        return new TT_HoriHeader(address, null);
    }

    @Nullable
    public static TT_HoriHeader createSafe(long address) {
        return address == 0L ? null : new TT_HoriHeader(address, null);
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

    public static short nadvance_Width_Max(long struct) {
        return UNSAFE.getShort(null, struct + (long)ADVANCE_WIDTH_MAX);
    }

    public static short nmin_Left_Side_Bearing(long struct) {
        return UNSAFE.getShort(null, struct + (long)MIN_LEFT_SIDE_BEARING);
    }

    public static short nmin_Right_Side_Bearing(long struct) {
        return UNSAFE.getShort(null, struct + (long)MIN_RIGHT_SIDE_BEARING);
    }

    public static short nxMax_Extent(long struct) {
        return UNSAFE.getShort(null, struct + (long)XMAX_EXTENT);
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

    public static short nnumber_Of_HMetrics(long struct) {
        return UNSAFE.getShort(null, struct + (long)NUMBER_OF_HMETRICS);
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
        Struct.Layout layout = TT_HoriHeader.__struct(TT_HoriHeader.__member(CLONG_SIZE), TT_HoriHeader.__member(2), TT_HoriHeader.__member(2), TT_HoriHeader.__member(2), TT_HoriHeader.__member(2), TT_HoriHeader.__member(2), TT_HoriHeader.__member(2), TT_HoriHeader.__member(2), TT_HoriHeader.__member(2), TT_HoriHeader.__member(2), TT_HoriHeader.__member(2), TT_HoriHeader.__array(2, 4), TT_HoriHeader.__member(2), TT_HoriHeader.__member(2), TT_HoriHeader.__member(POINTER_SIZE), TT_HoriHeader.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        VERSION = layout.offsetof(0);
        ASCENDER = layout.offsetof(1);
        DESCENDER = layout.offsetof(2);
        LINE_GAP = layout.offsetof(3);
        ADVANCE_WIDTH_MAX = layout.offsetof(4);
        MIN_LEFT_SIDE_BEARING = layout.offsetof(5);
        MIN_RIGHT_SIDE_BEARING = layout.offsetof(6);
        XMAX_EXTENT = layout.offsetof(7);
        CARET_SLOPE_RISE = layout.offsetof(8);
        CARET_SLOPE_RUN = layout.offsetof(9);
        CARET_OFFSET = layout.offsetof(10);
        RESERVED = layout.offsetof(11);
        METRIC_DATA_FORMAT = layout.offsetof(12);
        NUMBER_OF_HMETRICS = layout.offsetof(13);
        LONG_METRICS = layout.offsetof(14);
        SHORT_METRICS = layout.offsetof(15);
    }

    public static class Buffer
    extends StructBuffer<TT_HoriHeader, Buffer> {
        private static final TT_HoriHeader ELEMENT_FACTORY = TT_HoriHeader.create(-1L);

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
        protected TT_HoriHeader getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Fixed")
        public long Version() {
            return TT_HoriHeader.nVersion(this.address());
        }

        @NativeType(value="FT_Short")
        public short Ascender() {
            return TT_HoriHeader.nAscender(this.address());
        }

        @NativeType(value="FT_Short")
        public short Descender() {
            return TT_HoriHeader.nDescender(this.address());
        }

        @NativeType(value="FT_Short")
        public short Line_Gap() {
            return TT_HoriHeader.nLine_Gap(this.address());
        }

        @NativeType(value="FT_UShort")
        public short advance_Width_Max() {
            return TT_HoriHeader.nadvance_Width_Max(this.address());
        }

        @NativeType(value="FT_Short")
        public short min_Left_Side_Bearing() {
            return TT_HoriHeader.nmin_Left_Side_Bearing(this.address());
        }

        @NativeType(value="FT_Short")
        public short min_Right_Side_Bearing() {
            return TT_HoriHeader.nmin_Right_Side_Bearing(this.address());
        }

        @NativeType(value="FT_Short")
        public short xMax_Extent() {
            return TT_HoriHeader.nxMax_Extent(this.address());
        }

        @NativeType(value="FT_Short")
        public short caret_Slope_Rise() {
            return TT_HoriHeader.ncaret_Slope_Rise(this.address());
        }

        @NativeType(value="FT_Short")
        public short caret_Slope_Run() {
            return TT_HoriHeader.ncaret_Slope_Run(this.address());
        }

        @NativeType(value="FT_Short")
        public short caret_Offset() {
            return TT_HoriHeader.ncaret_Offset(this.address());
        }

        @NativeType(value="FT_Short[4]")
        public ShortBuffer Reserved() {
            return TT_HoriHeader.nReserved(this.address());
        }

        @NativeType(value="FT_Short")
        public short Reserved(int index) {
            return TT_HoriHeader.nReserved(this.address(), index);
        }

        @NativeType(value="FT_Short")
        public short metric_Data_Format() {
            return TT_HoriHeader.nmetric_Data_Format(this.address());
        }

        @NativeType(value="FT_UShort")
        public short number_Of_HMetrics() {
            return TT_HoriHeader.nnumber_Of_HMetrics(this.address());
        }

        @Nullable
        @NativeType(value="void *")
        public ByteBuffer long_metrics(int capacity) {
            return TT_HoriHeader.nlong_metrics(this.address(), capacity);
        }

        @Nullable
        @NativeType(value="void *")
        public ByteBuffer short_metrics(int capacity) {
            return TT_HoriHeader.nshort_metrics(this.address(), capacity);
        }
    }
}
