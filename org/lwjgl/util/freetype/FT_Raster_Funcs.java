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
import org.lwjgl.util.freetype.FT_Raster_DoneFunc;
import org.lwjgl.util.freetype.FT_Raster_DoneFuncI;
import org.lwjgl.util.freetype.FT_Raster_NewFunc;
import org.lwjgl.util.freetype.FT_Raster_NewFuncI;
import org.lwjgl.util.freetype.FT_Raster_RenderFunc;
import org.lwjgl.util.freetype.FT_Raster_RenderFuncI;
import org.lwjgl.util.freetype.FT_Raster_ResetFunc;
import org.lwjgl.util.freetype.FT_Raster_ResetFuncI;
import org.lwjgl.util.freetype.FT_Raster_SetModeFunc;
import org.lwjgl.util.freetype.FT_Raster_SetModeFuncI;

public class FT_Raster_Funcs
extends Struct<FT_Raster_Funcs>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int GLYPH_FORMAT;
    public static final int RASTER_NEW;
    public static final int RASTER_RESET;
    public static final int RASTER_SET_MODE;
    public static final int RASTER_RENDER;
    public static final int RASTER_DONE;

    protected FT_Raster_Funcs(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Raster_Funcs create(long address, @Nullable ByteBuffer container) {
        return new FT_Raster_Funcs(address, container);
    }

    public FT_Raster_Funcs(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Raster_Funcs.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Glyph_Format")
    public int glyph_format() {
        return FT_Raster_Funcs.nglyph_format(this.address());
    }

    @Nullable
    public FT_Raster_NewFunc raster_new() {
        return FT_Raster_Funcs.nraster_new(this.address());
    }

    @Nullable
    public FT_Raster_ResetFunc raster_reset() {
        return FT_Raster_Funcs.nraster_reset(this.address());
    }

    @Nullable
    public FT_Raster_SetModeFunc raster_set_mode() {
        return FT_Raster_Funcs.nraster_set_mode(this.address());
    }

    @Nullable
    public FT_Raster_RenderFunc raster_render() {
        return FT_Raster_Funcs.nraster_render(this.address());
    }

    @Nullable
    public FT_Raster_DoneFunc raster_done() {
        return FT_Raster_Funcs.nraster_done(this.address());
    }

    public FT_Raster_Funcs glyph_format(@NativeType(value="FT_Glyph_Format") int value) {
        FT_Raster_Funcs.nglyph_format(this.address(), value);
        return this;
    }

    public FT_Raster_Funcs raster_new(@Nullable @NativeType(value="FT_Raster_NewFunc") FT_Raster_NewFuncI value) {
        FT_Raster_Funcs.nraster_new(this.address(), value);
        return this;
    }

    public FT_Raster_Funcs raster_reset(@Nullable @NativeType(value="FT_Raster_ResetFunc") FT_Raster_ResetFuncI value) {
        FT_Raster_Funcs.nraster_reset(this.address(), value);
        return this;
    }

    public FT_Raster_Funcs raster_set_mode(@Nullable @NativeType(value="FT_Raster_SetModeFunc") FT_Raster_SetModeFuncI value) {
        FT_Raster_Funcs.nraster_set_mode(this.address(), value);
        return this;
    }

    public FT_Raster_Funcs raster_render(@Nullable @NativeType(value="FT_Raster_RenderFunc") FT_Raster_RenderFuncI value) {
        FT_Raster_Funcs.nraster_render(this.address(), value);
        return this;
    }

    public FT_Raster_Funcs raster_done(@Nullable @NativeType(value="FT_Raster_DoneFunc") FT_Raster_DoneFuncI value) {
        FT_Raster_Funcs.nraster_done(this.address(), value);
        return this;
    }

    public FT_Raster_Funcs set(int glyph_format, FT_Raster_NewFuncI raster_new, FT_Raster_ResetFuncI raster_reset, FT_Raster_SetModeFuncI raster_set_mode, FT_Raster_RenderFuncI raster_render, FT_Raster_DoneFuncI raster_done) {
        this.glyph_format(glyph_format);
        this.raster_new(raster_new);
        this.raster_reset(raster_reset);
        this.raster_set_mode(raster_set_mode);
        this.raster_render(raster_render);
        this.raster_done(raster_done);
        return this;
    }

    public FT_Raster_Funcs set(FT_Raster_Funcs src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Raster_Funcs malloc() {
        return new FT_Raster_Funcs(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Raster_Funcs calloc() {
        return new FT_Raster_Funcs(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Raster_Funcs create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Raster_Funcs(MemoryUtil.memAddress(container), container);
    }

    public static FT_Raster_Funcs create(long address) {
        return new FT_Raster_Funcs(address, null);
    }

    @Nullable
    public static FT_Raster_Funcs createSafe(long address) {
        return address == 0L ? null : new FT_Raster_Funcs(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Raster_Funcs.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Raster_Funcs.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Raster_Funcs malloc(MemoryStack stack) {
        return new FT_Raster_Funcs(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Raster_Funcs calloc(MemoryStack stack) {
        return new FT_Raster_Funcs(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nglyph_format(long struct) {
        return UNSAFE.getInt(null, struct + (long)GLYPH_FORMAT);
    }

    @Nullable
    public static FT_Raster_NewFunc nraster_new(long struct) {
        return FT_Raster_NewFunc.createSafe(MemoryUtil.memGetAddress(struct + (long)RASTER_NEW));
    }

    @Nullable
    public static FT_Raster_ResetFunc nraster_reset(long struct) {
        return FT_Raster_ResetFunc.createSafe(MemoryUtil.memGetAddress(struct + (long)RASTER_RESET));
    }

    @Nullable
    public static FT_Raster_SetModeFunc nraster_set_mode(long struct) {
        return FT_Raster_SetModeFunc.createSafe(MemoryUtil.memGetAddress(struct + (long)RASTER_SET_MODE));
    }

    @Nullable
    public static FT_Raster_RenderFunc nraster_render(long struct) {
        return FT_Raster_RenderFunc.createSafe(MemoryUtil.memGetAddress(struct + (long)RASTER_RENDER));
    }

    @Nullable
    public static FT_Raster_DoneFunc nraster_done(long struct) {
        return FT_Raster_DoneFunc.createSafe(MemoryUtil.memGetAddress(struct + (long)RASTER_DONE));
    }

    public static void nglyph_format(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)GLYPH_FORMAT, value);
    }

    public static void nraster_new(long struct, @Nullable FT_Raster_NewFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)RASTER_NEW, MemoryUtil.memAddressSafe(value));
    }

    public static void nraster_reset(long struct, @Nullable FT_Raster_ResetFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)RASTER_RESET, MemoryUtil.memAddressSafe(value));
    }

    public static void nraster_set_mode(long struct, @Nullable FT_Raster_SetModeFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)RASTER_SET_MODE, MemoryUtil.memAddressSafe(value));
    }

    public static void nraster_render(long struct, @Nullable FT_Raster_RenderFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)RASTER_RENDER, MemoryUtil.memAddressSafe(value));
    }

    public static void nraster_done(long struct, @Nullable FT_Raster_DoneFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)RASTER_DONE, MemoryUtil.memAddressSafe(value));
    }

    static {
        Struct.Layout layout = FT_Raster_Funcs.__struct(FT_Raster_Funcs.__member(4), FT_Raster_Funcs.__member(POINTER_SIZE), FT_Raster_Funcs.__member(POINTER_SIZE), FT_Raster_Funcs.__member(POINTER_SIZE), FT_Raster_Funcs.__member(POINTER_SIZE), FT_Raster_Funcs.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        GLYPH_FORMAT = layout.offsetof(0);
        RASTER_NEW = layout.offsetof(1);
        RASTER_RESET = layout.offsetof(2);
        RASTER_SET_MODE = layout.offsetof(3);
        RASTER_RENDER = layout.offsetof(4);
        RASTER_DONE = layout.offsetof(5);
    }

    public static class Buffer
    extends StructBuffer<FT_Raster_Funcs, Buffer>
    implements NativeResource {
        private static final FT_Raster_Funcs ELEMENT_FACTORY = FT_Raster_Funcs.create(-1L);

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
        protected FT_Raster_Funcs getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Glyph_Format")
        public int glyph_format() {
            return FT_Raster_Funcs.nglyph_format(this.address());
        }

        @Nullable
        public FT_Raster_NewFunc raster_new() {
            return FT_Raster_Funcs.nraster_new(this.address());
        }

        @Nullable
        public FT_Raster_ResetFunc raster_reset() {
            return FT_Raster_Funcs.nraster_reset(this.address());
        }

        @Nullable
        public FT_Raster_SetModeFunc raster_set_mode() {
            return FT_Raster_Funcs.nraster_set_mode(this.address());
        }

        @Nullable
        public FT_Raster_RenderFunc raster_render() {
            return FT_Raster_Funcs.nraster_render(this.address());
        }

        @Nullable
        public FT_Raster_DoneFunc raster_done() {
            return FT_Raster_Funcs.nraster_done(this.address());
        }

        public Buffer glyph_format(@NativeType(value="FT_Glyph_Format") int value) {
            FT_Raster_Funcs.nglyph_format(this.address(), value);
            return this;
        }

        public Buffer raster_new(@Nullable @NativeType(value="FT_Raster_NewFunc") FT_Raster_NewFuncI value) {
            FT_Raster_Funcs.nraster_new(this.address(), value);
            return this;
        }

        public Buffer raster_reset(@Nullable @NativeType(value="FT_Raster_ResetFunc") FT_Raster_ResetFuncI value) {
            FT_Raster_Funcs.nraster_reset(this.address(), value);
            return this;
        }

        public Buffer raster_set_mode(@Nullable @NativeType(value="FT_Raster_SetModeFunc") FT_Raster_SetModeFuncI value) {
            FT_Raster_Funcs.nraster_set_mode(this.address(), value);
            return this;
        }

        public Buffer raster_render(@Nullable @NativeType(value="FT_Raster_RenderFunc") FT_Raster_RenderFuncI value) {
            FT_Raster_Funcs.nraster_render(this.address(), value);
            return this;
        }

        public Buffer raster_done(@Nullable @NativeType(value="FT_Raster_DoneFunc") FT_Raster_DoneFuncI value) {
            FT_Raster_Funcs.nraster_done(this.address(), value);
            return this;
        }
    }
}
