package org.lwjgl.system.linux.liburing;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct io_uring_napi")
public class IOURingNAPI
extends Struct<IOURingNAPI>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int BUSY_POLL_TO;
    public static final int PREFER_BUSY_POLL;
    public static final int PAD;
    public static final int RESV;

    protected IOURingNAPI(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingNAPI create(long address, @Nullable ByteBuffer container) {
        return new IOURingNAPI(address, container);
    }

    public IOURingNAPI(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingNAPI.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u32")
    public int busy_poll_to() {
        return IOURingNAPI.nbusy_poll_to(this.address());
    }

    @NativeType(value="__u8")
    public byte prefer_busy_poll() {
        return IOURingNAPI.nprefer_busy_poll(this.address());
    }

    public IOURingNAPI busy_poll_to(@NativeType(value="__u32") int value) {
        IOURingNAPI.nbusy_poll_to(this.address(), value);
        return this;
    }

    public IOURingNAPI prefer_busy_poll(@NativeType(value="__u8") byte value) {
        IOURingNAPI.nprefer_busy_poll(this.address(), value);
        return this;
    }

    public IOURingNAPI set(int busy_poll_to, byte prefer_busy_poll) {
        this.busy_poll_to(busy_poll_to);
        this.prefer_busy_poll(prefer_busy_poll);
        return this;
    }

    public IOURingNAPI set(IOURingNAPI src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingNAPI malloc() {
        return new IOURingNAPI(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingNAPI calloc() {
        return new IOURingNAPI(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingNAPI create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingNAPI(MemoryUtil.memAddress(container), container);
    }

    public static IOURingNAPI create(long address) {
        return new IOURingNAPI(address, null);
    }

    @Nullable
    public static IOURingNAPI createSafe(long address) {
        return address == 0L ? null : new IOURingNAPI(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingNAPI.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingNAPI.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingNAPI malloc(MemoryStack stack) {
        return new IOURingNAPI(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingNAPI calloc(MemoryStack stack) {
        return new IOURingNAPI(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nbusy_poll_to(long struct) {
        return UNSAFE.getInt(null, struct + (long)BUSY_POLL_TO);
    }

    public static byte nprefer_busy_poll(long struct) {
        return UNSAFE.getByte(null, struct + (long)PREFER_BUSY_POLL);
    }

    public static ByteBuffer npad(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)PAD, 3);
    }

    public static byte npad(long struct, int index) {
        return UNSAFE.getByte(null, struct + (long)PAD + Checks.check(index, 3) * 1L);
    }

    public static long nresv(long struct) {
        return UNSAFE.getLong(null, struct + (long)RESV);
    }

    public static void nbusy_poll_to(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)BUSY_POLL_TO, value);
    }

    public static void nprefer_busy_poll(long struct, byte value) {
        UNSAFE.putByte(null, struct + (long)PREFER_BUSY_POLL, value);
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

    public static void nresv(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)RESV, value);
    }

    static {
        Struct.Layout layout = IOURingNAPI.__struct(IOURingNAPI.__member(4), IOURingNAPI.__member(1), IOURingNAPI.__array(1, 3), IOURingNAPI.__member(8));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        BUSY_POLL_TO = layout.offsetof(0);
        PREFER_BUSY_POLL = layout.offsetof(1);
        PAD = layout.offsetof(2);
        RESV = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<IOURingNAPI, Buffer>
    implements NativeResource {
        private static final IOURingNAPI ELEMENT_FACTORY = IOURingNAPI.create(-1L);

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
        protected IOURingNAPI getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u32")
        public int busy_poll_to() {
            return IOURingNAPI.nbusy_poll_to(this.address());
        }

        @NativeType(value="__u8")
        public byte prefer_busy_poll() {
            return IOURingNAPI.nprefer_busy_poll(this.address());
        }

        public Buffer busy_poll_to(@NativeType(value="__u32") int value) {
            IOURingNAPI.nbusy_poll_to(this.address(), value);
            return this;
        }

        public Buffer prefer_busy_poll(@NativeType(value="__u8") byte value) {
            IOURingNAPI.nprefer_busy_poll(this.address(), value);
            return this;
        }
    }
}
