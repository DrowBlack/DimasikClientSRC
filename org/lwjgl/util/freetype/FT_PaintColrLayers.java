package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_LayerIterator;

public class FT_PaintColrLayers
extends Struct<FT_PaintColrLayers> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int LAYER_ITERATOR;

    protected FT_PaintColrLayers(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_PaintColrLayers create(long address, @Nullable ByteBuffer container) {
        return new FT_PaintColrLayers(address, container);
    }

    public FT_PaintColrLayers(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_PaintColrLayers.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public FT_LayerIterator layer_iterator() {
        return FT_PaintColrLayers.nlayer_iterator(this.address());
    }

    public static FT_PaintColrLayers create(long address) {
        return new FT_PaintColrLayers(address, null);
    }

    @Nullable
    public static FT_PaintColrLayers createSafe(long address) {
        return address == 0L ? null : new FT_PaintColrLayers(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_LayerIterator nlayer_iterator(long struct) {
        return FT_LayerIterator.create(struct + (long)LAYER_ITERATOR);
    }

    static {
        Struct.Layout layout = FT_PaintColrLayers.__struct(FT_PaintColrLayers.__member(FT_LayerIterator.SIZEOF, FT_LayerIterator.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        LAYER_ITERATOR = layout.offsetof(0);
    }

    public static class Buffer
    extends StructBuffer<FT_PaintColrLayers, Buffer> {
        private static final FT_PaintColrLayers ELEMENT_FACTORY = FT_PaintColrLayers.create(-1L);

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
        protected FT_PaintColrLayers getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public FT_LayerIterator layer_iterator() {
            return FT_PaintColrLayers.nlayer_iterator(this.address());
        }
    }
}
