package org.lwjgl.system.linux;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct iovec")
public class IOVec
extends Struct<IOVec>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int IOV_BASE;
    public static final int IOV_LEN;

    protected IOVec(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOVec create(long address, @Nullable ByteBuffer container) {
        return new IOVec(address, container);
    }

    public IOVec(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOVec.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @Nullable
    @NativeType(value="void *")
    public ByteBuffer iov_base() {
        return IOVec.niov_base(this.address());
    }

    @NativeType(value="size_t")
    public long iov_len() {
        return IOVec.niov_len(this.address());
    }

    public IOVec iov_base(@Nullable @NativeType(value="void *") ByteBuffer value) {
        IOVec.niov_base(this.address(), value);
        return this;
    }

    public IOVec iov_len(@NativeType(value="size_t") long value) {
        IOVec.niov_len(this.address(), value);
        return this;
    }

    public IOVec set(@Nullable ByteBuffer iov_base, long iov_len) {
        this.iov_base(iov_base);
        this.iov_len(iov_len);
        return this;
    }

    public IOVec set(IOVec src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOVec malloc() {
        return new IOVec(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOVec calloc() {
        return new IOVec(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOVec create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOVec(MemoryUtil.memAddress(container), container);
    }

    public static IOVec create(long address) {
        return new IOVec(address, null);
    }

    @Nullable
    public static IOVec createSafe(long address) {
        return address == 0L ? null : new IOVec(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOVec.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOVec.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOVec malloc(MemoryStack stack) {
        return new IOVec(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOVec calloc(MemoryStack stack) {
        return new IOVec(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    @Nullable
    public static ByteBuffer niov_base(long struct) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)IOV_BASE), (int)IOVec.niov_len(struct));
    }

    public static long niov_len(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)IOV_LEN);
    }

    public static void niov_base(long struct, @Nullable ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)IOV_BASE, MemoryUtil.memAddressSafe(value));
    }

    public static void niov_len(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)IOV_LEN, value);
    }

    static {
        Struct.Layout layout = IOVec.__struct(IOVec.__member(POINTER_SIZE), IOVec.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        IOV_BASE = layout.offsetof(0);
        IOV_LEN = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<IOVec, Buffer>
    implements NativeResource {
        private static final IOVec ELEMENT_FACTORY = IOVec.create(-1L);

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
        protected IOVec getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @Nullable
        @NativeType(value="void *")
        public ByteBuffer iov_base() {
            return IOVec.niov_base(this.address());
        }

        @NativeType(value="size_t")
        public long iov_len() {
            return IOVec.niov_len(this.address());
        }

        public Buffer iov_base(@Nullable @NativeType(value="void *") ByteBuffer value) {
            IOVec.niov_base(this.address(), value);
            return this;
        }

        public Buffer iov_len(@NativeType(value="size_t") long value) {
            IOVec.niov_len(this.address(), value);
            return this;
        }
    }
}
