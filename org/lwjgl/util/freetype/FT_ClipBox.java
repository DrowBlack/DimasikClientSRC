package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_Vector;

public class FT_ClipBox
extends Struct<FT_ClipBox>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int BOTTOM_LEFT;
    public static final int TOP_LEFT;
    public static final int TOP_RIGHT;
    public static final int BOTTOM_RIGHT;

    protected FT_ClipBox(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_ClipBox create(long address, @Nullable ByteBuffer container) {
        return new FT_ClipBox(address, container);
    }

    public FT_ClipBox(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_ClipBox.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public FT_Vector bottom_left() {
        return FT_ClipBox.nbottom_left(this.address());
    }

    public FT_Vector top_left() {
        return FT_ClipBox.ntop_left(this.address());
    }

    public FT_Vector top_right() {
        return FT_ClipBox.ntop_right(this.address());
    }

    public FT_Vector bottom_right() {
        return FT_ClipBox.nbottom_right(this.address());
    }

    public static FT_ClipBox malloc() {
        return new FT_ClipBox(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_ClipBox calloc() {
        return new FT_ClipBox(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_ClipBox create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_ClipBox(MemoryUtil.memAddress(container), container);
    }

    public static FT_ClipBox create(long address) {
        return new FT_ClipBox(address, null);
    }

    @Nullable
    public static FT_ClipBox createSafe(long address) {
        return address == 0L ? null : new FT_ClipBox(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_ClipBox.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_ClipBox.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_ClipBox malloc(MemoryStack stack) {
        return new FT_ClipBox(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_ClipBox calloc(MemoryStack stack) {
        return new FT_ClipBox(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static FT_Vector nbottom_left(long struct) {
        return FT_Vector.create(struct + (long)BOTTOM_LEFT);
    }

    public static FT_Vector ntop_left(long struct) {
        return FT_Vector.create(struct + (long)TOP_LEFT);
    }

    public static FT_Vector ntop_right(long struct) {
        return FT_Vector.create(struct + (long)TOP_RIGHT);
    }

    public static FT_Vector nbottom_right(long struct) {
        return FT_Vector.create(struct + (long)BOTTOM_RIGHT);
    }

    static {
        Struct.Layout layout = FT_ClipBox.__struct(FT_ClipBox.__member(FT_Vector.SIZEOF, FT_Vector.ALIGNOF), FT_ClipBox.__member(FT_Vector.SIZEOF, FT_Vector.ALIGNOF), FT_ClipBox.__member(FT_Vector.SIZEOF, FT_Vector.ALIGNOF), FT_ClipBox.__member(FT_Vector.SIZEOF, FT_Vector.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        BOTTOM_LEFT = layout.offsetof(0);
        TOP_LEFT = layout.offsetof(1);
        TOP_RIGHT = layout.offsetof(2);
        BOTTOM_RIGHT = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FT_ClipBox, Buffer>
    implements NativeResource {
        private static final FT_ClipBox ELEMENT_FACTORY = FT_ClipBox.create(-1L);

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
        protected FT_ClipBox getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public FT_Vector bottom_left() {
            return FT_ClipBox.nbottom_left(this.address());
        }

        public FT_Vector top_left() {
            return FT_ClipBox.ntop_left(this.address());
        }

        public FT_Vector top_right() {
            return FT_ClipBox.ntop_right(this.address());
        }

        public FT_Vector bottom_right() {
            return FT_ClipBox.nbottom_right(this.address());
        }
    }
}
