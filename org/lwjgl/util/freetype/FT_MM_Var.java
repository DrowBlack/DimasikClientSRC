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
import org.lwjgl.util.freetype.FT_Var_Axis;
import org.lwjgl.util.freetype.FT_Var_Named_Style;

public class FT_MM_Var
extends Struct<FT_MM_Var>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int NUM_AXIS;
    public static final int NUM_DESIGNS;
    public static final int NUM_NAMEDSTYLES;
    public static final int AXIS;
    public static final int NAMEDSTYLE;

    protected FT_MM_Var(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_MM_Var create(long address, @Nullable ByteBuffer container) {
        return new FT_MM_Var(address, container);
    }

    public FT_MM_Var(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_MM_Var.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_UInt")
    public int num_axis() {
        return FT_MM_Var.nnum_axis(this.address());
    }

    @NativeType(value="FT_UInt")
    public int num_designs() {
        return FT_MM_Var.nnum_designs(this.address());
    }

    @NativeType(value="FT_UInt")
    public int num_namedstyles() {
        return FT_MM_Var.nnum_namedstyles(this.address());
    }

    @NativeType(value="FT_Var_Axis *")
    public FT_Var_Axis.Buffer axis() {
        return FT_MM_Var.naxis(this.address());
    }

    @NativeType(value="FT_Var_Named_Style *")
    public FT_Var_Named_Style.Buffer namedstyle() {
        return FT_MM_Var.nnamedstyle(this.address());
    }

    public static FT_MM_Var malloc() {
        return new FT_MM_Var(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_MM_Var calloc() {
        return new FT_MM_Var(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_MM_Var create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_MM_Var(MemoryUtil.memAddress(container), container);
    }

    public static FT_MM_Var create(long address) {
        return new FT_MM_Var(address, null);
    }

    @Nullable
    public static FT_MM_Var createSafe(long address) {
        return address == 0L ? null : new FT_MM_Var(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_MM_Var.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_MM_Var.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_MM_Var malloc(MemoryStack stack) {
        return new FT_MM_Var(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_MM_Var calloc(MemoryStack stack) {
        return new FT_MM_Var(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    public static int nnum_namedstyles(long struct) {
        return UNSAFE.getInt(null, struct + (long)NUM_NAMEDSTYLES);
    }

    public static FT_Var_Axis.Buffer naxis(long struct) {
        return FT_Var_Axis.create(MemoryUtil.memGetAddress(struct + (long)AXIS), FT_MM_Var.nnum_axis(struct));
    }

    public static FT_Var_Named_Style.Buffer nnamedstyle(long struct) {
        return FT_Var_Named_Style.create(MemoryUtil.memGetAddress(struct + (long)NAMEDSTYLE), FT_MM_Var.nnum_namedstyles(struct));
    }

    static {
        Struct.Layout layout = FT_MM_Var.__struct(FT_MM_Var.__member(4), FT_MM_Var.__member(4), FT_MM_Var.__member(4), FT_MM_Var.__member(POINTER_SIZE), FT_MM_Var.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        NUM_AXIS = layout.offsetof(0);
        NUM_DESIGNS = layout.offsetof(1);
        NUM_NAMEDSTYLES = layout.offsetof(2);
        AXIS = layout.offsetof(3);
        NAMEDSTYLE = layout.offsetof(4);
    }

    public static class Buffer
    extends StructBuffer<FT_MM_Var, Buffer>
    implements NativeResource {
        private static final FT_MM_Var ELEMENT_FACTORY = FT_MM_Var.create(-1L);

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
        protected FT_MM_Var getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_UInt")
        public int num_axis() {
            return FT_MM_Var.nnum_axis(this.address());
        }

        @NativeType(value="FT_UInt")
        public int num_designs() {
            return FT_MM_Var.nnum_designs(this.address());
        }

        @NativeType(value="FT_UInt")
        public int num_namedstyles() {
            return FT_MM_Var.nnum_namedstyles(this.address());
        }

        @NativeType(value="FT_Var_Axis *")
        public FT_Var_Axis.Buffer axis() {
            return FT_MM_Var.naxis(this.address());
        }

        @NativeType(value="FT_Var_Named_Style *")
        public FT_Var_Named_Style.Buffer namedstyle() {
            return FT_MM_Var.nnamedstyle(this.address());
        }
    }
}
