package org.lwjgl.system.libffi;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct ffi_type")
public class FFIType
extends Struct<FFIType>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int SIZE;
    public static final int ALIGNMENT;
    public static final int TYPE;
    public static final int ELEMENTS;

    protected FFIType(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FFIType create(long address, @Nullable ByteBuffer container) {
        return new FFIType(address, container);
    }

    public FFIType(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FFIType.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="size_t")
    public long size() {
        return FFIType.nsize(this.address());
    }

    @NativeType(value="unsigned short")
    public short alignment() {
        return FFIType.nalignment(this.address());
    }

    @NativeType(value="unsigned short")
    public short type() {
        return FFIType.ntype(this.address());
    }

    @Nullable
    @NativeType(value="ffi_type *")
    public PointerBuffer elements(int capacity) {
        return FFIType.nelements(this.address(), capacity);
    }

    public FFIType size(@NativeType(value="size_t") long value) {
        FFIType.nsize(this.address(), value);
        return this;
    }

    public FFIType alignment(@NativeType(value="unsigned short") short value) {
        FFIType.nalignment(this.address(), value);
        return this;
    }

    public FFIType type(@NativeType(value="unsigned short") short value) {
        FFIType.ntype(this.address(), value);
        return this;
    }

    public FFIType elements(@Nullable @NativeType(value="ffi_type *") PointerBuffer value) {
        FFIType.nelements(this.address(), value);
        return this;
    }

    public FFIType set(long size, short alignment, short type, @Nullable PointerBuffer elements) {
        this.size(size);
        this.alignment(alignment);
        this.type(type);
        this.elements(elements);
        return this;
    }

    public FFIType set(FFIType src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FFIType malloc() {
        return new FFIType(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FFIType calloc() {
        return new FFIType(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FFIType create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FFIType(MemoryUtil.memAddress(container), container);
    }

    public static FFIType create(long address) {
        return new FFIType(address, null);
    }

    @Nullable
    public static FFIType createSafe(long address) {
        return address == 0L ? null : new FFIType(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FFIType.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FFIType.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FFIType malloc(MemoryStack stack) {
        return new FFIType(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FFIType calloc(MemoryStack stack) {
        return new FFIType(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nsize(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)SIZE);
    }

    public static short nalignment(long struct) {
        return UNSAFE.getShort(null, struct + (long)ALIGNMENT);
    }

    public static short ntype(long struct) {
        return UNSAFE.getShort(null, struct + (long)TYPE);
    }

    @Nullable
    public static PointerBuffer nelements(long struct, int capacity) {
        return MemoryUtil.memPointerBufferSafe(MemoryUtil.memGetAddress(struct + (long)ELEMENTS), capacity);
    }

    public static void nsize(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)SIZE, value);
    }

    public static void nalignment(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)ALIGNMENT, value);
    }

    public static void ntype(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)TYPE, value);
    }

    public static void nelements(long struct, @Nullable PointerBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)ELEMENTS, MemoryUtil.memAddressSafe(value));
    }

    static {
        Struct.Layout layout = FFIType.__struct(FFIType.__member(POINTER_SIZE), FFIType.__member(2), FFIType.__member(2), FFIType.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        SIZE = layout.offsetof(0);
        ALIGNMENT = layout.offsetof(1);
        TYPE = layout.offsetof(2);
        ELEMENTS = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FFIType, Buffer>
    implements NativeResource {
        private static final FFIType ELEMENT_FACTORY = FFIType.create(-1L);

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
        protected FFIType getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="size_t")
        public long size() {
            return FFIType.nsize(this.address());
        }

        @NativeType(value="unsigned short")
        public short alignment() {
            return FFIType.nalignment(this.address());
        }

        @NativeType(value="unsigned short")
        public short type() {
            return FFIType.ntype(this.address());
        }

        @Nullable
        @NativeType(value="ffi_type *")
        public PointerBuffer elements(int capacity) {
            return FFIType.nelements(this.address(), capacity);
        }

        public Buffer size(@NativeType(value="size_t") long value) {
            FFIType.nsize(this.address(), value);
            return this;
        }

        public Buffer alignment(@NativeType(value="unsigned short") short value) {
            FFIType.nalignment(this.address(), value);
            return this;
        }

        public Buffer type(@NativeType(value="unsigned short") short value) {
            FFIType.ntype(this.address(), value);
            return this;
        }

        public Buffer elements(@Nullable @NativeType(value="ffi_type *") PointerBuffer value) {
            FFIType.nelements(this.address(), value);
            return this;
        }
    }
}
