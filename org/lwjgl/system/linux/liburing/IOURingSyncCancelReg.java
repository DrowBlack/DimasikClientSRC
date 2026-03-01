package org.lwjgl.system.linux.liburing;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
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
import org.lwjgl.system.linux.KernelTimespec;

@NativeType(value="struct io_uring_sync_cancel_reg")
public class IOURingSyncCancelReg
extends Struct<IOURingSyncCancelReg>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int ADDR;
    public static final int FD;
    public static final int FLAGS;
    public static final int TIMEOUT;
    public static final int PAD;

    protected IOURingSyncCancelReg(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingSyncCancelReg create(long address, @Nullable ByteBuffer container) {
        return new IOURingSyncCancelReg(address, container);
    }

    public IOURingSyncCancelReg(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingSyncCancelReg.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u64")
    public long addr() {
        return IOURingSyncCancelReg.naddr(this.address());
    }

    @NativeType(value="__s32")
    public int fd() {
        return IOURingSyncCancelReg.nfd(this.address());
    }

    @NativeType(value="__u32")
    public int flags() {
        return IOURingSyncCancelReg.nflags(this.address());
    }

    @NativeType(value="struct __kernel_timespec")
    public KernelTimespec timeout() {
        return IOURingSyncCancelReg.ntimeout(this.address());
    }

    public IOURingSyncCancelReg addr(@NativeType(value="__u64") long value) {
        IOURingSyncCancelReg.naddr(this.address(), value);
        return this;
    }

    public IOURingSyncCancelReg fd(@NativeType(value="__s32") int value) {
        IOURingSyncCancelReg.nfd(this.address(), value);
        return this;
    }

    public IOURingSyncCancelReg flags(@NativeType(value="__u32") int value) {
        IOURingSyncCancelReg.nflags(this.address(), value);
        return this;
    }

    public IOURingSyncCancelReg timeout(@NativeType(value="struct __kernel_timespec") KernelTimespec value) {
        IOURingSyncCancelReg.ntimeout(this.address(), value);
        return this;
    }

    public IOURingSyncCancelReg timeout(Consumer<KernelTimespec> consumer) {
        consumer.accept(this.timeout());
        return this;
    }

    public IOURingSyncCancelReg set(long addr, int fd, int flags, KernelTimespec timeout) {
        this.addr(addr);
        this.fd(fd);
        this.flags(flags);
        this.timeout(timeout);
        return this;
    }

    public IOURingSyncCancelReg set(IOURingSyncCancelReg src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingSyncCancelReg malloc() {
        return new IOURingSyncCancelReg(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingSyncCancelReg calloc() {
        return new IOURingSyncCancelReg(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingSyncCancelReg create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingSyncCancelReg(MemoryUtil.memAddress(container), container);
    }

    public static IOURingSyncCancelReg create(long address) {
        return new IOURingSyncCancelReg(address, null);
    }

    @Nullable
    public static IOURingSyncCancelReg createSafe(long address) {
        return address == 0L ? null : new IOURingSyncCancelReg(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingSyncCancelReg.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingSyncCancelReg.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingSyncCancelReg malloc(MemoryStack stack) {
        return new IOURingSyncCancelReg(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingSyncCancelReg calloc(MemoryStack stack) {
        return new IOURingSyncCancelReg(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long naddr(long struct) {
        return UNSAFE.getLong(null, struct + (long)ADDR);
    }

    public static int nfd(long struct) {
        return UNSAFE.getInt(null, struct + (long)FD);
    }

    public static int nflags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FLAGS);
    }

    public static KernelTimespec ntimeout(long struct) {
        return KernelTimespec.create(struct + (long)TIMEOUT);
    }

    public static LongBuffer npad(long struct) {
        return MemoryUtil.memLongBuffer(struct + (long)PAD, 4);
    }

    public static long npad(long struct, int index) {
        return UNSAFE.getLong(null, struct + (long)PAD + Checks.check(index, 4) * 8L);
    }

    public static void naddr(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)ADDR, value);
    }

    public static void nfd(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FD, value);
    }

    public static void nflags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FLAGS, value);
    }

    public static void ntimeout(long struct, KernelTimespec value) {
        MemoryUtil.memCopy(value.address(), struct + (long)TIMEOUT, KernelTimespec.SIZEOF);
    }

    public static void npad(long struct, LongBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 4);
        }
        MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + (long)PAD, value.remaining() * 8);
    }

    public static void npad(long struct, int index, long value) {
        UNSAFE.putLong(null, struct + (long)PAD + Checks.check(index, 4) * 8L, value);
    }

    static {
        Struct.Layout layout = IOURingSyncCancelReg.__struct(IOURingSyncCancelReg.__member(8), IOURingSyncCancelReg.__member(4), IOURingSyncCancelReg.__member(4), IOURingSyncCancelReg.__member(KernelTimespec.SIZEOF, KernelTimespec.ALIGNOF), IOURingSyncCancelReg.__array(8, 4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        ADDR = layout.offsetof(0);
        FD = layout.offsetof(1);
        FLAGS = layout.offsetof(2);
        TIMEOUT = layout.offsetof(3);
        PAD = layout.offsetof(4);
    }

    public static class Buffer
    extends StructBuffer<IOURingSyncCancelReg, Buffer>
    implements NativeResource {
        private static final IOURingSyncCancelReg ELEMENT_FACTORY = IOURingSyncCancelReg.create(-1L);

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
        protected IOURingSyncCancelReg getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u64")
        public long addr() {
            return IOURingSyncCancelReg.naddr(this.address());
        }

        @NativeType(value="__s32")
        public int fd() {
            return IOURingSyncCancelReg.nfd(this.address());
        }

        @NativeType(value="__u32")
        public int flags() {
            return IOURingSyncCancelReg.nflags(this.address());
        }

        @NativeType(value="struct __kernel_timespec")
        public KernelTimespec timeout() {
            return IOURingSyncCancelReg.ntimeout(this.address());
        }

        public Buffer addr(@NativeType(value="__u64") long value) {
            IOURingSyncCancelReg.naddr(this.address(), value);
            return this;
        }

        public Buffer fd(@NativeType(value="__s32") int value) {
            IOURingSyncCancelReg.nfd(this.address(), value);
            return this;
        }

        public Buffer flags(@NativeType(value="__u32") int value) {
            IOURingSyncCancelReg.nflags(this.address(), value);
            return this;
        }

        public Buffer timeout(@NativeType(value="struct __kernel_timespec") KernelTimespec value) {
            IOURingSyncCancelReg.ntimeout(this.address(), value);
            return this;
        }

        public Buffer timeout(Consumer<KernelTimespec> consumer) {
            consumer.accept(this.timeout());
            return this;
        }
    }
}
