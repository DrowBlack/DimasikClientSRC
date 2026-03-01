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
import org.lwjgl.system.linux.liburing.IOURingProbeOp;

@NativeType(value="struct io_uring_probe")
public class IOURingProbe
extends Struct<IOURingProbe>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int LAST_OP;
    public static final int OPS_LEN;
    public static final int RESV;
    public static final int RESV2;
    public static final int OPS;

    protected IOURingProbe(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingProbe create(long address, @Nullable ByteBuffer container) {
        return new IOURingProbe(address, container);
    }

    public IOURingProbe(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingProbe.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u8")
    public byte last_op() {
        return IOURingProbe.nlast_op(this.address());
    }

    @NativeType(value="__u8")
    public byte ops_len() {
        return IOURingProbe.nops_len(this.address());
    }

    @NativeType(value="struct io_uring_probe_op[0]")
    public IOURingProbeOp.Buffer ops() {
        return IOURingProbe.nops(this.address());
    }

    @NativeType(value="struct io_uring_probe_op")
    public IOURingProbeOp ops(int index) {
        return IOURingProbe.nops(this.address(), index);
    }

    public IOURingProbe last_op(@NativeType(value="__u8") byte value) {
        IOURingProbe.nlast_op(this.address(), value);
        return this;
    }

    public IOURingProbe ops_len(@NativeType(value="__u8") byte value) {
        IOURingProbe.nops_len(this.address(), value);
        return this;
    }

    public IOURingProbe ops(@NativeType(value="struct io_uring_probe_op[0]") IOURingProbeOp.Buffer value) {
        IOURingProbe.nops(this.address(), value);
        return this;
    }

    public IOURingProbe ops(int index, @NativeType(value="struct io_uring_probe_op") IOURingProbeOp value) {
        IOURingProbe.nops(this.address(), index, value);
        return this;
    }

    public IOURingProbe ops(Consumer<IOURingProbeOp.Buffer> consumer) {
        consumer.accept(this.ops());
        return this;
    }

    public IOURingProbe ops(int index, Consumer<IOURingProbeOp> consumer) {
        consumer.accept(this.ops(index));
        return this;
    }

    public IOURingProbe set(byte last_op, byte ops_len, IOURingProbeOp.Buffer ops) {
        this.last_op(last_op);
        this.ops_len(ops_len);
        this.ops(ops);
        return this;
    }

    public IOURingProbe set(IOURingProbe src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingProbe malloc() {
        return new IOURingProbe(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingProbe calloc() {
        return new IOURingProbe(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingProbe create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingProbe(MemoryUtil.memAddress(container), container);
    }

    public static IOURingProbe create(long address) {
        return new IOURingProbe(address, null);
    }

    @Nullable
    public static IOURingProbe createSafe(long address) {
        return address == 0L ? null : new IOURingProbe(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingProbe.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingProbe.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingProbe malloc(MemoryStack stack) {
        return new IOURingProbe(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingProbe calloc(MemoryStack stack) {
        return new IOURingProbe(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static byte nlast_op(long struct) {
        return UNSAFE.getByte(null, struct + (long)LAST_OP);
    }

    public static byte nops_len(long struct) {
        return UNSAFE.getByte(null, struct + (long)OPS_LEN);
    }

    public static short nresv(long struct) {
        return UNSAFE.getShort(null, struct + (long)RESV);
    }

    public static IntBuffer nresv2(long struct) {
        return MemoryUtil.memIntBuffer(struct + (long)RESV2, 3);
    }

    public static int nresv2(long struct, int index) {
        return UNSAFE.getInt(null, struct + (long)RESV2 + Checks.check(index, 3) * 4L);
    }

    public static IOURingProbeOp.Buffer nops(long struct) {
        return IOURingProbeOp.create(struct + (long)OPS, 0);
    }

    public static IOURingProbeOp nops(long struct, int index) {
        return IOURingProbeOp.create(struct + (long)OPS + Checks.check(index, 0) * (long)IOURingProbeOp.SIZEOF);
    }

    public static void nlast_op(long struct, byte value) {
        UNSAFE.putByte(null, struct + (long)LAST_OP, value);
    }

    public static void nops_len(long struct, byte value) {
        UNSAFE.putByte(null, struct + (long)OPS_LEN, value);
    }

    public static void nresv(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)RESV, value);
    }

    public static void nresv2(long struct, IntBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 3);
        }
        MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + (long)RESV2, value.remaining() * 4);
    }

    public static void nresv2(long struct, int index, int value) {
        UNSAFE.putInt(null, struct + (long)RESV2 + Checks.check(index, 3) * 4L, value);
    }

    public static void nops(long struct, IOURingProbeOp.Buffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 0);
        }
        MemoryUtil.memCopy(value.address(), struct + (long)OPS, value.remaining() * IOURingProbeOp.SIZEOF);
    }

    public static void nops(long struct, int index, IOURingProbeOp value) {
        MemoryUtil.memCopy(value.address(), struct + (long)OPS + Checks.check(index, 0) * (long)IOURingProbeOp.SIZEOF, IOURingProbeOp.SIZEOF);
    }

    static {
        Struct.Layout layout = IOURingProbe.__struct(IOURingProbe.__member(1), IOURingProbe.__member(1), IOURingProbe.__member(2), IOURingProbe.__array(4, 3), IOURingProbe.__array(IOURingProbeOp.SIZEOF, IOURingProbeOp.ALIGNOF, 0));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        LAST_OP = layout.offsetof(0);
        OPS_LEN = layout.offsetof(1);
        RESV = layout.offsetof(2);
        RESV2 = layout.offsetof(3);
        OPS = layout.offsetof(4);
    }

    public static class Buffer
    extends StructBuffer<IOURingProbe, Buffer>
    implements NativeResource {
        private static final IOURingProbe ELEMENT_FACTORY = IOURingProbe.create(-1L);

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
        protected IOURingProbe getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u8")
        public byte last_op() {
            return IOURingProbe.nlast_op(this.address());
        }

        @NativeType(value="__u8")
        public byte ops_len() {
            return IOURingProbe.nops_len(this.address());
        }

        @NativeType(value="struct io_uring_probe_op[0]")
        public IOURingProbeOp.Buffer ops() {
            return IOURingProbe.nops(this.address());
        }

        @NativeType(value="struct io_uring_probe_op")
        public IOURingProbeOp ops(int index) {
            return IOURingProbe.nops(this.address(), index);
        }

        public Buffer last_op(@NativeType(value="__u8") byte value) {
            IOURingProbe.nlast_op(this.address(), value);
            return this;
        }

        public Buffer ops_len(@NativeType(value="__u8") byte value) {
            IOURingProbe.nops_len(this.address(), value);
            return this;
        }

        public Buffer ops(@NativeType(value="struct io_uring_probe_op[0]") IOURingProbeOp.Buffer value) {
            IOURingProbe.nops(this.address(), value);
            return this;
        }

        public Buffer ops(int index, @NativeType(value="struct io_uring_probe_op") IOURingProbeOp value) {
            IOURingProbe.nops(this.address(), index, value);
            return this;
        }

        public Buffer ops(Consumer<IOURingProbeOp.Buffer> consumer) {
            consumer.accept(this.ops());
            return this;
        }

        public Buffer ops(int index, Consumer<IOURingProbeOp> consumer) {
            consumer.accept(this.ops(index));
            return this;
        }
    }
}
