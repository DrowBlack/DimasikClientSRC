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
import org.lwjgl.util.freetype.FT_Outline_ConicToFunc;
import org.lwjgl.util.freetype.FT_Outline_ConicToFuncI;
import org.lwjgl.util.freetype.FT_Outline_CubicToFunc;
import org.lwjgl.util.freetype.FT_Outline_CubicToFuncI;
import org.lwjgl.util.freetype.FT_Outline_LineToFunc;
import org.lwjgl.util.freetype.FT_Outline_LineToFuncI;
import org.lwjgl.util.freetype.FT_Outline_MoveToFunc;
import org.lwjgl.util.freetype.FT_Outline_MoveToFuncI;

public class FT_Outline_Funcs
extends Struct<FT_Outline_Funcs>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int MOVE_TO;
    public static final int LINE_TO;
    public static final int CONIC_TO;
    public static final int CUBIC_TO;
    public static final int SHIFT;
    public static final int DELTA;

    protected FT_Outline_Funcs(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Outline_Funcs create(long address, @Nullable ByteBuffer container) {
        return new FT_Outline_Funcs(address, container);
    }

    public FT_Outline_Funcs(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Outline_Funcs.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @Nullable
    public FT_Outline_MoveToFunc move_to() {
        return FT_Outline_Funcs.nmove_to(this.address());
    }

    @Nullable
    public FT_Outline_LineToFunc line_to() {
        return FT_Outline_Funcs.nline_to(this.address());
    }

    @Nullable
    public FT_Outline_ConicToFunc conic_to() {
        return FT_Outline_Funcs.nconic_to(this.address());
    }

    @Nullable
    public FT_Outline_CubicToFunc cubic_to() {
        return FT_Outline_Funcs.ncubic_to(this.address());
    }

    public int shift() {
        return FT_Outline_Funcs.nshift(this.address());
    }

    @NativeType(value="FT_Pos")
    public long delta() {
        return FT_Outline_Funcs.ndelta(this.address());
    }

    public FT_Outline_Funcs move_to(@Nullable @NativeType(value="FT_Outline_MoveToFunc") FT_Outline_MoveToFuncI value) {
        FT_Outline_Funcs.nmove_to(this.address(), value);
        return this;
    }

    public FT_Outline_Funcs line_to(@Nullable @NativeType(value="FT_Outline_LineToFunc") FT_Outline_LineToFuncI value) {
        FT_Outline_Funcs.nline_to(this.address(), value);
        return this;
    }

    public FT_Outline_Funcs conic_to(@Nullable @NativeType(value="FT_Outline_ConicToFunc") FT_Outline_ConicToFuncI value) {
        FT_Outline_Funcs.nconic_to(this.address(), value);
        return this;
    }

    public FT_Outline_Funcs cubic_to(@Nullable @NativeType(value="FT_Outline_CubicToFunc") FT_Outline_CubicToFuncI value) {
        FT_Outline_Funcs.ncubic_to(this.address(), value);
        return this;
    }

    public FT_Outline_Funcs shift(int value) {
        FT_Outline_Funcs.nshift(this.address(), value);
        return this;
    }

    public FT_Outline_Funcs delta(@NativeType(value="FT_Pos") long value) {
        FT_Outline_Funcs.ndelta(this.address(), value);
        return this;
    }

    public FT_Outline_Funcs set(FT_Outline_MoveToFuncI move_to, FT_Outline_LineToFuncI line_to, FT_Outline_ConicToFuncI conic_to, FT_Outline_CubicToFuncI cubic_to, int shift, long delta) {
        this.move_to(move_to);
        this.line_to(line_to);
        this.conic_to(conic_to);
        this.cubic_to(cubic_to);
        this.shift(shift);
        this.delta(delta);
        return this;
    }

    public FT_Outline_Funcs set(FT_Outline_Funcs src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Outline_Funcs malloc() {
        return new FT_Outline_Funcs(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Outline_Funcs calloc() {
        return new FT_Outline_Funcs(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Outline_Funcs create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Outline_Funcs(MemoryUtil.memAddress(container), container);
    }

    public static FT_Outline_Funcs create(long address) {
        return new FT_Outline_Funcs(address, null);
    }

    @Nullable
    public static FT_Outline_Funcs createSafe(long address) {
        return address == 0L ? null : new FT_Outline_Funcs(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Outline_Funcs.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Outline_Funcs.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Outline_Funcs malloc(MemoryStack stack) {
        return new FT_Outline_Funcs(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Outline_Funcs calloc(MemoryStack stack) {
        return new FT_Outline_Funcs(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    @Nullable
    public static FT_Outline_MoveToFunc nmove_to(long struct) {
        return FT_Outline_MoveToFunc.createSafe(MemoryUtil.memGetAddress(struct + (long)MOVE_TO));
    }

    @Nullable
    public static FT_Outline_LineToFunc nline_to(long struct) {
        return FT_Outline_LineToFunc.createSafe(MemoryUtil.memGetAddress(struct + (long)LINE_TO));
    }

    @Nullable
    public static FT_Outline_ConicToFunc nconic_to(long struct) {
        return FT_Outline_ConicToFunc.createSafe(MemoryUtil.memGetAddress(struct + (long)CONIC_TO));
    }

    @Nullable
    public static FT_Outline_CubicToFunc ncubic_to(long struct) {
        return FT_Outline_CubicToFunc.createSafe(MemoryUtil.memGetAddress(struct + (long)CUBIC_TO));
    }

    public static int nshift(long struct) {
        return UNSAFE.getInt(null, struct + (long)SHIFT);
    }

    public static long ndelta(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)DELTA);
    }

    public static void nmove_to(long struct, @Nullable FT_Outline_MoveToFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)MOVE_TO, MemoryUtil.memAddressSafe(value));
    }

    public static void nline_to(long struct, @Nullable FT_Outline_LineToFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)LINE_TO, MemoryUtil.memAddressSafe(value));
    }

    public static void nconic_to(long struct, @Nullable FT_Outline_ConicToFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)CONIC_TO, MemoryUtil.memAddressSafe(value));
    }

    public static void ncubic_to(long struct, @Nullable FT_Outline_CubicToFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)CUBIC_TO, MemoryUtil.memAddressSafe(value));
    }

    public static void nshift(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)SHIFT, value);
    }

    public static void ndelta(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)DELTA, value);
    }

    static {
        Struct.Layout layout = FT_Outline_Funcs.__struct(FT_Outline_Funcs.__member(POINTER_SIZE), FT_Outline_Funcs.__member(POINTER_SIZE), FT_Outline_Funcs.__member(POINTER_SIZE), FT_Outline_Funcs.__member(POINTER_SIZE), FT_Outline_Funcs.__member(4), FT_Outline_Funcs.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        MOVE_TO = layout.offsetof(0);
        LINE_TO = layout.offsetof(1);
        CONIC_TO = layout.offsetof(2);
        CUBIC_TO = layout.offsetof(3);
        SHIFT = layout.offsetof(4);
        DELTA = layout.offsetof(5);
    }

    public static class Buffer
    extends StructBuffer<FT_Outline_Funcs, Buffer>
    implements NativeResource {
        private static final FT_Outline_Funcs ELEMENT_FACTORY = FT_Outline_Funcs.create(-1L);

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
        protected FT_Outline_Funcs getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @Nullable
        public FT_Outline_MoveToFunc move_to() {
            return FT_Outline_Funcs.nmove_to(this.address());
        }

        @Nullable
        public FT_Outline_LineToFunc line_to() {
            return FT_Outline_Funcs.nline_to(this.address());
        }

        @Nullable
        public FT_Outline_ConicToFunc conic_to() {
            return FT_Outline_Funcs.nconic_to(this.address());
        }

        @Nullable
        public FT_Outline_CubicToFunc cubic_to() {
            return FT_Outline_Funcs.ncubic_to(this.address());
        }

        public int shift() {
            return FT_Outline_Funcs.nshift(this.address());
        }

        @NativeType(value="FT_Pos")
        public long delta() {
            return FT_Outline_Funcs.ndelta(this.address());
        }

        public Buffer move_to(@Nullable @NativeType(value="FT_Outline_MoveToFunc") FT_Outline_MoveToFuncI value) {
            FT_Outline_Funcs.nmove_to(this.address(), value);
            return this;
        }

        public Buffer line_to(@Nullable @NativeType(value="FT_Outline_LineToFunc") FT_Outline_LineToFuncI value) {
            FT_Outline_Funcs.nline_to(this.address(), value);
            return this;
        }

        public Buffer conic_to(@Nullable @NativeType(value="FT_Outline_ConicToFunc") FT_Outline_ConicToFuncI value) {
            FT_Outline_Funcs.nconic_to(this.address(), value);
            return this;
        }

        public Buffer cubic_to(@Nullable @NativeType(value="FT_Outline_CubicToFunc") FT_Outline_CubicToFuncI value) {
            FT_Outline_Funcs.ncubic_to(this.address(), value);
            return this;
        }

        public Buffer shift(int value) {
            FT_Outline_Funcs.nshift(this.address(), value);
            return this;
        }

        public Buffer delta(@NativeType(value="FT_Pos") long value) {
            FT_Outline_Funcs.ndelta(this.address(), value);
            return this;
        }
    }
}
