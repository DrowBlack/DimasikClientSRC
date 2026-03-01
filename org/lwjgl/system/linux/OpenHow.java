package org.lwjgl.system.linux;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct open_how")
public class OpenHow
extends Struct<OpenHow>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int FLAGS;
    public static final int MODE;
    public static final int RESOLVE;

    protected OpenHow(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected OpenHow create(long address, @Nullable ByteBuffer container) {
        return new OpenHow(address, container);
    }

    public OpenHow(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), OpenHow.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u64")
    public long flags() {
        return OpenHow.nflags(this.address());
    }

    @NativeType(value="__u64")
    public long mode() {
        return OpenHow.nmode(this.address());
    }

    @NativeType(value="__u64")
    public long resolve() {
        return OpenHow.nresolve(this.address());
    }

    public OpenHow flags(@NativeType(value="__u64") long value) {
        OpenHow.nflags(this.address(), value);
        return this;
    }

    public OpenHow mode(@NativeType(value="__u64") long value) {
        OpenHow.nmode(this.address(), value);
        return this;
    }

    public OpenHow resolve(@NativeType(value="__u64") long value) {
        OpenHow.nresolve(this.address(), value);
        return this;
    }

    public OpenHow set(long flags, long mode, long resolve) {
        this.flags(flags);
        this.mode(mode);
        this.resolve(resolve);
        return this;
    }

    public OpenHow set(OpenHow src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static OpenHow malloc() {
        return new OpenHow(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static OpenHow calloc() {
        return new OpenHow(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static OpenHow create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new OpenHow(MemoryUtil.memAddress(container), container);
    }

    public static OpenHow create(long address) {
        return new OpenHow(address, null);
    }

    @Nullable
    public static OpenHow createSafe(long address) {
        return address == 0L ? null : new OpenHow(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(OpenHow.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = OpenHow.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static OpenHow malloc(MemoryStack stack) {
        return new OpenHow(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static OpenHow calloc(MemoryStack stack) {
        return new OpenHow(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nflags(long struct) {
        return UNSAFE.getLong(null, struct + (long)FLAGS);
    }

    public static long nmode(long struct) {
        return UNSAFE.getLong(null, struct + (long)MODE);
    }

    public static long nresolve(long struct) {
        return UNSAFE.getLong(null, struct + (long)RESOLVE);
    }

    public static void nflags(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)FLAGS, value);
    }

    public static void nmode(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)MODE, value);
    }

    public static void nresolve(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)RESOLVE, value);
    }

    static {
        Struct.Layout layout = OpenHow.__struct(OpenHow.__member(8), OpenHow.__member(8), OpenHow.__member(8));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        FLAGS = layout.offsetof(0);
        MODE = layout.offsetof(1);
        RESOLVE = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<OpenHow, Buffer>
    implements NativeResource {
        private static final OpenHow ELEMENT_FACTORY = OpenHow.create(-1L);

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
        protected OpenHow getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u64")
        public long flags() {
            return OpenHow.nflags(this.address());
        }

        @NativeType(value="__u64")
        public long mode() {
            return OpenHow.nmode(this.address());
        }

        @NativeType(value="__u64")
        public long resolve() {
            return OpenHow.nresolve(this.address());
        }

        public Buffer flags(@NativeType(value="__u64") long value) {
            OpenHow.nflags(this.address(), value);
            return this;
        }

        public Buffer mode(@NativeType(value="__u64") long value) {
            OpenHow.nmode(this.address(), value);
            return this;
        }

        public Buffer resolve(@NativeType(value="__u64") long value) {
            OpenHow.nresolve(this.address(), value);
            return this;
        }
    }
}
