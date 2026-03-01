package org.lwjgl.glfw;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct GLFWgammaramp")
public class GLFWGammaRamp
extends Struct<GLFWGammaRamp>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int RED;
    public static final int GREEN;
    public static final int BLUE;
    public static final int SIZE;

    protected GLFWGammaRamp(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected GLFWGammaRamp create(long address, @Nullable ByteBuffer container) {
        return new GLFWGammaRamp(address, container);
    }

    public GLFWGammaRamp(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), GLFWGammaRamp.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="unsigned short *")
    public ShortBuffer red() {
        return GLFWGammaRamp.nred(this.address());
    }

    @NativeType(value="unsigned short *")
    public ShortBuffer green() {
        return GLFWGammaRamp.ngreen(this.address());
    }

    @NativeType(value="unsigned short *")
    public ShortBuffer blue() {
        return GLFWGammaRamp.nblue(this.address());
    }

    @NativeType(value="unsigned int")
    public int size() {
        return GLFWGammaRamp.nsize(this.address());
    }

    public GLFWGammaRamp red(@NativeType(value="unsigned short *") ShortBuffer value) {
        GLFWGammaRamp.nred(this.address(), value);
        return this;
    }

    public GLFWGammaRamp green(@NativeType(value="unsigned short *") ShortBuffer value) {
        GLFWGammaRamp.ngreen(this.address(), value);
        return this;
    }

    public GLFWGammaRamp blue(@NativeType(value="unsigned short *") ShortBuffer value) {
        GLFWGammaRamp.nblue(this.address(), value);
        return this;
    }

    public GLFWGammaRamp size(@NativeType(value="unsigned int") int value) {
        GLFWGammaRamp.nsize(this.address(), value);
        return this;
    }

    public GLFWGammaRamp set(ShortBuffer red, ShortBuffer green, ShortBuffer blue, int size) {
        this.red(red);
        this.green(green);
        this.blue(blue);
        this.size(size);
        return this;
    }

    public GLFWGammaRamp set(GLFWGammaRamp src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static GLFWGammaRamp malloc() {
        return new GLFWGammaRamp(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static GLFWGammaRamp calloc() {
        return new GLFWGammaRamp(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static GLFWGammaRamp create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new GLFWGammaRamp(MemoryUtil.memAddress(container), container);
    }

    public static GLFWGammaRamp create(long address) {
        return new GLFWGammaRamp(address, null);
    }

    @Nullable
    public static GLFWGammaRamp createSafe(long address) {
        return address == 0L ? null : new GLFWGammaRamp(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(GLFWGammaRamp.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = GLFWGammaRamp.__create(capacity, SIZEOF);
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
    public static GLFWGammaRamp mallocStack() {
        return GLFWGammaRamp.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static GLFWGammaRamp callocStack() {
        return GLFWGammaRamp.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static GLFWGammaRamp mallocStack(MemoryStack stack) {
        return GLFWGammaRamp.malloc(stack);
    }

    @Deprecated
    public static GLFWGammaRamp callocStack(MemoryStack stack) {
        return GLFWGammaRamp.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return GLFWGammaRamp.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return GLFWGammaRamp.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return GLFWGammaRamp.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return GLFWGammaRamp.calloc(capacity, stack);
    }

    public static GLFWGammaRamp malloc(MemoryStack stack) {
        return new GLFWGammaRamp(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static GLFWGammaRamp calloc(MemoryStack stack) {
        return new GLFWGammaRamp(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static ShortBuffer nred(long struct) {
        return MemoryUtil.memShortBuffer(MemoryUtil.memGetAddress(struct + (long)RED), GLFWGammaRamp.nsize(struct));
    }

    public static ShortBuffer ngreen(long struct) {
        return MemoryUtil.memShortBuffer(MemoryUtil.memGetAddress(struct + (long)GREEN), GLFWGammaRamp.nsize(struct));
    }

    public static ShortBuffer nblue(long struct) {
        return MemoryUtil.memShortBuffer(MemoryUtil.memGetAddress(struct + (long)BLUE), GLFWGammaRamp.nsize(struct));
    }

    public static int nsize(long struct) {
        return UNSAFE.getInt(null, struct + (long)SIZE);
    }

    public static void nred(long struct, ShortBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)RED, MemoryUtil.memAddress(value));
    }

    public static void ngreen(long struct, ShortBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)GREEN, MemoryUtil.memAddress(value));
    }

    public static void nblue(long struct, ShortBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)BLUE, MemoryUtil.memAddress(value));
    }

    public static void nsize(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)SIZE, value);
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)RED));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)GREEN));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)BLUE));
    }

    static {
        Struct.Layout layout = GLFWGammaRamp.__struct(GLFWGammaRamp.__member(POINTER_SIZE), GLFWGammaRamp.__member(POINTER_SIZE), GLFWGammaRamp.__member(POINTER_SIZE), GLFWGammaRamp.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        RED = layout.offsetof(0);
        GREEN = layout.offsetof(1);
        BLUE = layout.offsetof(2);
        SIZE = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<GLFWGammaRamp, Buffer>
    implements NativeResource {
        private static final GLFWGammaRamp ELEMENT_FACTORY = GLFWGammaRamp.create(-1L);

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
        protected GLFWGammaRamp getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="unsigned short *")
        public ShortBuffer red() {
            return GLFWGammaRamp.nred(this.address());
        }

        @NativeType(value="unsigned short *")
        public ShortBuffer green() {
            return GLFWGammaRamp.ngreen(this.address());
        }

        @NativeType(value="unsigned short *")
        public ShortBuffer blue() {
            return GLFWGammaRamp.nblue(this.address());
        }

        @NativeType(value="unsigned int")
        public int size() {
            return GLFWGammaRamp.nsize(this.address());
        }

        public Buffer red(@NativeType(value="unsigned short *") ShortBuffer value) {
            GLFWGammaRamp.nred(this.address(), value);
            return this;
        }

        public Buffer green(@NativeType(value="unsigned short *") ShortBuffer value) {
            GLFWGammaRamp.ngreen(this.address(), value);
            return this;
        }

        public Buffer blue(@NativeType(value="unsigned short *") ShortBuffer value) {
            GLFWGammaRamp.nblue(this.address(), value);
            return this;
        }

        public Buffer size(@NativeType(value="unsigned int") int value) {
            GLFWGammaRamp.nsize(this.address(), value);
            return this;
        }
    }
}
