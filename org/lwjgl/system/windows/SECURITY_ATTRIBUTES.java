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

public class SECURITY_ATTRIBUTES
extends Struct<SECURITY_ATTRIBUTES>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int NLENGTH;
    public static final int LPSECURITYDESCRIPTOR;
    public static final int BINHERITHANDLE;

    protected SECURITY_ATTRIBUTES(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected SECURITY_ATTRIBUTES create(long address, @Nullable ByteBuffer container) {
        return new SECURITY_ATTRIBUTES(address, container);
    }

    public SECURITY_ATTRIBUTES(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), SECURITY_ATTRIBUTES.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="DWORD")
    public int nLength() {
        return SECURITY_ATTRIBUTES.nnLength(this.address());
    }

    @NativeType(value="LPVOID")
    public long lpSecurityDescriptor() {
        return SECURITY_ATTRIBUTES.nlpSecurityDescriptor(this.address());
    }

    @NativeType(value="BOOL")
    public boolean bInheritHandle() {
        return SECURITY_ATTRIBUTES.nbInheritHandle(this.address()) != 0;
    }

    public SECURITY_ATTRIBUTES nLength(@NativeType(value="DWORD") int value) {
        SECURITY_ATTRIBUTES.nnLength(this.address(), value);
        return this;
    }

    public SECURITY_ATTRIBUTES lpSecurityDescriptor(@NativeType(value="LPVOID") long value) {
        SECURITY_ATTRIBUTES.nlpSecurityDescriptor(this.address(), value);
        return this;
    }

    public SECURITY_ATTRIBUTES bInheritHandle(@NativeType(value="BOOL") boolean value) {
        SECURITY_ATTRIBUTES.nbInheritHandle(this.address(), value ? 1 : 0);
        return this;
    }

    public SECURITY_ATTRIBUTES set(int nLength, long lpSecurityDescriptor, boolean bInheritHandle) {
        this.nLength(nLength);
        this.lpSecurityDescriptor(lpSecurityDescriptor);
        this.bInheritHandle(bInheritHandle);
        return this;
    }

    public SECURITY_ATTRIBUTES set(SECURITY_ATTRIBUTES src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static SECURITY_ATTRIBUTES malloc() {
        return new SECURITY_ATTRIBUTES(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static SECURITY_ATTRIBUTES calloc() {
        return new SECURITY_ATTRIBUTES(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static SECURITY_ATTRIBUTES create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new SECURITY_ATTRIBUTES(MemoryUtil.memAddress(container), container);
    }

    public static SECURITY_ATTRIBUTES create(long address) {
        return new SECURITY_ATTRIBUTES(address, null);
    }

    @Nullable
    public static SECURITY_ATTRIBUTES createSafe(long address) {
        return address == 0L ? null : new SECURITY_ATTRIBUTES(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(SECURITY_ATTRIBUTES.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = SECURITY_ATTRIBUTES.__create(capacity, SIZEOF);
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
    public static SECURITY_ATTRIBUTES mallocStack() {
        return SECURITY_ATTRIBUTES.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static SECURITY_ATTRIBUTES callocStack() {
        return SECURITY_ATTRIBUTES.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static SECURITY_ATTRIBUTES mallocStack(MemoryStack stack) {
        return SECURITY_ATTRIBUTES.malloc(stack);
    }

    @Deprecated
    public static SECURITY_ATTRIBUTES callocStack(MemoryStack stack) {
        return SECURITY_ATTRIBUTES.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return SECURITY_ATTRIBUTES.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return SECURITY_ATTRIBUTES.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return SECURITY_ATTRIBUTES.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return SECURITY_ATTRIBUTES.calloc(capacity, stack);
    }

    public static SECURITY_ATTRIBUTES malloc(MemoryStack stack) {
        return new SECURITY_ATTRIBUTES(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static SECURITY_ATTRIBUTES calloc(MemoryStack stack) {
        return new SECURITY_ATTRIBUTES(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nnLength(long struct) {
        return UNSAFE.getInt(null, struct + (long)NLENGTH);
    }

    public static long nlpSecurityDescriptor(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)LPSECURITYDESCRIPTOR);
    }

    public static int nbInheritHandle(long struct) {
        return UNSAFE.getInt(null, struct + (long)BINHERITHANDLE);
    }

    public static void nnLength(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)NLENGTH, value);
    }

    public static void nlpSecurityDescriptor(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)LPSECURITYDESCRIPTOR, Checks.check(value));
    }

    public static void nbInheritHandle(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)BINHERITHANDLE, value);
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)LPSECURITYDESCRIPTOR));
    }

    static {
        Struct.Layout layout = SECURITY_ATTRIBUTES.__struct(SECURITY_ATTRIBUTES.__member(4), SECURITY_ATTRIBUTES.__member(POINTER_SIZE), SECURITY_ATTRIBUTES.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        NLENGTH = layout.offsetof(0);
        LPSECURITYDESCRIPTOR = layout.offsetof(1);
        BINHERITHANDLE = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<SECURITY_ATTRIBUTES, Buffer>
    implements NativeResource {
        private static final SECURITY_ATTRIBUTES ELEMENT_FACTORY = SECURITY_ATTRIBUTES.create(-1L);

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
        protected SECURITY_ATTRIBUTES getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="DWORD")
        public int nLength() {
            return SECURITY_ATTRIBUTES.nnLength(this.address());
        }

        @NativeType(value="LPVOID")
        public long lpSecurityDescriptor() {
            return SECURITY_ATTRIBUTES.nlpSecurityDescriptor(this.address());
        }

        @NativeType(value="BOOL")
        public boolean bInheritHandle() {
            return SECURITY_ATTRIBUTES.nbInheritHandle(this.address()) != 0;
        }

        public Buffer nLength(@NativeType(value="DWORD") int value) {
            SECURITY_ATTRIBUTES.nnLength(this.address(), value);
            return this;
        }

        public Buffer lpSecurityDescriptor(@NativeType(value="LPVOID") long value) {
            SECURITY_ATTRIBUTES.nlpSecurityDescriptor(this.address(), value);
            return this;
        }

        public Buffer bInheritHandle(@NativeType(value="BOOL") boolean value) {
            SECURITY_ATTRIBUTES.nbInheritHandle(this.address(), value ? 1 : 0);
            return this;
        }
    }
}
