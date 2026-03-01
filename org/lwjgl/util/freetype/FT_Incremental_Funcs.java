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
import org.lwjgl.util.freetype.FT_Incremental_FreeGlyphDataFunc;
import org.lwjgl.util.freetype.FT_Incremental_FreeGlyphDataFuncI;
import org.lwjgl.util.freetype.FT_Incremental_GetGlyphDataFunc;
import org.lwjgl.util.freetype.FT_Incremental_GetGlyphDataFuncI;
import org.lwjgl.util.freetype.FT_Incremental_GetGlyphMetricsFunc;
import org.lwjgl.util.freetype.FT_Incremental_GetGlyphMetricsFuncI;

@NativeType(value="struct FT_Incremental_FuncsRec")
public class FT_Incremental_Funcs
extends Struct<FT_Incremental_Funcs>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int GET_GLYPH_DATA;
    public static final int FREE_GLYPH_DATA;
    public static final int GET_GLYPH_METRICS;

    protected FT_Incremental_Funcs(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Incremental_Funcs create(long address, @Nullable ByteBuffer container) {
        return new FT_Incremental_Funcs(address, container);
    }

    public FT_Incremental_Funcs(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Incremental_Funcs.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public FT_Incremental_GetGlyphDataFunc get_glyph_data() {
        return FT_Incremental_Funcs.nget_glyph_data(this.address());
    }

    public FT_Incremental_FreeGlyphDataFunc free_glyph_data() {
        return FT_Incremental_Funcs.nfree_glyph_data(this.address());
    }

    @Nullable
    public FT_Incremental_GetGlyphMetricsFunc get_glyph_metrics() {
        return FT_Incremental_Funcs.nget_glyph_metrics(this.address());
    }

    public FT_Incremental_Funcs get_glyph_data(@NativeType(value="FT_Incremental_GetGlyphDataFunc") FT_Incremental_GetGlyphDataFuncI value) {
        FT_Incremental_Funcs.nget_glyph_data(this.address(), value);
        return this;
    }

    public FT_Incremental_Funcs free_glyph_data(@NativeType(value="FT_Incremental_FreeGlyphDataFunc") FT_Incremental_FreeGlyphDataFuncI value) {
        FT_Incremental_Funcs.nfree_glyph_data(this.address(), value);
        return this;
    }

    public FT_Incremental_Funcs get_glyph_metrics(@Nullable @NativeType(value="FT_Incremental_GetGlyphMetricsFunc") FT_Incremental_GetGlyphMetricsFuncI value) {
        FT_Incremental_Funcs.nget_glyph_metrics(this.address(), value);
        return this;
    }

    public FT_Incremental_Funcs set(FT_Incremental_GetGlyphDataFuncI get_glyph_data, FT_Incremental_FreeGlyphDataFuncI free_glyph_data, FT_Incremental_GetGlyphMetricsFuncI get_glyph_metrics) {
        this.get_glyph_data(get_glyph_data);
        this.free_glyph_data(free_glyph_data);
        this.get_glyph_metrics(get_glyph_metrics);
        return this;
    }

    public FT_Incremental_Funcs set(FT_Incremental_Funcs src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Incremental_Funcs malloc() {
        return new FT_Incremental_Funcs(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Incremental_Funcs calloc() {
        return new FT_Incremental_Funcs(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Incremental_Funcs create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Incremental_Funcs(MemoryUtil.memAddress(container), container);
    }

    public static FT_Incremental_Funcs create(long address) {
        return new FT_Incremental_Funcs(address, null);
    }

    @Nullable
    public static FT_Incremental_Funcs createSafe(long address) {
        return address == 0L ? null : new FT_Incremental_Funcs(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Incremental_Funcs.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Incremental_Funcs.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Incremental_Funcs malloc(MemoryStack stack) {
        return new FT_Incremental_Funcs(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Incremental_Funcs calloc(MemoryStack stack) {
        return new FT_Incremental_Funcs(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static FT_Incremental_GetGlyphDataFunc nget_glyph_data(long struct) {
        return FT_Incremental_GetGlyphDataFunc.create(MemoryUtil.memGetAddress(struct + (long)GET_GLYPH_DATA));
    }

    public static FT_Incremental_FreeGlyphDataFunc nfree_glyph_data(long struct) {
        return FT_Incremental_FreeGlyphDataFunc.create(MemoryUtil.memGetAddress(struct + (long)FREE_GLYPH_DATA));
    }

    @Nullable
    public static FT_Incremental_GetGlyphMetricsFunc nget_glyph_metrics(long struct) {
        return FT_Incremental_GetGlyphMetricsFunc.createSafe(MemoryUtil.memGetAddress(struct + (long)GET_GLYPH_METRICS));
    }

    public static void nget_glyph_data(long struct, FT_Incremental_GetGlyphDataFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)GET_GLYPH_DATA, value.address());
    }

    public static void nfree_glyph_data(long struct, FT_Incremental_FreeGlyphDataFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)FREE_GLYPH_DATA, value.address());
    }

    public static void nget_glyph_metrics(long struct, @Nullable FT_Incremental_GetGlyphMetricsFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)GET_GLYPH_METRICS, MemoryUtil.memAddressSafe(value));
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)GET_GLYPH_DATA));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)FREE_GLYPH_DATA));
    }

    static {
        Struct.Layout layout = FT_Incremental_Funcs.__struct(FT_Incremental_Funcs.__member(POINTER_SIZE), FT_Incremental_Funcs.__member(POINTER_SIZE), FT_Incremental_Funcs.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        GET_GLYPH_DATA = layout.offsetof(0);
        FREE_GLYPH_DATA = layout.offsetof(1);
        GET_GLYPH_METRICS = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<FT_Incremental_Funcs, Buffer>
    implements NativeResource {
        private static final FT_Incremental_Funcs ELEMENT_FACTORY = FT_Incremental_Funcs.create(-1L);

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
        protected FT_Incremental_Funcs getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public FT_Incremental_GetGlyphDataFunc get_glyph_data() {
            return FT_Incremental_Funcs.nget_glyph_data(this.address());
        }

        public FT_Incremental_FreeGlyphDataFunc free_glyph_data() {
            return FT_Incremental_Funcs.nfree_glyph_data(this.address());
        }

        @Nullable
        public FT_Incremental_GetGlyphMetricsFunc get_glyph_metrics() {
            return FT_Incremental_Funcs.nget_glyph_metrics(this.address());
        }

        public Buffer get_glyph_data(@NativeType(value="FT_Incremental_GetGlyphDataFunc") FT_Incremental_GetGlyphDataFuncI value) {
            FT_Incremental_Funcs.nget_glyph_data(this.address(), value);
            return this;
        }

        public Buffer free_glyph_data(@NativeType(value="FT_Incremental_FreeGlyphDataFunc") FT_Incremental_FreeGlyphDataFuncI value) {
            FT_Incremental_Funcs.nfree_glyph_data(this.address(), value);
            return this;
        }

        public Buffer get_glyph_metrics(@Nullable @NativeType(value="FT_Incremental_GetGlyphMetricsFunc") FT_Incremental_GetGlyphMetricsFuncI value) {
            FT_Incremental_Funcs.nget_glyph_metrics(this.address(), value);
            return this;
        }
    }
}
