package org.lwjgl.stb;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBRPContext;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct stbtt_pack_context")
public class STBTTPackContext
extends Struct<STBTTPackContext>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int USER_ALLOCATOR_CONTEXT;
    public static final int PACK_INFO;
    public static final int WIDTH;
    public static final int HEIGHT;
    public static final int STRIDE_IN_BYTES;
    public static final int PADDING;
    public static final int SKIP_MISSING;
    public static final int H_OVERSAMPLE;
    public static final int V_OVERSAMPLE;
    public static final int PIXELS;
    public static final int NODES;

    protected STBTTPackContext(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected STBTTPackContext create(long address, @Nullable ByteBuffer container) {
        return new STBTTPackContext(address, container);
    }

    public STBTTPackContext(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), STBTTPackContext.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="void *")
    public long user_allocator_context() {
        return STBTTPackContext.nuser_allocator_context(this.address());
    }

    @NativeType(value="stbrp_context *")
    public STBRPContext pack_info() {
        return STBTTPackContext.npack_info(this.address());
    }

    public int width() {
        return STBTTPackContext.nwidth(this.address());
    }

    public int height() {
        return STBTTPackContext.nheight(this.address());
    }

    public int stride_in_bytes() {
        return STBTTPackContext.nstride_in_bytes(this.address());
    }

    public int padding() {
        return STBTTPackContext.npadding(this.address());
    }

    @NativeType(value="int")
    public boolean skip_missing() {
        return STBTTPackContext.nskip_missing(this.address()) != 0;
    }

    @NativeType(value="unsigned int")
    public int h_oversample() {
        return STBTTPackContext.nh_oversample(this.address());
    }

    @NativeType(value="unsigned int")
    public int v_oversample() {
        return STBTTPackContext.nv_oversample(this.address());
    }

    @NativeType(value="unsigned char *")
    public ByteBuffer pixels(int capacity) {
        return STBTTPackContext.npixels(this.address(), capacity);
    }

    @NativeType(value="stbrp_node *")
    public STBRPNode.Buffer nodes(int capacity) {
        return STBTTPackContext.nnodes(this.address(), capacity);
    }

    public static STBTTPackContext malloc() {
        return new STBTTPackContext(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static STBTTPackContext calloc() {
        return new STBTTPackContext(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static STBTTPackContext create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new STBTTPackContext(MemoryUtil.memAddress(container), container);
    }

    public static STBTTPackContext create(long address) {
        return new STBTTPackContext(address, null);
    }

    @Nullable
    public static STBTTPackContext createSafe(long address) {
        return address == 0L ? null : new STBTTPackContext(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(STBTTPackContext.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = STBTTPackContext.__create(capacity, SIZEOF);
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
    public static STBTTPackContext mallocStack() {
        return STBTTPackContext.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBTTPackContext callocStack() {
        return STBTTPackContext.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBTTPackContext mallocStack(MemoryStack stack) {
        return STBTTPackContext.malloc(stack);
    }

    @Deprecated
    public static STBTTPackContext callocStack(MemoryStack stack) {
        return STBTTPackContext.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return STBTTPackContext.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return STBTTPackContext.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return STBTTPackContext.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return STBTTPackContext.calloc(capacity, stack);
    }

    public static STBTTPackContext malloc(MemoryStack stack) {
        return new STBTTPackContext(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static STBTTPackContext calloc(MemoryStack stack) {
        return new STBTTPackContext(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nuser_allocator_context(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)USER_ALLOCATOR_CONTEXT);
    }

    public static STBRPContext npack_info(long struct) {
        return STBRPContext.create(MemoryUtil.memGetAddress(struct + (long)PACK_INFO));
    }

    public static int nwidth(long struct) {
        return UNSAFE.getInt(null, struct + (long)WIDTH);
    }

    public static int nheight(long struct) {
        return UNSAFE.getInt(null, struct + (long)HEIGHT);
    }

    public static int nstride_in_bytes(long struct) {
        return UNSAFE.getInt(null, struct + (long)STRIDE_IN_BYTES);
    }

    public static int npadding(long struct) {
        return UNSAFE.getInt(null, struct + (long)PADDING);
    }

    public static int nskip_missing(long struct) {
        return UNSAFE.getInt(null, struct + (long)SKIP_MISSING);
    }

    public static int nh_oversample(long struct) {
        return UNSAFE.getInt(null, struct + (long)H_OVERSAMPLE);
    }

    public static int nv_oversample(long struct) {
        return UNSAFE.getInt(null, struct + (long)V_OVERSAMPLE);
    }

    public static ByteBuffer npixels(long struct, int capacity) {
        return MemoryUtil.memByteBuffer(MemoryUtil.memGetAddress(struct + (long)PIXELS), capacity);
    }

    public static STBRPNode.Buffer nnodes(long struct, int capacity) {
        return STBRPNode.create(MemoryUtil.memGetAddress(struct + (long)NODES), capacity);
    }

    static {
        Struct.Layout layout = STBTTPackContext.__struct(STBTTPackContext.__member(POINTER_SIZE), STBTTPackContext.__member(POINTER_SIZE), STBTTPackContext.__member(4), STBTTPackContext.__member(4), STBTTPackContext.__member(4), STBTTPackContext.__member(4), STBTTPackContext.__member(4), STBTTPackContext.__member(4), STBTTPackContext.__member(4), STBTTPackContext.__member(POINTER_SIZE), STBTTPackContext.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        USER_ALLOCATOR_CONTEXT = layout.offsetof(0);
        PACK_INFO = layout.offsetof(1);
        WIDTH = layout.offsetof(2);
        HEIGHT = layout.offsetof(3);
        STRIDE_IN_BYTES = layout.offsetof(4);
        PADDING = layout.offsetof(5);
        SKIP_MISSING = layout.offsetof(6);
        H_OVERSAMPLE = layout.offsetof(7);
        V_OVERSAMPLE = layout.offsetof(8);
        PIXELS = layout.offsetof(9);
        NODES = layout.offsetof(10);
    }

    public static class Buffer
    extends StructBuffer<STBTTPackContext, Buffer>
    implements NativeResource {
        private static final STBTTPackContext ELEMENT_FACTORY = STBTTPackContext.create(-1L);

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
        protected STBTTPackContext getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="void *")
        public long user_allocator_context() {
            return STBTTPackContext.nuser_allocator_context(this.address());
        }

        @NativeType(value="stbrp_context *")
        public STBRPContext pack_info() {
            return STBTTPackContext.npack_info(this.address());
        }

        public int width() {
            return STBTTPackContext.nwidth(this.address());
        }

        public int height() {
            return STBTTPackContext.nheight(this.address());
        }

        public int stride_in_bytes() {
            return STBTTPackContext.nstride_in_bytes(this.address());
        }

        public int padding() {
            return STBTTPackContext.npadding(this.address());
        }

        @NativeType(value="int")
        public boolean skip_missing() {
            return STBTTPackContext.nskip_missing(this.address()) != 0;
        }

        @NativeType(value="unsigned int")
        public int h_oversample() {
            return STBTTPackContext.nh_oversample(this.address());
        }

        @NativeType(value="unsigned int")
        public int v_oversample() {
            return STBTTPackContext.nv_oversample(this.address());
        }

        @NativeType(value="unsigned char *")
        public ByteBuffer pixels(int capacity) {
            return STBTTPackContext.npixels(this.address(), capacity);
        }

        @NativeType(value="stbrp_node *")
        public STBRPNode.Buffer nodes(int capacity) {
            return STBTTPackContext.nnodes(this.address(), capacity);
        }
    }
}
