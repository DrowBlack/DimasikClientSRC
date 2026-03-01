package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_ColorLine;
import org.lwjgl.util.freetype.FT_Vector;

public class FT_PaintSweepGradient
extends Struct<FT_PaintSweepGradient> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int COLORLINE;
    public static final int CENTER;
    public static final int START_ANGLE;
    public static final int END_ANGLE;

    protected FT_PaintSweepGradient(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_PaintSweepGradient create(long address, @Nullable ByteBuffer container) {
        return new FT_PaintSweepGradient(address, container);
    }

    public FT_PaintSweepGradient(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_PaintSweepGradient.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public FT_ColorLine colorline() {
        return FT_PaintSweepGradient.ncolorline(this.address());
    }

    public FT_Vector center() {
        return FT_PaintSweepGradient.ncenter(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long start_angle() {
        return FT_PaintSweepGradient.nstart_angle(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long end_angle() {
        return FT_PaintSweepGradient.nend_angle(this.address());
    }

    public static FT_PaintSweepGradient create(long address) {
        return new FT_PaintSweepGradient(address, null);
    }

    @Nullable
    public static FT_PaintSweepGradient createSafe(long address) {
        return address == 0L ? null : new FT_PaintSweepGradient(address, null);
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

    public static FT_Vector ncenter(long struct) {
        return FT_Vector.create(struct + (long)CENTER);
    }

    public static long nstart_angle(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)START_ANGLE);
    }

    public static long nend_angle(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)END_ANGLE);
    }

    static {
        Struct.Layout layout = FT_PaintSweepGradient.__struct(FT_PaintSweepGradient.__member(FT_ColorLine.SIZEOF, FT_ColorLine.ALIGNOF), FT_PaintSweepGradient.__member(FT_Vector.SIZEOF, FT_Vector.ALIGNOF), FT_PaintSweepGradient.__member(CLONG_SIZE), FT_PaintSweepGradient.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        COLORLINE = layout.offsetof(0);
        CENTER = layout.offsetof(1);
        START_ANGLE = layout.offsetof(2);
        END_ANGLE = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FT_PaintSweepGradient, Buffer> {
        private static final FT_PaintSweepGradient ELEMENT_FACTORY = FT_PaintSweepGradient.create(-1L);

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
        protected FT_PaintSweepGradient getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public FT_ColorLine colorline() {
            return FT_PaintSweepGradient.ncolorline(this.address());
        }

        public FT_Vector center() {
            return FT_PaintSweepGradient.ncenter(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long start_angle() {
            return FT_PaintSweepGradient.nstart_angle(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long end_angle() {
            return FT_PaintSweepGradient.nend_angle(this.address());
        }
    }
}
