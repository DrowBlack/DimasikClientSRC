package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class FT_Data
extends Struct<FT_Data>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int POINTER;
    public static final int LENGTH;

    protected FT_Data(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Data create(long address, @Nullable ByteBuffer container) {
        return new FT_Data(address, container);
    }

    public FT_Data(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Data.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Byte const *")
    public ByteBuffer pointer() {
        return FT_Data.npointer(this.address());
    }

    @NativeType(value="FT_UInt")
    public int length() {
        return FT_Data.nlength(this.address());
    }

    public FT_Data pointer(@NativeType(value="FT_Byte const *") ByteBuffer value) {
        FT_Data.npointer(this.address(), value);
        return this;
    }

    public FT_Data set(FT_Data src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Data malloc() {
        return new FT_Data(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Data calloc() {
        return new FT_Data(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Data create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Data(MemoryUtil.memAddress(container), container);
    }

    public static FT_Data create(long address) {
        return new FT_Data(address, null);
    }

    @Nullable
    public static FT_Data createSafe(long address) {
        return address == 0L ? null : new FT_Data(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Data.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Data.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Data malloc(MemoryStack stack) {
        return new FT_Data(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Data calloc(MemoryStack stack) {
        return new FT_Data(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static ByteBuffer npointer(long struct) {
        return MemoryUtil.memByteBuffer(MemoryUtil.memGetAddress(struct + (long)POINTER), FT_Data.nlength(struct));
    }

    public static int nlength(long struct) {
        return UNSAFE.getInt(null, struct + (long)LENGTH);
    }

    public static void npointer(long struct, ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)POINTER, MemoryUtil.memAddress(value));
        FT_Data.nlength(struct, value.remaining());
    }

    public static void nlength(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)LENGTH, value);
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)POINTER));
    }

    static {
        Struct.Layout layout = FT_Data.__struct(FT_Data.__member(POINTER_SIZE), FT_Data.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        POINTER = layout.offsetof(0);
        LENGTH = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_Data, Buffer>
    implements NativeResource {
        private static final FT_Data ELEMENT_FACTORY = FT_Data.create(-1L);

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
        protected FT_Data getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Byte const *")
        public ByteBuffer pointer() {
            return FT_Data.npointer(this.address());
        }

        @NativeType(value="FT_UInt")
        public int length() {
            return FT_Data.nlength(this.address());
        }

        public Buffer pointer(@NativeType(value="FT_Byte const *") ByteBuffer value) {
            FT_Data.npointer(this.address(), value);
            return this;
        }
    }
}
