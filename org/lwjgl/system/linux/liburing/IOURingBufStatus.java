package org.lwjgl.system.linux.liburing;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct io_uring_buf_status")
public class IOURingBufStatus
extends Struct<IOURingBufStatus>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int BUF_GROUP;
    public static final int HEAD;
    public static final int RESV;

    protected IOURingBufStatus(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingBufStatus create(long address, @Nullable ByteBuffer container) {
        return new IOURingBufStatus(address, container);
    }

    public IOURingBufStatus(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingBufStatus.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u32")
    public int buf_group() {
        return IOURingBufStatus.nbuf_group(this.address());
    }

    @NativeType(value="__u32")
    public int head() {
        return IOURingBufStatus.nhead(this.address());
    }

    public IOURingBufStatus buf_group(@NativeType(value="__u32") int value) {
        IOURingBufStatus.nbuf_group(this.address(), value);
        return this;
    }

    public IOURingBufStatus head(@NativeType(value="__u32") int value) {
        IOURingBufStatus.nhead(this.address(), value);
        return this;
    }

    public IOURingBufStatus set(int buf_group, int head) {
        this.buf_group(buf_group);
        this.head(head);
        return this;
    }

    public IOURingBufStatus set(IOURingBufStatus src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingBufStatus malloc() {
        return new IOURingBufStatus(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingBufStatus calloc() {
        return new IOURingBufStatus(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingBufStatus create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingBufStatus(MemoryUtil.memAddress(container), container);
    }

    public static IOURingBufStatus create(long address) {
        return new IOURingBufStatus(address, null);
    }

    @Nullable
    public static IOURingBufStatus createSafe(long address) {
        return address == 0L ? null : new IOURingBufStatus(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingBufStatus.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingBufStatus.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingBufStatus malloc(MemoryStack stack) {
        return new IOURingBufStatus(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingBufStatus calloc(MemoryStack stack) {
        return new IOURingBufStatus(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nbuf_group(long struct) {
        return UNSAFE.getInt(null, struct + (long)BUF_GROUP);
    }

    public static int nhead(long struct) {
        return UNSAFE.getInt(null, struct + (long)HEAD);
    }

    public static IntBuffer nresv(long struct) {
        return MemoryUtil.memIntBuffer(struct + (long)RESV, 8);
    }

    public static int nresv(long struct, int index) {
        return UNSAFE.getInt(null, struct + (long)RESV + Checks.check(index, 8) * 4L);
    }

    public static void nbuf_group(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)BUF_GROUP, value);
    }

    public static void nhead(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)HEAD, value);
    }

    public static void nresv(long struct, IntBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 8);
        }
        MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + (long)RESV, value.remaining() * 4);
    }

    public static void nresv(long struct, int index, int value) {
        UNSAFE.putInt(null, struct + (long)RESV + Checks.check(index, 8) * 4L, value);
    }

    static {
        Struct.Layout layout = IOURingBufStatus.__struct(IOURingBufStatus.__member(4), IOURingBufStatus.__member(4), IOURingBufStatus.__array(4, 8));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        BUF_GROUP = layout.offsetof(0);
        HEAD = layout.offsetof(1);
        RESV = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<IOURingBufStatus, Buffer>
    implements NativeResource {
        private static final IOURingBufStatus ELEMENT_FACTORY = IOURingBufStatus.create(-1L);

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
        protected IOURingBufStatus getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u32")
        public int buf_group() {
            return IOURingBufStatus.nbuf_group(this.address());
        }

        @NativeType(value="__u32")
        public int head() {
            return IOURingBufStatus.nhead(this.address());
        }

        public Buffer buf_group(@NativeType(value="__u32") int value) {
            IOURingBufStatus.nbuf_group(this.address(), value);
            return this;
        }

        public Buffer head(@NativeType(value="__u32") int value) {
            IOURingBufStatus.nhead(this.address(), value);
            return this;
        }
    }
}
