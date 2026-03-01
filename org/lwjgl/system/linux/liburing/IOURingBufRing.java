package org.lwjgl.system.linux.liburing;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.system.linux.liburing.IOURingBuf;

@NativeType(value="struct io_uring_buf_ring")
public class IOURingBufRing
extends Struct<IOURingBufRing>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int RESV1;
    public static final int RESV2;
    public static final int RESV3;
    public static final int TAIL;
    public static final int BUFS;

    protected IOURingBufRing(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingBufRing create(long address, @Nullable ByteBuffer container) {
        return new IOURingBufRing(address, container);
    }

    public IOURingBufRing(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingBufRing.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u64")
    public long resv1() {
        return IOURingBufRing.nresv1(this.address());
    }

    @NativeType(value="__u32")
    public int resv2() {
        return IOURingBufRing.nresv2(this.address());
    }

    @NativeType(value="__u16")
    public short resv3() {
        return IOURingBufRing.nresv3(this.address());
    }

    @NativeType(value="__u16")
    public short tail() {
        return IOURingBufRing.ntail(this.address());
    }

    @NativeType(value="struct io_uring_buf[0]")
    public IOURingBuf.Buffer bufs() {
        return IOURingBufRing.nbufs(this.address());
    }

    @NativeType(value="struct io_uring_buf")
    public IOURingBuf bufs(int index) {
        return IOURingBufRing.nbufs(this.address(), index);
    }

    public IOURingBufRing resv1(@NativeType(value="__u64") long value) {
        IOURingBufRing.nresv1(this.address(), value);
        return this;
    }

    public IOURingBufRing resv2(@NativeType(value="__u32") int value) {
        IOURingBufRing.nresv2(this.address(), value);
        return this;
    }

    public IOURingBufRing resv3(@NativeType(value="__u16") short value) {
        IOURingBufRing.nresv3(this.address(), value);
        return this;
    }

    public IOURingBufRing tail(@NativeType(value="__u16") short value) {
        IOURingBufRing.ntail(this.address(), value);
        return this;
    }

    public IOURingBufRing bufs(@NativeType(value="struct io_uring_buf[0]") IOURingBuf.Buffer value) {
        IOURingBufRing.nbufs(this.address(), value);
        return this;
    }

    public IOURingBufRing bufs(int index, @NativeType(value="struct io_uring_buf") IOURingBuf value) {
        IOURingBufRing.nbufs(this.address(), index, value);
        return this;
    }

    public IOURingBufRing bufs(Consumer<IOURingBuf.Buffer> consumer) {
        consumer.accept(this.bufs());
        return this;
    }

    public IOURingBufRing bufs(int index, Consumer<IOURingBuf> consumer) {
        consumer.accept(this.bufs(index));
        return this;
    }

    public IOURingBufRing set(IOURingBufRing src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingBufRing malloc() {
        return new IOURingBufRing(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingBufRing calloc() {
        return new IOURingBufRing(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingBufRing create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingBufRing(MemoryUtil.memAddress(container), container);
    }

    public static IOURingBufRing create(long address) {
        return new IOURingBufRing(address, null);
    }

    @Nullable
    public static IOURingBufRing createSafe(long address) {
        return address == 0L ? null : new IOURingBufRing(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingBufRing.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingBufRing.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingBufRing malloc(MemoryStack stack) {
        return new IOURingBufRing(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingBufRing calloc(MemoryStack stack) {
        return new IOURingBufRing(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nresv1(long struct) {
        return UNSAFE.getLong(null, struct + (long)RESV1);
    }

    public static int nresv2(long struct) {
        return UNSAFE.getInt(null, struct + (long)RESV2);
    }

    public static short nresv3(long struct) {
        return UNSAFE.getShort(null, struct + (long)RESV3);
    }

    public static short ntail(long struct) {
        return UNSAFE.getShort(null, struct + (long)TAIL);
    }

    public static IOURingBuf.Buffer nbufs(long struct) {
        return IOURingBuf.create(struct + (long)BUFS, 0);
    }

    public static IOURingBuf nbufs(long struct, int index) {
        return IOURingBuf.create(struct + (long)BUFS + Checks.check(index, 0) * (long)IOURingBuf.SIZEOF);
    }

    public static void nresv1(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)RESV1, value);
    }

    public static void nresv2(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RESV2, value);
    }

    public static void nresv3(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)RESV3, value);
    }

    public static void ntail(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)TAIL, value);
    }

    public static void nbufs(long struct, IOURingBuf.Buffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 0);
        }
        MemoryUtil.memCopy(value.address(), struct + (long)BUFS, value.remaining() * IOURingBuf.SIZEOF);
    }

    public static void nbufs(long struct, int index, IOURingBuf value) {
        MemoryUtil.memCopy(value.address(), struct + (long)BUFS + Checks.check(index, 0) * (long)IOURingBuf.SIZEOF, IOURingBuf.SIZEOF);
    }

    static {
        Struct.Layout layout = IOURingBufRing.__struct(IOURingBufRing.__union(IOURingBufRing.__struct(IOURingBufRing.__member(8), IOURingBufRing.__member(4), IOURingBufRing.__member(2), IOURingBufRing.__member(2)), IOURingBufRing.__array(IOURingBuf.SIZEOF, IOURingBuf.ALIGNOF, 0)));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        RESV1 = layout.offsetof(2);
        RESV2 = layout.offsetof(3);
        RESV3 = layout.offsetof(4);
        TAIL = layout.offsetof(5);
        BUFS = layout.offsetof(6);
    }

    public static class Buffer
    extends StructBuffer<IOURingBufRing, Buffer>
    implements NativeResource {
        private static final IOURingBufRing ELEMENT_FACTORY = IOURingBufRing.create(-1L);

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
        protected IOURingBufRing getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u64")
        public long resv1() {
            return IOURingBufRing.nresv1(this.address());
        }

        @NativeType(value="__u32")
        public int resv2() {
            return IOURingBufRing.nresv2(this.address());
        }

        @NativeType(value="__u16")
        public short resv3() {
            return IOURingBufRing.nresv3(this.address());
        }

        @NativeType(value="__u16")
        public short tail() {
            return IOURingBufRing.ntail(this.address());
        }

        @NativeType(value="struct io_uring_buf[0]")
        public IOURingBuf.Buffer bufs() {
            return IOURingBufRing.nbufs(this.address());
        }

        @NativeType(value="struct io_uring_buf")
        public IOURingBuf bufs(int index) {
            return IOURingBufRing.nbufs(this.address(), index);
        }

        public Buffer resv1(@NativeType(value="__u64") long value) {
            IOURingBufRing.nresv1(this.address(), value);
            return this;
        }

        public Buffer resv2(@NativeType(value="__u32") int value) {
            IOURingBufRing.nresv2(this.address(), value);
            return this;
        }

        public Buffer resv3(@NativeType(value="__u16") short value) {
            IOURingBufRing.nresv3(this.address(), value);
            return this;
        }

        public Buffer tail(@NativeType(value="__u16") short value) {
            IOURingBufRing.ntail(this.address(), value);
            return this;
        }

        public Buffer bufs(@NativeType(value="struct io_uring_buf[0]") IOURingBuf.Buffer value) {
            IOURingBufRing.nbufs(this.address(), value);
            return this;
        }

        public Buffer bufs(int index, @NativeType(value="struct io_uring_buf") IOURingBuf value) {
            IOURingBufRing.nbufs(this.address(), index, value);
            return this;
        }

        public Buffer bufs(Consumer<IOURingBuf.Buffer> consumer) {
            consumer.accept(this.bufs());
            return this;
        }

        public Buffer bufs(int index, Consumer<IOURingBuf> consumer) {
            consumer.accept(this.bufs(index));
            return this;
        }
    }
}
