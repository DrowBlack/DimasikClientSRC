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

public class FT_SfntLangTag
extends Struct<FT_SfntLangTag>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int STRING;
    public static final int STRING_LEN;

    protected FT_SfntLangTag(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_SfntLangTag create(long address, @Nullable ByteBuffer container) {
        return new FT_SfntLangTag(address, container);
    }

    public FT_SfntLangTag(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_SfntLangTag.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Byte *")
    public ByteBuffer string() {
        return FT_SfntLangTag.nstring(this.address());
    }

    @NativeType(value="FT_UInt")
    public int string_len() {
        return FT_SfntLangTag.nstring_len(this.address());
    }

    public static FT_SfntLangTag malloc() {
        return new FT_SfntLangTag(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_SfntLangTag calloc() {
        return new FT_SfntLangTag(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_SfntLangTag create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_SfntLangTag(MemoryUtil.memAddress(container), container);
    }

    public static FT_SfntLangTag create(long address) {
        return new FT_SfntLangTag(address, null);
    }

    @Nullable
    public static FT_SfntLangTag createSafe(long address) {
        return address == 0L ? null : new FT_SfntLangTag(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_SfntLangTag.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_SfntLangTag.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_SfntLangTag malloc(MemoryStack stack) {
        return new FT_SfntLangTag(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_SfntLangTag calloc(MemoryStack stack) {
        return new FT_SfntLangTag(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static ByteBuffer nstring(long struct) {
        return MemoryUtil.memByteBuffer(MemoryUtil.memGetAddress(struct + (long)STRING), FT_SfntLangTag.nstring_len(struct));
    }

    public static int nstring_len(long struct) {
        return UNSAFE.getInt(null, struct + (long)STRING_LEN);
    }

    static {
        Struct.Layout layout = FT_SfntLangTag.__struct(FT_SfntLangTag.__member(POINTER_SIZE), FT_SfntLangTag.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        STRING = layout.offsetof(0);
        STRING_LEN = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_SfntLangTag, Buffer>
    implements NativeResource {
        private static final FT_SfntLangTag ELEMENT_FACTORY = FT_SfntLangTag.create(-1L);

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
        protected FT_SfntLangTag getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Byte *")
        public ByteBuffer string() {
            return FT_SfntLangTag.nstring(this.address());
        }

        @NativeType(value="FT_UInt")
        public int string_len() {
            return FT_SfntLangTag.nstring_len(this.address());
        }
    }
}
