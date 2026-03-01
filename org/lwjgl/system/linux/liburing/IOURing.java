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
import org.lwjgl.system.linux.liburing.IOURingCQ;
import org.lwjgl.system.linux.liburing.IOURingSQ;

@NativeType(value="struct io_uring")
public class IOURing
extends Struct<IOURing>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int SQ;
    public static final int CQ;
    public static final int FLAGS;
    public static final int RING_FD;
    public static final int FEATURES;
    public static final int ENTER_RING_FD;
    public static final int INT_FLAGS;
    public static final int PAD;
    public static final int PAD2;

    protected IOURing(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURing create(long address, @Nullable ByteBuffer container) {
        return new IOURing(address, container);
    }

    public IOURing(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURing.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="struct io_uring_sq")
    public IOURingSQ sq() {
        return IOURing.nsq(this.address());
    }

    @NativeType(value="struct io_uring_cq")
    public IOURingCQ cq() {
        return IOURing.ncq(this.address());
    }

    @NativeType(value="unsigned int")
    public int flags() {
        return IOURing.nflags(this.address());
    }

    public int ring_fd() {
        return IOURing.nring_fd(this.address());
    }

    @NativeType(value="unsigned int")
    public int features() {
        return IOURing.nfeatures(this.address());
    }

    public int enter_ring_fd() {
        return IOURing.nenter_ring_fd(this.address());
    }

    @NativeType(value="__u8")
    public byte int_flags() {
        return IOURing.nint_flags(this.address());
    }

    public IOURing sq(@NativeType(value="struct io_uring_sq") IOURingSQ value) {
        IOURing.nsq(this.address(), value);
        return this;
    }

    public IOURing sq(Consumer<IOURingSQ> consumer) {
        consumer.accept(this.sq());
        return this;
    }

    public IOURing cq(@NativeType(value="struct io_uring_cq") IOURingCQ value) {
        IOURing.ncq(this.address(), value);
        return this;
    }

    public IOURing cq(Consumer<IOURingCQ> consumer) {
        consumer.accept(this.cq());
        return this;
    }

    public IOURing flags(@NativeType(value="unsigned int") int value) {
        IOURing.nflags(this.address(), value);
        return this;
    }

    public IOURing ring_fd(int value) {
        IOURing.nring_fd(this.address(), value);
        return this;
    }

    public IOURing features(@NativeType(value="unsigned int") int value) {
        IOURing.nfeatures(this.address(), value);
        return this;
    }

    public IOURing enter_ring_fd(int value) {
        IOURing.nenter_ring_fd(this.address(), value);
        return this;
    }

    public IOURing int_flags(@NativeType(value="__u8") byte value) {
        IOURing.nint_flags(this.address(), value);
        return this;
    }

    public IOURing set(IOURingSQ sq, IOURingCQ cq, int flags, int ring_fd, int features, int enter_ring_fd, byte int_flags) {
        this.sq(sq);
        this.cq(cq);
        this.flags(flags);
        this.ring_fd(ring_fd);
        this.features(features);
        this.enter_ring_fd(enter_ring_fd);
        this.int_flags(int_flags);
        return this;
    }

    public IOURing set(IOURing src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURing malloc() {
        return new IOURing(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURing calloc() {
        return new IOURing(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURing create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURing(MemoryUtil.memAddress(container), container);
    }

    public static IOURing create(long address) {
        return new IOURing(address, null);
    }

    @Nullable
    public static IOURing createSafe(long address) {
        return address == 0L ? null : new IOURing(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURing.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURing.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURing malloc(MemoryStack stack) {
        return new IOURing(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURing calloc(MemoryStack stack) {
        return new IOURing(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static IOURingSQ nsq(long struct) {
        return IOURingSQ.create(struct + (long)SQ);
    }

    public static IOURingCQ ncq(long struct) {
        return IOURingCQ.create(struct + (long)CQ);
    }

    public static int nflags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FLAGS);
    }

    public static int nring_fd(long struct) {
        return UNSAFE.getInt(null, struct + (long)RING_FD);
    }

    public static int nfeatures(long struct) {
        return UNSAFE.getInt(null, struct + (long)FEATURES);
    }

    public static int nenter_ring_fd(long struct) {
        return UNSAFE.getInt(null, struct + (long)ENTER_RING_FD);
    }

    public static byte nint_flags(long struct) {
        return UNSAFE.getByte(null, struct + (long)INT_FLAGS);
    }

    public static ByteBuffer npad(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)PAD, 3);
    }

    public static byte npad(long struct, int index) {
        return UNSAFE.getByte(null, struct + (long)PAD + Checks.check(index, 3) * 1L);
    }

    public static int npad2(long struct) {
        return UNSAFE.getInt(null, struct + (long)PAD2);
    }

    public static void nsq(long struct, IOURingSQ value) {
        MemoryUtil.memCopy(value.address(), struct + (long)SQ, IOURingSQ.SIZEOF);
    }

    public static void ncq(long struct, IOURingCQ value) {
        MemoryUtil.memCopy(value.address(), struct + (long)CQ, IOURingCQ.SIZEOF);
    }

    public static void nflags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FLAGS, value);
    }

    public static void nring_fd(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RING_FD, value);
    }

    public static void nfeatures(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FEATURES, value);
    }

    public static void nenter_ring_fd(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)ENTER_RING_FD, value);
    }

    public static void nint_flags(long struct, byte value) {
        UNSAFE.putByte(null, struct + (long)INT_FLAGS, value);
    }

    public static void npad(long struct, ByteBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 3);
        }
        MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + (long)PAD, value.remaining() * 1);
    }

    public static void npad(long struct, int index, byte value) {
        UNSAFE.putByte(null, struct + (long)PAD + Checks.check(index, 3) * 1L, value);
    }

    public static void npad2(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)PAD2, value);
    }

    public static void validate(long struct) {
        IOURingSQ.validate(struct + (long)SQ);
        IOURingCQ.validate(struct + (long)CQ);
    }

    static {
        Struct.Layout layout = IOURing.__struct(IOURing.__member(IOURingSQ.SIZEOF, IOURingSQ.ALIGNOF), IOURing.__member(IOURingCQ.SIZEOF, IOURingCQ.ALIGNOF), IOURing.__member(4), IOURing.__member(4), IOURing.__member(4), IOURing.__member(4), IOURing.__member(1), IOURing.__array(1, 3), IOURing.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        SQ = layout.offsetof(0);
        CQ = layout.offsetof(1);
        FLAGS = layout.offsetof(2);
        RING_FD = layout.offsetof(3);
        FEATURES = layout.offsetof(4);
        ENTER_RING_FD = layout.offsetof(5);
        INT_FLAGS = layout.offsetof(6);
        PAD = layout.offsetof(7);
        PAD2 = layout.offsetof(8);
    }

    public static class Buffer
    extends StructBuffer<IOURing, Buffer>
    implements NativeResource {
        private static final IOURing ELEMENT_FACTORY = IOURing.create(-1L);

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
        protected IOURing getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="struct io_uring_sq")
        public IOURingSQ sq() {
            return IOURing.nsq(this.address());
        }

        @NativeType(value="struct io_uring_cq")
        public IOURingCQ cq() {
            return IOURing.ncq(this.address());
        }

        @NativeType(value="unsigned int")
        public int flags() {
            return IOURing.nflags(this.address());
        }

        public int ring_fd() {
            return IOURing.nring_fd(this.address());
        }

        @NativeType(value="unsigned int")
        public int features() {
            return IOURing.nfeatures(this.address());
        }

        public int enter_ring_fd() {
            return IOURing.nenter_ring_fd(this.address());
        }

        @NativeType(value="__u8")
        public byte int_flags() {
            return IOURing.nint_flags(this.address());
        }

        public Buffer sq(@NativeType(value="struct io_uring_sq") IOURingSQ value) {
            IOURing.nsq(this.address(), value);
            return this;
        }

        public Buffer sq(Consumer<IOURingSQ> consumer) {
            consumer.accept(this.sq());
            return this;
        }

        public Buffer cq(@NativeType(value="struct io_uring_cq") IOURingCQ value) {
            IOURing.ncq(this.address(), value);
            return this;
        }

        public Buffer cq(Consumer<IOURingCQ> consumer) {
            consumer.accept(this.cq());
            return this;
        }

        public Buffer flags(@NativeType(value="unsigned int") int value) {
            IOURing.nflags(this.address(), value);
            return this;
        }

        public Buffer ring_fd(int value) {
            IOURing.nring_fd(this.address(), value);
            return this;
        }

        public Buffer features(@NativeType(value="unsigned int") int value) {
            IOURing.nfeatures(this.address(), value);
            return this;
        }

        public Buffer enter_ring_fd(int value) {
            IOURing.nenter_ring_fd(this.address(), value);
            return this;
        }

        public Buffer int_flags(@NativeType(value="__u8") byte value) {
            IOURing.nint_flags(this.address(), value);
            return this;
        }
    }
}
