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

@NativeType(value="struct f_owner_ex")
public class FOwnerEx
extends Struct<FOwnerEx>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int TYPE;
    public static final int PID;

    protected FOwnerEx(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FOwnerEx create(long address, @Nullable ByteBuffer container) {
        return new FOwnerEx(address, container);
    }

    public FOwnerEx(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FOwnerEx.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public int type() {
        return FOwnerEx.ntype(this.address());
    }

    @NativeType(value="pid_t")
    public int pid() {
        return FOwnerEx.npid(this.address());
    }

    public FOwnerEx type(int value) {
        FOwnerEx.ntype(this.address(), value);
        return this;
    }

    public FOwnerEx pid(@NativeType(value="pid_t") int value) {
        FOwnerEx.npid(this.address(), value);
        return this;
    }

    public FOwnerEx set(int type, int pid) {
        this.type(type);
        this.pid(pid);
        return this;
    }

    public FOwnerEx set(FOwnerEx src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FOwnerEx malloc() {
        return new FOwnerEx(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FOwnerEx calloc() {
        return new FOwnerEx(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FOwnerEx create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FOwnerEx(MemoryUtil.memAddress(container), container);
    }

    public static FOwnerEx create(long address) {
        return new FOwnerEx(address, null);
    }

    @Nullable
    public static FOwnerEx createSafe(long address) {
        return address == 0L ? null : new FOwnerEx(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FOwnerEx.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FOwnerEx.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FOwnerEx malloc(MemoryStack stack) {
        return new FOwnerEx(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FOwnerEx calloc(MemoryStack stack) {
        return new FOwnerEx(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int ntype(long struct) {
        return UNSAFE.getInt(null, struct + (long)TYPE);
    }

    public static int npid(long struct) {
        return UNSAFE.getInt(null, struct + (long)PID);
    }

    public static void ntype(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)TYPE, value);
    }

    public static void npid(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)PID, value);
    }

    static {
        Struct.Layout layout = FOwnerEx.__struct(FOwnerEx.__member(4), FOwnerEx.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        TYPE = layout.offsetof(0);
        PID = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FOwnerEx, Buffer>
    implements NativeResource {
        private static final FOwnerEx ELEMENT_FACTORY = FOwnerEx.create(-1L);

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
        protected FOwnerEx getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public int type() {
            return FOwnerEx.ntype(this.address());
        }

        @NativeType(value="pid_t")
        public int pid() {
            return FOwnerEx.npid(this.address());
        }

        public Buffer type(int value) {
            FOwnerEx.ntype(this.address(), value);
            return this;
        }

        public Buffer pid(@NativeType(value="pid_t") int value) {
            FOwnerEx.npid(this.address(), value);
            return this;
        }
    }
}
