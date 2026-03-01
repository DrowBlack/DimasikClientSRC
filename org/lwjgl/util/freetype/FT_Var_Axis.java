package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class FT_Var_Axis
extends Struct<FT_Var_Axis> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int NAME;
    public static final int MINIMUM;
    public static final int DEF;
    public static final int MAXIMUM;
    public static final int TAG;
    public static final int STRID;

    protected FT_Var_Axis(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Var_Axis create(long address, @Nullable ByteBuffer container) {
        return new FT_Var_Axis(address, container);
    }

    public FT_Var_Axis(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Var_Axis.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_String *")
    public ByteBuffer name() {
        return FT_Var_Axis.nname(this.address());
    }

    @NativeType(value="FT_String *")
    public String nameString() {
        return FT_Var_Axis.nnameString(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long minimum() {
        return FT_Var_Axis.nminimum(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long def() {
        return FT_Var_Axis.ndef(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long maximum() {
        return FT_Var_Axis.nmaximum(this.address());
    }

    @NativeType(value="FT_ULong")
    public long tag() {
        return FT_Var_Axis.ntag(this.address());
    }

    @NativeType(value="FT_UInt")
    public int strid() {
        return FT_Var_Axis.nstrid(this.address());
    }

    public static FT_Var_Axis create(long address) {
        return new FT_Var_Axis(address, null);
    }

    @Nullable
    public static FT_Var_Axis createSafe(long address) {
        return address == 0L ? null : new FT_Var_Axis(address, null);
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

    public static long ndef(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)DEF);
    }

    public static long nmaximum(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)MAXIMUM);
    }

    public static long ntag(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)TAG);
    }

    public static int nstrid(long struct) {
        return UNSAFE.getInt(null, struct + (long)STRID);
    }

    static {
        Struct.Layout layout = FT_Var_Axis.__struct(FT_Var_Axis.__member(POINTER_SIZE), FT_Var_Axis.__member(CLONG_SIZE), FT_Var_Axis.__member(CLONG_SIZE), FT_Var_Axis.__member(CLONG_SIZE), FT_Var_Axis.__member(CLONG_SIZE), FT_Var_Axis.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        NAME = layout.offsetof(0);
        MINIMUM = layout.offsetof(1);
        DEF = layout.offsetof(2);
        MAXIMUM = layout.offsetof(3);
        TAG = layout.offsetof(4);
        STRID = layout.offsetof(5);
    }

    public static class Buffer
    extends StructBuffer<FT_Var_Axis, Buffer> {
        private static final FT_Var_Axis ELEMENT_FACTORY = FT_Var_Axis.create(-1L);

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
        protected FT_Var_Axis getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_String *")
        public ByteBuffer name() {
            return FT_Var_Axis.nname(this.address());
        }

        @NativeType(value="FT_String *")
        public String nameString() {
            return FT_Var_Axis.nnameString(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long minimum() {
            return FT_Var_Axis.nminimum(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long def() {
            return FT_Var_Axis.ndef(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long maximum() {
            return FT_Var_Axis.nmaximum(this.address());
        }

        @NativeType(value="FT_ULong")
        public long tag() {
            return FT_Var_Axis.ntag(this.address());
        }

        @NativeType(value="FT_UInt")
        public int strid() {
            return FT_Var_Axis.nstrid(this.address());
        }
    }
}
