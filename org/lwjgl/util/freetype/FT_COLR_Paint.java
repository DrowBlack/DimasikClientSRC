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
import org.lwjgl.util.freetype.FT_PaintColrGlyph;
import org.lwjgl.util.freetype.FT_PaintColrLayers;
import org.lwjgl.util.freetype.FT_PaintComposite;
import org.lwjgl.util.freetype.FT_PaintGlyph;
import org.lwjgl.util.freetype.FT_PaintLinearGradient;
import org.lwjgl.util.freetype.FT_PaintRadialGradient;
import org.lwjgl.util.freetype.FT_PaintRotate;
import org.lwjgl.util.freetype.FT_PaintScale;
import org.lwjgl.util.freetype.FT_PaintSkew;
import org.lwjgl.util.freetype.FT_PaintSolid;
import org.lwjgl.util.freetype.FT_PaintSweepGradient;
import org.lwjgl.util.freetype.FT_PaintTransform;
import org.lwjgl.util.freetype.FT_PaintTranslate;

public class FT_COLR_Paint
extends Struct<FT_COLR_Paint>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int FORMAT;
    public static final int U;
    public static final int U_COLR_LAYERS;
    public static final int U_GLYPH;
    public static final int U_SOLID;
    public static final int U_LINEAR_GRADIENT;
    public static final int U_RADIAL_GRADIENT;
    public static final int U_SWEEP_GRADIENT;
    public static final int U_TRANSFORM;
    public static final int U_TRANSLATE;
    public static final int U_SCALE;
    public static final int U_ROTATE;
    public static final int U_SKEW;
    public static final int U_COMPOSITE;
    public static final int U_COLR_GLYPH;

    protected FT_COLR_Paint(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_COLR_Paint create(long address, @Nullable ByteBuffer container) {
        return new FT_COLR_Paint(address, container);
    }

    public FT_COLR_Paint(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_COLR_Paint.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_PaintFormat")
    public int format() {
        return FT_COLR_Paint.nformat(this.address());
    }

    public FT_PaintColrLayers u_colr_layers() {
        return FT_COLR_Paint.nu_colr_layers(this.address());
    }

    public FT_PaintGlyph u_glyph() {
        return FT_COLR_Paint.nu_glyph(this.address());
    }

    public FT_PaintSolid u_solid() {
        return FT_COLR_Paint.nu_solid(this.address());
    }

    public FT_PaintLinearGradient u_linear_gradient() {
        return FT_COLR_Paint.nu_linear_gradient(this.address());
    }

    public FT_PaintRadialGradient u_radial_gradient() {
        return FT_COLR_Paint.nu_radial_gradient(this.address());
    }

    public FT_PaintSweepGradient u_sweep_gradient() {
        return FT_COLR_Paint.nu_sweep_gradient(this.address());
    }

    public FT_PaintTransform u_transform() {
        return FT_COLR_Paint.nu_transform(this.address());
    }

    public FT_PaintTranslate u_translate() {
        return FT_COLR_Paint.nu_translate(this.address());
    }

    public FT_PaintScale u_scale() {
        return FT_COLR_Paint.nu_scale(this.address());
    }

    public FT_PaintRotate u_rotate() {
        return FT_COLR_Paint.nu_rotate(this.address());
    }

    public FT_PaintSkew u_skew() {
        return FT_COLR_Paint.nu_skew(this.address());
    }

    public FT_PaintComposite u_composite() {
        return FT_COLR_Paint.nu_composite(this.address());
    }

    public FT_PaintColrGlyph u_colr_glyph() {
        return FT_COLR_Paint.nu_colr_glyph(this.address());
    }

    public static FT_COLR_Paint malloc() {
        return new FT_COLR_Paint(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_COLR_Paint calloc() {
        return new FT_COLR_Paint(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_COLR_Paint create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_COLR_Paint(MemoryUtil.memAddress(container), container);
    }

    public static FT_COLR_Paint create(long address) {
        return new FT_COLR_Paint(address, null);
    }

    @Nullable
    public static FT_COLR_Paint createSafe(long address) {
        return address == 0L ? null : new FT_COLR_Paint(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_COLR_Paint.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_COLR_Paint.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_COLR_Paint malloc(MemoryStack stack) {
        return new FT_COLR_Paint(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_COLR_Paint calloc(MemoryStack stack) {
        return new FT_COLR_Paint(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nformat(long struct) {
        return UNSAFE.getInt(null, struct + (long)FORMAT);
    }

    public static FT_PaintColrLayers nu_colr_layers(long struct) {
        return FT_PaintColrLayers.create(struct + (long)U_COLR_LAYERS);
    }

    public static FT_PaintGlyph nu_glyph(long struct) {
        return FT_PaintGlyph.create(struct + (long)U_GLYPH);
    }

    public static FT_PaintSolid nu_solid(long struct) {
        return FT_PaintSolid.create(struct + (long)U_SOLID);
    }

    public static FT_PaintLinearGradient nu_linear_gradient(long struct) {
        return FT_PaintLinearGradient.create(struct + (long)U_LINEAR_GRADIENT);
    }

    public static FT_PaintRadialGradient nu_radial_gradient(long struct) {
        return FT_PaintRadialGradient.create(struct + (long)U_RADIAL_GRADIENT);
    }

    public static FT_PaintSweepGradient nu_sweep_gradient(long struct) {
        return FT_PaintSweepGradient.create(struct + (long)U_SWEEP_GRADIENT);
    }

    public static FT_PaintTransform nu_transform(long struct) {
        return FT_PaintTransform.create(struct + (long)U_TRANSFORM);
    }

    public static FT_PaintTranslate nu_translate(long struct) {
        return FT_PaintTranslate.create(struct + (long)U_TRANSLATE);
    }

    public static FT_PaintScale nu_scale(long struct) {
        return FT_PaintScale.create(struct + (long)U_SCALE);
    }

    public static FT_PaintRotate nu_rotate(long struct) {
        return FT_PaintRotate.create(struct + (long)U_ROTATE);
    }

    public static FT_PaintSkew nu_skew(long struct) {
        return FT_PaintSkew.create(struct + (long)U_SKEW);
    }

    public static FT_PaintComposite nu_composite(long struct) {
        return FT_PaintComposite.create(struct + (long)U_COMPOSITE);
    }

    public static FT_PaintColrGlyph nu_colr_glyph(long struct) {
        return FT_PaintColrGlyph.create(struct + (long)U_COLR_GLYPH);
    }

    static {
        Struct.Layout layout = FT_COLR_Paint.__struct(FT_COLR_Paint.__member(4), FT_COLR_Paint.__union(FT_COLR_Paint.__member(FT_PaintColrLayers.SIZEOF, FT_PaintColrLayers.ALIGNOF), FT_COLR_Paint.__member(FT_PaintGlyph.SIZEOF, FT_PaintGlyph.ALIGNOF), FT_COLR_Paint.__member(FT_PaintSolid.SIZEOF, FT_PaintSolid.ALIGNOF), FT_COLR_Paint.__member(FT_PaintLinearGradient.SIZEOF, FT_PaintLinearGradient.ALIGNOF), FT_COLR_Paint.__member(FT_PaintRadialGradient.SIZEOF, FT_PaintRadialGradient.ALIGNOF), FT_COLR_Paint.__member(FT_PaintSweepGradient.SIZEOF, FT_PaintSweepGradient.ALIGNOF), FT_COLR_Paint.__member(FT_PaintTransform.SIZEOF, FT_PaintTransform.ALIGNOF), FT_COLR_Paint.__member(FT_PaintTranslate.SIZEOF, FT_PaintTranslate.ALIGNOF), FT_COLR_Paint.__member(FT_PaintScale.SIZEOF, FT_PaintScale.ALIGNOF), FT_COLR_Paint.__member(FT_PaintRotate.SIZEOF, FT_PaintRotate.ALIGNOF), FT_COLR_Paint.__member(FT_PaintSkew.SIZEOF, FT_PaintSkew.ALIGNOF), FT_COLR_Paint.__member(FT_PaintComposite.SIZEOF, FT_PaintComposite.ALIGNOF), FT_COLR_Paint.__member(FT_PaintColrGlyph.SIZEOF, FT_PaintColrGlyph.ALIGNOF)));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        FORMAT = layout.offsetof(0);
        U = layout.offsetof(1);
        U_COLR_LAYERS = layout.offsetof(2);
        U_GLYPH = layout.offsetof(3);
        U_SOLID = layout.offsetof(4);
        U_LINEAR_GRADIENT = layout.offsetof(5);
        U_RADIAL_GRADIENT = layout.offsetof(6);
        U_SWEEP_GRADIENT = layout.offsetof(7);
        U_TRANSFORM = layout.offsetof(8);
        U_TRANSLATE = layout.offsetof(9);
        U_SCALE = layout.offsetof(10);
        U_ROTATE = layout.offsetof(11);
        U_SKEW = layout.offsetof(12);
        U_COMPOSITE = layout.offsetof(13);
        U_COLR_GLYPH = layout.offsetof(14);
    }

    public static class Buffer
    extends StructBuffer<FT_COLR_Paint, Buffer>
    implements NativeResource {
        private static final FT_COLR_Paint ELEMENT_FACTORY = FT_COLR_Paint.create(-1L);

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
        protected FT_COLR_Paint getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_PaintFormat")
        public int format() {
            return FT_COLR_Paint.nformat(this.address());
        }

        public FT_PaintColrLayers u_colr_layers() {
            return FT_COLR_Paint.nu_colr_layers(this.address());
        }

        public FT_PaintGlyph u_glyph() {
            return FT_COLR_Paint.nu_glyph(this.address());
        }

        public FT_PaintSolid u_solid() {
            return FT_COLR_Paint.nu_solid(this.address());
        }

        public FT_PaintLinearGradient u_linear_gradient() {
            return FT_COLR_Paint.nu_linear_gradient(this.address());
        }

        public FT_PaintRadialGradient u_radial_gradient() {
            return FT_COLR_Paint.nu_radial_gradient(this.address());
        }

        public FT_PaintSweepGradient u_sweep_gradient() {
            return FT_COLR_Paint.nu_sweep_gradient(this.address());
        }

        public FT_PaintTransform u_transform() {
            return FT_COLR_Paint.nu_transform(this.address());
        }

        public FT_PaintTranslate u_translate() {
            return FT_COLR_Paint.nu_translate(this.address());
        }

        public FT_PaintScale u_scale() {
            return FT_COLR_Paint.nu_scale(this.address());
        }

        public FT_PaintRotate u_rotate() {
            return FT_COLR_Paint.nu_rotate(this.address());
        }

        public FT_PaintSkew u_skew() {
            return FT_COLR_Paint.nu_skew(this.address());
        }

        public FT_PaintComposite u_composite() {
            return FT_COLR_Paint.nu_composite(this.address());
        }

        public FT_PaintColrGlyph u_colr_glyph() {
            return FT_COLR_Paint.nu_colr_glyph(this.address());
        }
    }
}
