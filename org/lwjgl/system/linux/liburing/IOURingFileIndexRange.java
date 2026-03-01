package org.lwjgl.system.linux.liburing;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct io_uring_file_index_range")
public class IOURingFileIndexRange
extends Struct<IOURingFileIndexRange>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int OFF;
    public static final int LEN;
    public static final int RESV;

    protected IOURingFileIndexRange(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingFileIndexRange create(long address, @Nullable ByteBuffer container) {
        return new IOURingFileIndexRange(address, container);
    }

    public IOURingFileIndexRange(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingFileIndexRange.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u32")
    public int off() {
        return IOURingFileIndexRange.noff(this.address());
    }

    @NativeType(value="__u32")
    public int len() {
        return IOURingFileIndexRange.nlen(this.address());
    }

    public IOURingFileIndexRange off(@NativeType(value="__u32") int value) {
        IOURingFileIndexRange.noff(this.address(), value);
        return this;
    }

    public IOURingFileIndexRange len(@NativeType(value="__u32") int value) {
        IOURingFileIndexRange.nlen(this.address(), value);
        return this;
    }

    public IOURingFileIndexRange set(int off, int len) {
        this.off(off);
        this.len(len);
        return this;
    }

    public IOURingFileIndexRange set(IOURingFileIndexRange src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingFileIndexRange malloc() {
        return new IOURingFileIndexRange(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingFileIndexRange calloc() {
        return new IOURingFileIndexRange(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingFileIndexRange create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingFileIndexRange(MemoryUtil.memAddress(container), container);
    }

    public static IOURingFileIndexRange create(long address) {
        return new IOURingFileIndexRange(address, null);
    }

    @Nullable
    public static IOURingFileIndexRange createSafe(long address) {
        return address == 0L ? null : new IOURingFileIndexRange(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingFileIndexRange.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingFileIndexRange.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingFileIndexRange malloc(MemoryStack stack) {
        return new IOURingFileIndexRange(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingFileIndexRange calloc(MemoryStack stack) {
        return new IOURingFileIndexRange(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int noff(long struct) {
        return UNSAFE.getInt(null, struct + (long)OFF);
    }

    public static int nlen(long struct) {
        return UNSAFE.getInt(null, struct + (long)LEN);
    }

    public static long nresv(long struct) {
        return UNSAFE.getLong(null, struct + (long)RESV);
    }

    public static void noff(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OFF, value);
    }

    public static void nlen(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)LEN, value);
    }

    public static void nresv(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)RESV, value);
    }

    static {
        Struct.Layout layout = IOURingFileIndexRange.__struct(IOURingFileIndexRange.__member(4), IOURingFileIndexRange.__member(4), IOURingFileIndexRange.__member(8));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        OFF = layout.offsetof(0);
        LEN = layout.offsetof(1);
        RESV = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<IOURingFileIndexRange, Buffer>
    implements NativeResource {
        private static final IOURingFileIndexRange ELEMENT_FACTORY = IOURingFileIndexRange.create(-1L);

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
        protected IOURingFileIndexRange getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u32")
        public int off() {
            return IOURingFileIndexRange.noff(this.address());
        }

        @NativeType(value="__u32")
        public int len() {
            return IOURingFileIndexRange.nlen(this.address());
        }

        public Buffer off(@NativeType(value="__u32") int value) {
            IOURingFileIndexRange.noff(this.address(), value);
            return this;
        }

        public Buffer len(@NativeType(value="__u32") int value) {
            IOURingFileIndexRange.nlen(this.address(), value);
            return this;
        }
    }
}
