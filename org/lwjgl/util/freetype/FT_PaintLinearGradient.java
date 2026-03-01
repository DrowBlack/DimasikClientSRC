package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_ColorLine;
import org.lwjgl.util.freetype.FT_Vector;

public class FT_PaintLinearGradient
extends Struct<FT_PaintLinearGradient> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int COLORLINE;
    public static final int P0;
    public static final int P1;
    public static final int P2;

    protected FT_PaintLinearGradient(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_PaintLinearGradient create(long address, @Nullable ByteBuffer container) {
        return new FT_PaintLinearGradient(address, container);
    }

    public FT_PaintLinearGradient(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_PaintLinearGradient.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public FT_ColorLine colorline() {
        return FT_PaintLinearGradient.ncolorline(this.address());
    }

    public FT_Vector p0() {
        return FT_PaintLinearGradient.np0(this.address());
    }

    public FT_Vector p1() {
        return FT_PaintLinearGradient.np1(this.address());
    }

    public FT_Vector p2() {
        return FT_PaintLinearGradient.np2(this.address());
    }

    public static FT_PaintLinearGradient create(long address) {
        return new FT_PaintLinearGradient(address, null);
    }

    @Nullable
    public static FT_PaintLinearGradient createSafe(long address) {
        return address == 0L ? null : new FT_PaintLinearGradient(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_ColorLine ncolorline(long struct) {
        return FT_ColorLine.create(struct + (long)COLORLINE);
    }

    public static FT_Vector np0(long struct) {
        return FT_Vector.create(struct + (long)P0);
    }

    public static FT_Vector np1(long struct) {
        return FT_Vector.create(struct + (long)P1);
    }

    public static FT_Vector np2(long struct) {
        return FT_Vector.create(struct + (long)P2);
    }

    static {
        Struct.Layout layout = FT_PaintLinearGradient.__struct(FT_PaintLinearGradient.__member(FT_ColorLine.SIZEOF, FT_ColorLine.ALIGNOF), FT_PaintLinearGradient.__member(FT_Vector.SIZEOF, FT_Vector.ALIGNOF), FT_PaintLinearGradient.__member(FT_Vector.SIZEOF, FT_Vector.ALIGNOF), FT_PaintLinearGradient.__member(FT_Vector.SIZEOF, FT_Vector.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        COLORLINE = layout.offsetof(0);
        P0 = layout.offsetof(1);
        P1 = layout.offsetof(2);
        P2 = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FT_PaintLinearGradient, Buffer> {
        private static final FT_PaintLinearGradient ELEMENT_FACTORY = FT_PaintLinearGradient.create(-1L);

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
        protected FT_PaintLinearGradient getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public FT_ColorLine colorline() {
            return FT_PaintLinearGradient.ncolorline(this.address());
        }

        public FT_Vector p0() {
            return FT_PaintLinearGradient.np0(this.address());
        }

        public FT_Vector p1() {
            return FT_PaintLinearGradient.np1(this.address());
        }

        public FT_Vector p2() {
            return FT_PaintLinearGradient.np2(this.address());
        }
    }
}
