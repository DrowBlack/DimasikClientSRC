package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_Affine23;
import org.lwjgl.util.freetype.FT_OpaquePaint;

public class FT_PaintTransform
extends Struct<FT_PaintTransform> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int PAINT;
    public static final int AFFINE;

    protected FT_PaintTransform(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_PaintTransform create(long address, @Nullable ByteBuffer container) {
        return new FT_PaintTransform(address, container);
    }

    public FT_PaintTransform(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_PaintTransform.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_OpaquePaintRec")
    public FT_OpaquePaint paint() {
        return FT_PaintTransform.npaint(this.address());
    }

    public FT_Affine23 affine() {
        return FT_PaintTransform.naffine(this.address());
    }

    public static FT_PaintTransform create(long address) {
        return new FT_PaintTransform(address, null);
    }

    @Nullable
    public static FT_PaintTransform createSafe(long address) {
        return address == 0L ? null : new FT_PaintTransform(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_OpaquePaint npaint(long struct) {
        return FT_OpaquePaint.create(struct + (long)PAINT);
    }

    public static FT_Affine23 naffine(long struct) {
        return FT_Affine23.create(struct + (long)AFFINE);
    }

    static {
        Struct.Layout layout = FT_PaintTransform.__struct(FT_PaintTransform.__member(FT_OpaquePaint.SIZEOF, FT_OpaquePaint.ALIGNOF), FT_PaintTransform.__member(FT_Affine23.SIZEOF, FT_Affine23.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        PAINT = layout.offsetof(0);
        AFFINE = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_PaintTransform, Buffer> {
        private static final FT_PaintTransform ELEMENT_FACTORY = FT_PaintTransform.create(-1L);

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
        protected FT_PaintTransform getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_OpaquePaintRec")
        public FT_OpaquePaint paint() {
            return FT_PaintTransform.npaint(this.address());
        }

        public FT_Affine23 affine() {
            return FT_PaintTransform.naffine(this.address());
        }
    }
}
