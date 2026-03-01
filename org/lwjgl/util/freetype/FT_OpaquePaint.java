package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct FT_OpaquePaintRec")
public class FT_OpaquePaint
extends Struct<FT_OpaquePaint>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int P;
    public static final int INSERT_ROOT_TRANSFORM;

    protected FT_OpaquePaint(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_OpaquePaint create(long address, @Nullable ByteBuffer container) {
        return new FT_OpaquePaint(address, container);
    }

    public FT_OpaquePaint(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_OpaquePaint.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @Nullable
    @NativeType(value="FT_Byte *")
    public ByteBuffer p(int capacity) {
        return FT_OpaquePaint.np(this.address(), capacity);
    }

    @NativeType(value="FT_Bool")
    public boolean insert_root_transform() {
        return FT_OpaquePaint.ninsert_root_transform(this.address());
    }

    public static FT_OpaquePaint malloc() {
        return new FT_OpaquePaint(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_OpaquePaint calloc() {
        return new FT_OpaquePaint(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_OpaquePaint create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_OpaquePaint(MemoryUtil.memAddress(container), container);
    }

    public static FT_OpaquePaint create(long address) {
        return new FT_OpaquePaint(address, null);
    }

    @Nullable
    public static FT_OpaquePaint createSafe(long address) {
        return address == 0L ? null : new FT_OpaquePaint(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_OpaquePaint.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_OpaquePaint.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_OpaquePaint malloc(MemoryStack stack) {
        return new FT_OpaquePaint(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_OpaquePaint calloc(MemoryStack stack) {
        return new FT_OpaquePaint(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    @Nullable
    public static ByteBuffer np(long struct, int capacity) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)P), capacity);
    }

    public static boolean ninsert_root_transform(long struct) {
        return UNSAFE.getByte(null, struct + (long)INSERT_ROOT_TRANSFORM) != 0;
    }

    static {
        Struct.Layout layout = FT_OpaquePaint.__struct(FT_OpaquePaint.__member(POINTER_SIZE), FT_OpaquePaint.__member(1));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        P = layout.offsetof(0);
        INSERT_ROOT_TRANSFORM = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_OpaquePaint, Buffer>
    implements NativeResource {
        private static final FT_OpaquePaint ELEMENT_FACTORY = FT_OpaquePaint.create(-1L);

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
        protected FT_OpaquePaint getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @Nullable
        @NativeType(value="FT_Byte *")
        public ByteBuffer p(int capacity) {
            return FT_OpaquePaint.np(this.address(), capacity);
        }

        @NativeType(value="FT_Bool")
        public boolean insert_root_transform() {
            return FT_OpaquePaint.ninsert_root_transform(this.address());
        }
    }
}
