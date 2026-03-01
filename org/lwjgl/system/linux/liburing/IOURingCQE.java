package org.lwjgl.system.linux.liburing;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct io_uring_cqe")
public class IOURingCQE
extends Struct<IOURingCQE>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int USER_DATA;
    public static final int RES;
    public static final int FLAGS;
    public static final int BIG_CQE;

    protected IOURingCQE(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingCQE create(long address, @Nullable ByteBuffer container) {
        return new IOURingCQE(address, container);
    }

    public IOURingCQE(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingCQE.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u64")
    public long user_data() {
        return IOURingCQE.nuser_data(this.address());
    }

    @NativeType(value="__s32")
    public int res() {
        return IOURingCQE.nres(this.address());
    }

    @NativeType(value="__u32")
    public int flags() {
        return IOURingCQE.nflags(this.address());
    }

    @NativeType(value="__u64[0]")
    public LongBuffer big_cqe() {
        return IOURingCQE.nbig_cqe(this.address());
    }

    @NativeType(value="__u64")
    public long big_cqe(int index) {
        return IOURingCQE.nbig_cqe(this.address(), index);
    }

    public IOURingCQE user_data(@NativeType(value="__u64") long value) {
        IOURingCQE.nuser_data(this.address(), value);
        return this;
    }

    public IOURingCQE res(@NativeType(value="__s32") int value) {
        IOURingCQE.nres(this.address(), value);
        return this;
    }

    public IOURingCQE flags(@NativeType(value="__u32") int value) {
        IOURingCQE.nflags(this.address(), value);
        return this;
    }

    public IOURingCQE big_cqe(@NativeType(value="__u64[0]") LongBuffer value) {
        IOURingCQE.nbig_cqe(this.address(), value);
        return this;
    }

    public IOURingCQE big_cqe(int index, @NativeType(value="__u64") long value) {
        IOURingCQE.nbig_cqe(this.address(), index, value);
        return this;
    }

    public IOURingCQE set(long user_data, int res, int flags, LongBuffer big_cqe) {
        this.user_data(user_data);
        this.res(res);
        this.flags(flags);
        this.big_cqe(big_cqe);
        return this;
    }

    public IOURingCQE set(IOURingCQE src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingCQE malloc() {
        return new IOURingCQE(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingCQE calloc() {
        return new IOURingCQE(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingCQE create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingCQE(MemoryUtil.memAddress(container), container);
    }

    public static IOURingCQE create(long address) {
        return new IOURingCQE(address, null);
    }

    @Nullable
    public static IOURingCQE createSafe(long address) {
        return address == 0L ? null : new IOURingCQE(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingCQE.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingCQE.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingCQE malloc(MemoryStack stack) {
        return new IOURingCQE(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingCQE calloc(MemoryStack stack) {
        return new IOURingCQE(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nuser_data(long struct) {
        return UNSAFE.getLong(null, struct + (long)USER_DATA);
    }

    public static int nres(long struct) {
        return UNSAFE.getInt(null, struct + (long)RES);
    }

    public static int nflags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FLAGS);
    }

    public static LongBuffer nbig_cqe(long struct) {
        return MemoryUtil.memLongBuffer(struct + (long)BIG_CQE, 0);
    }

    public static long nbig_cqe(long struct, int index) {
        return UNSAFE.getLong(null, struct + (long)BIG_CQE + Checks.check(index, 0) * 8L);
    }

    public static void nuser_data(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)USER_DATA, value);
    }

    public static void nres(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RES, value);
    }

    public static void nflags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FLAGS, value);
    }

    public static void nbig_cqe(long struct, LongBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 0);
        }
        MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + (long)BIG_CQE, value.remaining() * 8);
    }

    public static void nbig_cqe(long struct, int index, long value) {
        UNSAFE.putLong(null, struct + (long)BIG_CQE + Checks.check(index, 0) * 8L, value);
    }

    static {
        Struct.Layout layout = IOURingCQE.__struct(IOURingCQE.__member(8), IOURingCQE.__member(4), IOURingCQE.__member(4), IOURingCQE.__array(8, 0));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        USER_DATA = layout.offsetof(0);
        RES = layout.offsetof(1);
        FLAGS = layout.offsetof(2);
        BIG_CQE = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<IOURingCQE, Buffer>
    implements NativeResource {
        private static final IOURingCQE ELEMENT_FACTORY = IOURingCQE.create(-1L);

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
        protected IOURingCQE getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u64")
        public long user_data() {
            return IOURingCQE.nuser_data(this.address());
        }

        @NativeType(value="__s32")
        public int res() {
            return IOURingCQE.nres(this.address());
        }

        @NativeType(value="__u32")
        public int flags() {
            return IOURingCQE.nflags(this.address());
        }

        @NativeType(value="__u64[0]")
        public LongBuffer big_cqe() {
            return IOURingCQE.nbig_cqe(this.address());
        }

        @NativeType(value="__u64")
        public long big_cqe(int index) {
            return IOURingCQE.nbig_cqe(this.address(), index);
        }

        public Buffer user_data(@NativeType(value="__u64") long value) {
            IOURingCQE.nuser_data(this.address(), value);
            return this;
        }

        public Buffer res(@NativeType(value="__s32") int value) {
            IOURingCQE.nres(this.address(), value);
            return this;
        }

        public Buffer flags(@NativeType(value="__u32") int value) {
            IOURingCQE.nflags(this.address(), value);
            return this;
        }

        public Buffer big_cqe(@NativeType(value="__u64[0]") LongBuffer value) {
            IOURingCQE.nbig_cqe(this.address(), value);
            return this;
        }

        public Buffer big_cqe(int index, @NativeType(value="__u64") long value) {
            IOURingCQE.nbig_cqe(this.address(), index, value);
            return this;
        }
    }
}
