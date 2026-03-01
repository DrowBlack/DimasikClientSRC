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

public class FT_Color
extends Struct<FT_Color>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int BLUE;
    public static final int GREEN;
    public static final int RED;
    public static final int ALPHA;

    protected FT_Color(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Color create(long address, @Nullable ByteBuffer container) {
        return new FT_Color(address, container);
    }

    public FT_Color(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Color.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Byte")
    public byte blue() {
        return FT_Color.nblue(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte green() {
        return FT_Color.ngreen(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte red() {
        return FT_Color.nred(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte alpha() {
        return FT_Color.nalpha(this.address());
    }

    public FT_Color blue(@NativeType(value="FT_Byte") byte value) {
        FT_Color.nblue(this.address(), value);
        return this;
    }

    public FT_Color green(@NativeType(value="FT_Byte") byte value) {
        FT_Color.ngreen(this.address(), value);
        return this;
    }

    public FT_Color red(@NativeType(value="FT_Byte") byte value) {
        FT_Color.nred(this.address(), value);
        return this;
    }

    public FT_Color alpha(@NativeType(value="FT_Byte") byte value) {
        FT_Color.nalpha(this.address(), value);
        return this;
    }

    public FT_Color set(byte blue, byte green, byte red, byte alpha) {
        this.blue(blue);
        this.green(green);
        this.red(red);
        this.alpha(alpha);
        return this;
    }

    public FT_Color set(FT_Color src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Color malloc() {
        return new FT_Color(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Color calloc() {
        return new FT_Color(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Color create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Color(MemoryUtil.memAddress(container), container);
    }

    public static FT_Color create(long address) {
        return new FT_Color(address, null);
    }

    @Nullable
    public static FT_Color createSafe(long address) {
        return address == 0L ? null : new FT_Color(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Color.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Color.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Color malloc(MemoryStack stack) {
        return new FT_Color(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Color calloc(MemoryStack stack) {
        return new FT_Color(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static byte nblue(long struct) {
        return UNSAFE.getByte(null, struct + (long)BLUE);
    }

    public static byte ngreen(long struct) {
        return UNSAFE.getByte(null, struct + (long)GREEN);
    }

    public static byte nred(long struct) {
        return UNSAFE.getByte(null, struct + (long)RED);
    }

    public static byte nalpha(long struct) {
        return UNSAFE.getByte(null, struct + (long)ALPHA);
    }

    public static void nblue(long struct, byte value) {
        UNSAFE.putByte(null, struct + (long)BLUE, value);
    }

    public static void ngreen(long struct, byte value) {
        UNSAFE.putByte(null, struct + (long)GREEN, value);
    }

    public static void nred(long struct, byte value) {
        UNSAFE.putByte(null, struct + (long)RED, value);
    }

    public static void nalpha(long struct, byte value) {
        UNSAFE.putByte(null, struct + (long)ALPHA, value);
    }

    static {
        Struct.Layout layout = FT_Color.__struct(FT_Color.__member(1), FT_Color.__member(1), FT_Color.__member(1), FT_Color.__member(1));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        BLUE = layout.offsetof(0);
        GREEN = layout.offsetof(1);
        RED = layout.offsetof(2);
        ALPHA = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FT_Color, Buffer>
    implements NativeResource {
        private static final FT_Color ELEMENT_FACTORY = FT_Color.create(-1L);

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
        protected FT_Color getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Byte")
        public byte blue() {
            return FT_Color.nblue(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte green() {
            return FT_Color.ngreen(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte red() {
            return FT_Color.nred(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte alpha() {
            return FT_Color.nalpha(this.address());
        }

        public Buffer blue(@NativeType(value="FT_Byte") byte value) {
            FT_Color.nblue(this.address(), value);
            return this;
        }

        public Buffer green(@NativeType(value="FT_Byte") byte value) {
            FT_Color.ngreen(this.address(), value);
            return this;
        }

        public Buffer red(@NativeType(value="FT_Byte") byte value) {
            FT_Color.nred(this.address(), value);
            return this;
        }

        public Buffer alpha(@NativeType(value="FT_Byte") byte value) {
            FT_Color.nalpha(this.address(), value);
            return this;
        }
    }
}
