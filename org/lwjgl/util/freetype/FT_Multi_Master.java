package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_MM_Axis;

public class FT_Multi_Master
extends Struct<FT_Multi_Master>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int NUM_AXIS;
    public static final int NUM_DESIGNS;
    public static final int AXIS;

    protected FT_Multi_Master(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Multi_Master create(long address, @Nullable ByteBuffer container) {
        return new FT_Multi_Master(address, container);
    }

    public FT_Multi_Master(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Multi_Master.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_UInt")
    public int num_axis() {
        return FT_Multi_Master.nnum_axis(this.address());
    }

    @NativeType(value="FT_UInt")
    public int num_designs() {
        return FT_Multi_Master.nnum_designs(this.address());
    }

    @NativeType(value="FT_MM_Axis[T1_MAX_MM_AXIS]")
    public FT_MM_Axis.Buffer axis() {
        return FT_Multi_Master.naxis(this.address());
    }

    public FT_MM_Axis axis(int index) {
        return FT_Multi_Master.naxis(this.address(), index);
    }

    public static FT_Multi_Master malloc() {
        return new FT_Multi_Master(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Multi_Master calloc() {
        return new FT_Multi_Master(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Multi_Master create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Multi_Master(MemoryUtil.memAddress(container), container);
    }

    public static FT_Multi_Master create(long address) {
        return new FT_Multi_Master(address, null);
    }

    @Nullable
    public static FT_Multi_Master createSafe(long address) {
        return address == 0L ? null : new FT_Multi_Master(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Multi_Master.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Multi_Master.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Multi_Master malloc(MemoryStack stack) {
        return new FT_Multi_Master(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Multi_Master calloc(MemoryStack stack) {
        return new FT_Multi_Master(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nnum_axis(long struct) {
        return UNSAFE.getInt(null, struct + (long)NUM_AXIS);
    }

    public static int nnum_designs(long struct) {
        return UNSAFE.getInt(null, struct + (long)NUM_DESIGNS);
    }

    public static FT_MM_Axis.Buffer naxis(long struct) {
        return FT_MM_Axis.create(struct + (long)AXIS, 4);
    }

    public static FT_MM_Axis naxis(long struct, int index) {
        return FT_MM_Axis.create(struct + (long)AXIS + Checks.check(index, 4) * (long)FT_MM_Axis.SIZEOF);
    }

    static {
        Struct.Layout layout = FT_Multi_Master.__struct(FT_Multi_Master.__member(4), FT_Multi_Master.__member(4), FT_Multi_Master.__array(FT_MM_Axis.SIZEOF, FT_MM_Axis.ALIGNOF, 4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        NUM_AXIS = layout.offsetof(0);
        NUM_DESIGNS = layout.offsetof(1);
        AXIS = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<FT_Multi_Master, Buffer>
    implements NativeResource {
        private static final FT_Multi_Master ELEMENT_FACTORY = FT_Multi_Master.create(-1L);

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
        protected FT_Multi_Master getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_UInt")
        public int num_axis() {
            return FT_Multi_Master.nnum_axis(this.address());
        }

        @NativeType(value="FT_UInt")
        public int num_designs() {
            return FT_Multi_Master.nnum_designs(this.address());
        }

        @NativeType(value="FT_MM_Axis[T1_MAX_MM_AXIS]")
        public FT_MM_Axis.Buffer axis() {
            return FT_Multi_Master.naxis(this.address());
        }

        public FT_MM_Axis axis(int index) {
            return FT_Multi_Master.naxis(this.address(), index);
        }
    }
}
