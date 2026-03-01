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

@NativeType(value="struct io_uring_rsrc_register")
public class IOURingRSRCRegister
extends Struct<IOURingRSRCRegister>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int NR;
    public static final int FLAGS;
    public static final int RESV2;
    public static final int DATA;
    public static final int TAGS;

    protected IOURingRSRCRegister(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingRSRCRegister create(long address, @Nullable ByteBuffer container) {
        return new IOURingRSRCRegister(address, container);
    }

    public IOURingRSRCRegister(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingRSRCRegister.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u32")
    public int nr() {
        return IOURingRSRCRegister.nnr(this.address());
    }

    @NativeType(value="__u32")
    public int flags() {
        return IOURingRSRCRegister.nflags(this.address());
    }

    @NativeType(value="__u64")
    public long data() {
        return IOURingRSRCRegister.ndata(this.address());
    }

    @NativeType(value="__u64")
    public long tags() {
        return IOURingRSRCRegister.ntags(this.address());
    }

    public IOURingRSRCRegister nr(@NativeType(value="__u32") int value) {
        IOURingRSRCRegister.nnr(this.address(), value);
        return this;
    }

    public IOURingRSRCRegister flags(@NativeType(value="__u32") int value) {
        IOURingRSRCRegister.nflags(this.address(), value);
        return this;
    }

    public IOURingRSRCRegister data(@NativeType(value="__u64") long value) {
        IOURingRSRCRegister.ndata(this.address(), value);
        return this;
    }

    public IOURingRSRCRegister tags(@NativeType(value="__u64") long value) {
        IOURingRSRCRegister.ntags(this.address(), value);
        return this;
    }

    public IOURingRSRCRegister set(int nr, int flags, long data, long tags) {
        this.nr(nr);
        this.flags(flags);
        this.data(data);
        this.tags(tags);
        return this;
    }

    public IOURingRSRCRegister set(IOURingRSRCRegister src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingRSRCRegister malloc() {
        return new IOURingRSRCRegister(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingRSRCRegister calloc() {
        return new IOURingRSRCRegister(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingRSRCRegister create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingRSRCRegister(MemoryUtil.memAddress(container), container);
    }

    public static IOURingRSRCRegister create(long address) {
        return new IOURingRSRCRegister(address, null);
    }

    @Nullable
    public static IOURingRSRCRegister createSafe(long address) {
        return address == 0L ? null : new IOURingRSRCRegister(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingRSRCRegister.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingRSRCRegister.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingRSRCRegister malloc(MemoryStack stack) {
        return new IOURingRSRCRegister(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingRSRCRegister calloc(MemoryStack stack) {
        return new IOURingRSRCRegister(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nnr(long struct) {
        return UNSAFE.getInt(null, struct + (long)NR);
    }

    public static int nflags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FLAGS);
    }

    public static long nresv2(long struct) {
        return UNSAFE.getLong(null, struct + (long)RESV2);
    }

    public static long ndata(long struct) {
        return UNSAFE.getLong(null, struct + (long)DATA);
    }

    public static long ntags(long struct) {
        return UNSAFE.getLong(null, struct + (long)TAGS);
    }

    public static void nnr(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)NR, value);
    }

    public static void nflags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FLAGS, value);
    }

    public static void nresv2(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)RESV2, value);
    }

    public static void ndata(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)DATA, value);
    }

    public static void ntags(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)TAGS, value);
    }

    static {
        Struct.Layout layout = IOURingRSRCRegister.__struct(IOURingRSRCRegister.__member(4), IOURingRSRCRegister.__member(4), IOURingRSRCRegister.__member(8), IOURingRSRCRegister.__member(8), IOURingRSRCRegister.__member(8));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        NR = layout.offsetof(0);
        FLAGS = layout.offsetof(1);
        RESV2 = layout.offsetof(2);
        DATA = layout.offsetof(3);
        TAGS = layout.offsetof(4);
    }

    public static class Buffer
    extends StructBuffer<IOURingRSRCRegister, Buffer>
    implements NativeResource {
        private static final IOURingRSRCRegister ELEMENT_FACTORY = IOURingRSRCRegister.create(-1L);

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
        protected IOURingRSRCRegister getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u32")
        public int nr() {
            return IOURingRSRCRegister.nnr(this.address());
        }

        @NativeType(value="__u32")
        public int flags() {
            return IOURingRSRCRegister.nflags(this.address());
        }

        @NativeType(value="__u64")
        public long data() {
            return IOURingRSRCRegister.ndata(this.address());
        }

        @NativeType(value="__u64")
        public long tags() {
            return IOURingRSRCRegister.ntags(this.address());
        }

        public Buffer nr(@NativeType(value="__u32") int value) {
            IOURingRSRCRegister.nnr(this.address(), value);
            return this;
        }

        public Buffer flags(@NativeType(value="__u32") int value) {
            IOURingRSRCRegister.nflags(this.address(), value);
            return this;
        }

        public Buffer data(@NativeType(value="__u64") long value) {
            IOURingRSRCRegister.ndata(this.address(), value);
            return this;
        }

        public Buffer tags(@NativeType(value="__u64") long value) {
            IOURingRSRCRegister.ntags(this.address(), value);
            return this;
        }
    }
}
