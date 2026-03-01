package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class FT_Matrix
extends Struct<FT_Matrix>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int XX;
    public static final int XY;
    public static final int YX;
    public static final int YY;

    protected FT_Matrix(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Matrix create(long address, @Nullable ByteBuffer container) {
        return new FT_Matrix(address, container);
    }

    public FT_Matrix(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Matrix.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Fixed")
    public long xx() {
        return FT_Matrix.nxx(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long xy() {
        return FT_Matrix.nxy(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long yx() {
        return FT_Matrix.nyx(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long yy() {
        return FT_Matrix.nyy(this.address());
    }

    public FT_Matrix xx(@NativeType(value="FT_Fixed") long value) {
        FT_Matrix.nxx(this.address(), value);
        return this;
    }

    public FT_Matrix xy(@NativeType(value="FT_Fixed") long value) {
        FT_Matrix.nxy(this.address(), value);
        return this;
    }

    public FT_Matrix yx(@NativeType(value="FT_Fixed") long value) {
        FT_Matrix.nyx(this.address(), value);
        return this;
    }

    public FT_Matrix yy(@NativeType(value="FT_Fixed") long value) {
        FT_Matrix.nyy(this.address(), value);
        return this;
    }

    public FT_Matrix set(long xx, long xy, long yx, long yy) {
        this.xx(xx);
        this.xy(xy);
        this.yx(yx);
        this.yy(yy);
        return this;
    }

    public FT_Matrix set(FT_Matrix src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Matrix malloc() {
        return new FT_Matrix(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Matrix calloc() {
        return new FT_Matrix(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Matrix create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Matrix(MemoryUtil.memAddress(container), container);
    }

    public static FT_Matrix create(long address) {
        return new FT_Matrix(address, null);
    }

    @Nullable
    public static FT_Matrix createSafe(long address) {
        return address == 0L ? null : new FT_Matrix(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Matrix.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Matrix.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Matrix malloc(MemoryStack stack) {
        return new FT_Matrix(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Matrix calloc(MemoryStack stack) {
        return new FT_Matrix(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nxx(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)XX);
    }

    public static long nxy(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)XY);
    }

    public static long nyx(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)YX);
    }

    public static long nyy(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)YY);
    }

    public static void nxx(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)XX, value);
    }

    public static void nxy(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)XY, value);
    }

    public static void nyx(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)YX, value);
    }

    public static void nyy(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)YY, value);
    }

    static {
        Struct.Layout layout = FT_Matrix.__struct(FT_Matrix.__member(CLONG_SIZE), FT_Matrix.__member(CLONG_SIZE), FT_Matrix.__member(CLONG_SIZE), FT_Matrix.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        XX = layout.offsetof(0);
        XY = layout.offsetof(1);
        YX = layout.offsetof(2);
        YY = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FT_Matrix, Buffer>
    implements NativeResource {
        private static final FT_Matrix ELEMENT_FACTORY = FT_Matrix.create(-1L);

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
        protected FT_Matrix getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Fixed")
        public long xx() {
            return FT_Matrix.nxx(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long xy() {
            return FT_Matrix.nxy(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long yx() {
            return FT_Matrix.nyx(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long yy() {
            return FT_Matrix.nyy(this.address());
        }

        public Buffer xx(@NativeType(value="FT_Fixed") long value) {
            FT_Matrix.nxx(this.address(), value);
            return this;
        }

        public Buffer xy(@NativeType(value="FT_Fixed") long value) {
            FT_Matrix.nxy(this.address(), value);
            return this;
        }

        public Buffer yx(@NativeType(value="FT_Fixed") long value) {
            FT_Matrix.nyx(this.address(), value);
            return this;
        }

        public Buffer yy(@NativeType(value="FT_Fixed") long value) {
            FT_Matrix.nyy(this.address(), value);
            return this;
        }
    }
}
