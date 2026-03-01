package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class FT_StreamDesc
extends Struct<FT_StreamDesc>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int VALUE;
    public static final int POINTER;

    protected FT_StreamDesc(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_StreamDesc create(long address, @Nullable ByteBuffer container) {
        return new FT_StreamDesc(address, container);
    }

    public FT_StreamDesc(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_StreamDesc.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public long value() {
        return FT_StreamDesc.nvalue(this.address());
    }

    @NativeType(value="void *")
    public long pointer() {
        return FT_StreamDesc.npointer(this.address());
    }

    public FT_StreamDesc value(long value) {
        FT_StreamDesc.nvalue(this.address(), value);
        return this;
    }

    public FT_StreamDesc pointer(@NativeType(value="void *") long value) {
        FT_StreamDesc.npointer(this.address(), value);
        return this;
    }

    public FT_StreamDesc set(FT_StreamDesc src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_StreamDesc malloc() {
        return new FT_StreamDesc(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_StreamDesc calloc() {
        return new FT_StreamDesc(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_StreamDesc create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_StreamDesc(MemoryUtil.memAddress(container), container);
    }

    public static FT_StreamDesc create(long address) {
        return new FT_StreamDesc(address, null);
    }

    @Nullable
    public static FT_StreamDesc createSafe(long address) {
        return address == 0L ? null : new FT_StreamDesc(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_StreamDesc.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_StreamDesc.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_StreamDesc malloc(MemoryStack stack) {
        return new FT_StreamDesc(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_StreamDesc calloc(MemoryStack stack) {
        return new FT_StreamDesc(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nvalue(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)VALUE);
    }

    public static long npointer(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)POINTER);
    }

    public static void nvalue(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)VALUE, value);
    }

    public static void npointer(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)POINTER, value);
    }

    static {
        Struct.Layout layout = FT_StreamDesc.__union(FT_StreamDesc.__member(CLONG_SIZE), FT_StreamDesc.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        VALUE = layout.offsetof(0);
        POINTER = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_StreamDesc, Buffer>
    implements NativeResource {
        private static final FT_StreamDesc ELEMENT_FACTORY = FT_StreamDesc.create(-1L);

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
        protected FT_StreamDesc getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public long value() {
            return FT_StreamDesc.nvalue(this.address());
        }

        @NativeType(value="void *")
        public long pointer() {
            return FT_StreamDesc.npointer(this.address());
        }

        public Buffer value(long value) {
            FT_StreamDesc.nvalue(this.address(), value);
            return this;
        }

        public Buffer pointer(@NativeType(value="void *") long value) {
            FT_StreamDesc.npointer(this.address(), value);
            return this;
        }
    }
}
