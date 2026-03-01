package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct FT_ListNodeRec")
public class FT_ListNode
extends Struct<FT_ListNode> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int PREV;
    public static final int NEXT;
    public static final int DATA;

    protected FT_ListNode(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_ListNode create(long address, @Nullable ByteBuffer container) {
        return new FT_ListNode(address, container);
    }

    public FT_ListNode(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_ListNode.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @Nullable
    public FT_ListNode prev() {
        return FT_ListNode.nprev(this.address());
    }

    @Nullable
    public FT_ListNode next() {
        return FT_ListNode.nnext(this.address());
    }

    @NativeType(value="void *")
    public ByteBuffer data(int capacity) {
        return FT_ListNode.ndata(this.address(), capacity);
    }

    public static FT_ListNode create(long address) {
        return new FT_ListNode(address, null);
    }

    @Nullable
    public static FT_ListNode createSafe(long address) {
        return address == 0L ? null : new FT_ListNode(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    @Nullable
    public static FT_ListNode nprev(long struct) {
        return FT_ListNode.createSafe(MemoryUtil.memGetAddress(struct + (long)PREV));
    }

    @Nullable
    public static FT_ListNode nnext(long struct) {
        return FT_ListNode.createSafe(MemoryUtil.memGetAddress(struct + (long)NEXT));
    }

    public static ByteBuffer ndata(long struct, int capacity) {
        return MemoryUtil.memByteBuffer(MemoryUtil.memGetAddress(struct + (long)DATA), capacity);
    }

    static {
        Struct.Layout layout = FT_ListNode.__struct(FT_ListNode.__member(POINTER_SIZE), FT_ListNode.__member(POINTER_SIZE), FT_ListNode.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        PREV = layout.offsetof(0);
        NEXT = layout.offsetof(1);
        DATA = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<FT_ListNode, Buffer> {
        private static final FT_ListNode ELEMENT_FACTORY = FT_ListNode.create(-1L);

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
        protected FT_ListNode getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @Nullable
        public FT_ListNode prev() {
            return FT_ListNode.nprev(this.address());
        }

        @Nullable
        public FT_ListNode next() {
            return FT_ListNode.nnext(this.address());
        }

        @NativeType(value="void *")
        public ByteBuffer data(int capacity) {
            return FT_ListNode.ndata(this.address(), capacity);
        }
    }
}
