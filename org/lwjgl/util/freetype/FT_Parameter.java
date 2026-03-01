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

public class FT_Parameter
extends Struct<FT_Parameter>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int TAG;
    public static final int DATA;

    protected FT_Parameter(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Parameter create(long address, @Nullable ByteBuffer container) {
        return new FT_Parameter(address, container);
    }

    public FT_Parameter(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Parameter.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_ULong")
    public long tag() {
        return FT_Parameter.ntag(this.address());
    }

    @Nullable
    @NativeType(value="FT_Pointer")
    public ByteBuffer data(int capacity) {
        return FT_Parameter.ndata(this.address(), capacity);
    }

    public FT_Parameter tag(@NativeType(value="FT_ULong") long value) {
        FT_Parameter.ntag(this.address(), value);
        return this;
    }

    public FT_Parameter data(@Nullable @NativeType(value="FT_Pointer") ByteBuffer value) {
        FT_Parameter.ndata(this.address(), value);
        return this;
    }

    public FT_Parameter set(long tag, @Nullable ByteBuffer data) {
        this.tag(tag);
        this.data(data);
        return this;
    }

    public FT_Parameter set(FT_Parameter src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Parameter malloc() {
        return new FT_Parameter(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Parameter calloc() {
        return new FT_Parameter(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Parameter create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Parameter(MemoryUtil.memAddress(container), container);
    }

    public static FT_Parameter create(long address) {
        return new FT_Parameter(address, null);
    }

    @Nullable
    public static FT_Parameter createSafe(long address) {
        return address == 0L ? null : new FT_Parameter(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Parameter.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Parameter.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Parameter malloc(MemoryStack stack) {
        return new FT_Parameter(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Parameter calloc(MemoryStack stack) {
        return new FT_Parameter(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long ntag(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)TAG);
    }

    @Nullable
    public static ByteBuffer ndata(long struct, int capacity) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)DATA), capacity);
    }

    public static void ntag(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)TAG, value);
    }

    public static void ndata(long struct, @Nullable ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)DATA, MemoryUtil.memAddressSafe(value));
    }

    static {
        Struct.Layout layout = FT_Parameter.__struct(FT_Parameter.__member(CLONG_SIZE), FT_Parameter.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        TAG = layout.offsetof(0);
        DATA = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_Parameter, Buffer>
    implements NativeResource {
        private static final FT_Parameter ELEMENT_FACTORY = FT_Parameter.create(-1L);

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
        protected FT_Parameter getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_ULong")
        public long tag() {
            return FT_Parameter.ntag(this.address());
        }

        @Nullable
        @NativeType(value="FT_Pointer")
        public ByteBuffer data(int capacity) {
            return FT_Parameter.ndata(this.address(), capacity);
        }

        public Buffer tag(@NativeType(value="FT_ULong") long value) {
            FT_Parameter.ntag(this.address(), value);
            return this;
        }

        public Buffer data(@Nullable @NativeType(value="FT_Pointer") ByteBuffer value) {
            FT_Parameter.ndata(this.address(), value);
            return this;
        }
    }
}
