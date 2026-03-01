package org.lwjgl.system.linux.liburing;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.system.linux.liburing.IOURingSQE;

@NativeType(value="struct io_uring_sq")
public class IOURingSQ
extends Struct<IOURingSQ>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int KHEAD;
    public static final int KTAIL;
    public static final int KRING_MASK;
    public static final int KRING_ENTRIES;
    public static final int KFLAGS;
    public static final int KDROPPED;
    public static final int ARRAY;
    public static final int SQES;
    public static final int SQE_HEAD;
    public static final int SQE_TAIL;
    public static final int RING_SZ;
    public static final int RING_PTR;
    public static final int RING_MASK;
    public static final int RING_ENTRIES;
    public static final int PAD;

    protected IOURingSQ(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingSQ create(long address, @Nullable ByteBuffer container) {
        return new IOURingSQ(address, container);
    }

    public IOURingSQ(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingSQ.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="unsigned *")
    public IntBuffer khead(int capacity) {
        return IOURingSQ.nkhead(this.address(), capacity);
    }

    @NativeType(value="unsigned *")
    public IntBuffer ktail(int capacity) {
        return IOURingSQ.nktail(this.address(), capacity);
    }

    @NativeType(value="unsigned *")
    public IntBuffer kring_mask(int capacity) {
        return IOURingSQ.nkring_mask(this.address(), capacity);
    }

    @NativeType(value="unsigned *")
    public IntBuffer kring_entries(int capacity) {
        return IOURingSQ.nkring_entries(this.address(), capacity);
    }

    @NativeType(value="unsigned *")
    public IntBuffer kflags(int capacity) {
        return IOURingSQ.nkflags(this.address(), capacity);
    }

    @NativeType(value="unsigned *")
    public IntBuffer kdropped(int capacity) {
        return IOURingSQ.nkdropped(this.address(), capacity);
    }

    @NativeType(value="unsigned *")
    public IntBuffer array(int capacity) {
        return IOURingSQ.narray(this.address(), capacity);
    }

    @NativeType(value="struct io_uring_sqe *")
    public IOURingSQE sqes() {
        return IOURingSQ.nsqes(this.address());
    }

    @NativeType(value="unsigned")
    public int sqe_head() {
        return IOURingSQ.nsqe_head(this.address());
    }

    @NativeType(value="unsigned")
    public int sqe_tail() {
        return IOURingSQ.nsqe_tail(this.address());
    }

    @NativeType(value="size_t")
    public long ring_sz() {
        return IOURingSQ.nring_sz(this.address());
    }

    @NativeType(value="void *")
    public ByteBuffer ring_ptr() {
        return IOURingSQ.nring_ptr(this.address());
    }

    @NativeType(value="unsigned")
    public int ring_mask() {
        return IOURingSQ.nring_mask(this.address());
    }

    @NativeType(value="unsigned")
    public int ring_entries() {
        return IOURingSQ.nring_entries(this.address());
    }

    public IOURingSQ khead(@NativeType(value="unsigned *") IntBuffer value) {
        IOURingSQ.nkhead(this.address(), value);
        return this;
    }

    public IOURingSQ ktail(@NativeType(value="unsigned *") IntBuffer value) {
        IOURingSQ.nktail(this.address(), value);
        return this;
    }

    public IOURingSQ kring_mask(@NativeType(value="unsigned *") IntBuffer value) {
        IOURingSQ.nkring_mask(this.address(), value);
        return this;
    }

    public IOURingSQ kring_entries(@NativeType(value="unsigned *") IntBuffer value) {
        IOURingSQ.nkring_entries(this.address(), value);
        return this;
    }

    public IOURingSQ kflags(@NativeType(value="unsigned *") IntBuffer value) {
        IOURingSQ.nkflags(this.address(), value);
        return this;
    }

    public IOURingSQ kdropped(@NativeType(value="unsigned *") IntBuffer value) {
        IOURingSQ.nkdropped(this.address(), value);
        return this;
    }

    public IOURingSQ array(@NativeType(value="unsigned *") IntBuffer value) {
        IOURingSQ.narray(this.address(), value);
        return this;
    }

    public IOURingSQ sqes(@NativeType(value="struct io_uring_sqe *") IOURingSQE value) {
        IOURingSQ.nsqes(this.address(), value);
        return this;
    }

    public IOURingSQ sqe_head(@NativeType(value="unsigned") int value) {
        IOURingSQ.nsqe_head(this.address(), value);
        return this;
    }

    public IOURingSQ sqe_tail(@NativeType(value="unsigned") int value) {
        IOURingSQ.nsqe_tail(this.address(), value);
        return this;
    }

    public IOURingSQ ring_ptr(@NativeType(value="void *") ByteBuffer value) {
        IOURingSQ.nring_ptr(this.address(), value);
        return this;
    }

    public IOURingSQ ring_mask(@NativeType(value="unsigned") int value) {
        IOURingSQ.nring_mask(this.address(), value);
        return this;
    }

    public IOURingSQ ring_entries(@NativeType(value="unsigned") int value) {
        IOURingSQ.nring_entries(this.address(), value);
        return this;
    }

    public IOURingSQ set(IntBuffer khead, IntBuffer ktail, IntBuffer kring_mask, IntBuffer kring_entries, IntBuffer kflags, IntBuffer kdropped, IntBuffer array, IOURingSQE sqes, int sqe_head, int sqe_tail, ByteBuffer ring_ptr, int ring_mask, int ring_entries) {
        this.khead(khead);
        this.ktail(ktail);
        this.kring_mask(kring_mask);
        this.kring_entries(kring_entries);
        this.kflags(kflags);
        this.kdropped(kdropped);
        this.array(array);
        this.sqes(sqes);
        this.sqe_head(sqe_head);
        this.sqe_tail(sqe_tail);
        this.ring_ptr(ring_ptr);
        this.ring_mask(ring_mask);
        this.ring_entries(ring_entries);
        return this;
    }

    public IOURingSQ set(IOURingSQ src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingSQ malloc() {
        return new IOURingSQ(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingSQ calloc() {
        return new IOURingSQ(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingSQ create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingSQ(MemoryUtil.memAddress(container), container);
    }

    public static IOURingSQ create(long address) {
        return new IOURingSQ(address, null);
    }

    @Nullable
    public static IOURingSQ createSafe(long address) {
        return address == 0L ? null : new IOURingSQ(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingSQ.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingSQ.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingSQ malloc(MemoryStack stack) {
        return new IOURingSQ(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingSQ calloc(MemoryStack stack) {
        return new IOURingSQ(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static IntBuffer nkhead(long struct, int capacity) {
        return MemoryUtil.memIntBuffer(MemoryUtil.memGetAddress(struct + (long)KHEAD), capacity);
    }

    public static IntBuffer nktail(long struct, int capacity) {
        return MemoryUtil.memIntBuffer(MemoryUtil.memGetAddress(struct + (long)KTAIL), capacity);
    }

    public static IntBuffer nkring_mask(long struct, int capacity) {
        return MemoryUtil.memIntBuffer(MemoryUtil.memGetAddress(struct + (long)KRING_MASK), capacity);
    }

    public static IntBuffer nkring_entries(long struct, int capacity) {
        return MemoryUtil.memIntBuffer(MemoryUtil.memGetAddress(struct + (long)KRING_ENTRIES), capacity);
    }

    public static IntBuffer nkflags(long struct, int capacity) {
        return MemoryUtil.memIntBuffer(MemoryUtil.memGetAddress(struct + (long)KFLAGS), capacity);
    }

    public static IntBuffer nkdropped(long struct, int capacity) {
        return MemoryUtil.memIntBuffer(MemoryUtil.memGetAddress(struct + (long)KDROPPED), capacity);
    }

    public static IntBuffer narray(long struct, int capacity) {
        return MemoryUtil.memIntBuffer(MemoryUtil.memGetAddress(struct + (long)ARRAY), capacity);
    }

    public static IOURingSQE nsqes(long struct) {
        return IOURingSQE.create(MemoryUtil.memGetAddress(struct + (long)SQES));
    }

    public static int nsqe_head(long struct) {
        return UNSAFE.getInt(null, struct + (long)SQE_HEAD);
    }

    public static int nsqe_tail(long struct) {
        return UNSAFE.getInt(null, struct + (long)SQE_TAIL);
    }

    public static long nring_sz(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)RING_SZ);
    }

    public static ByteBuffer nring_ptr(long struct) {
        return MemoryUtil.memByteBuffer(MemoryUtil.memGetAddress(struct + (long)RING_PTR), (int)IOURingSQ.nring_sz(struct));
    }

    public static int nring_mask(long struct) {
        return UNSAFE.getInt(null, struct + (long)RING_MASK);
    }

    public static int nring_entries(long struct) {
        return UNSAFE.getInt(null, struct + (long)RING_ENTRIES);
    }

    public static IntBuffer npad(long struct) {
        return MemoryUtil.memIntBuffer(struct + (long)PAD, 2);
    }

    public static int npad(long struct, int index) {
        return UNSAFE.getInt(null, struct + (long)PAD + Checks.check(index, 2) * 4L);
    }

    public static void nkhead(long struct, IntBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)KHEAD, MemoryUtil.memAddress(value));
    }

    public static void nktail(long struct, IntBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)KTAIL, MemoryUtil.memAddress(value));
    }

    public static void nkring_mask(long struct, IntBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)KRING_MASK, MemoryUtil.memAddress(value));
    }

    public static void nkring_entries(long struct, IntBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)KRING_ENTRIES, MemoryUtil.memAddress(value));
    }

    public static void nkflags(long struct, IntBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)KFLAGS, MemoryUtil.memAddress(value));
    }

    public static void nkdropped(long struct, IntBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)KDROPPED, MemoryUtil.memAddress(value));
    }

    public static void narray(long struct, IntBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)ARRAY, MemoryUtil.memAddress(value));
    }

    public static void nsqes(long struct, IOURingSQE value) {
        MemoryUtil.memPutAddress(struct + (long)SQES, value.address());
    }

    public static void nsqe_head(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)SQE_HEAD, value);
    }

    public static void nsqe_tail(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)SQE_TAIL, value);
    }

    public static void nring_sz(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)RING_SZ, value);
    }

    public static void nring_ptr(long struct, ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)RING_PTR, MemoryUtil.memAddress(value));
        IOURingSQ.nring_sz(struct, value.remaining());
    }

    public static void nring_mask(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RING_MASK, value);
    }

    public static void nring_entries(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RING_ENTRIES, value);
    }

    public static void npad(long struct, IntBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 2);
        }
        MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + (long)PAD, value.remaining() * 4);
    }

    public static void npad(long struct, int index, int value) {
        UNSAFE.putInt(null, struct + (long)PAD + Checks.check(index, 2) * 4L, value);
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)KHEAD));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)KTAIL));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)KRING_MASK));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)KRING_ENTRIES));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)KFLAGS));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)KDROPPED));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)ARRAY));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)SQES));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)RING_PTR));
    }

    static {
        Struct.Layout layout = IOURingSQ.__struct(IOURingSQ.__member(POINTER_SIZE), IOURingSQ.__member(POINTER_SIZE), IOURingSQ.__member(POINTER_SIZE), IOURingSQ.__member(POINTER_SIZE), IOURingSQ.__member(POINTER_SIZE), IOURingSQ.__member(POINTER_SIZE), IOURingSQ.__member(POINTER_SIZE), IOURingSQ.__member(POINTER_SIZE), IOURingSQ.__member(4), IOURingSQ.__member(4), IOURingSQ.__member(POINTER_SIZE), IOURingSQ.__member(POINTER_SIZE), IOURingSQ.__member(4), IOURingSQ.__member(4), IOURingSQ.__array(4, 2));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        KHEAD = layout.offsetof(0);
        KTAIL = layout.offsetof(1);
        KRING_MASK = layout.offsetof(2);
        KRING_ENTRIES = layout.offsetof(3);
        KFLAGS = layout.offsetof(4);
        KDROPPED = layout.offsetof(5);
        ARRAY = layout.offsetof(6);
        SQES = layout.offsetof(7);
        SQE_HEAD = layout.offsetof(8);
        SQE_TAIL = layout.offsetof(9);
        RING_SZ = layout.offsetof(10);
        RING_PTR = layout.offsetof(11);
        RING_MASK = layout.offsetof(12);
        RING_ENTRIES = layout.offsetof(13);
        PAD = layout.offsetof(14);
    }

    public static class Buffer
    extends StructBuffer<IOURingSQ, Buffer>
    implements NativeResource {
        private static final IOURingSQ ELEMENT_FACTORY = IOURingSQ.create(-1L);

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
        protected IOURingSQ getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="unsigned *")
        public IntBuffer khead(int capacity) {
            return IOURingSQ.nkhead(this.address(), capacity);
        }

        @NativeType(value="unsigned *")
        public IntBuffer ktail(int capacity) {
            return IOURingSQ.nktail(this.address(), capacity);
        }

        @NativeType(value="unsigned *")
        public IntBuffer kring_mask(int capacity) {
            return IOURingSQ.nkring_mask(this.address(), capacity);
        }

        @NativeType(value="unsigned *")
        public IntBuffer kring_entries(int capacity) {
            return IOURingSQ.nkring_entries(this.address(), capacity);
        }

        @NativeType(value="unsigned *")
        public IntBuffer kflags(int capacity) {
            return IOURingSQ.nkflags(this.address(), capacity);
        }

        @NativeType(value="unsigned *")
        public IntBuffer kdropped(int capacity) {
            return IOURingSQ.nkdropped(this.address(), capacity);
        }

        @NativeType(value="unsigned *")
        public IntBuffer array(int capacity) {
            return IOURingSQ.narray(this.address(), capacity);
        }

        @NativeType(value="struct io_uring_sqe *")
        public IOURingSQE sqes() {
            return IOURingSQ.nsqes(this.address());
        }

        @NativeType(value="unsigned")
        public int sqe_head() {
            return IOURingSQ.nsqe_head(this.address());
        }

        @NativeType(value="unsigned")
        public int sqe_tail() {
            return IOURingSQ.nsqe_tail(this.address());
        }

        @NativeType(value="size_t")
        public long ring_sz() {
            return IOURingSQ.nring_sz(this.address());
        }

        @NativeType(value="void *")
        public ByteBuffer ring_ptr() {
            return IOURingSQ.nring_ptr(this.address());
        }

        @NativeType(value="unsigned")
        public int ring_mask() {
            return IOURingSQ.nring_mask(this.address());
        }

        @NativeType(value="unsigned")
        public int ring_entries() {
            return IOURingSQ.nring_entries(this.address());
        }

        public Buffer khead(@NativeType(value="unsigned *") IntBuffer value) {
            IOURingSQ.nkhead(this.address(), value);
            return this;
        }

        public Buffer ktail(@NativeType(value="unsigned *") IntBuffer value) {
            IOURingSQ.nktail(this.address(), value);
            return this;
        }

        public Buffer kring_mask(@NativeType(value="unsigned *") IntBuffer value) {
            IOURingSQ.nkring_mask(this.address(), value);
            return this;
        }

        public Buffer kring_entries(@NativeType(value="unsigned *") IntBuffer value) {
            IOURingSQ.nkring_entries(this.address(), value);
            return this;
        }

        public Buffer kflags(@NativeType(value="unsigned *") IntBuffer value) {
            IOURingSQ.nkflags(this.address(), value);
            return this;
        }

        public Buffer kdropped(@NativeType(value="unsigned *") IntBuffer value) {
            IOURingSQ.nkdropped(this.address(), value);
            return this;
        }

        public Buffer array(@NativeType(value="unsigned *") IntBuffer value) {
            IOURingSQ.narray(this.address(), value);
            return this;
        }

        public Buffer sqes(@NativeType(value="struct io_uring_sqe *") IOURingSQE value) {
            IOURingSQ.nsqes(this.address(), value);
            return this;
        }

        public Buffer sqe_head(@NativeType(value="unsigned") int value) {
            IOURingSQ.nsqe_head(this.address(), value);
            return this;
        }

        public Buffer sqe_tail(@NativeType(value="unsigned") int value) {
            IOURingSQ.nsqe_tail(this.address(), value);
            return this;
        }

        public Buffer ring_ptr(@NativeType(value="void *") ByteBuffer value) {
            IOURingSQ.nring_ptr(this.address(), value);
            return this;
        }

        public Buffer ring_mask(@NativeType(value="unsigned") int value) {
            IOURingSQ.nring_mask(this.address(), value);
            return this;
        }

        public Buffer ring_entries(@NativeType(value="unsigned") int value) {
            IOURingSQ.nring_entries(this.address(), value);
            return this;
        }
    }
}
