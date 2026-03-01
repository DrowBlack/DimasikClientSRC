package org.lwjgl.stb;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.LibSTB;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct stbtt_fontinfo")
public class STBTTFontinfo
extends Struct<STBTTFontinfo>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;

    private static native int offsets(long var0);

    protected STBTTFontinfo(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected STBTTFontinfo create(long address, @Nullable ByteBuffer container) {
        return new STBTTFontinfo(address, container);
    }

    public STBTTFontinfo(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), STBTTFontinfo.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public static STBTTFontinfo malloc() {
        return new STBTTFontinfo(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static STBTTFontinfo calloc() {
        return new STBTTFontinfo(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static STBTTFontinfo create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new STBTTFontinfo(MemoryUtil.memAddress(container), container);
    }

    public static STBTTFontinfo create(long address) {
        return new STBTTFontinfo(address, null);
    }

    @Nullable
    public static STBTTFontinfo createSafe(long address) {
        return address == 0L ? null : new STBTTFontinfo(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(STBTTFontinfo.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = STBTTFontinfo.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    @Deprecated
    public static STBTTFontinfo mallocStack() {
        return STBTTFontinfo.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBTTFontinfo callocStack() {
        return STBTTFontinfo.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBTTFontinfo mallocStack(MemoryStack stack) {
        return STBTTFontinfo.malloc(stack);
    }

    @Deprecated
    public static STBTTFontinfo callocStack(MemoryStack stack) {
        return STBTTFontinfo.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return STBTTFontinfo.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return STBTTFontinfo.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return STBTTFontinfo.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return STBTTFontinfo.calloc(capacity, stack);
    }

    public static STBTTFontinfo malloc(MemoryStack stack) {
        return new STBTTFontinfo(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static STBTTFontinfo calloc(MemoryStack stack) {
        return new STBTTFontinfo(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    static {
        LibSTB.initialize();
        try (MemoryStack stack = MemoryStack.stackPush();){
            IntBuffer offsets = stack.mallocInt(1);
            SIZEOF = STBTTFontinfo.offsets(MemoryUtil.memAddress(offsets));
            ALIGNOF = offsets.get(0);
        }
    }

    public static class Buffer
    extends StructBuffer<STBTTFontinfo, Buffer>
    implements NativeResource {
        private static final STBTTFontinfo ELEMENT_FACTORY = STBTTFontinfo.create(-1L);

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
        protected STBTTFontinfo getElementFactory() {
            return ELEMENT_FACTORY;
        }
    }
}
