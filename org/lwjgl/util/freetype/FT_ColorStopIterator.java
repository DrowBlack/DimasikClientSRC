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

public class FT_ColorStopIterator
extends Struct<FT_ColorStopIterator>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int NUM_COLOR_STOPS;
    public static final int CURRENT_COLOR_STOP;
    public static final int P;
    public static final int READ_VARIABLE;

    protected FT_ColorStopIterator(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_ColorStopIterator create(long address, @Nullable ByteBuffer container) {
        return new FT_ColorStopIterator(address, container);
    }

    public FT_ColorStopIterator(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_ColorStopIterator.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_UInt")
    public int num_color_stops() {
        return FT_ColorStopIterator.nnum_color_stops(this.address());
    }

    @NativeType(value="FT_UInt")
    public int current_color_stop() {
        return FT_ColorStopIterator.ncurrent_color_stop(this.address());
    }

    @Nullable
    @NativeType(value="FT_Byte *")
    public ByteBuffer p(int capacity) {
        return FT_ColorStopIterator.np(this.address(), capacity);
    }

    @NativeType(value="FT_Bool")
    public boolean read_variable() {
        return FT_ColorStopIterator.nread_variable(this.address());
    }

    public static FT_ColorStopIterator malloc() {
        return new FT_ColorStopIterator(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_ColorStopIterator calloc() {
        return new FT_ColorStopIterator(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_ColorStopIterator create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_ColorStopIterator(MemoryUtil.memAddress(container), container);
    }

    public static FT_ColorStopIterator create(long address) {
        return new FT_ColorStopIterator(address, null);
    }

    @Nullable
    public static FT_ColorStopIterator createSafe(long address) {
        return address == 0L ? null : new FT_ColorStopIterator(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_ColorStopIterator.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_ColorStopIterator.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_ColorStopIterator malloc(MemoryStack stack) {
        return new FT_ColorStopIterator(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_ColorStopIterator calloc(MemoryStack stack) {
        return new FT_ColorStopIterator(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nnum_color_stops(long struct) {
        return UNSAFE.getInt(null, struct + (long)NUM_COLOR_STOPS);
    }

    public static int ncurrent_color_stop(long struct) {
        return UNSAFE.getInt(null, struct + (long)CURRENT_COLOR_STOP);
    }

    @Nullable
    public static ByteBuffer np(long struct, int capacity) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)P), capacity);
    }

    public static boolean nread_variable(long struct) {
        return UNSAFE.getByte(null, struct + (long)READ_VARIABLE) != 0;
    }

    static {
        Struct.Layout layout = FT_ColorStopIterator.__struct(FT_ColorStopIterator.__member(4), FT_ColorStopIterator.__member(4), FT_ColorStopIterator.__member(POINTER_SIZE), FT_ColorStopIterator.__member(1));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        NUM_COLOR_STOPS = layout.offsetof(0);
        CURRENT_COLOR_STOP = layout.offsetof(1);
        P = layout.offsetof(2);
        READ_VARIABLE = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FT_ColorStopIterator, Buffer>
    implements NativeResource {
        private static final FT_ColorStopIterator ELEMENT_FACTORY = FT_ColorStopIterator.create(-1L);

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
        protected FT_ColorStopIterator getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_UInt")
        public int num_color_stops() {
            return FT_ColorStopIterator.nnum_color_stops(this.address());
        }

        @NativeType(value="FT_UInt")
        public int current_color_stop() {
            return FT_ColorStopIterator.ncurrent_color_stop(this.address());
        }

        @Nullable
        @NativeType(value="FT_Byte *")
        public ByteBuffer p(int capacity) {
            return FT_ColorStopIterator.np(this.address(), capacity);
        }

        @NativeType(value="FT_Bool")
        public boolean read_variable() {
            return FT_ColorStopIterator.nread_variable(this.address());
        }
    }
}
