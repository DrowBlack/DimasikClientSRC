package org.lwjgl.system.linux.liburing;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
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
import org.lwjgl.system.linux.liburing.IOCQRingOffsets;
import org.lwjgl.system.linux.liburing.IOSQRingOffsets;

@NativeType(value="struct io_uring_params")
public class IOURingParams
extends Struct<IOURingParams>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int SQ_ENTRIES;
    public static final int CQ_ENTRIES;
    public static final int FLAGS;
    public static final int SQ_THREAD_CPU;
    public static final int SQ_THREAD_IDLE;
    public static final int FEATURES;
    public static final int WQ_FD;
    public static final int RESV;
    public static final int SQ_OFF;
    public static final int CQ_OFF;

    protected IOURingParams(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingParams create(long address, @Nullable ByteBuffer container) {
        return new IOURingParams(address, container);
    }

    public IOURingParams(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingParams.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u32")
    public int sq_entries() {
        return IOURingParams.nsq_entries(this.address());
    }

    @NativeType(value="__u32")
    public int cq_entries() {
        return IOURingParams.ncq_entries(this.address());
    }

    @NativeType(value="__u32")
    public int flags() {
        return IOURingParams.nflags(this.address());
    }

    @NativeType(value="__u32")
    public int sq_thread_cpu() {
        return IOURingParams.nsq_thread_cpu(this.address());
    }

    @NativeType(value="__u32")
    public int sq_thread_idle() {
        return IOURingParams.nsq_thread_idle(this.address());
    }

    @NativeType(value="__u32")
    public int features() {
        return IOURingParams.nfeatures(this.address());
    }

    @NativeType(value="__u32")
    public int wq_fd() {
        return IOURingParams.nwq_fd(this.address());
    }

    @NativeType(value="struct io_sqring_offsets")
    public IOSQRingOffsets sq_off() {
        return IOURingParams.nsq_off(this.address());
    }

    @NativeType(value="struct io_cqring_offsets")
    public IOCQRingOffsets cq_off() {
        return IOURingParams.ncq_off(this.address());
    }

    public IOURingParams sq_entries(@NativeType(value="__u32") int value) {
        IOURingParams.nsq_entries(this.address(), value);
        return this;
    }

    public IOURingParams cq_entries(@NativeType(value="__u32") int value) {
        IOURingParams.ncq_entries(this.address(), value);
        return this;
    }

    public IOURingParams flags(@NativeType(value="__u32") int value) {
        IOURingParams.nflags(this.address(), value);
        return this;
    }

    public IOURingParams sq_thread_cpu(@NativeType(value="__u32") int value) {
        IOURingParams.nsq_thread_cpu(this.address(), value);
        return this;
    }

    public IOURingParams sq_thread_idle(@NativeType(value="__u32") int value) {
        IOURingParams.nsq_thread_idle(this.address(), value);
        return this;
    }

    public IOURingParams features(@NativeType(value="__u32") int value) {
        IOURingParams.nfeatures(this.address(), value);
        return this;
    }

    public IOURingParams wq_fd(@NativeType(value="__u32") int value) {
        IOURingParams.nwq_fd(this.address(), value);
        return this;
    }

    public IOURingParams sq_off(@NativeType(value="struct io_sqring_offsets") IOSQRingOffsets value) {
        IOURingParams.nsq_off(this.address(), value);
        return this;
    }

    public IOURingParams sq_off(Consumer<IOSQRingOffsets> consumer) {
        consumer.accept(this.sq_off());
        return this;
    }

    public IOURingParams cq_off(@NativeType(value="struct io_cqring_offsets") IOCQRingOffsets value) {
        IOURingParams.ncq_off(this.address(), value);
        return this;
    }

    public IOURingParams cq_off(Consumer<IOCQRingOffsets> consumer) {
        consumer.accept(this.cq_off());
        return this;
    }

    public IOURingParams set(int sq_entries, int cq_entries, int flags, int sq_thread_cpu, int sq_thread_idle, int features, int wq_fd, IOSQRingOffsets sq_off, IOCQRingOffsets cq_off) {
        this.sq_entries(sq_entries);
        this.cq_entries(cq_entries);
        this.flags(flags);
        this.sq_thread_cpu(sq_thread_cpu);
        this.sq_thread_idle(sq_thread_idle);
        this.features(features);
        this.wq_fd(wq_fd);
        this.sq_off(sq_off);
        this.cq_off(cq_off);
        return this;
    }

    public IOURingParams set(IOURingParams src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingParams malloc() {
        return new IOURingParams(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingParams calloc() {
        return new IOURingParams(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingParams create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingParams(MemoryUtil.memAddress(container), container);
    }

    public static IOURingParams create(long address) {
        return new IOURingParams(address, null);
    }

    @Nullable
    public static IOURingParams createSafe(long address) {
        return address == 0L ? null : new IOURingParams(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingParams.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingParams.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingParams malloc(MemoryStack stack) {
        return new IOURingParams(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingParams calloc(MemoryStack stack) {
        return new IOURingParams(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nsq_entries(long struct) {
        return UNSAFE.getInt(null, struct + (long)SQ_ENTRIES);
    }

    public static int ncq_entries(long struct) {
        return UNSAFE.getInt(null, struct + (long)CQ_ENTRIES);
    }

    public static int nflags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FLAGS);
    }

    public static int nsq_thread_cpu(long struct) {
        return UNSAFE.getInt(null, struct + (long)SQ_THREAD_CPU);
    }

    public static int nsq_thread_idle(long struct) {
        return UNSAFE.getInt(null, struct + (long)SQ_THREAD_IDLE);
    }

    public static int nfeatures(long struct) {
        return UNSAFE.getInt(null, struct + (long)FEATURES);
    }

    public static int nwq_fd(long struct) {
        return UNSAFE.getInt(null, struct + (long)WQ_FD);
    }

    public static IntBuffer nresv(long struct) {
        return MemoryUtil.memIntBuffer(struct + (long)RESV, 3);
    }

    public static int nresv(long struct, int index) {
        return UNSAFE.getInt(null, struct + (long)RESV + Checks.check(index, 3) * 4L);
    }

    public static IOSQRingOffsets nsq_off(long struct) {
        return IOSQRingOffsets.create(struct + (long)SQ_OFF);
    }

    public static IOCQRingOffsets ncq_off(long struct) {
        return IOCQRingOffsets.create(struct + (long)CQ_OFF);
    }

    public static void nsq_entries(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)SQ_ENTRIES, value);
    }

    public static void ncq_entries(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)CQ_ENTRIES, value);
    }

    public static void nflags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FLAGS, value);
    }

    public static void nsq_thread_cpu(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)SQ_THREAD_CPU, value);
    }

    public static void nsq_thread_idle(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)SQ_THREAD_IDLE, value);
    }

    public static void nfeatures(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FEATURES, value);
    }

    public static void nwq_fd(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)WQ_FD, value);
    }

    public static void nresv(long struct, IntBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 3);
        }
        MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + (long)RESV, value.remaining() * 4);
    }

    public static void nresv(long struct, int index, int value) {
        UNSAFE.putInt(null, struct + (long)RESV + Checks.check(index, 3) * 4L, value);
    }

    public static void nsq_off(long struct, IOSQRingOffsets value) {
        MemoryUtil.memCopy(value.address(), struct + (long)SQ_OFF, IOSQRingOffsets.SIZEOF);
    }

    public static void ncq_off(long struct, IOCQRingOffsets value) {
        MemoryUtil.memCopy(value.address(), struct + (long)CQ_OFF, IOCQRingOffsets.SIZEOF);
    }

    static {
        Struct.Layout layout = IOURingParams.__struct(IOURingParams.__member(4), IOURingParams.__member(4), IOURingParams.__member(4), IOURingParams.__member(4), IOURingParams.__member(4), IOURingParams.__member(4), IOURingParams.__member(4), IOURingParams.__array(4, 3), IOURingParams.__member(IOSQRingOffsets.SIZEOF, IOSQRingOffsets.ALIGNOF), IOURingParams.__member(IOCQRingOffsets.SIZEOF, IOCQRingOffsets.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        SQ_ENTRIES = layout.offsetof(0);
        CQ_ENTRIES = layout.offsetof(1);
        FLAGS = layout.offsetof(2);
        SQ_THREAD_CPU = layout.offsetof(3);
        SQ_THREAD_IDLE = layout.offsetof(4);
        FEATURES = layout.offsetof(5);
        WQ_FD = layout.offsetof(6);
        RESV = layout.offsetof(7);
        SQ_OFF = layout.offsetof(8);
        CQ_OFF = layout.offsetof(9);
    }

    public static class Buffer
    extends StructBuffer<IOURingParams, Buffer>
    implements NativeResource {
        private static final IOURingParams ELEMENT_FACTORY = IOURingParams.create(-1L);

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
        protected IOURingParams getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u32")
        public int sq_entries() {
            return IOURingParams.nsq_entries(this.address());
        }

        @NativeType(value="__u32")
        public int cq_entries() {
            return IOURingParams.ncq_entries(this.address());
        }

        @NativeType(value="__u32")
        public int flags() {
            return IOURingParams.nflags(this.address());
        }

        @NativeType(value="__u32")
        public int sq_thread_cpu() {
            return IOURingParams.nsq_thread_cpu(this.address());
        }

        @NativeType(value="__u32")
        public int sq_thread_idle() {
            return IOURingParams.nsq_thread_idle(this.address());
        }

        @NativeType(value="__u32")
        public int features() {
            return IOURingParams.nfeatures(this.address());
        }

        @NativeType(value="__u32")
        public int wq_fd() {
            return IOURingParams.nwq_fd(this.address());
        }

        @NativeType(value="struct io_sqring_offsets")
        public IOSQRingOffsets sq_off() {
            return IOURingParams.nsq_off(this.address());
        }

        @NativeType(value="struct io_cqring_offsets")
        public IOCQRingOffsets cq_off() {
            return IOURingParams.ncq_off(this.address());
        }

        public Buffer sq_entries(@NativeType(value="__u32") int value) {
            IOURingParams.nsq_entries(this.address(), value);
            return this;
        }

        public Buffer cq_entries(@NativeType(value="__u32") int value) {
            IOURingParams.ncq_entries(this.address(), value);
            return this;
        }

        public Buffer flags(@NativeType(value="__u32") int value) {
            IOURingParams.nflags(this.address(), value);
            return this;
        }

        public Buffer sq_thread_cpu(@NativeType(value="__u32") int value) {
            IOURingParams.nsq_thread_cpu(this.address(), value);
            return this;
        }

        public Buffer sq_thread_idle(@NativeType(value="__u32") int value) {
            IOURingParams.nsq_thread_idle(this.address(), value);
            return this;
        }

        public Buffer features(@NativeType(value="__u32") int value) {
            IOURingParams.nfeatures(this.address(), value);
            return this;
        }

        public Buffer wq_fd(@NativeType(value="__u32") int value) {
            IOURingParams.nwq_fd(this.address(), value);
            return this;
        }

        public Buffer sq_off(@NativeType(value="struct io_sqring_offsets") IOSQRingOffsets value) {
            IOURingParams.nsq_off(this.address(), value);
            return this;
        }

        public Buffer sq_off(Consumer<IOSQRingOffsets> consumer) {
            consumer.accept(this.sq_off());
            return this;
        }

        public Buffer cq_off(@NativeType(value="struct io_cqring_offsets") IOCQRingOffsets value) {
            IOURingParams.ncq_off(this.address(), value);
            return this;
        }

        public Buffer cq_off(Consumer<IOCQRingOffsets> consumer) {
            consumer.accept(this.cq_off());
            return this;
        }
    }
}
