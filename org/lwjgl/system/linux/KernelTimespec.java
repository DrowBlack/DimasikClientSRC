package org.lwjgl.system.linux;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct __kernel_timespec")
public class KernelTimespec
extends Struct<KernelTimespec>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int TV_SEC;
    public static final int TV_NSEC;

    protected KernelTimespec(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected KernelTimespec create(long address, @Nullable ByteBuffer container) {
        return new KernelTimespec(address, container);
    }

    public KernelTimespec(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), KernelTimespec.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="int64_t")
    public long tv_sec() {
        return KernelTimespec.ntv_sec(this.address());
    }

    @NativeType(value="long long")
    public long tv_nsec() {
        return KernelTimespec.ntv_nsec(this.address());
    }

    public KernelTimespec tv_sec(@NativeType(value="int64_t") long value) {
        KernelTimespec.ntv_sec(this.address(), value);
        return this;
    }

    public KernelTimespec tv_nsec(@NativeType(value="long long") long value) {
        KernelTimespec.ntv_nsec(this.address(), value);
        return this;
    }

    public KernelTimespec set(long tv_sec, long tv_nsec) {
        this.tv_sec(tv_sec);
        this.tv_nsec(tv_nsec);
        return this;
    }

    public KernelTimespec set(KernelTimespec src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static KernelTimespec malloc() {
        return new KernelTimespec(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static KernelTimespec calloc() {
        return new KernelTimespec(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static KernelTimespec create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new KernelTimespec(MemoryUtil.memAddress(container), container);
    }

    public static KernelTimespec create(long address) {
        return new KernelTimespec(address, null);
    }

    @Nullable
    public static KernelTimespec createSafe(long address) {
        return address == 0L ? null : new KernelTimespec(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(KernelTimespec.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = KernelTimespec.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static KernelTimespec malloc(MemoryStack stack) {
        return new KernelTimespec(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static KernelTimespec calloc(MemoryStack stack) {
        return new KernelTimespec(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long ntv_sec(long struct) {
        return UNSAFE.getLong(null, struct + (long)TV_SEC);
    }

    public static long ntv_nsec(long struct) {
        return UNSAFE.getLong(null, struct + (long)TV_NSEC);
    }

    public static void ntv_sec(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)TV_SEC, value);
    }

    public static void ntv_nsec(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)TV_NSEC, value);
    }

    static {
        Struct.Layout layout = KernelTimespec.__struct(KernelTimespec.__member(8), KernelTimespec.__member(8));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        TV_SEC = layout.offsetof(0);
        TV_NSEC = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<KernelTimespec, Buffer>
    implements NativeResource {
        private static final KernelTimespec ELEMENT_FACTORY = KernelTimespec.create(-1L);

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
        protected KernelTimespec getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="int64_t")
        public long tv_sec() {
            return KernelTimespec.ntv_sec(this.address());
        }

        @NativeType(value="long long")
        public long tv_nsec() {
            return KernelTimespec.ntv_nsec(this.address());
        }

        public Buffer tv_sec(@NativeType(value="int64_t") long value) {
            KernelTimespec.ntv_sec(this.address(), value);
            return this;
        }

        public Buffer tv_nsec(@NativeType(value="long long") long value) {
            KernelTimespec.ntv_nsec(this.address(), value);
            return this;
        }
    }
}
