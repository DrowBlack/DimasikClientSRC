package org.lwjgl.system.windows;

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

public class DATA_BLOB
extends Struct<DATA_BLOB>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int CBDATA;
    public static final int PBDATA;

    protected DATA_BLOB(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected DATA_BLOB create(long address, @Nullable ByteBuffer container) {
        return new DATA_BLOB(address, container);
    }

    public DATA_BLOB(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), DATA_BLOB.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="DWORD")
    public int cbData() {
        return DATA_BLOB.ncbData(this.address());
    }

    @NativeType(value="BYTE *")
    public ByteBuffer pbData() {
        return DATA_BLOB.npbData(this.address());
    }

    public DATA_BLOB pbData(@NativeType(value="BYTE *") ByteBuffer value) {
        DATA_BLOB.npbData(this.address(), value);
        return this;
    }

    public DATA_BLOB set(DATA_BLOB src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static DATA_BLOB malloc() {
        return new DATA_BLOB(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static DATA_BLOB calloc() {
        return new DATA_BLOB(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static DATA_BLOB create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new DATA_BLOB(MemoryUtil.memAddress(container), container);
    }

    public static DATA_BLOB create(long address) {
        return new DATA_BLOB(address, null);
    }

    @Nullable
    public static DATA_BLOB createSafe(long address) {
        return address == 0L ? null : new DATA_BLOB(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(DATA_BLOB.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = DATA_BLOB.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static DATA_BLOB malloc(MemoryStack stack) {
        return new DATA_BLOB(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static DATA_BLOB calloc(MemoryStack stack) {
        return new DATA_BLOB(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int ncbData(long struct) {
        return UNSAFE.getInt(null, struct + (long)CBDATA);
    }

    public static ByteBuffer npbData(long struct) {
        return MemoryUtil.memByteBuffer(MemoryUtil.memGetAddress(struct + (long)PBDATA), DATA_BLOB.ncbData(struct));
    }

    public static void ncbData(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)CBDATA, value);
    }

    public static void npbData(long struct, ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)PBDATA, MemoryUtil.memAddress(value));
        DATA_BLOB.ncbData(struct, value.remaining());
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)PBDATA));
    }

    static {
        Struct.Layout layout = DATA_BLOB.__struct(DATA_BLOB.__member(4), DATA_BLOB.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        CBDATA = layout.offsetof(0);
        PBDATA = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<DATA_BLOB, Buffer>
    implements NativeResource {
        private static final DATA_BLOB ELEMENT_FACTORY = DATA_BLOB.create(-1L);

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
        protected DATA_BLOB getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="DWORD")
        public int cbData() {
            return DATA_BLOB.ncbData(this.address());
        }

        @NativeType(value="BYTE *")
        public ByteBuffer pbData() {
            return DATA_BLOB.npbData(this.address());
        }

        public Buffer pbData(@NativeType(value="BYTE *") ByteBuffer value) {
            DATA_BLOB.npbData(this.address(), value);
            return this;
        }
    }
}
