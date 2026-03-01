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

@NativeType(value="struct io_cqring_offsets")
public class IOCQRingOffsets
extends Struct<IOCQRingOffsets>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int HEAD;
    public static final int TAIL;
    public static final int RING_MASK;
    public static final int RING_ENTRIES;
    public static final int OVERFLOW;
    public static final int CQES;
    public static final int FLAGS;
    public static final int RESV1;
    public static final int USER_ADDR;

    protected IOCQRingOffsets(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOCQRingOffsets create(long address, @Nullable ByteBuffer container) {
        return new IOCQRingOffsets(address, container);
    }

    public IOCQRingOffsets(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOCQRingOffsets.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u32")
    public int head() {
        return IOCQRingOffsets.nhead(this.address());
    }

    @NativeType(value="__u32")
    public int tail() {
        return IOCQRingOffsets.ntail(this.address());
    }

    @NativeType(value="__u32")
    public int ring_mask() {
        return IOCQRingOffsets.nring_mask(this.address());
    }

    @NativeType(value="__u32")
    public int ring_entries() {
        return IOCQRingOffsets.nring_entries(this.address());
    }

    @NativeType(value="__u32")
    public int overflow() {
        return IOCQRingOffsets.noverflow(this.address());
    }

    @NativeType(value="__u32")
    public int cqes() {
        return IOCQRingOffsets.ncqes(this.address());
    }

    @NativeType(value="__u32")
    public int flags() {
        return IOCQRingOffsets.nflags(this.address());
    }

    @NativeType(value="__u64")
    public long user_addr() {
        return IOCQRingOffsets.nuser_addr(this.address());
    }

    public IOCQRingOffsets head(@NativeType(value="__u32") int value) {
        IOCQRingOffsets.nhead(this.address(), value);
        return this;
    }

    public IOCQRingOffsets tail(@NativeType(value="__u32") int value) {
        IOCQRingOffsets.ntail(this.address(), value);
        return this;
    }

    public IOCQRingOffsets ring_mask(@NativeType(value="__u32") int value) {
        IOCQRingOffsets.nring_mask(this.address(), value);
        return this;
    }

    public IOCQRingOffsets ring_entries(@NativeType(value="__u32") int value) {
        IOCQRingOffsets.nring_entries(this.address(), value);
        return this;
    }

    public IOCQRingOffsets overflow(@NativeType(value="__u32") int value) {
        IOCQRingOffsets.noverflow(this.address(), value);
        return this;
    }

    public IOCQRingOffsets cqes(@NativeType(value="__u32") int value) {
        IOCQRingOffsets.ncqes(this.address(), value);
        return this;
    }

    public IOCQRingOffsets flags(@NativeType(value="__u32") int value) {
        IOCQRingOffsets.nflags(this.address(), value);
        return this;
    }

    public IOCQRingOffsets user_addr(@NativeType(value="__u64") long value) {
        IOCQRingOffsets.nuser_addr(this.address(), value);
        return this;
    }

    public IOCQRingOffsets set(int head, int tail, int ring_mask, int ring_entries, int overflow, int cqes, int flags, long user_addr) {
        this.head(head);
        this.tail(tail);
        this.ring_mask(ring_mask);
        this.ring_entries(ring_entries);
        this.overflow(overflow);
        this.cqes(cqes);
        this.flags(flags);
        this.user_addr(user_addr);
        return this;
    }

    public IOCQRingOffsets set(IOCQRingOffsets src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOCQRingOffsets malloc() {
        return new IOCQRingOffsets(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOCQRingOffsets calloc() {
        return new IOCQRingOffsets(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOCQRingOffsets create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOCQRingOffsets(MemoryUtil.memAddress(container), container);
    }

    public static IOCQRingOffsets create(long address) {
        return new IOCQRingOffsets(address, null);
    }

    @Nullable
    public static IOCQRingOffsets createSafe(long address) {
        return address == 0L ? null : new IOCQRingOffsets(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOCQRingOffsets.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOCQRingOffsets.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOCQRingOffsets malloc(MemoryStack stack) {
        return new IOCQRingOffsets(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOCQRingOffsets calloc(MemoryStack stack) {
        return new IOCQRingOffsets(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nhead(long struct) {
        return UNSAFE.getInt(null, struct + (long)HEAD);
    }

    public static int ntail(long struct) {
        return UNSAFE.getInt(null, struct + (long)TAIL);
    }

    public static int nring_mask(long struct) {
        return UNSAFE.getInt(null, struct + (long)RING_MASK);
    }

    public static int nring_entries(long struct) {
        return UNSAFE.getInt(null, struct + (long)RING_ENTRIES);
    }

    public static int noverflow(long struct) {
        return UNSAFE.getInt(null, struct + (long)OVERFLOW);
    }

    public static int ncqes(long struct) {
        return UNSAFE.getInt(null, struct + (long)CQES);
    }

    public static int nflags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FLAGS);
    }

    public static int nresv1(long struct) {
        return UNSAFE.getInt(null, struct + (long)RESV1);
    }

    public static long nuser_addr(long struct) {
        return UNSAFE.getLong(null, struct + (long)USER_ADDR);
    }

    public static void nhead(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)HEAD, value);
    }

    public static void ntail(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)TAIL, value);
    }

    public static void nring_mask(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RING_MASK, value);
    }

    public static void nring_entries(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RING_ENTRIES, value);
    }

    public static void noverflow(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OVERFLOW, value);
    }

    public static void ncqes(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)CQES, value);
    }

    public static void nflags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FLAGS, value);
    }

    public static void nresv1(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RESV1, value);
    }

    public static void nuser_addr(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)USER_ADDR, value);
    }

    static {
        Struct.Layout layout = IOCQRingOffsets.__struct(IOCQRingOffsets.__member(4), IOCQRingOffsets.__member(4), IOCQRingOffsets.__member(4), IOCQRingOffsets.__member(4), IOCQRingOffsets.__member(4), IOCQRingOffsets.__member(4), IOCQRingOffsets.__member(4), IOCQRingOffsets.__member(4), IOCQRingOffsets.__member(8));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        HEAD = layout.offsetof(0);
        TAIL = layout.offsetof(1);
        RING_MASK = layout.offsetof(2);
        RING_ENTRIES = layout.offsetof(3);
        OVERFLOW = layout.offsetof(4);
        CQES = layout.offsetof(5);
        FLAGS = layout.offsetof(6);
        RESV1 = layout.offsetof(7);
        USER_ADDR = layout.offsetof(8);
    }

    public static class Buffer
    extends StructBuffer<IOCQRingOffsets, Buffer>
    implements NativeResource {
        private static final IOCQRingOffsets ELEMENT_FACTORY = IOCQRingOffsets.create(-1L);

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
        protected IOCQRingOffsets getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u32")
        public int head() {
            return IOCQRingOffsets.nhead(this.address());
        }

        @NativeType(value="__u32")
        public int tail() {
            return IOCQRingOffsets.ntail(this.address());
        }

        @NativeType(value="__u32")
        public int ring_mask() {
            return IOCQRingOffsets.nring_mask(this.address());
        }

        @NativeType(value="__u32")
        public int ring_entries() {
            return IOCQRingOffsets.nring_entries(this.address());
        }

        @NativeType(value="__u32")
        public int overflow() {
            return IOCQRingOffsets.noverflow(this.address());
        }

        @NativeType(value="__u32")
        public int cqes() {
            return IOCQRingOffsets.ncqes(this.address());
        }

        @NativeType(value="__u32")
        public int flags() {
            return IOCQRingOffsets.nflags(this.address());
        }

        @NativeType(value="__u64")
        public long user_addr() {
            return IOCQRingOffsets.nuser_addr(this.address());
        }

        public Buffer head(@NativeType(value="__u32") int value) {
            IOCQRingOffsets.nhead(this.address(), value);
            return this;
        }

        public Buffer tail(@NativeType(value="__u32") int value) {
            IOCQRingOffsets.ntail(this.address(), value);
            return this;
        }

        public Buffer ring_mask(@NativeType(value="__u32") int value) {
            IOCQRingOffsets.nring_mask(this.address(), value);
            return this;
        }

        public Buffer ring_entries(@NativeType(value="__u32") int value) {
            IOCQRingOffsets.nring_entries(this.address(), value);
            return this;
        }

        public Buffer overflow(@NativeType(value="__u32") int value) {
            IOCQRingOffsets.noverflow(this.address(), value);
            return this;
        }

        public Buffer cqes(@NativeType(value="__u32") int value) {
            IOCQRingOffsets.ncqes(this.address(), value);
            return this;
        }

        public Buffer flags(@NativeType(value="__u32") int value) {
            IOCQRingOffsets.nflags(this.address(), value);
            return this;
        }

        public Buffer user_addr(@NativeType(value="__u64") long value) {
            IOCQRingOffsets.nuser_addr(this.address(), value);
            return this;
        }
    }
}
