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
import org.lwjgl.system.linux.liburing.IOURingCQE;

@NativeType(value="struct io_uring_cq")
public class IOURingCQ
extends Struct<IOURingCQ>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int KHEAD;
    public static final int KTAIL;
    public static final int KRING_MASK;
    public static final int KRING_ENTRIES;
    public static final int KFLAGS;
    public static final int KOVERFLOW;
    public static final int CQES;
    public static final int RING_SZ;
    public static final int RING_PTR;
    public static final int RING_MASK;
    public static final int RING_ENTRIES;
    public static final int PAD;

    protected IOURingCQ(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingCQ create(long address, @Nullable ByteBuffer container) {
        return new IOURingCQ(address, container);
    }

    public IOURingCQ(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingCQ.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="unsigned *")
    public IntBuffer khead(int capacity) {
        return IOURingCQ.nkhead(this.address(), capacity);
    }

    @NativeType(value="unsigned *")
    public IntBuffer ktail(int capacity) {
        return IOURingCQ.nktail(this.address(), capacity);
    }

    @NativeType(value="unsigned *")
    public IntBuffer kring_mask(int capacity) {
        return IOURingCQ.nkring_mask(this.address(), capacity);
    }

    @NativeType(value="unsigned *")
    public IntBuffer kring_entries(int capacity) {
        return IOURingCQ.nkring_entries(this.address(), capacity);
    }

    @NativeType(value="unsigned *")
    public IntBuffer kflags(int capacity) {
        return IOURingCQ.nkflags(this.address(), capacity);
    }

    @NativeType(value="unsigned *")
    public IntBuffer koverflow(int capacity) {
        return IOURingCQ.nkoverflow(this.address(), capacity);
    }

    @NativeType(value="struct io_uring_cqe *")
    public IOURingCQE cqes() {
        return IOURingCQ.ncqes(this.address());
    }

    @NativeType(value="size_t")
    public long ring_sz() {
        return IOURingCQ.nring_sz(this.address());
    }

    @NativeType(value="void *")
    public ByteBuffer ring_ptr() {
        return IOURingCQ.nring_ptr(this.address());
    }

    @NativeType(value="unsigned")
    public int ring_mask() {
        return IOURingCQ.nring_mask(this.address());
    }

    @NativeType(value="unsigned")
    public int ring_entries() {
        return IOURingCQ.nring_entries(this.address());
    }

    public IOURingCQ khead(@NativeType(value="unsigned *") IntBuffer value) {
        IOURingCQ.nkhead(this.address(), value);
        return this;
    }

    public IOURingCQ ktail(@NativeType(value="unsigned *") IntBuffer value) {
        IOURingCQ.nktail(this.address(), value);
        return this;
    }

    public IOURingCQ kring_mask(@NativeType(value="unsigned *") IntBuffer value) {
        IOURingCQ.nkring_mask(this.address(), value);
        return this;
    }

    public IOURingCQ kring_entries(@NativeType(value="unsigned *") IntBuffer value) {
        IOURingCQ.nkring_entries(this.address(), value);
        return this;
    }

    public IOURingCQ kflags(@NativeType(value="unsigned *") IntBuffer value) {
        IOURingCQ.nkflags(this.address(), value);
        return this;
    }

    public IOURingCQ koverflow(@NativeType(value="unsigned *") IntBuffer value) {
        IOURingCQ.nkoverflow(this.address(), value);
        return this;
    }

    public IOURingCQ cqes(@NativeType(value="struct io_uring_cqe *") IOURingCQE value) {
        IOURingCQ.ncqes(this.address(), value);
        return this;
    }

    public IOURingCQ ring_ptr(@NativeType(value="void *") ByteBuffer value) {
        IOURingCQ.nring_ptr(this.address(), value);
        return this;
    }

    public IOURingCQ ring_mask(@NativeType(value="unsigned") int value) {
        IOURingCQ.nring_mask(this.address(), value);
        return this;
    }

    public IOURingCQ ring_entries(@NativeType(value="unsigned") int value) {
        IOURingCQ.nring_entries(this.address(), value);
        return this;
    }

    public IOURingCQ set(IntBuffer khead, IntBuffer ktail, IntBuffer kring_mask, IntBuffer kring_entries, IntBuffer kflags, IntBuffer koverflow, IOURingCQE cqes, ByteBuffer ring_ptr, int ring_mask, int ring_entries) {
        this.khead(khead);
        this.ktail(ktail);
        this.kring_mask(kring_mask);
        this.kring_entries(kring_entries);
        this.kflags(kflags);
        this.koverflow(koverflow);
        this.cqes(cqes);
        this.ring_ptr(ring_ptr);
        this.ring_mask(ring_mask);
        this.ring_entries(ring_entries);
        return this;
    }

    public IOURingCQ set(IOURingCQ src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingCQ malloc() {
        return new IOURingCQ(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingCQ calloc() {
        return new IOURingCQ(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingCQ create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingCQ(MemoryUtil.memAddress(container), container);
    }

    public static IOURingCQ create(long address) {
        return new IOURingCQ(address, null);
    }

    @Nullable
    public static IOURingCQ createSafe(long address) {
        return address == 0L ? null : new IOURingCQ(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingCQ.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingCQ.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingCQ malloc(MemoryStack stack) {
        return new IOURingCQ(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingCQ calloc(MemoryStack stack) {
        return new IOURingCQ(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    public static IntBuffer nkoverflow(long struct, int capacity) {
        return MemoryUtil.memIntBuffer(MemoryUtil.memGetAddress(struct + (long)KOVERFLOW), capacity);
    }

    public static IOURingCQE ncqes(long struct) {
        return IOURingCQE.create(MemoryUtil.memGetAddress(struct + (long)CQES));
    }

    public static long nring_sz(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)RING_SZ);
    }

    public static ByteBuffer nring_ptr(long struct) {
        return MemoryUtil.memByteBuffer(MemoryUtil.memGetAddress(struct + (long)RING_PTR), (int)IOURingCQ.nring_sz(struct));
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

    public static void nkoverflow(long struct, IntBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)KOVERFLOW, MemoryUtil.memAddress(value));
    }

    public static void ncqes(long struct, IOURingCQE value) {
        MemoryUtil.memPutAddress(struct + (long)CQES, value.address());
    }

    public static void nring_sz(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)RING_SZ, value);
    }

    public static void nring_ptr(long struct, ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)RING_PTR, MemoryUtil.memAddress(value));
        IOURingCQ.nring_sz(struct, value.remaining());
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
        Checks.check(MemoryUtil.memGetAddress(struct + (long)KOVERFLOW));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)CQES));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)RING_PTR));
    }

    static {
        Struct.Layout layout = IOURingCQ.__struct(IOURingCQ.__member(POINTER_SIZE), IOURingCQ.__member(POINTER_SIZE), IOURingCQ.__member(POINTER_SIZE), IOURingCQ.__member(POINTER_SIZE), IOURingCQ.__member(POINTER_SIZE), IOURingCQ.__member(POINTER_SIZE), IOURingCQ.__member(POINTER_SIZE), IOURingCQ.__member(POINTER_SIZE), IOURingCQ.__member(POINTER_SIZE), IOURingCQ.__member(4), IOURingCQ.__member(4), IOURingCQ.__array(4, 2));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        KHEAD = layout.offsetof(0);
        KTAIL = layout.offsetof(1);
        KRING_MASK = layout.offsetof(2);
        KRING_ENTRIES = layout.offsetof(3);
        KFLAGS = layout.offsetof(4);
        KOVERFLOW = layout.offsetof(5);
        CQES = layout.offsetof(6);
        RING_SZ = layout.offsetof(7);
        RING_PTR = layout.offsetof(8);
        RING_MASK = layout.offsetof(9);
        RING_ENTRIES = layout.offsetof(10);
        PAD = layout.offsetof(11);
    }

    public static class Buffer
    extends StructBuffer<IOURingCQ, Buffer>
    implements NativeResource {
        private static final IOURingCQ ELEMENT_FACTORY = IOURingCQ.create(-1L);

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
        protected IOURingCQ getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="unsigned *")
        public IntBuffer khead(int capacity) {
            return IOURingCQ.nkhead(this.address(), capacity);
        }

        @NativeType(value="unsigned *")
        public IntBuffer ktail(int capacity) {
            return IOURingCQ.nktail(this.address(), capacity);
        }

        @NativeType(value="unsigned *")
        public IntBuffer kring_mask(int capacity) {
            return IOURingCQ.nkring_mask(this.address(), capacity);
        }

        @NativeType(value="unsigned *")
        public IntBuffer kring_entries(int capacity) {
            return IOURingCQ.nkring_entries(this.address(), capacity);
        }

        @NativeType(value="unsigned *")
        public IntBuffer kflags(int capacity) {
            return IOURingCQ.nkflags(this.address(), capacity);
        }

        @NativeType(value="unsigned *")
        public IntBuffer koverflow(int capacity) {
            return IOURingCQ.nkoverflow(this.address(), capacity);
        }

        @NativeType(value="struct io_uring_cqe *")
        public IOURingCQE cqes() {
            return IOURingCQ.ncqes(this.address());
        }

        @NativeType(value="size_t")
        public long ring_sz() {
            return IOURingCQ.nring_sz(this.address());
        }

        @NativeType(value="void *")
        public ByteBuffer ring_ptr() {
            return IOURingCQ.nring_ptr(this.address());
        }

        @NativeType(value="unsigned")
        public int ring_mask() {
            return IOURingCQ.nring_mask(this.address());
        }

        @NativeType(value="unsigned")
        public int ring_entries() {
            return IOURingCQ.nring_entries(this.address());
        }

        public Buffer khead(@NativeType(value="unsigned *") IntBuffer value) {
            IOURingCQ.nkhead(this.address(), value);
            return this;
        }

        public Buffer ktail(@NativeType(value="unsigned *") IntBuffer value) {
            IOURingCQ.nktail(this.address(), value);
            return this;
        }

        public Buffer kring_mask(@NativeType(value="unsigned *") IntBuffer value) {
            IOURingCQ.nkring_mask(this.address(), value);
            return this;
        }

        public Buffer kring_entries(@NativeType(value="unsigned *") IntBuffer value) {
            IOURingCQ.nkring_entries(this.address(), value);
            return this;
        }

        public Buffer kflags(@NativeType(value="unsigned *") IntBuffer value) {
            IOURingCQ.nkflags(this.address(), value);
            return this;
        }

        public Buffer koverflow(@NativeType(value="unsigned *") IntBuffer value) {
            IOURingCQ.nkoverflow(this.address(), value);
            return this;
        }

        public Buffer cqes(@NativeType(value="struct io_uring_cqe *") IOURingCQE value) {
            IOURingCQ.ncqes(this.address(), value);
            return this;
        }

        public Buffer ring_ptr(@NativeType(value="void *") ByteBuffer value) {
            IOURingCQ.nring_ptr(this.address(), value);
            return this;
        }

        public Buffer ring_mask(@NativeType(value="unsigned") int value) {
            IOURingCQ.nring_mask(this.address(), value);
            return this;
        }

        public Buffer ring_entries(@NativeType(value="unsigned") int value) {
            IOURingCQ.nring_entries(this.address(), value);
            return this;
        }
    }
}
