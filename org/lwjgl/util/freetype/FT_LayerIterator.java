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

public class FT_LayerIterator
extends Struct<FT_LayerIterator>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int NUM_LAYERS;
    public static final int LAYER;
    public static final int P;

    protected FT_LayerIterator(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_LayerIterator create(long address, @Nullable ByteBuffer container) {
        return new FT_LayerIterator(address, container);
    }

    public FT_LayerIterator(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_LayerIterator.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_UInt")
    public int num_layers() {
        return FT_LayerIterator.nnum_layers(this.address());
    }

    @NativeType(value="FT_UInt")
    public int layer() {
        return FT_LayerIterator.nlayer(this.address());
    }

    @Nullable
    @NativeType(value="FT_Byte *")
    public ByteBuffer p(int capacity) {
        return FT_LayerIterator.np(this.address(), capacity);
    }

    public static FT_LayerIterator malloc() {
        return new FT_LayerIterator(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_LayerIterator calloc() {
        return new FT_LayerIterator(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_LayerIterator create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_LayerIterator(MemoryUtil.memAddress(container), container);
    }

    public static FT_LayerIterator create(long address) {
        return new FT_LayerIterator(address, null);
    }

    @Nullable
    public static FT_LayerIterator createSafe(long address) {
        return address == 0L ? null : new FT_LayerIterator(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_LayerIterator.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_LayerIterator.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_LayerIterator malloc(MemoryStack stack) {
        return new FT_LayerIterator(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_LayerIterator calloc(MemoryStack stack) {
        return new FT_LayerIterator(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nnum_layers(long struct) {
        return UNSAFE.getInt(null, struct + (long)NUM_LAYERS);
    }

    public static int nlayer(long struct) {
        return UNSAFE.getInt(null, struct + (long)LAYER);
    }

    @Nullable
    public static ByteBuffer np(long struct, int capacity) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)P), capacity);
    }

    static {
        Struct.Layout layout = FT_LayerIterator.__struct(FT_LayerIterator.__member(4), FT_LayerIterator.__member(4), FT_LayerIterator.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        NUM_LAYERS = layout.offsetof(0);
        LAYER = layout.offsetof(1);
        P = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<FT_LayerIterator, Buffer>
    implements NativeResource {
        private static final FT_LayerIterator ELEMENT_FACTORY = FT_LayerIterator.create(-1L);

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
        protected FT_LayerIterator getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_UInt")
        public int num_layers() {
            return FT_LayerIterator.nnum_layers(this.address());
        }

        @NativeType(value="FT_UInt")
        public int layer() {
            return FT_LayerIterator.nlayer(this.address());
        }

        @Nullable
        @NativeType(value="FT_Byte *")
        public ByteBuffer p(int capacity) {
            return FT_LayerIterator.np(this.address(), capacity);
        }
    }
}
