package org.lwjgl.stb;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct stbrp_node")
public class STBRPNode
extends Struct<STBRPNode>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int X;
    public static final int Y;
    public static final int NEXT;

    protected STBRPNode(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected STBRPNode create(long address, @Nullable ByteBuffer container) {
        return new STBRPNode(address, container);
    }

    public STBRPNode(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), STBRPNode.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="stbrp_coord")
    public int x() {
        return STBRPNode.nx(this.address());
    }

    @NativeType(value="stbrp_coord")
    public int y() {
        return STBRPNode.ny(this.address());
    }

    @Nullable
    @NativeType(value="stbrp_node *")
    public STBRPNode next() {
        return STBRPNode.nnext(this.address());
    }

    public static STBRPNode malloc() {
        return new STBRPNode(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static STBRPNode calloc() {
        return new STBRPNode(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static STBRPNode create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new STBRPNode(MemoryUtil.memAddress(container), container);
    }

    public static STBRPNode create(long address) {
        return new STBRPNode(address, null);
    }

    @Nullable
    public static STBRPNode createSafe(long address) {
        return address == 0L ? null : new STBRPNode(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(STBRPNode.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = STBRPNode.__create(capacity, SIZEOF);
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
    public static STBRPNode mallocStack() {
        return STBRPNode.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBRPNode callocStack() {
        return STBRPNode.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBRPNode mallocStack(MemoryStack stack) {
        return STBRPNode.malloc(stack);
    }

    @Deprecated
    public static STBRPNode callocStack(MemoryStack stack) {
        return STBRPNode.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return STBRPNode.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return STBRPNode.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return STBRPNode.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return STBRPNode.calloc(capacity, stack);
    }

    public static STBRPNode malloc(MemoryStack stack) {
        return new STBRPNode(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static STBRPNode calloc(MemoryStack stack) {
        return new STBRPNode(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nx(long struct) {
        return UNSAFE.getInt(null, struct + (long)X);
    }

    public static int ny(long struct) {
        return UNSAFE.getInt(null, struct + (long)Y);
    }

    @Nullable
    public static STBRPNode nnext(long struct) {
        return STBRPNode.createSafe(MemoryUtil.memGetAddress(struct + (long)NEXT));
    }

    static {
        Struct.Layout layout = STBRPNode.__struct(STBRPNode.__member(4), STBRPNode.__member(4), STBRPNode.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        X = layout.offsetof(0);
        Y = layout.offsetof(1);
        NEXT = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<STBRPNode, Buffer>
    implements NativeResource {
        private static final STBRPNode ELEMENT_FACTORY = STBRPNode.create(-1L);

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
        protected STBRPNode getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="stbrp_coord")
        public int x() {
            return STBRPNode.nx(this.address());
        }

        @NativeType(value="stbrp_coord")
        public int y() {
            return STBRPNode.ny(this.address());
        }

        @Nullable
        @NativeType(value="stbrp_node *")
        public STBRPNode next() {
            return STBRPNode.nnext(this.address());
        }
    }
}
