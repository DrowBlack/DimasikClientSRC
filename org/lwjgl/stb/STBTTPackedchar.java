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

@NativeType(value="struct stbtt_packedchar")
public class STBTTPackedchar
extends Struct<STBTTPackedchar>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int X0;
    public static final int Y0;
    public static final int X1;
    public static final int Y1;
    public static final int XOFF;
    public static final int YOFF;
    public static final int XADVANCE;
    public static final int XOFF2;
    public static final int YOFF2;

    protected STBTTPackedchar(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected STBTTPackedchar create(long address, @Nullable ByteBuffer container) {
        return new STBTTPackedchar(address, container);
    }

    public STBTTPackedchar(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), STBTTPackedchar.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="unsigned short")
    public short x0() {
        return STBTTPackedchar.nx0(this.address());
    }

    @NativeType(value="unsigned short")
    public short y0() {
        return STBTTPackedchar.ny0(this.address());
    }

    @NativeType(value="unsigned short")
    public short x1() {
        return STBTTPackedchar.nx1(this.address());
    }

    @NativeType(value="unsigned short")
    public short y1() {
        return STBTTPackedchar.ny1(this.address());
    }

    public float xoff() {
        return STBTTPackedchar.nxoff(this.address());
    }

    public float yoff() {
        return STBTTPackedchar.nyoff(this.address());
    }

    public float xadvance() {
        return STBTTPackedchar.nxadvance(this.address());
    }

    public float xoff2() {
        return STBTTPackedchar.nxoff2(this.address());
    }

    public float yoff2() {
        return STBTTPackedchar.nyoff2(this.address());
    }

    public STBTTPackedchar x0(@NativeType(value="unsigned short") short value) {
        STBTTPackedchar.nx0(this.address(), value);
        return this;
    }

    public STBTTPackedchar y0(@NativeType(value="unsigned short") short value) {
        STBTTPackedchar.ny0(this.address(), value);
        return this;
    }

    public STBTTPackedchar x1(@NativeType(value="unsigned short") short value) {
        STBTTPackedchar.nx1(this.address(), value);
        return this;
    }

    public STBTTPackedchar y1(@NativeType(value="unsigned short") short value) {
        STBTTPackedchar.ny1(this.address(), value);
        return this;
    }

    public STBTTPackedchar xoff(float value) {
        STBTTPackedchar.nxoff(this.address(), value);
        return this;
    }

    public STBTTPackedchar yoff(float value) {
        STBTTPackedchar.nyoff(this.address(), value);
        return this;
    }

    public STBTTPackedchar xadvance(float value) {
        STBTTPackedchar.nxadvance(this.address(), value);
        return this;
    }

    public STBTTPackedchar xoff2(float value) {
        STBTTPackedchar.nxoff2(this.address(), value);
        return this;
    }

    public STBTTPackedchar yoff2(float value) {
        STBTTPackedchar.nyoff2(this.address(), value);
        return this;
    }

    public STBTTPackedchar set(short x0, short y0, short x1, short y1, float xoff, float yoff, float xadvance, float xoff2, float yoff2) {
        this.x0(x0);
        this.y0(y0);
        this.x1(x1);
        this.y1(y1);
        this.xoff(xoff);
        this.yoff(yoff);
        this.xadvance(xadvance);
        this.xoff2(xoff2);
        this.yoff2(yoff2);
        return this;
    }

    public STBTTPackedchar set(STBTTPackedchar src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static STBTTPackedchar malloc() {
        return new STBTTPackedchar(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static STBTTPackedchar calloc() {
        return new STBTTPackedchar(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static STBTTPackedchar create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new STBTTPackedchar(MemoryUtil.memAddress(container), container);
    }

    public static STBTTPackedchar create(long address) {
        return new STBTTPackedchar(address, null);
    }

    @Nullable
    public static STBTTPackedchar createSafe(long address) {
        return address == 0L ? null : new STBTTPackedchar(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(STBTTPackedchar.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = STBTTPackedchar.__create(capacity, SIZEOF);
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
    public static STBTTPackedchar mallocStack() {
        return STBTTPackedchar.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBTTPackedchar callocStack() {
        return STBTTPackedchar.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBTTPackedchar mallocStack(MemoryStack stack) {
        return STBTTPackedchar.malloc(stack);
    }

    @Deprecated
    public static STBTTPackedchar callocStack(MemoryStack stack) {
        return STBTTPackedchar.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return STBTTPackedchar.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return STBTTPackedchar.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return STBTTPackedchar.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return STBTTPackedchar.calloc(capacity, stack);
    }

    public static STBTTPackedchar malloc(MemoryStack stack) {
        return new STBTTPackedchar(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static STBTTPackedchar calloc(MemoryStack stack) {
        return new STBTTPackedchar(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static short nx0(long struct) {
        return UNSAFE.getShort(null, struct + (long)X0);
    }

    public static short ny0(long struct) {
        return UNSAFE.getShort(null, struct + (long)Y0);
    }

    public static short nx1(long struct) {
        return UNSAFE.getShort(null, struct + (long)X1);
    }

    public static short ny1(long struct) {
        return UNSAFE.getShort(null, struct + (long)Y1);
    }

    public static float nxoff(long struct) {
        return UNSAFE.getFloat(null, struct + (long)XOFF);
    }

    public static float nyoff(long struct) {
        return UNSAFE.getFloat(null, struct + (long)YOFF);
    }

    public static float nxadvance(long struct) {
        return UNSAFE.getFloat(null, struct + (long)XADVANCE);
    }

    public static float nxoff2(long struct) {
        return UNSAFE.getFloat(null, struct + (long)XOFF2);
    }

    public static float nyoff2(long struct) {
        return UNSAFE.getFloat(null, struct + (long)YOFF2);
    }

    public static void nx0(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)X0, value);
    }

    public static void ny0(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)Y0, value);
    }

    public static void nx1(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)X1, value);
    }

    public static void ny1(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)Y1, value);
    }

    public static void nxoff(long struct, float value) {
        UNSAFE.putFloat(null, struct + (long)XOFF, value);
    }

    public static void nyoff(long struct, float value) {
        UNSAFE.putFloat(null, struct + (long)YOFF, value);
    }

    public static void nxadvance(long struct, float value) {
        UNSAFE.putFloat(null, struct + (long)XADVANCE, value);
    }

    public static void nxoff2(long struct, float value) {
        UNSAFE.putFloat(null, struct + (long)XOFF2, value);
    }

    public static void nyoff2(long struct, float value) {
        UNSAFE.putFloat(null, struct + (long)YOFF2, value);
    }

    static {
        Struct.Layout layout = STBTTPackedchar.__struct(STBTTPackedchar.__member(2), STBTTPackedchar.__member(2), STBTTPackedchar.__member(2), STBTTPackedchar.__member(2), STBTTPackedchar.__member(4), STBTTPackedchar.__member(4), STBTTPackedchar.__member(4), STBTTPackedchar.__member(4), STBTTPackedchar.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        X0 = layout.offsetof(0);
        Y0 = layout.offsetof(1);
        X1 = layout.offsetof(2);
        Y1 = layout.offsetof(3);
        XOFF = layout.offsetof(4);
        YOFF = layout.offsetof(5);
        XADVANCE = layout.offsetof(6);
        XOFF2 = layout.offsetof(7);
        YOFF2 = layout.offsetof(8);
    }

    public static class Buffer
    extends StructBuffer<STBTTPackedchar, Buffer>
    implements NativeResource {
        private static final STBTTPackedchar ELEMENT_FACTORY = STBTTPackedchar.create(-1L);

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
        protected STBTTPackedchar getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="unsigned short")
        public short x0() {
            return STBTTPackedchar.nx0(this.address());
        }

        @NativeType(value="unsigned short")
        public short y0() {
            return STBTTPackedchar.ny0(this.address());
        }

        @NativeType(value="unsigned short")
        public short x1() {
            return STBTTPackedchar.nx1(this.address());
        }

        @NativeType(value="unsigned short")
        public short y1() {
            return STBTTPackedchar.ny1(this.address());
        }

        public float xoff() {
            return STBTTPackedchar.nxoff(this.address());
        }

        public float yoff() {
            return STBTTPackedchar.nyoff(this.address());
        }

        public float xadvance() {
            return STBTTPackedchar.nxadvance(this.address());
        }

        public float xoff2() {
            return STBTTPackedchar.nxoff2(this.address());
        }

        public float yoff2() {
            return STBTTPackedchar.nyoff2(this.address());
        }

        public Buffer x0(@NativeType(value="unsigned short") short value) {
            STBTTPackedchar.nx0(this.address(), value);
            return this;
        }

        public Buffer y0(@NativeType(value="unsigned short") short value) {
            STBTTPackedchar.ny0(this.address(), value);
            return this;
        }

        public Buffer x1(@NativeType(value="unsigned short") short value) {
            STBTTPackedchar.nx1(this.address(), value);
            return this;
        }

        public Buffer y1(@NativeType(value="unsigned short") short value) {
            STBTTPackedchar.ny1(this.address(), value);
            return this;
        }

        public Buffer xoff(float value) {
            STBTTPackedchar.nxoff(this.address(), value);
            return this;
        }

        public Buffer yoff(float value) {
            STBTTPackedchar.nyoff(this.address(), value);
            return this;
        }

        public Buffer xadvance(float value) {
            STBTTPackedchar.nxadvance(this.address(), value);
            return this;
        }

        public Buffer xoff2(float value) {
            STBTTPackedchar.nxoff2(this.address(), value);
            return this;
        }

        public Buffer yoff2(float value) {
            STBTTPackedchar.nyoff2(this.address(), value);
            return this;
        }
    }
}
