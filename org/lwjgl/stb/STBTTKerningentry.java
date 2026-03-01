package org.lwjgl.stb;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct stbtt_kerningentry")
public class STBTTKerningentry
extends Struct<STBTTKerningentry>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int GLYPH1;
    public static final int GLYPH2;
    public static final int ADVANCE;

    protected STBTTKerningentry(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected STBTTKerningentry create(long address, @Nullable ByteBuffer container) {
        return new STBTTKerningentry(address, container);
    }

    public STBTTKerningentry(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), STBTTKerningentry.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public int glyph1() {
        return STBTTKerningentry.nglyph1(this.address());
    }

    public int glyph2() {
        return STBTTKerningentry.nglyph2(this.address());
    }

    public int advance() {
        return STBTTKerningentry.nadvance(this.address());
    }

    public static STBTTKerningentry malloc() {
        return new STBTTKerningentry(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static STBTTKerningentry calloc() {
        return new STBTTKerningentry(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static STBTTKerningentry create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new STBTTKerningentry(MemoryUtil.memAddress(container), container);
    }

    public static STBTTKerningentry create(long address) {
        return new STBTTKerningentry(address, null);
    }

    @Nullable
    public static STBTTKerningentry createSafe(long address) {
        return address == 0L ? null : new STBTTKerningentry(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(STBTTKerningentry.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = STBTTKerningentry.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static STBTTKerningentry malloc(MemoryStack stack) {
        return new STBTTKerningentry(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static STBTTKerningentry calloc(MemoryStack stack) {
        return new STBTTKerningentry(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nglyph1(long struct) {
        return UNSAFE.getInt(null, struct + (long)GLYPH1);
    }

    public static int nglyph2(long struct) {
        return UNSAFE.getInt(null, struct + (long)GLYPH2);
    }

    public static int nadvance(long struct) {
        return UNSAFE.getInt(null, struct + (long)ADVANCE);
    }

    static {
        Struct.Layout layout = STBTTKerningentry.__struct(STBTTKerningentry.__member(4), STBTTKerningentry.__member(4), STBTTKerningentry.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        GLYPH1 = layout.offsetof(0);
        GLYPH2 = layout.offsetof(1);
        ADVANCE = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<STBTTKerningentry, Buffer>
    implements NativeResource {
        private static final STBTTKerningentry ELEMENT_FACTORY = STBTTKerningentry.create(-1L);

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
        protected STBTTKerningentry getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public int glyph1() {
            return STBTTKerningentry.nglyph1(this.address());
        }

        public int glyph2() {
            return STBTTKerningentry.nglyph2(this.address());
        }

        public int advance() {
            return STBTTKerningentry.nadvance(this.address());
        }
    }
}
