package org.lwjgl.glfw;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWAllocateCallback;
import org.lwjgl.glfw.GLFWAllocateCallbackI;
import org.lwjgl.glfw.GLFWDeallocateCallback;
import org.lwjgl.glfw.GLFWDeallocateCallbackI;
import org.lwjgl.glfw.GLFWReallocateCallback;
import org.lwjgl.glfw.GLFWReallocateCallbackI;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct GLFWallocator")
public class GLFWAllocator
extends Struct<GLFWAllocator>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int ALLOCATE;
    public static final int REALLOCATE;
    public static final int DEALLOCATE;
    public static final int USER;

    protected GLFWAllocator(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected GLFWAllocator create(long address, @Nullable ByteBuffer container) {
        return new GLFWAllocator(address, container);
    }

    public GLFWAllocator(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), GLFWAllocator.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="GLFWallocatefun")
    public GLFWAllocateCallback allocate() {
        return GLFWAllocator.nallocate(this.address());
    }

    @NativeType(value="GLFWreallocatefun")
    public GLFWReallocateCallback reallocate() {
        return GLFWAllocator.nreallocate(this.address());
    }

    @NativeType(value="GLFWdeallocatefun")
    public GLFWDeallocateCallback deallocate() {
        return GLFWAllocator.ndeallocate(this.address());
    }

    @NativeType(value="void *")
    public long user() {
        return GLFWAllocator.nuser(this.address());
    }

    public GLFWAllocator allocate(@NativeType(value="GLFWallocatefun") GLFWAllocateCallbackI value) {
        GLFWAllocator.nallocate(this.address(), value);
        return this;
    }

    public GLFWAllocator reallocate(@NativeType(value="GLFWreallocatefun") GLFWReallocateCallbackI value) {
        GLFWAllocator.nreallocate(this.address(), value);
        return this;
    }

    public GLFWAllocator deallocate(@NativeType(value="GLFWdeallocatefun") GLFWDeallocateCallbackI value) {
        GLFWAllocator.ndeallocate(this.address(), value);
        return this;
    }

    public GLFWAllocator user(@NativeType(value="void *") long value) {
        GLFWAllocator.nuser(this.address(), value);
        return this;
    }

    public GLFWAllocator set(GLFWAllocateCallbackI allocate, GLFWReallocateCallbackI reallocate, GLFWDeallocateCallbackI deallocate, long user) {
        this.allocate(allocate);
        this.reallocate(reallocate);
        this.deallocate(deallocate);
        this.user(user);
        return this;
    }

    public GLFWAllocator set(GLFWAllocator src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static GLFWAllocator malloc() {
        return new GLFWAllocator(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static GLFWAllocator calloc() {
        return new GLFWAllocator(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static GLFWAllocator create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new GLFWAllocator(MemoryUtil.memAddress(container), container);
    }

    public static GLFWAllocator create(long address) {
        return new GLFWAllocator(address, null);
    }

    @Nullable
    public static GLFWAllocator createSafe(long address) {
        return address == 0L ? null : new GLFWAllocator(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(GLFWAllocator.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = GLFWAllocator.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static GLFWAllocator malloc(MemoryStack stack) {
        return new GLFWAllocator(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static GLFWAllocator calloc(MemoryStack stack) {
        return new GLFWAllocator(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static GLFWAllocateCallback nallocate(long struct) {
        return GLFWAllocateCallback.create(MemoryUtil.memGetAddress(struct + (long)ALLOCATE));
    }

    public static GLFWReallocateCallback nreallocate(long struct) {
        return GLFWReallocateCallback.create(MemoryUtil.memGetAddress(struct + (long)REALLOCATE));
    }

    public static GLFWDeallocateCallback ndeallocate(long struct) {
        return GLFWDeallocateCallback.create(MemoryUtil.memGetAddress(struct + (long)DEALLOCATE));
    }

    public static long nuser(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)USER);
    }

    public static void nallocate(long struct, GLFWAllocateCallbackI value) {
        MemoryUtil.memPutAddress(struct + (long)ALLOCATE, value.address());
    }

    public static void nreallocate(long struct, GLFWReallocateCallbackI value) {
        MemoryUtil.memPutAddress(struct + (long)REALLOCATE, value.address());
    }

    public static void ndeallocate(long struct, GLFWDeallocateCallbackI value) {
        MemoryUtil.memPutAddress(struct + (long)DEALLOCATE, value.address());
    }

    public static void nuser(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)USER, value);
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)ALLOCATE));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)REALLOCATE));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)DEALLOCATE));
    }

    static {
        Struct.Layout layout = GLFWAllocator.__struct(GLFWAllocator.__member(POINTER_SIZE), GLFWAllocator.__member(POINTER_SIZE), GLFWAllocator.__member(POINTER_SIZE), GLFWAllocator.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        ALLOCATE = layout.offsetof(0);
        REALLOCATE = layout.offsetof(1);
        DEALLOCATE = layout.offsetof(2);
        USER = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<GLFWAllocator, Buffer>
    implements NativeResource {
        private static final GLFWAllocator ELEMENT_FACTORY = GLFWAllocator.create(-1L);

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
        protected GLFWAllocator getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="GLFWallocatefun")
        public GLFWAllocateCallback allocate() {
            return GLFWAllocator.nallocate(this.address());
        }

        @NativeType(value="GLFWreallocatefun")
        public GLFWReallocateCallback reallocate() {
            return GLFWAllocator.nreallocate(this.address());
        }

        @NativeType(value="GLFWdeallocatefun")
        public GLFWDeallocateCallback deallocate() {
            return GLFWAllocator.ndeallocate(this.address());
        }

        @NativeType(value="void *")
        public long user() {
            return GLFWAllocator.nuser(this.address());
        }

        public Buffer allocate(@NativeType(value="GLFWallocatefun") GLFWAllocateCallbackI value) {
            GLFWAllocator.nallocate(this.address(), value);
            return this;
        }

        public Buffer reallocate(@NativeType(value="GLFWreallocatefun") GLFWReallocateCallbackI value) {
            GLFWAllocator.nreallocate(this.address(), value);
            return this;
        }

        public Buffer deallocate(@NativeType(value="GLFWdeallocatefun") GLFWDeallocateCallbackI value) {
            GLFWAllocator.ndeallocate(this.address(), value);
            return this;
        }

        public Buffer user(@NativeType(value="void *") long value) {
            GLFWAllocator.nuser(this.address(), value);
            return this;
        }
    }
}
