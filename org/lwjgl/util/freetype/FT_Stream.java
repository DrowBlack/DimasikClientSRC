package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_Memory;
import org.lwjgl.util.freetype.FT_StreamDesc;
import org.lwjgl.util.freetype.FT_Stream_CloseFunc;
import org.lwjgl.util.freetype.FT_Stream_CloseFuncI;
import org.lwjgl.util.freetype.FT_Stream_IoFunc;
import org.lwjgl.util.freetype.FT_Stream_IoFuncI;

@NativeType(value="struct FT_StreamRec")
public class FT_Stream
extends Struct<FT_Stream>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int BASE;
    public static final int SIZE;
    public static final int POS;
    public static final int DESCRIPTOR;
    public static final int PATHNAME;
    public static final int READ;
    public static final int CLOSE;
    public static final int MEMORY;
    public static final int CURSOR;
    public static final int LIMIT;

    protected FT_Stream(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Stream create(long address, @Nullable ByteBuffer container) {
        return new FT_Stream(address, container);
    }

    public FT_Stream(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Stream.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @Nullable
    @NativeType(value="unsigned char *")
    public ByteBuffer base() {
        return FT_Stream.nbase(this.address());
    }

    @NativeType(value="unsigned long")
    public long size() {
        return FT_Stream.nsize(this.address());
    }

    @NativeType(value="unsigned long")
    public long pos() {
        return FT_Stream.npos(this.address());
    }

    public FT_StreamDesc descriptor() {
        return FT_Stream.ndescriptor(this.address());
    }

    public FT_StreamDesc pathname() {
        return FT_Stream.npathname(this.address());
    }

    @Nullable
    public FT_Stream_IoFunc read() {
        return FT_Stream.nread(this.address());
    }

    @Nullable
    public FT_Stream_CloseFunc close$() {
        return FT_Stream.nclose$(this.address());
    }

    public FT_Stream base(@Nullable @NativeType(value="unsigned char *") ByteBuffer value) {
        FT_Stream.nbase(this.address(), value);
        return this;
    }

    public FT_Stream size(@NativeType(value="unsigned long") long value) {
        FT_Stream.nsize(this.address(), value);
        return this;
    }

    public FT_Stream pos(@NativeType(value="unsigned long") long value) {
        FT_Stream.npos(this.address(), value);
        return this;
    }

    public FT_Stream descriptor(FT_StreamDesc value) {
        FT_Stream.ndescriptor(this.address(), value);
        return this;
    }

    public FT_Stream descriptor(Consumer<FT_StreamDesc> consumer) {
        consumer.accept(this.descriptor());
        return this;
    }

    public FT_Stream pathname(FT_StreamDesc value) {
        FT_Stream.npathname(this.address(), value);
        return this;
    }

    public FT_Stream pathname(Consumer<FT_StreamDesc> consumer) {
        consumer.accept(this.pathname());
        return this;
    }

    public FT_Stream read(@Nullable @NativeType(value="FT_Stream_IoFunc") FT_Stream_IoFuncI value) {
        FT_Stream.nread(this.address(), value);
        return this;
    }

    public FT_Stream close$(@Nullable @NativeType(value="FT_Stream_CloseFunc") FT_Stream_CloseFuncI value) {
        FT_Stream.nclose$(this.address(), value);
        return this;
    }

    public FT_Stream set(@Nullable ByteBuffer base, long size, long pos, FT_StreamDesc descriptor, FT_StreamDesc pathname, @Nullable FT_Stream_IoFuncI read, @Nullable FT_Stream_CloseFuncI close$) {
        this.base(base);
        this.size(size);
        this.pos(pos);
        this.descriptor(descriptor);
        this.pathname(pathname);
        this.read(read);
        this.close$(close$);
        return this;
    }

    public FT_Stream set(FT_Stream src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Stream malloc() {
        return new FT_Stream(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Stream calloc() {
        return new FT_Stream(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Stream create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Stream(MemoryUtil.memAddress(container), container);
    }

    public static FT_Stream create(long address) {
        return new FT_Stream(address, null);
    }

    @Nullable
    public static FT_Stream createSafe(long address) {
        return address == 0L ? null : new FT_Stream(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Stream.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Stream.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Stream malloc(MemoryStack stack) {
        return new FT_Stream(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Stream calloc(MemoryStack stack) {
        return new FT_Stream(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    @Nullable
    public static ByteBuffer nbase(long struct) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)BASE), (int)FT_Stream.nsize(struct));
    }

    public static long nsize(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)SIZE);
    }

    public static long npos(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)POS);
    }

    public static FT_StreamDesc ndescriptor(long struct) {
        return FT_StreamDesc.create(struct + (long)DESCRIPTOR);
    }

    public static FT_StreamDesc npathname(long struct) {
        return FT_StreamDesc.create(struct + (long)PATHNAME);
    }

    @Nullable
    public static FT_Stream_IoFunc nread(long struct) {
        return FT_Stream_IoFunc.createSafe(MemoryUtil.memGetAddress(struct + (long)READ));
    }

    @Nullable
    public static FT_Stream_CloseFunc nclose$(long struct) {
        return FT_Stream_CloseFunc.createSafe(MemoryUtil.memGetAddress(struct + (long)CLOSE));
    }

    @Nullable
    public static FT_Memory nmemory(long struct) {
        return FT_Memory.createSafe(MemoryUtil.memGetAddress(struct + (long)MEMORY));
    }

    @Nullable
    public static ByteBuffer ncursor(long struct, int capacity) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)CURSOR), capacity);
    }

    @Nullable
    public static ByteBuffer nlimit$(long struct, int capacity) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)LIMIT), capacity);
    }

    public static void nbase(long struct, @Nullable ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)BASE, MemoryUtil.memAddressSafe(value));
        FT_Stream.nsize(struct, value == null ? 0L : (long)value.remaining());
    }

    public static void nsize(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)SIZE, value);
    }

    public static void npos(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)POS, value);
    }

    public static void ndescriptor(long struct, FT_StreamDesc value) {
        MemoryUtil.memCopy(value.address(), struct + (long)DESCRIPTOR, FT_StreamDesc.SIZEOF);
    }

    public static void npathname(long struct, FT_StreamDesc value) {
        MemoryUtil.memCopy(value.address(), struct + (long)PATHNAME, FT_StreamDesc.SIZEOF);
    }

    public static void nread(long struct, @Nullable FT_Stream_IoFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)READ, MemoryUtil.memAddressSafe(value));
    }

    public static void nclose$(long struct, @Nullable FT_Stream_CloseFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)CLOSE, MemoryUtil.memAddressSafe(value));
    }

    public static void nmemory(long struct, @Nullable FT_Memory value) {
        MemoryUtil.memPutAddress(struct + (long)MEMORY, MemoryUtil.memAddressSafe(value));
    }

    public static void ncursor(long struct, @Nullable ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)CURSOR, MemoryUtil.memAddressSafe(value));
    }

    public static void nlimit$(long struct, @Nullable ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)LIMIT, MemoryUtil.memAddressSafe(value));
    }

    static {
        Struct.Layout layout = FT_Stream.__struct(FT_Stream.__member(POINTER_SIZE), FT_Stream.__member(CLONG_SIZE), FT_Stream.__member(CLONG_SIZE), FT_Stream.__member(FT_StreamDesc.SIZEOF, FT_StreamDesc.ALIGNOF), FT_Stream.__member(FT_StreamDesc.SIZEOF, FT_StreamDesc.ALIGNOF), FT_Stream.__member(POINTER_SIZE), FT_Stream.__member(POINTER_SIZE), FT_Stream.__member(POINTER_SIZE), FT_Stream.__member(POINTER_SIZE), FT_Stream.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        BASE = layout.offsetof(0);
        SIZE = layout.offsetof(1);
        POS = layout.offsetof(2);
        DESCRIPTOR = layout.offsetof(3);
        PATHNAME = layout.offsetof(4);
        READ = layout.offsetof(5);
        CLOSE = layout.offsetof(6);
        MEMORY = layout.offsetof(7);
        CURSOR = layout.offsetof(8);
        LIMIT = layout.offsetof(9);
    }

    public static class Buffer
    extends StructBuffer<FT_Stream, Buffer>
    implements NativeResource {
        private static final FT_Stream ELEMENT_FACTORY = FT_Stream.create(-1L);

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
        protected FT_Stream getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @Nullable
        @NativeType(value="unsigned char *")
        public ByteBuffer base() {
            return FT_Stream.nbase(this.address());
        }

        @NativeType(value="unsigned long")
        public long size() {
            return FT_Stream.nsize(this.address());
        }

        @NativeType(value="unsigned long")
        public long pos() {
            return FT_Stream.npos(this.address());
        }

        public FT_StreamDesc descriptor() {
            return FT_Stream.ndescriptor(this.address());
        }

        public FT_StreamDesc pathname() {
            return FT_Stream.npathname(this.address());
        }

        @Nullable
        public FT_Stream_IoFunc read() {
            return FT_Stream.nread(this.address());
        }

        @Nullable
        public FT_Stream_CloseFunc close$() {
            return FT_Stream.nclose$(this.address());
        }

        public Buffer base(@Nullable @NativeType(value="unsigned char *") ByteBuffer value) {
            FT_Stream.nbase(this.address(), value);
            return this;
        }

        public Buffer size(@NativeType(value="unsigned long") long value) {
            FT_Stream.nsize(this.address(), value);
            return this;
        }

        public Buffer pos(@NativeType(value="unsigned long") long value) {
            FT_Stream.npos(this.address(), value);
            return this;
        }

        public Buffer descriptor(FT_StreamDesc value) {
            FT_Stream.ndescriptor(this.address(), value);
            return this;
        }

        public Buffer descriptor(Consumer<FT_StreamDesc> consumer) {
            consumer.accept(this.descriptor());
            return this;
        }

        public Buffer pathname(FT_StreamDesc value) {
            FT_Stream.npathname(this.address(), value);
            return this;
        }

        public Buffer pathname(Consumer<FT_StreamDesc> consumer) {
            consumer.accept(this.pathname());
            return this;
        }

        public Buffer read(@Nullable @NativeType(value="FT_Stream_IoFunc") FT_Stream_IoFuncI value) {
            FT_Stream.nread(this.address(), value);
            return this;
        }

        public Buffer close$(@Nullable @NativeType(value="FT_Stream_CloseFunc") FT_Stream_CloseFuncI value) {
            FT_Stream.nclose$(this.address(), value);
            return this;
        }
    }
}
