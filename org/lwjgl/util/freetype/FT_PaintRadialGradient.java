package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_ColorLine;
import org.lwjgl.util.freetype.FT_Vector;

public class FT_PaintRadialGradient
extends Struct<FT_PaintRadialGradient> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int COLORLINE;
    public static final int C0;
    public static final int R0;
    public static final int C1;
    public static final int R1;

    protected FT_PaintRadialGradient(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_PaintRadialGradient create(long address, @Nullable ByteBuffer container) {
        return new FT_PaintRadialGradient(address, container);
    }

    public FT_PaintRadialGradient(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_PaintRadialGradient.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public FT_ColorLine colorline() {
        return FT_PaintRadialGradient.ncolorline(this.address());
    }

    public FT_Vector c0() {
        return FT_PaintRadialGradient.nc0(this.address());
    }

    @NativeType(value="FT_Pos")
    public long r0() {
        return FT_PaintRadialGradient.nr0(this.address());
    }

    public FT_Vector c1() {
        return FT_PaintRadialGradient.nc1(this.address());
    }

    @NativeType(value="FT_Pos")
    public long r1() {
        return FT_PaintRadialGradient.nr1(this.address());
    }

    public static FT_PaintRadialGradient create(long address) {
        return new FT_PaintRadialGradient(address, null);
    }

    @Nullable
    public static FT_PaintRadialGradient createSafe(long address) {
        return address == 0L ? null : new FT_PaintRadialGradient(address, null);
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

    public static FT_Vector nc0(long struct) {
        return FT_Vector.create(struct + (long)C0);
    }

    public static long nr0(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)R0);
    }

    public static FT_Vector nc1(long struct) {
        return FT_Vector.create(struct + (long)C1);
    }

    public static long nr1(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)R1);
    }

    static {
        Struct.Layout layout = FT_PaintRadialGradient.__struct(FT_PaintRadialGradient.__member(FT_ColorLine.SIZEOF, FT_ColorLine.ALIGNOF), FT_PaintRadialGradient.__member(FT_Vector.SIZEOF, FT_Vector.ALIGNOF), FT_PaintRadialGradient.__member(CLONG_SIZE), FT_PaintRadialGradient.__member(FT_Vector.SIZEOF, FT_Vector.ALIGNOF), FT_PaintRadialGradient.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        COLORLINE = layout.offsetof(0);
        C0 = layout.offsetof(1);
        R0 = layout.offsetof(2);
        C1 = layout.offsetof(3);
        R1 = layout.offsetof(4);
    }

    public static class Buffer
    extends StructBuffer<FT_PaintRadialGradient, Buffer> {
        private static final FT_PaintRadialGradient ELEMENT_FACTORY = FT_PaintRadialGradient.create(-1L);

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
        protected FT_PaintRadialGradient getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public FT_ColorLine colorline() {
            return FT_PaintRadialGradient.ncolorline(this.address());
        }

        public FT_Vector c0() {
            return FT_PaintRadialGradient.nc0(this.address());
        }

        @NativeType(value="FT_Pos")
        public long r0() {
            return FT_PaintRadialGradient.nr0(this.address());
        }

        public FT_Vector c1() {
            return FT_PaintRadialGradient.nc1(this.address());
        }

        @NativeType(value="FT_Pos")
        public long r1() {
            return FT_PaintRadialGradient.nr1(this.address());
        }
    }
}
