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

@NativeType(value="struct stbrp_rect")
public class STBRPRect
extends Struct<STBRPRect>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int ID;
    public static final int W;
    public static final int H;
    public static final int X;
    public static final int Y;
    public static final int WAS_PACKED;

    protected STBRPRect(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected STBRPRect create(long address, @Nullable ByteBuffer container) {
        return new STBRPRect(address, container);
    }

    public STBRPRect(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), STBRPRect.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public int id() {
        return STBRPRect.nid(this.address());
    }

    @NativeType(value="stbrp_coord")
    public int w() {
        return STBRPRect.nw(this.address());
    }

    @NativeType(value="stbrp_coord")
    public int h() {
        return STBRPRect.nh(this.address());
    }

    @NativeType(value="stbrp_coord")
    public int x() {
        return STBRPRect.nx(this.address());
    }

    @NativeType(value="stbrp_coord")
    public int y() {
        return STBRPRect.ny(this.address());
    }

    @NativeType(value="int")
    public boolean was_packed() {
        return STBRPRect.nwas_packed(this.address()) != 0;
    }

    public STBRPRect id(int value) {
        STBRPRect.nid(this.address(), value);
        return this;
    }

    public STBRPRect w(@NativeType(value="stbrp_coord") int value) {
        STBRPRect.nw(this.address(), value);
        return this;
    }

    public STBRPRect h(@NativeType(value="stbrp_coord") int value) {
        STBRPRect.nh(this.address(), value);
        return this;
    }

    public STBRPRect x(@NativeType(value="stbrp_coord") int value) {
        STBRPRect.nx(this.address(), value);
        return this;
    }

    public STBRPRect y(@NativeType(value="stbrp_coord") int value) {
        STBRPRect.ny(this.address(), value);
        return this;
    }

    public STBRPRect was_packed(@NativeType(value="int") boolean value) {
        STBRPRect.nwas_packed(this.address(), value ? 1 : 0);
        return this;
    }

    public STBRPRect set(int id, int w, int h, int x, int y, boolean was_packed) {
        this.id(id);
        this.w(w);
        this.h(h);
        this.x(x);
        this.y(y);
        this.was_packed(was_packed);
        return this;
    }

    public STBRPRect set(STBRPRect src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static STBRPRect malloc() {
        return new STBRPRect(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static STBRPRect calloc() {
        return new STBRPRect(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static STBRPRect create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new STBRPRect(MemoryUtil.memAddress(container), container);
    }

    public static STBRPRect create(long address) {
        return new STBRPRect(address, null);
    }

    @Nullable
    public static STBRPRect createSafe(long address) {
        return address == 0L ? null : new STBRPRect(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(STBRPRect.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = STBRPRect.__create(capacity, SIZEOF);
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
    public static STBRPRect mallocStack() {
        return STBRPRect.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBRPRect callocStack() {
        return STBRPRect.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBRPRect mallocStack(MemoryStack stack) {
        return STBRPRect.malloc(stack);
    }

    @Deprecated
    public static STBRPRect callocStack(MemoryStack stack) {
        return STBRPRect.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return STBRPRect.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return STBRPRect.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return STBRPRect.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return STBRPRect.calloc(capacity, stack);
    }

    public static STBRPRect malloc(MemoryStack stack) {
        return new STBRPRect(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static STBRPRect calloc(MemoryStack stack) {
        return new STBRPRect(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nid(long struct) {
        return UNSAFE.getInt(null, struct + (long)ID);
    }

    public static int nw(long struct) {
        return UNSAFE.getInt(null, struct + (long)W);
    }

    public static int nh(long struct) {
        return UNSAFE.getInt(null, struct + (long)H);
    }

    public static int nx(long struct) {
        return UNSAFE.getInt(null, struct + (long)X);
    }

    public static int ny(long struct) {
        return UNSAFE.getInt(null, struct + (long)Y);
    }

    public static int nwas_packed(long struct) {
        return UNSAFE.getInt(null, struct + (long)WAS_PACKED);
    }

    public static void nid(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)ID, value);
    }

    public static void nw(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)W, value);
    }

    public static void nh(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)H, value);
    }

    public static void nx(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)X, value);
    }

    public static void ny(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)Y, value);
    }

    public static void nwas_packed(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)WAS_PACKED, value);
    }

    static {
        Struct.Layout layout = STBRPRect.__struct(STBRPRect.__member(4), STBRPRect.__member(4), STBRPRect.__member(4), STBRPRect.__member(4), STBRPRect.__member(4), STBRPRect.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        ID = layout.offsetof(0);
        W = layout.offsetof(1);
        H = layout.offsetof(2);
        X = layout.offsetof(3);
        Y = layout.offsetof(4);
        WAS_PACKED = layout.offsetof(5);
    }

    public static class Buffer
    extends StructBuffer<STBRPRect, Buffer>
    implements NativeResource {
        private static final STBRPRect ELEMENT_FACTORY = STBRPRect.create(-1L);

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
        protected STBRPRect getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public int id() {
            return STBRPRect.nid(this.address());
        }

        @NativeType(value="stbrp_coord")
        public int w() {
            return STBRPRect.nw(this.address());
        }

        @NativeType(value="stbrp_coord")
        public int h() {
            return STBRPRect.nh(this.address());
        }

        @NativeType(value="stbrp_coord")
        public int x() {
            return STBRPRect.nx(this.address());
        }

        @NativeType(value="stbrp_coord")
        public int y() {
            return STBRPRect.ny(this.address());
        }

        @NativeType(value="int")
        public boolean was_packed() {
            return STBRPRect.nwas_packed(this.address()) != 0;
        }

        public Buffer id(int value) {
            STBRPRect.nid(this.address(), value);
            return this;
        }

        public Buffer w(@NativeType(value="stbrp_coord") int value) {
            STBRPRect.nw(this.address(), value);
            return this;
        }

        public Buffer h(@NativeType(value="stbrp_coord") int value) {
            STBRPRect.nh(this.address(), value);
            return this;
        }

        public Buffer x(@NativeType(value="stbrp_coord") int value) {
            STBRPRect.nx(this.address(), value);
            return this;
        }

        public Buffer y(@NativeType(value="stbrp_coord") int value) {
            STBRPRect.ny(this.address(), value);
            return this;
        }

        public Buffer was_packed(@NativeType(value="int") boolean value) {
            STBRPRect.nwas_packed(this.address(), value ? 1 : 0);
            return this;
        }
    }
}
