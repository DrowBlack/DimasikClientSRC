package org.lwjgl.system.macosx;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct objc_method_description")
public class ObjCMethodDescription
extends Struct<ObjCMethodDescription>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int NAME;
    public static final int TYPES;

    protected ObjCMethodDescription(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected ObjCMethodDescription create(long address, @Nullable ByteBuffer container) {
        return new ObjCMethodDescription(address, container);
    }

    public ObjCMethodDescription(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), ObjCMethodDescription.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="SEL")
    public long name() {
        return ObjCMethodDescription.nname(this.address());
    }

    @NativeType(value="char *")
    public ByteBuffer types() {
        return ObjCMethodDescription.ntypes(this.address());
    }

    @NativeType(value="char *")
    public String typesString() {
        return ObjCMethodDescription.ntypesString(this.address());
    }

    public static ObjCMethodDescription malloc() {
        return new ObjCMethodDescription(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static ObjCMethodDescription calloc() {
        return new ObjCMethodDescription(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static ObjCMethodDescription create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new ObjCMethodDescription(MemoryUtil.memAddress(container), container);
    }

    public static ObjCMethodDescription create(long address) {
        return new ObjCMethodDescription(address, null);
    }

    @Nullable
    public static ObjCMethodDescription createSafe(long address) {
        return address == 0L ? null : new ObjCMethodDescription(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(ObjCMethodDescription.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = ObjCMethodDescription.__create(capacity, SIZEOF);
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
    public static ObjCMethodDescription mallocStack() {
        return ObjCMethodDescription.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static ObjCMethodDescription callocStack() {
        return ObjCMethodDescription.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static ObjCMethodDescription mallocStack(MemoryStack stack) {
        return ObjCMethodDescription.malloc(stack);
    }

    @Deprecated
    public static ObjCMethodDescription callocStack(MemoryStack stack) {
        return ObjCMethodDescription.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return ObjCMethodDescription.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return ObjCMethodDescription.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return ObjCMethodDescription.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return ObjCMethodDescription.calloc(capacity, stack);
    }

    public static ObjCMethodDescription malloc(MemoryStack stack) {
        return new ObjCMethodDescription(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static ObjCMethodDescription calloc(MemoryStack stack) {
        return new ObjCMethodDescription(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nname(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)NAME);
    }

    public static ByteBuffer ntypes(long struct) {
        return MemoryUtil.memByteBufferNT1(MemoryUtil.memGetAddress(struct + (long)TYPES));
    }

    public static String ntypesString(long struct) {
        return MemoryUtil.memUTF8(MemoryUtil.memGetAddress(struct + (long)TYPES));
    }

    static {
        Struct.Layout layout = ObjCMethodDescription.__struct(ObjCMethodDescription.__member(POINTER_SIZE), ObjCMethodDescription.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        NAME = layout.offsetof(0);
        TYPES = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<ObjCMethodDescription, Buffer>
    implements NativeResource {
        private static final ObjCMethodDescription ELEMENT_FACTORY = ObjCMethodDescription.create(-1L);

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
        protected ObjCMethodDescription getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="SEL")
        public long name() {
            return ObjCMethodDescription.nname(this.address());
        }

        @NativeType(value="char *")
        public ByteBuffer types() {
            return ObjCMethodDescription.ntypes(this.address());
        }

        @NativeType(value="char *")
        public String typesString() {
            return ObjCMethodDescription.ntypesString(this.address());
        }
    }
}
