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

@NativeType(value="struct io_uring_recvmsg_out")
public class IOURingRecvmsgOut
extends Struct<IOURingRecvmsgOut>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int NAMELEN;
    public static final int CONTROLLEN;
    public static final int PAYLOADLEN;
    public static final int FLAGS;

    protected IOURingRecvmsgOut(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingRecvmsgOut create(long address, @Nullable ByteBuffer container) {
        return new IOURingRecvmsgOut(address, container);
    }

    public IOURingRecvmsgOut(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingRecvmsgOut.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u32")
    public int namelen() {
        return IOURingRecvmsgOut.nnamelen(this.address());
    }

    @NativeType(value="__u32")
    public int controllen() {
        return IOURingRecvmsgOut.ncontrollen(this.address());
    }

    @NativeType(value="__u32")
    public int payloadlen() {
        return IOURingRecvmsgOut.npayloadlen(this.address());
    }

    @NativeType(value="__u32")
    public int flags() {
        return IOURingRecvmsgOut.nflags(this.address());
    }

    public IOURingRecvmsgOut namelen(@NativeType(value="__u32") int value) {
        IOURingRecvmsgOut.nnamelen(this.address(), value);
        return this;
    }

    public IOURingRecvmsgOut controllen(@NativeType(value="__u32") int value) {
        IOURingRecvmsgOut.ncontrollen(this.address(), value);
        return this;
    }

    public IOURingRecvmsgOut payloadlen(@NativeType(value="__u32") int value) {
        IOURingRecvmsgOut.npayloadlen(this.address(), value);
        return this;
    }

    public IOURingRecvmsgOut flags(@NativeType(value="__u32") int value) {
        IOURingRecvmsgOut.nflags(this.address(), value);
        return this;
    }

    public IOURingRecvmsgOut set(int namelen, int controllen, int payloadlen, int flags) {
        this.namelen(namelen);
        this.controllen(controllen);
        this.payloadlen(payloadlen);
        this.flags(flags);
        return this;
    }

    public IOURingRecvmsgOut set(IOURingRecvmsgOut src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingRecvmsgOut malloc() {
        return new IOURingRecvmsgOut(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingRecvmsgOut calloc() {
        return new IOURingRecvmsgOut(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingRecvmsgOut create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingRecvmsgOut(MemoryUtil.memAddress(container), container);
    }

    public static IOURingRecvmsgOut create(long address) {
        return new IOURingRecvmsgOut(address, null);
    }

    @Nullable
    public static IOURingRecvmsgOut createSafe(long address) {
        return address == 0L ? null : new IOURingRecvmsgOut(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingRecvmsgOut.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingRecvmsgOut.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingRecvmsgOut malloc(MemoryStack stack) {
        return new IOURingRecvmsgOut(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingRecvmsgOut calloc(MemoryStack stack) {
        return new IOURingRecvmsgOut(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nnamelen(long struct) {
        return UNSAFE.getInt(null, struct + (long)NAMELEN);
    }

    public static int ncontrollen(long struct) {
        return UNSAFE.getInt(null, struct + (long)CONTROLLEN);
    }

    public static int npayloadlen(long struct) {
        return UNSAFE.getInt(null, struct + (long)PAYLOADLEN);
    }

    public static int nflags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FLAGS);
    }

    public static void nnamelen(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)NAMELEN, value);
    }

    public static void ncontrollen(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)CONTROLLEN, value);
    }

    public static void npayloadlen(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)PAYLOADLEN, value);
    }

    public static void nflags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FLAGS, value);
    }

    static {
        Struct.Layout layout = IOURingRecvmsgOut.__struct(IOURingRecvmsgOut.__member(4), IOURingRecvmsgOut.__member(4), IOURingRecvmsgOut.__member(4), IOURingRecvmsgOut.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        NAMELEN = layout.offsetof(0);
        CONTROLLEN = layout.offsetof(1);
        PAYLOADLEN = layout.offsetof(2);
        FLAGS = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<IOURingRecvmsgOut, Buffer>
    implements NativeResource {
        private static final IOURingRecvmsgOut ELEMENT_FACTORY = IOURingRecvmsgOut.create(-1L);

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
        protected IOURingRecvmsgOut getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u32")
        public int namelen() {
            return IOURingRecvmsgOut.nnamelen(this.address());
        }

        @NativeType(value="__u32")
        public int controllen() {
            return IOURingRecvmsgOut.ncontrollen(this.address());
        }

        @NativeType(value="__u32")
        public int payloadlen() {
            return IOURingRecvmsgOut.npayloadlen(this.address());
        }

        @NativeType(value="__u32")
        public int flags() {
            return IOURingRecvmsgOut.nflags(this.address());
        }

        public Buffer namelen(@NativeType(value="__u32") int value) {
            IOURingRecvmsgOut.nnamelen(this.address(), value);
            return this;
        }

        public Buffer controllen(@NativeType(value="__u32") int value) {
            IOURingRecvmsgOut.ncontrollen(this.address(), value);
            return this;
        }

        public Buffer payloadlen(@NativeType(value="__u32") int value) {
            IOURingRecvmsgOut.npayloadlen(this.address(), value);
            return this;
        }

        public Buffer flags(@NativeType(value="__u32") int value) {
            IOURingRecvmsgOut.nflags(this.address(), value);
            return this;
        }
    }
}
