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

public class RECT
extends Struct<RECT>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int LEFT;
    public static final int TOP;
    public static final int RIGHT;
    public static final int BOTTOM;

    protected RECT(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected RECT create(long address, @Nullable ByteBuffer container) {
        return new RECT(address, container);
    }

    public RECT(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), RECT.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="LONG")
    public int left() {
        return RECT.nleft(this.address());
    }

    @NativeType(value="LONG")
    public int top() {
        return RECT.ntop(this.address());
    }

    @NativeType(value="LONG")
    public int right() {
        return RECT.nright(this.address());
    }

    @NativeType(value="LONG")
    public int bottom() {
        return RECT.nbottom(this.address());
    }

    public RECT left(@NativeType(value="LONG") int value) {
        RECT.nleft(this.address(), value);
        return this;
    }

    public RECT top(@NativeType(value="LONG") int value) {
        RECT.ntop(this.address(), value);
        return this;
    }

    public RECT right(@NativeType(value="LONG") int value) {
        RECT.nright(this.address(), value);
        return this;
    }

    public RECT bottom(@NativeType(value="LONG") int value) {
        RECT.nbottom(this.address(), value);
        return this;
    }

    public RECT set(int left, int top, int right, int bottom) {
        this.left(left);
        this.top(top);
        this.right(right);
        this.bottom(bottom);
        return this;
    }

    public RECT set(RECT src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static RECT malloc() {
        return new RECT(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static RECT calloc() {
        return new RECT(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static RECT create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new RECT(MemoryUtil.memAddress(container), container);
    }

    public static RECT create(long address) {
        return new RECT(address, null);
    }

    @Nullable
    public static RECT createSafe(long address) {
        return address == 0L ? null : new RECT(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(RECT.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = RECT.__create(capacity, SIZEOF);
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
    public static RECT mallocStack() {
        return RECT.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static RECT callocStack() {
        return RECT.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static RECT mallocStack(MemoryStack stack) {
        return RECT.malloc(stack);
    }

    @Deprecated
    public static RECT callocStack(MemoryStack stack) {
        return RECT.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return RECT.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return RECT.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return RECT.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return RECT.calloc(capacity, stack);
    }

    public static RECT malloc(MemoryStack stack) {
        return new RECT(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static RECT calloc(MemoryStack stack) {
        return new RECT(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nleft(long struct) {
        return UNSAFE.getInt(null, struct + (long)LEFT);
    }

    public static int ntop(long struct) {
        return UNSAFE.getInt(null, struct + (long)TOP);
    }

    public static int nright(long struct) {
        return UNSAFE.getInt(null, struct + (long)RIGHT);
    }

    public static int nbottom(long struct) {
        return UNSAFE.getInt(null, struct + (long)BOTTOM);
    }

    public static void nleft(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)LEFT, value);
    }

    public static void ntop(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)TOP, value);
    }

    public static void nright(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RIGHT, value);
    }

    public static void nbottom(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)BOTTOM, value);
    }

    static {
        Struct.Layout layout = RECT.__struct(RECT.__member(4), RECT.__member(4), RECT.__member(4), RECT.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        LEFT = layout.offsetof(0);
        TOP = layout.offsetof(1);
        RIGHT = layout.offsetof(2);
        BOTTOM = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<RECT, Buffer>
    implements NativeResource {
        private static final RECT ELEMENT_FACTORY = RECT.create(-1L);

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
        protected RECT getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="LONG")
        public int left() {
            return RECT.nleft(this.address());
        }

        @NativeType(value="LONG")
        public int top() {
            return RECT.ntop(this.address());
        }

        @NativeType(value="LONG")
        public int right() {
            return RECT.nright(this.address());
        }

        @NativeType(value="LONG")
        public int bottom() {
            return RECT.nbottom(this.address());
        }

        public Buffer left(@NativeType(value="LONG") int value) {
            RECT.nleft(this.address(), value);
            return this;
        }

        public Buffer top(@NativeType(value="LONG") int value) {
            RECT.ntop(this.address(), value);
            return this;
        }

        public Buffer right(@NativeType(value="LONG") int value) {
            RECT.nright(this.address(), value);
            return this;
        }

        public Buffer bottom(@NativeType(value="LONG") int value) {
            RECT.nbottom(this.address(), value);
            return this;
        }
    }
}
