package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_OpaquePaint;

public class FT_PaintTranslate
extends Struct<FT_PaintTranslate> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int PAINT;
    public static final int DX;
    public static final int DY;

    protected FT_PaintTranslate(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_PaintTranslate create(long address, @Nullable ByteBuffer container) {
        return new FT_PaintTranslate(address, container);
    }

    public FT_PaintTranslate(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_PaintTranslate.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_OpaquePaintRec")
    public FT_OpaquePaint paint() {
        return FT_PaintTranslate.npaint(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long dx() {
        return FT_PaintTranslate.ndx(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long dy() {
        return FT_PaintTranslate.ndy(this.address());
    }

    public static FT_PaintTranslate create(long address) {
        return new FT_PaintTranslate(address, null);
    }

    @Nullable
    public static FT_PaintTranslate createSafe(long address) {
        return address == 0L ? null : new FT_PaintTranslate(address, null);
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

    public static long ndx(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)DX);
    }

    public static long ndy(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)DY);
    }

    static {
        Struct.Layout layout = FT_PaintTranslate.__struct(FT_PaintTranslate.__member(FT_OpaquePaint.SIZEOF, FT_OpaquePaint.ALIGNOF), FT_PaintTranslate.__member(CLONG_SIZE), FT_PaintTranslate.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        PAINT = layout.offsetof(0);
        DX = layout.offsetof(1);
        DY = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<FT_PaintTranslate, Buffer> {
        private static final FT_PaintTranslate ELEMENT_FACTORY = FT_PaintTranslate.create(-1L);

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
        protected FT_PaintTranslate getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_OpaquePaintRec")
        public FT_OpaquePaint paint() {
            return FT_PaintTranslate.npaint(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long dx() {
            return FT_PaintTranslate.ndx(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long dy() {
            return FT_PaintTranslate.ndy(this.address());
        }
    }
}
