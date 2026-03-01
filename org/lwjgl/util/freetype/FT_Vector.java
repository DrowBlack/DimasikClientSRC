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

public class FT_Vector
extends Struct<FT_Vector>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int X;
    public static final int Y;

    protected FT_Vector(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Vector create(long address, @Nullable ByteBuffer container) {
        return new FT_Vector(address, container);
    }

    public FT_Vector(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Vector.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Pos")
    public long x() {
        return FT_Vector.nx(this.address());
    }

    @NativeType(value="FT_Pos")
    public long y() {
        return FT_Vector.ny(this.address());
    }

    public FT_Vector x(@NativeType(value="FT_Pos") long value) {
        FT_Vector.nx(this.address(), value);
        return this;
    }

    public FT_Vector y(@NativeType(value="FT_Pos") long value) {
        FT_Vector.ny(this.address(), value);
        return this;
    }

    public FT_Vector set(long x, long y) {
        this.x(x);
        this.y(y);
        return this;
    }

    public FT_Vector set(FT_Vector src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Vector malloc() {
        return new FT_Vector(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Vector calloc() {
        return new FT_Vector(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Vector create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Vector(MemoryUtil.memAddress(container), container);
    }

    public static FT_Vector create(long address) {
        return new FT_Vector(address, null);
    }

    @Nullable
    public static FT_Vector createSafe(long address) {
        return address == 0L ? null : new FT_Vector(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Vector.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Vector.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Vector malloc(MemoryStack stack) {
        return new FT_Vector(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Vector calloc(MemoryStack stack) {
        return new FT_Vector(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nx(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)X);
    }

    public static long ny(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)Y);
    }

    public static void nx(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)X, value);
    }

    public static void ny(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)Y, value);
    }

    static {
        Struct.Layout layout = FT_Vector.__struct(FT_Vector.__member(CLONG_SIZE), FT_Vector.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        X = layout.offsetof(0);
        Y = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_Vector, Buffer>
    implements NativeResource {
        private static final FT_Vector ELEMENT_FACTORY = FT_Vector.create(-1L);

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
        protected FT_Vector getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Pos")
        public long x() {
            return FT_Vector.nx(this.address());
        }

        @NativeType(value="FT_Pos")
        public long y() {
            return FT_Vector.ny(this.address());
        }

        public Buffer x(@NativeType(value="FT_Pos") long value) {
            FT_Vector.nx(this.address(), value);
            return this;
        }

        public Buffer y(@NativeType(value="FT_Pos") long value) {
            FT_Vector.ny(this.address(), value);
            return this;
        }
    }
}
