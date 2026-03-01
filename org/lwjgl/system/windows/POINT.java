package org.lwjgl.system.windows;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class POINT
extends Struct<POINT>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int X;
    public static final int Y;

    protected POINT(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected POINT create(long address, @Nullable ByteBuffer container) {
        return new POINT(address, container);
    }

    public POINT(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), POINT.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="LONG")
    public int x() {
        return POINT.nx(this.address());
    }

    @NativeType(value="LONG")
    public int y() {
        return POINT.ny(this.address());
    }

    public POINT x(@NativeType(value="LONG") int value) {
        POINT.nx(this.address(), value);
        return this;
    }

    public POINT y(@NativeType(value="LONG") int value) {
        POINT.ny(this.address(), value);
        return this;
    }

    public POINT set(int x, int y) {
        this.x(x);
        this.y(y);
        return this;
    }

    public POINT set(POINT src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static POINT malloc() {
        return new POINT(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static POINT calloc() {
        return new POINT(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static POINT create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new POINT(MemoryUtil.memAddress(container), container);
    }

    public static POINT create(long address) {
        return new POINT(address, null);
    }

    @Nullable
    public static POINT createSafe(long address) {
        return address == 0L ? null : new POINT(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(POINT.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = POINT.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    @Deprecated
    public static POINT mallocStack() {
        return POINT.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static POINT callocStack() {
        return POINT.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static POINT mallocStack(MemoryStack stack) {
        return POINT.malloc(stack);
    }

    @Deprecated
    public static POINT callocStack(MemoryStack stack) {
        return POINT.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return POINT.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return POINT.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return POINT.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return POINT.calloc(capacity, stack);
    }

    public static POINT malloc(MemoryStack stack) {
        return new POINT(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static POINT calloc(MemoryStack stack) {
        return new POINT(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nx(long struct) {
        return UNSAFE.getInt(null, struct + (long)X);
    }

    public static int ny(long struct) {
        return UNSAFE.getInt(null, struct + (long)Y);
    }

    public static void nx(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)X, value);
    }

    public static void ny(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)Y, value);
    }

    static {
        Struct.Layout layout = POINT.__struct(POINT.__member(4), POINT.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        X = layout.offsetof(0);
        Y = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<POINT, Buffer>
    implements NativeResource {
        private static final POINT ELEMENT_FACTORY = POINT.create(-1L);

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
        protected POINT getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="LONG")
        public int x() {
            return POINT.nx(this.address());
        }

        @NativeType(value="LONG")
        public int y() {
            return POINT.ny(this.address());
        }

        public Buffer x(@NativeType(value="LONG") int value) {
            POINT.nx(this.address(), value);
            return this;
        }

        public Buffer y(@NativeType(value="LONG") int value) {
            POINT.ny(this.address(), value);
            return this;
        }
    }
}
