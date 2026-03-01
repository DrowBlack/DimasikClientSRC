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

public class FT_BBox
extends Struct<FT_BBox>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int XMIN;
    public static final int YMIN;
    public static final int XMAX;
    public static final int YMAX;

    protected FT_BBox(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_BBox create(long address, @Nullable ByteBuffer container) {
        return new FT_BBox(address, container);
    }

    public FT_BBox(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_BBox.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Pos")
    public long xMin() {
        return FT_BBox.nxMin(this.address());
    }

    @NativeType(value="FT_Pos")
    public long yMin() {
        return FT_BBox.nyMin(this.address());
    }

    @NativeType(value="FT_Pos")
    public long xMax() {
        return FT_BBox.nxMax(this.address());
    }

    @NativeType(value="FT_Pos")
    public long yMax() {
        return FT_BBox.nyMax(this.address());
    }

    public FT_BBox xMin(@NativeType(value="FT_Pos") long value) {
        FT_BBox.nxMin(this.address(), value);
        return this;
    }

    public FT_BBox yMin(@NativeType(value="FT_Pos") long value) {
        FT_BBox.nyMin(this.address(), value);
        return this;
    }

    public FT_BBox xMax(@NativeType(value="FT_Pos") long value) {
        FT_BBox.nxMax(this.address(), value);
        return this;
    }

    public FT_BBox yMax(@NativeType(value="FT_Pos") long value) {
        FT_BBox.nyMax(this.address(), value);
        return this;
    }

    public FT_BBox set(long xMin, long yMin, long xMax, long yMax) {
        this.xMin(xMin);
        this.yMin(yMin);
        this.xMax(xMax);
        this.yMax(yMax);
        return this;
    }

    public FT_BBox set(FT_BBox src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_BBox malloc() {
        return new FT_BBox(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_BBox calloc() {
        return new FT_BBox(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_BBox create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_BBox(MemoryUtil.memAddress(container), container);
    }

    public static FT_BBox create(long address) {
        return new FT_BBox(address, null);
    }

    @Nullable
    public static FT_BBox createSafe(long address) {
        return address == 0L ? null : new FT_BBox(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_BBox.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_BBox.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_BBox malloc(MemoryStack stack) {
        return new FT_BBox(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_BBox calloc(MemoryStack stack) {
        return new FT_BBox(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nxMin(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)XMIN);
    }

    public static long nyMin(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)YMIN);
    }

    public static long nxMax(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)XMAX);
    }

    public static long nyMax(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)YMAX);
    }

    public static void nxMin(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)XMIN, value);
    }

    public static void nyMin(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)YMIN, value);
    }

    public static void nxMax(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)XMAX, value);
    }

    public static void nyMax(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)YMAX, value);
    }

    static {
        Struct.Layout layout = FT_BBox.__struct(FT_BBox.__member(CLONG_SIZE), FT_BBox.__member(CLONG_SIZE), FT_BBox.__member(CLONG_SIZE), FT_BBox.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        XMIN = layout.offsetof(0);
        YMIN = layout.offsetof(1);
        XMAX = layout.offsetof(2);
        YMAX = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FT_BBox, Buffer>
    implements NativeResource {
        private static final FT_BBox ELEMENT_FACTORY = FT_BBox.create(-1L);

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
        protected FT_BBox getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Pos")
        public long xMin() {
            return FT_BBox.nxMin(this.address());
        }

        @NativeType(value="FT_Pos")
        public long yMin() {
            return FT_BBox.nyMin(this.address());
        }

        @NativeType(value="FT_Pos")
        public long xMax() {
            return FT_BBox.nxMax(this.address());
        }

        @NativeType(value="FT_Pos")
        public long yMax() {
            return FT_BBox.nyMax(this.address());
        }

        public Buffer xMin(@NativeType(value="FT_Pos") long value) {
            FT_BBox.nxMin(this.address(), value);
            return this;
        }

        public Buffer yMin(@NativeType(value="FT_Pos") long value) {
            FT_BBox.nyMin(this.address(), value);
            return this;
        }

        public Buffer xMax(@NativeType(value="FT_Pos") long value) {
            FT_BBox.nxMax(this.address(), value);
            return this;
        }

        public Buffer yMax(@NativeType(value="FT_Pos") long value) {
            FT_BBox.nyMax(this.address(), value);
            return this;
        }
    }
}
