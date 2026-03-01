package org.lwjgl.stb;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct stbrp_context")
public class STBRPContext
extends Struct<STBRPContext>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int WIDTH;
    public static final int HEIGHT;
    public static final int ALIGN;
    public static final int INIT_MODE;
    public static final int HEURISTIC;
    public static final int NUM_NODES;
    public static final int ACTIVE_HEAD;
    public static final int FREE_HEAD;
    public static final int EXTRA;

    protected STBRPContext(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected STBRPContext create(long address, @Nullable ByteBuffer container) {
        return new STBRPContext(address, container);
    }

    public STBRPContext(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), STBRPContext.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public int width() {
        return STBRPContext.nwidth(this.address());
    }

    public int height() {
        return STBRPContext.nheight(this.address());
    }

    public int align() {
        return STBRPContext.nalign(this.address());
    }

    public int init_mode() {
        return STBRPContext.ninit_mode(this.address());
    }

    public int heuristic() {
        return STBRPContext.nheuristic(this.address());
    }

    public int num_nodes() {
        return STBRPContext.nnum_nodes(this.address());
    }

    @Nullable
    @NativeType(value="stbrp_node *")
    public STBRPNode active_head() {
        return STBRPContext.nactive_head(this.address());
    }

    @Nullable
    @NativeType(value="stbrp_node *")
    public STBRPNode free_head() {
        return STBRPContext.nfree_head(this.address());
    }

    @NativeType(value="stbrp_node[2]")
    public STBRPNode.Buffer extra() {
        return STBRPContext.nextra(this.address());
    }

    @NativeType(value="stbrp_node")
    public STBRPNode extra(int index) {
        return STBRPContext.nextra(this.address(), index);
    }

    public static STBRPContext malloc() {
        return new STBRPContext(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static STBRPContext calloc() {
        return new STBRPContext(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static STBRPContext create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new STBRPContext(MemoryUtil.memAddress(container), container);
    }

    public static STBRPContext create(long address) {
        return new STBRPContext(address, null);
    }

    @Nullable
    public static STBRPContext createSafe(long address) {
        return address == 0L ? null : new STBRPContext(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(STBRPContext.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = STBRPContext.__create(capacity, SIZEOF);
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
    public static STBRPContext mallocStack() {
        return STBRPContext.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBRPContext callocStack() {
        return STBRPContext.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBRPContext mallocStack(MemoryStack stack) {
        return STBRPContext.malloc(stack);
    }

    @Deprecated
    public static STBRPContext callocStack(MemoryStack stack) {
        return STBRPContext.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return STBRPContext.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return STBRPContext.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return STBRPContext.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return STBRPContext.calloc(capacity, stack);
    }

    public static STBRPContext malloc(MemoryStack stack) {
        return new STBRPContext(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static STBRPContext calloc(MemoryStack stack) {
        return new STBRPContext(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nwidth(long struct) {
        return UNSAFE.getInt(null, struct + (long)WIDTH);
    }

    public static int nheight(long struct) {
        return UNSAFE.getInt(null, struct + (long)HEIGHT);
    }

    public static int nalign(long struct) {
        return UNSAFE.getInt(null, struct + (long)ALIGN);
    }

    public static int ninit_mode(long struct) {
        return UNSAFE.getInt(null, struct + (long)INIT_MODE);
    }

    public static int nheuristic(long struct) {
        return UNSAFE.getInt(null, struct + (long)HEURISTIC);
    }

    public static int nnum_nodes(long struct) {
        return UNSAFE.getInt(null, struct + (long)NUM_NODES);
    }

    @Nullable
    public static STBRPNode nactive_head(long struct) {
        return STBRPNode.createSafe(MemoryUtil.memGetAddress(struct + (long)ACTIVE_HEAD));
    }

    @Nullable
    public static STBRPNode nfree_head(long struct) {
        return STBRPNode.createSafe(MemoryUtil.memGetAddress(struct + (long)FREE_HEAD));
    }

    public static STBRPNode.Buffer nextra(long struct) {
        return STBRPNode.create(struct + (long)EXTRA, 2);
    }

    public static STBRPNode nextra(long struct, int index) {
        return STBRPNode.create(struct + (long)EXTRA + Checks.check(index, 2) * (long)STBRPNode.SIZEOF);
    }

    static {
        Struct.Layout layout = STBRPContext.__struct(STBRPContext.__member(4), STBRPContext.__member(4), STBRPContext.__member(4), STBRPContext.__member(4), STBRPContext.__member(4), STBRPContext.__member(4), STBRPContext.__member(POINTER_SIZE), STBRPContext.__member(POINTER_SIZE), STBRPContext.__array(STBRPNode.SIZEOF, STBRPNode.ALIGNOF, 2));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        WIDTH = layout.offsetof(0);
        HEIGHT = layout.offsetof(1);
        ALIGN = layout.offsetof(2);
        INIT_MODE = layout.offsetof(3);
        HEURISTIC = layout.offsetof(4);
        NUM_NODES = layout.offsetof(5);
        ACTIVE_HEAD = layout.offsetof(6);
        FREE_HEAD = layout.offsetof(7);
        EXTRA = layout.offsetof(8);
    }

    public static class Buffer
    extends StructBuffer<STBRPContext, Buffer>
    implements NativeResource {
        private static final STBRPContext ELEMENT_FACTORY = STBRPContext.create(-1L);

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
        protected STBRPContext getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public int width() {
            return STBRPContext.nwidth(this.address());
        }

        public int height() {
            return STBRPContext.nheight(this.address());
        }

        public int align() {
            return STBRPContext.nalign(this.address());
        }

        public int init_mode() {
            return STBRPContext.ninit_mode(this.address());
        }

        public int heuristic() {
            return STBRPContext.nheuristic(this.address());
        }

        public int num_nodes() {
            return STBRPContext.nnum_nodes(this.address());
        }

        @Nullable
        @NativeType(value="stbrp_node *")
        public STBRPNode active_head() {
            return STBRPContext.nactive_head(this.address());
        }

        @Nullable
        @NativeType(value="stbrp_node *")
        public STBRPNode free_head() {
            return STBRPContext.nfree_head(this.address());
        }

        @NativeType(value="stbrp_node[2]")
        public STBRPNode.Buffer extra() {
            return STBRPContext.nextra(this.address());
        }

        @NativeType(value="stbrp_node")
        public STBRPNode extra(int index) {
            return STBRPContext.nextra(this.address(), index);
        }
    }
}
