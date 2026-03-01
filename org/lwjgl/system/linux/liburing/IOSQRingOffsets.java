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

@NativeType(value="struct io_sqring_offsets")
public class IOSQRingOffsets
extends Struct<IOSQRingOffsets>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int HEAD;
    public static final int TAIL;
    public static final int RING_MASK;
    public static final int RING_ENTRIES;
    public static final int FLAGS;
    public static final int DROPPED;
    public static final int ARRAY;
    public static final int RESV1;
    public static final int USER_ADDR;

    protected IOSQRingOffsets(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOSQRingOffsets create(long address, @Nullable ByteBuffer container) {
        return new IOSQRingOffsets(address, container);
    }

    public IOSQRingOffsets(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOSQRingOffsets.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u32")
    public int head() {
        return IOSQRingOffsets.nhead(this.address());
    }

    @NativeType(value="__u32")
    public int tail() {
        return IOSQRingOffsets.ntail(this.address());
    }

    @NativeType(value="__u32")
    public int ring_mask() {
        return IOSQRingOffsets.nring_mask(this.address());
    }

    @NativeType(value="__u32")
    public int ring_entries() {
        return IOSQRingOffsets.nring_entries(this.address());
    }

    @NativeType(value="__u32")
    public int flags() {
        return IOSQRingOffsets.nflags(this.address());
    }

    @NativeType(value="__u32")
    public int dropped() {
        return IOSQRingOffsets.ndropped(this.address());
    }

    @NativeType(value="__u32")
    public int array() {
        return IOSQRingOffsets.narray(this.address());
    }

    @NativeType(value="__u64")
    public long user_addr() {
        return IOSQRingOffsets.nuser_addr(this.address());
    }

    public IOSQRingOffsets head(@NativeType(value="__u32") int value) {
        IOSQRingOffsets.nhead(this.address(), value);
        return this;
    }

    public IOSQRingOffsets tail(@NativeType(value="__u32") int value) {
        IOSQRingOffsets.ntail(this.address(), value);
        return this;
    }

    public IOSQRingOffsets ring_mask(@NativeType(value="__u32") int value) {
        IOSQRingOffsets.nring_mask(this.address(), value);
        return this;
    }

    public IOSQRingOffsets ring_entries(@NativeType(value="__u32") int value) {
        IOSQRingOffsets.nring_entries(this.address(), value);
        return this;
    }

    public IOSQRingOffsets flags(@NativeType(value="__u32") int value) {
        IOSQRingOffsets.nflags(this.address(), value);
        return this;
    }

    public IOSQRingOffsets dropped(@NativeType(value="__u32") int value) {
        IOSQRingOffsets.ndropped(this.address(), value);
        return this;
    }

    public IOSQRingOffsets array(@NativeType(value="__u32") int value) {
        IOSQRingOffsets.narray(this.address(), value);
        return this;
    }

    public IOSQRingOffsets user_addr(@NativeType(value="__u64") long value) {
        IOSQRingOffsets.nuser_addr(this.address(), value);
        return this;
    }

    public IOSQRingOffsets set(int head, int tail, int ring_mask, int ring_entries, int flags, int dropped, int array, long user_addr) {
        this.head(head);
        this.tail(tail);
        this.ring_mask(ring_mask);
        this.ring_entries(ring_entries);
        this.flags(flags);
        this.dropped(dropped);
        this.array(array);
        this.user_addr(user_addr);
        return this;
    }

    public IOSQRingOffsets set(IOSQRingOffsets src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOSQRingOffsets malloc() {
        return new IOSQRingOffsets(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOSQRingOffsets calloc() {
        return new IOSQRingOffsets(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOSQRingOffsets create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOSQRingOffsets(MemoryUtil.memAddress(container), container);
    }

    public static IOSQRingOffsets create(long address) {
        return new IOSQRingOffsets(address, null);
    }

    @Nullable
    public static IOSQRingOffsets createSafe(long address) {
        return address == 0L ? null : new IOSQRingOffsets(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOSQRingOffsets.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOSQRingOffsets.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOSQRingOffsets malloc(MemoryStack stack) {
        return new IOSQRingOffsets(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOSQRingOffsets calloc(MemoryStack stack) {
        return new IOSQRingOffsets(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    public static int nflags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FLAGS);
    }

    public static int ndropped(long struct) {
        return UNSAFE.getInt(null, struct + (long)DROPPED);
    }

    public static int narray(long struct) {
        return UNSAFE.getInt(null, struct + (long)ARRAY);
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

    public static void nflags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FLAGS, value);
    }

    public static void ndropped(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)DROPPED, value);
    }

    public static void narray(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)ARRAY, value);
    }

    public static void nresv1(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RESV1, value);
    }

    public static void nuser_addr(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)USER_ADDR, value);
    }

    static {
        Struct.Layout layout = IOSQRingOffsets.__struct(IOSQRingOffsets.__member(4), IOSQRingOffsets.__member(4), IOSQRingOffsets.__member(4), IOSQRingOffsets.__member(4), IOSQRingOffsets.__member(4), IOSQRingOffsets.__member(4), IOSQRingOffsets.__member(4), IOSQRingOffsets.__member(4), IOSQRingOffsets.__member(8));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        HEAD = layout.offsetof(0);
        TAIL = layout.offsetof(1);
        RING_MASK = layout.offsetof(2);
        RING_ENTRIES = layout.offsetof(3);
        FLAGS = layout.offsetof(4);
        DROPPED = layout.offsetof(5);
        ARRAY = layout.offsetof(6);
        RESV1 = layout.offsetof(7);
        USER_ADDR = layout.offsetof(8);
    }

    public static class Buffer
    extends StructBuffer<IOSQRingOffsets, Buffer>
    implements NativeResource {
        private static final IOSQRingOffsets ELEMENT_FACTORY = IOSQRingOffsets.create(-1L);

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
        protected IOSQRingOffsets getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u32")
        public int head() {
            return IOSQRingOffsets.nhead(this.address());
        }

        @NativeType(value="__u32")
        public int tail() {
            return IOSQRingOffsets.ntail(this.address());
        }

        @NativeType(value="__u32")
        public int ring_mask() {
            return IOSQRingOffsets.nring_mask(this.address());
        }

        @NativeType(value="__u32")
        public int ring_entries() {
            return IOSQRingOffsets.nring_entries(this.address());
        }

        @NativeType(value="__u32")
        public int flags() {
            return IOSQRingOffsets.nflags(this.address());
        }

        @NativeType(value="__u32")
        public int dropped() {
            return IOSQRingOffsets.ndropped(this.address());
        }

        @NativeType(value="__u32")
        public int array() {
            return IOSQRingOffsets.narray(this.address());
        }

        @NativeType(value="__u64")
        public long user_addr() {
            return IOSQRingOffsets.nuser_addr(this.address());
        }

        public Buffer head(@NativeType(value="__u32") int value) {
            IOSQRingOffsets.nhead(this.address(), value);
            return this;
        }

        public Buffer tail(@NativeType(value="__u32") int value) {
            IOSQRingOffsets.ntail(this.address(), value);
            return this;
        }

        public Buffer ring_mask(@NativeType(value="__u32") int value) {
            IOSQRingOffsets.nring_mask(this.address(), value);
            return this;
        }

        public Buffer ring_entries(@NativeType(value="__u32") int value) {
            IOSQRingOffsets.nring_entries(this.address(), value);
            return this;
        }

        public Buffer flags(@NativeType(value="__u32") int value) {
            IOSQRingOffsets.nflags(this.address(), value);
            return this;
        }

        public Buffer dropped(@NativeType(value="__u32") int value) {
            IOSQRingOffsets.ndropped(this.address(), value);
            return this;
        }

        public Buffer array(@NativeType(value="__u32") int value) {
            IOSQRingOffsets.narray(this.address(), value);
            return this;
        }

        public Buffer user_addr(@NativeType(value="__u64") long value) {
            IOSQRingOffsets.nuser_addr(this.address(), value);
            return this;
        }
    }
}
