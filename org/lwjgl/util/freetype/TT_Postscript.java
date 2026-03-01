package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class TT_Postscript
extends Struct<TT_Postscript> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int FORMATTYPE;
    public static final int ITALICANGLE;
    public static final int UNDERLINEPOSITION;
    public static final int UNDERLINETHICKNESS;
    public static final int ISFIXEDPITCH;
    public static final int MINMEMTYPE42;
    public static final int MAXMEMTYPE42;
    public static final int MINMEMTYPE1;
    public static final int MAXMEMTYPE1;

    protected TT_Postscript(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected TT_Postscript create(long address, @Nullable ByteBuffer container) {
        return new TT_Postscript(address, container);
    }

    public TT_Postscript(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), TT_Postscript.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Fixed")
    public long FormatType() {
        return TT_Postscript.nFormatType(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long italicAngle() {
        return TT_Postscript.nitalicAngle(this.address());
    }

    @NativeType(value="FT_Short")
    public short underlinePosition() {
        return TT_Postscript.nunderlinePosition(this.address());
    }

    @NativeType(value="FT_Short")
    public short underlineThickness() {
        return TT_Postscript.nunderlineThickness(this.address());
    }

    @NativeType(value="FT_ULong")
    public long isFixedPitch() {
        return TT_Postscript.nisFixedPitch(this.address());
    }

    @NativeType(value="FT_ULong")
    public long minMemType42() {
        return TT_Postscript.nminMemType42(this.address());
    }

    @NativeType(value="FT_ULong")
    public long maxMemType42() {
        return TT_Postscript.nmaxMemType42(this.address());
    }

    @NativeType(value="FT_ULong")
    public long minMemType1() {
        return TT_Postscript.nminMemType1(this.address());
    }

    @NativeType(value="FT_ULong")
    public long maxMemType1() {
        return TT_Postscript.nmaxMemType1(this.address());
    }

    public static TT_Postscript create(long address) {
        return new TT_Postscript(address, null);
    }

    @Nullable
    public static TT_Postscript createSafe(long address) {
        return address == 0L ? null : new TT_Postscript(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static long nFormatType(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)FORMATTYPE);
    }

    public static long nitalicAngle(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)ITALICANGLE);
    }

    public static short nunderlinePosition(long struct) {
        return UNSAFE.getShort(null, struct + (long)UNDERLINEPOSITION);
    }

    public static short nunderlineThickness(long struct) {
        return UNSAFE.getShort(null, struct + (long)UNDERLINETHICKNESS);
    }

    public static long nisFixedPitch(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)ISFIXEDPITCH);
    }

    public static long nminMemType42(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)MINMEMTYPE42);
    }

    public static long nmaxMemType42(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)MAXMEMTYPE42);
    }

    public static long nminMemType1(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)MINMEMTYPE1);
    }

    public static long nmaxMemType1(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)MAXMEMTYPE1);
    }

    static {
        Struct.Layout layout = TT_Postscript.__struct(TT_Postscript.__member(CLONG_SIZE), TT_Postscript.__member(CLONG_SIZE), TT_Postscript.__member(2), TT_Postscript.__member(2), TT_Postscript.__member(CLONG_SIZE), TT_Postscript.__member(CLONG_SIZE), TT_Postscript.__member(CLONG_SIZE), TT_Postscript.__member(CLONG_SIZE), TT_Postscript.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        FORMATTYPE = layout.offsetof(0);
        ITALICANGLE = layout.offsetof(1);
        UNDERLINEPOSITION = layout.offsetof(2);
        UNDERLINETHICKNESS = layout.offsetof(3);
        ISFIXEDPITCH = layout.offsetof(4);
        MINMEMTYPE42 = layout.offsetof(5);
        MAXMEMTYPE42 = layout.offsetof(6);
        MINMEMTYPE1 = layout.offsetof(7);
        MAXMEMTYPE1 = layout.offsetof(8);
    }

    public static class Buffer
    extends StructBuffer<TT_Postscript, Buffer> {
        private static final TT_Postscript ELEMENT_FACTORY = TT_Postscript.create(-1L);

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
        protected TT_Postscript getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Fixed")
        public long FormatType() {
            return TT_Postscript.nFormatType(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long italicAngle() {
            return TT_Postscript.nitalicAngle(this.address());
        }

        @NativeType(value="FT_Short")
        public short underlinePosition() {
            return TT_Postscript.nunderlinePosition(this.address());
        }

        @NativeType(value="FT_Short")
        public short underlineThickness() {
            return TT_Postscript.nunderlineThickness(this.address());
        }

        @NativeType(value="FT_ULong")
        public long isFixedPitch() {
            return TT_Postscript.nisFixedPitch(this.address());
        }

        @NativeType(value="FT_ULong")
        public long minMemType42() {
            return TT_Postscript.nminMemType42(this.address());
        }

        @NativeType(value="FT_ULong")
        public long maxMemType42() {
            return TT_Postscript.nmaxMemType42(this.address());
        }

        @NativeType(value="FT_ULong")
        public long minMemType1() {
            return TT_Postscript.nminMemType1(this.address());
        }

        @NativeType(value="FT_ULong")
        public long maxMemType1() {
            return TT_Postscript.nmaxMemType1(this.address());
        }
    }
}
