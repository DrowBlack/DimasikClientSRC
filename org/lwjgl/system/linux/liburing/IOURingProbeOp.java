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

@NativeType(value="struct io_uring_probe_op")
public class IOURingProbeOp
extends Struct<IOURingProbeOp>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int OP;
    public static final int RESV;
    public static final int FLAGS;
    public static final int RESV2;

    protected IOURingProbeOp(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingProbeOp create(long address, @Nullable ByteBuffer container) {
        return new IOURingProbeOp(address, container);
    }

    public IOURingProbeOp(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingProbeOp.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u8")
    public byte op() {
        return IOURingProbeOp.nop(this.address());
    }

    @NativeType(value="__u16")
    public short flags() {
        return IOURingProbeOp.nflags(this.address());
    }

    public IOURingProbeOp op(@NativeType(value="__u8") byte value) {
        IOURingProbeOp.nop(this.address(), value);
        return this;
    }

    public IOURingProbeOp flags(@NativeType(value="__u16") short value) {
        IOURingProbeOp.nflags(this.address(), value);
        return this;
    }

    public IOURingProbeOp set(byte op, short flags) {
        this.op(op);
        this.flags(flags);
        return this;
    }

    public IOURingProbeOp set(IOURingProbeOp src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingProbeOp malloc() {
        return new IOURingProbeOp(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingProbeOp calloc() {
        return new IOURingProbeOp(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingProbeOp create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingProbeOp(MemoryUtil.memAddress(container), container);
    }

    public static IOURingProbeOp create(long address) {
        return new IOURingProbeOp(address, null);
    }

    @Nullable
    public static IOURingProbeOp createSafe(long address) {
        return address == 0L ? null : new IOURingProbeOp(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingProbeOp.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingProbeOp.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingProbeOp malloc(MemoryStack stack) {
        return new IOURingProbeOp(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingProbeOp calloc(MemoryStack stack) {
        return new IOURingProbeOp(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static byte nop(long struct) {
        return UNSAFE.getByte(null, struct + (long)OP);
    }

    public static byte nresv(long struct) {
        return UNSAFE.getByte(null, struct + (long)RESV);
    }

    public static short nflags(long struct) {
        return UNSAFE.getShort(null, struct + (long)FLAGS);
    }

    public static int nresv2(long struct) {
        return UNSAFE.getInt(null, struct + (long)RESV2);
    }

    public static void nop(long struct, byte value) {
        UNSAFE.putByte(null, struct + (long)OP, value);
    }

    public static void nresv(long struct, byte value) {
        UNSAFE.putByte(null, struct + (long)RESV, value);
    }

    public static void nflags(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)FLAGS, value);
    }

    public static void nresv2(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RESV2, value);
    }

    static {
        Struct.Layout layout = IOURingProbeOp.__struct(IOURingProbeOp.__member(1), IOURingProbeOp.__member(1), IOURingProbeOp.__member(2), IOURingProbeOp.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        OP = layout.offsetof(0);
        RESV = layout.offsetof(1);
        FLAGS = layout.offsetof(2);
        RESV2 = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<IOURingProbeOp, Buffer>
    implements NativeResource {
        private static final IOURingProbeOp ELEMENT_FACTORY = IOURingProbeOp.create(-1L);

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
        protected IOURingProbeOp getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u8")
        public byte op() {
            return IOURingProbeOp.nop(this.address());
        }

        @NativeType(value="__u16")
        public short flags() {
            return IOURingProbeOp.nflags(this.address());
        }

        public Buffer op(@NativeType(value="__u8") byte value) {
            IOURingProbeOp.nop(this.address(), value);
            return this;
        }

        public Buffer flags(@NativeType(value="__u16") short value) {
            IOURingProbeOp.nflags(this.address(), value);
            return this;
        }
    }
}
