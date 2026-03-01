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

@NativeType(value="struct stbtt_aligned_quad")
public class STBTTAlignedQuad
extends Struct<STBTTAlignedQuad>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int X0;
    public static final int Y0;
    public static final int S0;
    public static final int T0;
    public static final int X1;
    public static final int Y1;
    public static final int S1;
    public static final int T1;

    protected STBTTAlignedQuad(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected STBTTAlignedQuad create(long address, @Nullable ByteBuffer container) {
        return new STBTTAlignedQuad(address, container);
    }

    public STBTTAlignedQuad(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), STBTTAlignedQuad.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public float x0() {
        return STBTTAlignedQuad.nx0(this.address());
    }

    public float y0() {
        return STBTTAlignedQuad.ny0(this.address());
    }

    public float s0() {
        return STBTTAlignedQuad.ns0(this.address());
    }

    public float t0() {
        return STBTTAlignedQuad.nt0(this.address());
    }

    public float x1() {
        return STBTTAlignedQuad.nx1(this.address());
    }

    public float y1() {
        return STBTTAlignedQuad.ny1(this.address());
    }

    public float s1() {
        return STBTTAlignedQuad.ns1(this.address());
    }

    public float t1() {
        return STBTTAlignedQuad.nt1(this.address());
    }

    public static STBTTAlignedQuad malloc() {
        return new STBTTAlignedQuad(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static STBTTAlignedQuad calloc() {
        return new STBTTAlignedQuad(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static STBTTAlignedQuad create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new STBTTAlignedQuad(MemoryUtil.memAddress(container), container);
    }

    public static STBTTAlignedQuad create(long address) {
        return new STBTTAlignedQuad(address, null);
    }

    @Nullable
    public static STBTTAlignedQuad createSafe(long address) {
        return address == 0L ? null : new STBTTAlignedQuad(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(STBTTAlignedQuad.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = STBTTAlignedQuad.__create(capacity, SIZEOF);
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
    public static STBTTAlignedQuad mallocStack() {
        return STBTTAlignedQuad.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBTTAlignedQuad callocStack() {
        return STBTTAlignedQuad.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBTTAlignedQuad mallocStack(MemoryStack stack) {
        return STBTTAlignedQuad.malloc(stack);
    }

    @Deprecated
    public static STBTTAlignedQuad callocStack(MemoryStack stack) {
        return STBTTAlignedQuad.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return STBTTAlignedQuad.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return STBTTAlignedQuad.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return STBTTAlignedQuad.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return STBTTAlignedQuad.calloc(capacity, stack);
    }

    public static STBTTAlignedQuad malloc(MemoryStack stack) {
        return new STBTTAlignedQuad(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static STBTTAlignedQuad calloc(MemoryStack stack) {
        return new STBTTAlignedQuad(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static float nx0(long struct) {
        return UNSAFE.getFloat(null, struct + (long)X0);
    }

    public static float ny0(long struct) {
        return UNSAFE.getFloat(null, struct + (long)Y0);
    }

    public static float ns0(long struct) {
        return UNSAFE.getFloat(null, struct + (long)S0);
    }

    public static float nt0(long struct) {
        return UNSAFE.getFloat(null, struct + (long)T0);
    }

    public static float nx1(long struct) {
        return UNSAFE.getFloat(null, struct + (long)X1);
    }

    public static float ny1(long struct) {
        return UNSAFE.getFloat(null, struct + (long)Y1);
    }

    public static float ns1(long struct) {
        return UNSAFE.getFloat(null, struct + (long)S1);
    }

    public static float nt1(long struct) {
        return UNSAFE.getFloat(null, struct + (long)T1);
    }

    static {
        Struct.Layout layout = STBTTAlignedQuad.__struct(STBTTAlignedQuad.__member(4), STBTTAlignedQuad.__member(4), STBTTAlignedQuad.__member(4), STBTTAlignedQuad.__member(4), STBTTAlignedQuad.__member(4), STBTTAlignedQuad.__member(4), STBTTAlignedQuad.__member(4), STBTTAlignedQuad.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        X0 = layout.offsetof(0);
        Y0 = layout.offsetof(1);
        S0 = layout.offsetof(2);
        T0 = layout.offsetof(3);
        X1 = layout.offsetof(4);
        Y1 = layout.offsetof(5);
        S1 = layout.offsetof(6);
        T1 = layout.offsetof(7);
    }

    public static class Buffer
    extends StructBuffer<STBTTAlignedQuad, Buffer>
    implements NativeResource {
        private static final STBTTAlignedQuad ELEMENT_FACTORY = STBTTAlignedQuad.create(-1L);

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
        protected STBTTAlignedQuad getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public float x0() {
            return STBTTAlignedQuad.nx0(this.address());
        }

        public float y0() {
            return STBTTAlignedQuad.ny0(this.address());
        }

        public float s0() {
            return STBTTAlignedQuad.ns0(this.address());
        }

        public float t0() {
            return STBTTAlignedQuad.nt0(this.address());
        }

        public float x1() {
            return STBTTAlignedQuad.nx1(this.address());
        }

        public float y1() {
            return STBTTAlignedQuad.ny1(this.address());
        }

        public float s1() {
            return STBTTAlignedQuad.ns1(this.address());
        }

        public float t1() {
            return STBTTAlignedQuad.nt1(this.address());
        }
    }
}
