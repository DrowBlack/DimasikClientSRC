package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_ListNode;

@NativeType(value="struct FT_ListRec")
public class FT_List
extends Struct<FT_List> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int HEAD;
    public static final int TAIL;

    protected FT_List(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_List create(long address, @Nullable ByteBuffer container) {
        return new FT_List(address, container);
    }

    public FT_List(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_List.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @Nullable
    public FT_ListNode head() {
        return FT_List.nhead(this.address());
    }

    @Nullable
    public FT_ListNode tail() {
        return FT_List.ntail(this.address());
    }

    public static FT_List create(long address) {
        return new FT_List(address, null);
    }

    @Nullable
    public static FT_List createSafe(long address) {
        return address == 0L ? null : new FT_List(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    @Nullable
    public static FT_ListNode nhead(long struct) {
        return FT_ListNode.createSafe(MemoryUtil.memGetAddress(struct + (long)HEAD));
    }

    @Nullable
    public static FT_ListNode ntail(long struct) {
        return FT_ListNode.createSafe(MemoryUtil.memGetAddress(struct + (long)TAIL));
    }

    static {
        Struct.Layout layout = FT_List.__struct(FT_List.__member(POINTER_SIZE), FT_List.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        HEAD = layout.offsetof(0);
        TAIL = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_List, Buffer> {
        private static final FT_List ELEMENT_FACTORY = FT_List.create(-1L);

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
        protected FT_List getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @Nullable
        public FT_ListNode head() {
            return FT_List.nhead(this.address());
        }

        @Nullable
        public FT_ListNode tail() {
            return FT_List.ntail(this.address());
        }
    }
}
