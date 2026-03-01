package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_OpaquePaint;

public class FT_PaintComposite
extends Struct<FT_PaintComposite> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int SOURCE_PAINT;
    public static final int COMPOSITE_MODE;
    public static final int BACKDROP_PAINT;

    protected FT_PaintComposite(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_PaintComposite create(long address, @Nullable ByteBuffer container) {
        return new FT_PaintComposite(address, container);
    }

    public FT_PaintComposite(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_PaintComposite.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_OpaquePaintRec")
    public FT_OpaquePaint source_paint() {
        return FT_PaintComposite.nsource_paint(this.address());
    }

    @NativeType(value="FT_Composite_Mode")
    public int composite_mode() {
        return FT_PaintComposite.ncomposite_mode(this.address());
    }

    @NativeType(value="FT_OpaquePaintRec")
    public FT_OpaquePaint backdrop_paint() {
        return FT_PaintComposite.nbackdrop_paint(this.address());
    }

    public static FT_PaintComposite create(long address) {
        return new FT_PaintComposite(address, null);
    }

    @Nullable
    public static FT_PaintComposite createSafe(long address) {
        return address == 0L ? null : new FT_PaintComposite(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_OpaquePaint nsource_paint(long struct) {
        return FT_OpaquePaint.create(struct + (long)SOURCE_PAINT);
    }

    public static int ncomposite_mode(long struct) {
        return UNSAFE.getInt(null, struct + (long)COMPOSITE_MODE);
    }

    public static FT_OpaquePaint nbackdrop_paint(long struct) {
        return FT_OpaquePaint.create(struct + (long)BACKDROP_PAINT);
    }

    static {
        Struct.Layout layout = FT_PaintComposite.__struct(FT_PaintComposite.__member(FT_OpaquePaint.SIZEOF, FT_OpaquePaint.ALIGNOF), FT_PaintComposite.__member(4), FT_PaintComposite.__member(FT_OpaquePaint.SIZEOF, FT_OpaquePaint.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        SOURCE_PAINT = layout.offsetof(0);
        COMPOSITE_MODE = layout.offsetof(1);
        BACKDROP_PAINT = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<FT_PaintComposite, Buffer> {
        private static final FT_PaintComposite ELEMENT_FACTORY = FT_PaintComposite.create(-1L);

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
        protected FT_PaintComposite getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_OpaquePaintRec")
        public FT_OpaquePaint source_paint() {
            return FT_PaintComposite.nsource_paint(this.address());
        }

        @NativeType(value="FT_Composite_Mode")
        public int composite_mode() {
            return FT_PaintComposite.ncomposite_mode(this.address());
        }

        @NativeType(value="FT_OpaquePaintRec")
        public FT_OpaquePaint backdrop_paint() {
            return FT_PaintComposite.nbackdrop_paint(this.address());
        }
    }
}
