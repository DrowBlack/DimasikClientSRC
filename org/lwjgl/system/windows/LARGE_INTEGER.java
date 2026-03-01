package org.lwjgl.system.windows;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class LARGE_INTEGER
extends Struct<LARGE_INTEGER>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int U;
    public static final int U_LOWPART;
    public static final int U_HIGHPART;
    public static final int QUADPART;

    protected LARGE_INTEGER(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected LARGE_INTEGER create(long address, @Nullable ByteBuffer container) {
        return new LARGE_INTEGER(address, container);
    }

    public LARGE_INTEGER(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), LARGE_INTEGER.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="DWORD")
    public int u_LowPart() {
        return LARGE_INTEGER.nu_LowPart(this.address());
    }

    @NativeType(value="LONG")
    public int u_HighPart() {
        return LARGE_INTEGER.nu_HighPart(this.address());
    }

    @NativeType(value="LONGLONG")
    public long QuadPart() {
        return LARGE_INTEGER.nQuadPart(this.address());
    }

    public LARGE_INTEGER u_LowPart(@NativeType(value="DWORD") int value) {
        LARGE_INTEGER.nu_LowPart(this.address(), value);
        return this;
    }

    public LARGE_INTEGER u_HighPart(@NativeType(value="LONG") int value) {
        LARGE_INTEGER.nu_HighPart(this.address(), value);
        return this;
    }

    public LARGE_INTEGER QuadPart(@NativeType(value="LONGLONG") long value) {
        LARGE_INTEGER.nQuadPart(this.address(), value);
        return this;
    }

    public LARGE_INTEGER set(LARGE_INTEGER src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static LARGE_INTEGER malloc() {
        return new LARGE_INTEGER(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static LARGE_INTEGER calloc() {
        return new LARGE_INTEGER(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static LARGE_INTEGER create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new LARGE_INTEGER(MemoryUtil.memAddress(container), container);
    }

    public static LARGE_INTEGER create(long address) {
        return new LARGE_INTEGER(address, null);
    }

    @Nullable
    public static LARGE_INTEGER createSafe(long address) {
        return address == 0L ? null : new LARGE_INTEGER(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(LARGE_INTEGER.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = LARGE_INTEGER.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static LARGE_INTEGER malloc(MemoryStack stack) {
        return new LARGE_INTEGER(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static LARGE_INTEGER calloc(MemoryStack stack) {
        return new LARGE_INTEGER(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nu_LowPart(long struct) {
        return UNSAFE.getInt(null, struct + (long)U_LOWPART);
    }

    public static int nu_HighPart(long struct) {
        return UNSAFE.getInt(null, struct + (long)U_HIGHPART);
    }

    public static long nQuadPart(long struct) {
        return UNSAFE.getLong(null, struct + (long)QUADPART);
    }

    public static void nu_LowPart(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)U_LOWPART, value);
    }

    public static void nu_HighPart(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)U_HIGHPART, value);
    }

    public static void nQuadPart(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)QUADPART, value);
    }

    static {
        Struct.Layout layout = LARGE_INTEGER.__union(LARGE_INTEGER.__struct(LARGE_INTEGER.__member(4), LARGE_INTEGER.__member(4)), LARGE_INTEGER.__member(8));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        U = layout.offsetof(0);
        U_LOWPART = layout.offsetof(1);
        U_HIGHPART = layout.offsetof(2);
        QUADPART = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<LARGE_INTEGER, Buffer>
    implements NativeResource {
        private static final LARGE_INTEGER ELEMENT_FACTORY = LARGE_INTEGER.create(-1L);

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
        protected LARGE_INTEGER getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="DWORD")
        public int u_LowPart() {
            return LARGE_INTEGER.nu_LowPart(this.address());
        }

        @NativeType(value="LONG")
        public int u_HighPart() {
            return LARGE_INTEGER.nu_HighPart(this.address());
        }

        @NativeType(value="LONGLONG")
        public long QuadPart() {
            return LARGE_INTEGER.nQuadPart(this.address());
        }

        public Buffer u_LowPart(@NativeType(value="DWORD") int value) {
            LARGE_INTEGER.nu_LowPart(this.address(), value);
            return this;
        }

        public Buffer u_HighPart(@NativeType(value="LONG") int value) {
            LARGE_INTEGER.nu_HighPart(this.address(), value);
            return this;
        }

        public Buffer QuadPart(@NativeType(value="LONGLONG") long value) {
            LARGE_INTEGER.nQuadPart(this.address(), value);
            return this;
        }
    }
}
