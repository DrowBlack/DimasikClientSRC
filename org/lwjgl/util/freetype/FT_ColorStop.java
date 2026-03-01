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
import org.lwjgl.util.freetype.FT_ColorIndex;

public class FT_ColorStop
extends Struct<FT_ColorStop>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int STOP_OFFSET;
    public static final int COLOR;

    protected FT_ColorStop(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_ColorStop create(long address, @Nullable ByteBuffer container) {
        return new FT_ColorStop(address, container);
    }

    public FT_ColorStop(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_ColorStop.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Fixed")
    public long stop_offset() {
        return FT_ColorStop.nstop_offset(this.address());
    }

    public FT_ColorIndex color() {
        return FT_ColorStop.ncolor(this.address());
    }

    public static FT_ColorStop malloc() {
        return new FT_ColorStop(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_ColorStop calloc() {
        return new FT_ColorStop(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_ColorStop create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_ColorStop(MemoryUtil.memAddress(container), container);
    }

    public static FT_ColorStop create(long address) {
        return new FT_ColorStop(address, null);
    }

    @Nullable
    public static FT_ColorStop createSafe(long address) {
        return address == 0L ? null : new FT_ColorStop(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_ColorStop.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_ColorStop.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_ColorStop malloc(MemoryStack stack) {
        return new FT_ColorStop(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_ColorStop calloc(MemoryStack stack) {
        return new FT_ColorStop(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nstop_offset(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)STOP_OFFSET);
    }

    public static FT_ColorIndex ncolor(long struct) {
        return FT_ColorIndex.create(struct + (long)COLOR);
    }

    static {
        Struct.Layout layout = FT_ColorStop.__struct(FT_ColorStop.__member(CLONG_SIZE), FT_ColorStop.__member(FT_ColorIndex.SIZEOF, FT_ColorIndex.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        STOP_OFFSET = layout.offsetof(0);
        COLOR = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_ColorStop, Buffer>
    implements NativeResource {
        private static final FT_ColorStop ELEMENT_FACTORY = FT_ColorStop.create(-1L);

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
        protected FT_ColorStop getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Fixed")
        public long stop_offset() {
            return FT_ColorStop.nstop_offset(this.address());
        }

        public FT_ColorIndex color() {
            return FT_ColorStop.ncolor(this.address());
        }
    }
}
