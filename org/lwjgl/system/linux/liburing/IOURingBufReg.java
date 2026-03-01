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

@NativeType(value="struct io_uring_buf_reg")
public class IOURingBufReg
extends Struct<IOURingBufReg>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int RING_ADDR;
    public static final int RING_ENTRIES;
    public static final int BGID;
    public static final int FLAGS;
    public static final int RESV;

    protected IOURingBufReg(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingBufReg create(long address, @Nullable ByteBuffer container) {
        return new IOURingBufReg(address, container);
    }

    public IOURingBufReg(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingBufReg.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u64")
    public long ring_addr() {
        return IOURingBufReg.nring_addr(this.address());
    }

    @NativeType(value="__u32")
    public int ring_entries() {
        return IOURingBufReg.nring_entries(this.address());
    }

    @NativeType(value="__u16")
    public short bgid() {
        return IOURingBufReg.nbgid(this.address());
    }

    @NativeType(value="__u16")
    public short flags() {
        return IOURingBufReg.nflags(this.address());
    }

    public IOURingBufReg ring_addr(@NativeType(value="__u64") long value) {
        IOURingBufReg.nring_addr(this.address(), value);
        return this;
    }

    public IOURingBufReg ring_entries(@NativeType(value="__u32") int value) {
        IOURingBufReg.nring_entries(this.address(), value);
        return this;
    }

    public IOURingBufReg bgid(@NativeType(value="__u16") short value) {
        IOURingBufReg.nbgid(this.address(), value);
        return this;
    }

    public IOURingBufReg flags(@NativeType(value="__u16") short value) {
        IOURingBufReg.nflags(this.address(), value);
        return this;
    }

    public IOURingBufReg set(long ring_addr, int ring_entries, short bgid, short flags) {
        this.ring_addr(ring_addr);
        this.ring_entries(ring_entries);
        this.bgid(bgid);
        this.flags(flags);
        return this;
    }

    public IOURingBufReg set(IOURingBufReg src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingBufReg malloc() {
        return new IOURingBufReg(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingBufReg calloc() {
        return new IOURingBufReg(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingBufReg create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingBufReg(MemoryUtil.memAddress(container), container);
    }

    public static IOURingBufReg create(long address) {
        return new IOURingBufReg(address, null);
    }

    @Nullable
    public static IOURingBufReg createSafe(long address) {
        return address == 0L ? null : new IOURingBufReg(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingBufReg.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingBufReg.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingBufReg malloc(MemoryStack stack) {
        return new IOURingBufReg(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingBufReg calloc(MemoryStack stack) {
        return new IOURingBufReg(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nring_addr(long struct) {
        return UNSAFE.getLong(null, struct + (long)RING_ADDR);
    }

    public static int nring_entries(long struct) {
        return UNSAFE.getInt(null, struct + (long)RING_ENTRIES);
    }

    public static short nbgid(long struct) {
        return UNSAFE.getShort(null, struct + (long)BGID);
    }

    public static short nflags(long struct) {
        return UNSAFE.getShort(null, struct + (long)FLAGS);
    }

    public static LongBuffer nresv(long struct) {
        return MemoryUtil.memLongBuffer(struct + (long)RESV, 3);
    }

    public static long nresv(long struct, int index) {
        return UNSAFE.getLong(null, struct + (long)RESV + Checks.check(index, 3) * 8L);
    }

    public static void nring_addr(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)RING_ADDR, value);
    }

    public static void nring_entries(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RING_ENTRIES, value);
    }

    public static void nbgid(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)BGID, value);
    }

    public static void nflags(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)FLAGS, value);
    }

    public static void nresv(long struct, LongBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 3);
        }
        MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + (long)RESV, value.remaining() * 8);
    }

    public static void nresv(long struct, int index, long value) {
        UNSAFE.putLong(null, struct + (long)RESV + Checks.check(index, 3) * 8L, value);
    }

    static {
        Struct.Layout layout = IOURingBufReg.__struct(IOURingBufReg.__member(8), IOURingBufReg.__member(4), IOURingBufReg.__member(2), IOURingBufReg.__member(2), IOURingBufReg.__array(8, 3));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        RING_ADDR = layout.offsetof(0);
        RING_ENTRIES = layout.offsetof(1);
        BGID = layout.offsetof(2);
        FLAGS = layout.offsetof(3);
        RESV = layout.offsetof(4);
    }

    public static class Buffer
    extends StructBuffer<IOURingBufReg, Buffer>
    implements NativeResource {
        private static final IOURingBufReg ELEMENT_FACTORY = IOURingBufReg.create(-1L);

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
        protected IOURingBufReg getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u64")
        public long ring_addr() {
            return IOURingBufReg.nring_addr(this.address());
        }

        @NativeType(value="__u32")
        public int ring_entries() {
            return IOURingBufReg.nring_entries(this.address());
        }

        @NativeType(value="__u16")
        public short bgid() {
            return IOURingBufReg.nbgid(this.address());
        }

        @NativeType(value="__u16")
        public short flags() {
            return IOURingBufReg.nflags(this.address());
        }

        public Buffer ring_addr(@NativeType(value="__u64") long value) {
            IOURingBufReg.nring_addr(this.address(), value);
            return this;
        }

        public Buffer ring_entries(@NativeType(value="__u32") int value) {
            IOURingBufReg.nring_entries(this.address(), value);
            return this;
        }

        public Buffer bgid(@NativeType(value="__u16") short value) {
            IOURingBufReg.nbgid(this.address(), value);
            return this;
        }

        public Buffer flags(@NativeType(value="__u16") short value) {
            IOURingBufReg.nflags(this.address(), value);
            return this;
        }
    }
}
