package org.lwjgl.system.linux;

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

@NativeType(value="union epoll_data_t")
public class EpollData
extends Struct<EpollData>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int PTR;
    public static final int FD;
    public static final int U32;
    public static final int U64;

    protected EpollData(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected EpollData create(long address, @Nullable ByteBuffer container) {
        return new EpollData(address, container);
    }

    public EpollData(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), EpollData.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="void *")
    public long ptr() {
        return EpollData.nptr(this.address());
    }

    public int fd() {
        return EpollData.nfd(this.address());
    }

    @NativeType(value="uint32_t")
    public int u32() {
        return EpollData.nu32(this.address());
    }

    @NativeType(value="uint64_t")
    public long u64() {
        return EpollData.nu64(this.address());
    }

    public EpollData ptr(@NativeType(value="void *") long value) {
        EpollData.nptr(this.address(), value);
        return this;
    }

    public EpollData fd(int value) {
        EpollData.nfd(this.address(), value);
        return this;
    }

    public EpollData u32(@NativeType(value="uint32_t") int value) {
        EpollData.nu32(this.address(), value);
        return this;
    }

    public EpollData u64(@NativeType(value="uint64_t") long value) {
        EpollData.nu64(this.address(), value);
        return this;
    }

    public EpollData set(EpollData src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static EpollData malloc() {
        return new EpollData(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static EpollData calloc() {
        return new EpollData(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static EpollData create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new EpollData(MemoryUtil.memAddress(container), container);
    }

    public static EpollData create(long address) {
        return new EpollData(address, null);
    }

    @Nullable
    public static EpollData createSafe(long address) {
        return address == 0L ? null : new EpollData(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(EpollData.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = EpollData.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static EpollData malloc(MemoryStack stack) {
        return new EpollData(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static EpollData calloc(MemoryStack stack) {
        return new EpollData(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nptr(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)PTR);
    }

    public static int nfd(long struct) {
        return UNSAFE.getInt(null, struct + (long)FD);
    }

    public static int nu32(long struct) {
        return UNSAFE.getInt(null, struct + (long)U32);
    }

    public static long nu64(long struct) {
        return UNSAFE.getLong(null, struct + (long)U64);
    }

    public static void nptr(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)PTR, Checks.check(value));
    }

    public static void nfd(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FD, value);
    }

    public static void nu32(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)U32, value);
    }

    public static void nu64(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)U64, value);
    }

    static {
        Struct.Layout layout = EpollData.__union(EpollData.__member(POINTER_SIZE), EpollData.__member(4), EpollData.__member(4), EpollData.__member(8));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        PTR = layout.offsetof(0);
        FD = layout.offsetof(1);
        U32 = layout.offsetof(2);
        U64 = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<EpollData, Buffer>
    implements NativeResource {
        private static final EpollData ELEMENT_FACTORY = EpollData.create(-1L);

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
        protected EpollData getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="void *")
        public long ptr() {
            return EpollData.nptr(this.address());
        }

        public int fd() {
            return EpollData.nfd(this.address());
        }

        @NativeType(value="uint32_t")
        public int u32() {
            return EpollData.nu32(this.address());
        }

        @NativeType(value="uint64_t")
        public long u64() {
            return EpollData.nu64(this.address());
        }

        public Buffer ptr(@NativeType(value="void *") long value) {
            EpollData.nptr(this.address(), value);
            return this;
        }

        public Buffer fd(int value) {
            EpollData.nfd(this.address(), value);
            return this;
        }

        public Buffer u32(@NativeType(value="uint32_t") int value) {
            EpollData.nu32(this.address(), value);
            return this;
        }

        public Buffer u64(@NativeType(value="uint64_t") long value) {
            EpollData.nu64(this.address(), value);
            return this;
        }
    }
}
