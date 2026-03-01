package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class FT_MM_Axis
extends Struct<FT_MM_Axis> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int NAME;
    public static final int MINIMUM;
    public static final int MAXIMUM;

    protected FT_MM_Axis(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_MM_Axis create(long address, @Nullable ByteBuffer container) {
        return new FT_MM_Axis(address, container);
    }

    public FT_MM_Axis(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_MM_Axis.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_String *")
    public ByteBuffer name() {
        return FT_MM_Axis.nname(this.address());
    }

    @NativeType(value="FT_String *")
    public String nameString() {
        return FT_MM_Axis.nnameString(this.address());
    }

    @NativeType(value="FT_Long")
    public long minimum() {
        return FT_MM_Axis.nminimum(this.address());
    }

    @NativeType(value="FT_Long")
    public long maximum() {
        return FT_MM_Axis.nmaximum(this.address());
    }

    public static FT_MM_Axis create(long address) {
        return new FT_MM_Axis(address, null);
    }

    @Nullable
    public static FT_MM_Axis createSafe(long address) {
        return address == 0L ? null : new FT_MM_Axis(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static ByteBuffer nname(long struct) {
        return MemoryUtil.memByteBufferNT1(MemoryUtil.memGetAddress(struct + (long)NAME));
    }

    public static String nnameString(long struct) {
        return MemoryUtil.memUTF8(MemoryUtil.memGetAddress(struct + (long)NAME));
    }

    public static long nminimum(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)MINIMUM);
    }

    public static long nmaximum(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)MAXIMUM);
    }

    static {
        Struct.Layout layout = FT_MM_Axis.__struct(FT_MM_Axis.__member(POINTER_SIZE), FT_MM_Axis.__member(CLONG_SIZE), FT_MM_Axis.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        NAME = layout.offsetof(0);
        MINIMUM = layout.offsetof(1);
        MAXIMUM = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<FT_MM_Axis, Buffer> {
        private static final FT_MM_Axis ELEMENT_FACTORY = FT_MM_Axis.create(-1L);

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
        protected FT_MM_Axis getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_String *")
        public ByteBuffer name() {
            return FT_MM_Axis.nname(this.address());
        }

        @NativeType(value="FT_String *")
        public String nameString() {
            return FT_MM_Axis.nnameString(this.address());
        }

        @NativeType(value="FT_Long")
        public long minimum() {
            return FT_MM_Axis.nminimum(this.address());
        }

        @NativeType(value="FT_Long")
        public long maximum() {
            return FT_MM_Axis.nmaximum(this.address());
        }
    }
}
