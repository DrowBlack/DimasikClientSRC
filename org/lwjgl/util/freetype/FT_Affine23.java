package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class FT_Affine23
extends Struct<FT_Affine23> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int XX;
    public static final int XY;
    public static final int DX;
    public static final int YX;
    public static final int YY;
    public static final int DY;

    protected FT_Affine23(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Affine23 create(long address, @Nullable ByteBuffer container) {
        return new FT_Affine23(address, container);
    }

    public FT_Affine23(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Affine23.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Fixed")
    public long xx() {
        return FT_Affine23.nxx(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long xy() {
        return FT_Affine23.nxy(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long dx() {
        return FT_Affine23.ndx(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long yx() {
        return FT_Affine23.nyx(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long yy() {
        return FT_Affine23.nyy(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long dy() {
        return FT_Affine23.ndy(this.address());
    }

    public static FT_Affine23 create(long address) {
        return new FT_Affine23(address, null);
    }

    @Nullable
    public static FT_Affine23 createSafe(long address) {
        return address == 0L ? null : new FT_Affine23(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static long nxx(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)XX);
    }

    public static long nxy(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)XY);
    }

    public static long ndx(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)DX);
    }

    public static long nyx(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)YX);
    }

    public static long nyy(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)YY);
    }

    public static long ndy(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)DY);
    }

    static {
        Struct.Layout layout = FT_Affine23.__struct(FT_Affine23.__member(CLONG_SIZE), FT_Affine23.__member(CLONG_SIZE), FT_Affine23.__member(CLONG_SIZE), FT_Affine23.__member(CLONG_SIZE), FT_Affine23.__member(CLONG_SIZE), FT_Affine23.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        XX = layout.offsetof(0);
        XY = layout.offsetof(1);
        DX = layout.offsetof(2);
        YX = layout.offsetof(3);
        YY = layout.offsetof(4);
        DY = layout.offsetof(5);
    }

    public static class Buffer
    extends StructBuffer<FT_Affine23, Buffer> {
        private static final FT_Affine23 ELEMENT_FACTORY = FT_Affine23.create(-1L);

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
        protected FT_Affine23 getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Fixed")
        public long xx() {
            return FT_Affine23.nxx(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long xy() {
            return FT_Affine23.nxy(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long dx() {
            return FT_Affine23.ndx(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long yx() {
            return FT_Affine23.nyx(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long yy() {
            return FT_Affine23.nyy(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long dy() {
            return FT_Affine23.ndy(this.address());
        }
    }
}
