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

public class FT_UnitVector
extends Struct<FT_UnitVector>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int X;
    public static final int Y;

    protected FT_UnitVector(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_UnitVector create(long address, @Nullable ByteBuffer container) {
        return new FT_UnitVector(address, container);
    }

    public FT_UnitVector(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_UnitVector.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_F2Dot14")
    public short x() {
        return FT_UnitVector.nx(this.address());
    }

    @NativeType(value="FT_F2Dot14")
    public short y() {
        return FT_UnitVector.ny(this.address());
    }

    public FT_UnitVector x(@NativeType(value="FT_F2Dot14") short value) {
        FT_UnitVector.nx(this.address(), value);
        return this;
    }

    public FT_UnitVector y(@NativeType(value="FT_F2Dot14") short value) {
        FT_UnitVector.ny(this.address(), value);
        return this;
    }

    public FT_UnitVector set(short x, short y) {
        this.x(x);
        this.y(y);
        return this;
    }

    public FT_UnitVector set(FT_UnitVector src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_UnitVector malloc() {
        return new FT_UnitVector(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_UnitVector calloc() {
        return new FT_UnitVector(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_UnitVector create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_UnitVector(MemoryUtil.memAddress(container), container);
    }

    public static FT_UnitVector create(long address) {
        return new FT_UnitVector(address, null);
    }

    @Nullable
    public static FT_UnitVector createSafe(long address) {
        return address == 0L ? null : new FT_UnitVector(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_UnitVector.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_UnitVector.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_UnitVector malloc(MemoryStack stack) {
        return new FT_UnitVector(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_UnitVector calloc(MemoryStack stack) {
        return new FT_UnitVector(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static short nx(long struct) {
        return UNSAFE.getShort(null, struct + (long)X);
    }

    public static short ny(long struct) {
        return UNSAFE.getShort(null, struct + (long)Y);
    }

    public static void nx(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)X, value);
    }

    public static void ny(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)Y, value);
    }

    static {
        Struct.Layout layout = FT_UnitVector.__struct(FT_UnitVector.__member(2), FT_UnitVector.__member(2));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        X = layout.offsetof(0);
        Y = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_UnitVector, Buffer>
    implements NativeResource {
        private static final FT_UnitVector ELEMENT_FACTORY = FT_UnitVector.create(-1L);

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
        protected FT_UnitVector getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_F2Dot14")
        public short x() {
            return FT_UnitVector.nx(this.address());
        }

        @NativeType(value="FT_F2Dot14")
        public short y() {
            return FT_UnitVector.ny(this.address());
        }

        public Buffer x(@NativeType(value="FT_F2Dot14") short value) {
            FT_UnitVector.nx(this.address(), value);
            return this;
        }

        public Buffer y(@NativeType(value="FT_F2Dot14") short value) {
            FT_UnitVector.ny(this.address(), value);
            return this;
        }
    }
}
