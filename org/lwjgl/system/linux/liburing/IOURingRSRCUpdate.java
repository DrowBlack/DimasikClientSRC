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

@NativeType(value="struct io_uring_rsrc_update")
public class IOURingRSRCUpdate
extends Struct<IOURingRSRCUpdate>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int OFFSET;
    public static final int RESV;
    public static final int DATA;

    protected IOURingRSRCUpdate(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingRSRCUpdate create(long address, @Nullable ByteBuffer container) {
        return new IOURingRSRCUpdate(address, container);
    }

    public IOURingRSRCUpdate(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingRSRCUpdate.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u32")
    public int offset() {
        return IOURingRSRCUpdate.noffset(this.address());
    }

    @NativeType(value="__u64")
    public long data() {
        return IOURingRSRCUpdate.ndata(this.address());
    }

    public IOURingRSRCUpdate offset(@NativeType(value="__u32") int value) {
        IOURingRSRCUpdate.noffset(this.address(), value);
        return this;
    }

    public IOURingRSRCUpdate data(@NativeType(value="__u64") long value) {
        IOURingRSRCUpdate.ndata(this.address(), value);
        return this;
    }

    public IOURingRSRCUpdate set(int offset, long data) {
        this.offset(offset);
        this.data(data);
        return this;
    }

    public IOURingRSRCUpdate set(IOURingRSRCUpdate src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingRSRCUpdate malloc() {
        return new IOURingRSRCUpdate(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingRSRCUpdate calloc() {
        return new IOURingRSRCUpdate(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingRSRCUpdate create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingRSRCUpdate(MemoryUtil.memAddress(container), container);
    }

    public static IOURingRSRCUpdate create(long address) {
        return new IOURingRSRCUpdate(address, null);
    }

    @Nullable
    public static IOURingRSRCUpdate createSafe(long address) {
        return address == 0L ? null : new IOURingRSRCUpdate(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingRSRCUpdate.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingRSRCUpdate.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingRSRCUpdate malloc(MemoryStack stack) {
        return new IOURingRSRCUpdate(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingRSRCUpdate calloc(MemoryStack stack) {
        return new IOURingRSRCUpdate(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int noffset(long struct) {
        return UNSAFE.getInt(null, struct + (long)OFFSET);
    }

    public static int nresv(long struct) {
        return UNSAFE.getInt(null, struct + (long)RESV);
    }

    public static long ndata(long struct) {
        return UNSAFE.getLong(null, struct + (long)DATA);
    }

    public static void noffset(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OFFSET, value);
    }

    public static void nresv(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RESV, value);
    }

    public static void ndata(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)DATA, value);
    }

    static {
        Struct.Layout layout = IOURingRSRCUpdate.__struct(IOURingRSRCUpdate.__member(4), IOURingRSRCUpdate.__member(4), IOURingRSRCUpdate.__member(8));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        OFFSET = layout.offsetof(0);
        RESV = layout.offsetof(1);
        DATA = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<IOURingRSRCUpdate, Buffer>
    implements NativeResource {
        private static final IOURingRSRCUpdate ELEMENT_FACTORY = IOURingRSRCUpdate.create(-1L);

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
        protected IOURingRSRCUpdate getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u32")
        public int offset() {
            return IOURingRSRCUpdate.noffset(this.address());
        }

        @NativeType(value="__u64")
        public long data() {
            return IOURingRSRCUpdate.ndata(this.address());
        }

        public Buffer offset(@NativeType(value="__u32") int value) {
            IOURingRSRCUpdate.noffset(this.address(), value);
            return this;
        }

        public Buffer data(@NativeType(value="__u64") long value) {
            IOURingRSRCUpdate.ndata(this.address(), value);
            return this;
        }
    }
}
