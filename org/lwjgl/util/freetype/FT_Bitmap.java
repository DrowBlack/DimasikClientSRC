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

public class FT_Bitmap
extends Struct<FT_Bitmap>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int ROWS;
    public static final int WIDTH;
    public static final int PITCH;
    public static final int BUFFER;
    public static final int NUM_GRAYS;
    public static final int PIXEL_MODE;
    public static final int PALETTE_MODE;
    public static final int PALETTE;

    protected FT_Bitmap(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Bitmap create(long address, @Nullable ByteBuffer container) {
        return new FT_Bitmap(address, container);
    }

    public FT_Bitmap(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Bitmap.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="unsigned int")
    public int rows() {
        return FT_Bitmap.nrows(this.address());
    }

    @NativeType(value="unsigned int")
    public int width() {
        return FT_Bitmap.nwidth(this.address());
    }

    public int pitch() {
        return FT_Bitmap.npitch(this.address());
    }

    @Nullable
    @NativeType(value="unsigned char *")
    public ByteBuffer buffer(int capacity) {
        return FT_Bitmap.nbuffer(this.address(), capacity);
    }

    @NativeType(value="unsigned short")
    public short num_grays() {
        return FT_Bitmap.nnum_grays(this.address());
    }

    @NativeType(value="unsigned char")
    public byte pixel_mode() {
        return FT_Bitmap.npixel_mode(this.address());
    }

    @NativeType(value="unsigned char")
    public byte palette_mode() {
        return FT_Bitmap.npalette_mode(this.address());
    }

    @NativeType(value="void *")
    public long palette() {
        return FT_Bitmap.npalette(this.address());
    }

    public static FT_Bitmap malloc() {
        return new FT_Bitmap(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Bitmap calloc() {
        return new FT_Bitmap(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Bitmap create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Bitmap(MemoryUtil.memAddress(container), container);
    }

    public static FT_Bitmap create(long address) {
        return new FT_Bitmap(address, null);
    }

    @Nullable
    public static FT_Bitmap createSafe(long address) {
        return address == 0L ? null : new FT_Bitmap(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Bitmap.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Bitmap.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Bitmap malloc(MemoryStack stack) {
        return new FT_Bitmap(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Bitmap calloc(MemoryStack stack) {
        return new FT_Bitmap(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nrows(long struct) {
        return UNSAFE.getInt(null, struct + (long)ROWS);
    }

    public static int nwidth(long struct) {
        return UNSAFE.getInt(null, struct + (long)WIDTH);
    }

    public static int npitch(long struct) {
        return UNSAFE.getInt(null, struct + (long)PITCH);
    }

    @Nullable
    public static ByteBuffer nbuffer(long struct, int capacity) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)BUFFER), capacity);
    }

    public static short nnum_grays(long struct) {
        return UNSAFE.getShort(null, struct + (long)NUM_GRAYS);
    }

    public static byte npixel_mode(long struct) {
        return UNSAFE.getByte(null, struct + (long)PIXEL_MODE);
    }

    public static byte npalette_mode(long struct) {
        return UNSAFE.getByte(null, struct + (long)PALETTE_MODE);
    }

    public static long npalette(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)PALETTE);
    }

    static {
        Struct.Layout layout = FT_Bitmap.__struct(FT_Bitmap.__member(4), FT_Bitmap.__member(4), FT_Bitmap.__member(4), FT_Bitmap.__member(POINTER_SIZE), FT_Bitmap.__member(2), FT_Bitmap.__member(1), FT_Bitmap.__member(1), FT_Bitmap.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        ROWS = layout.offsetof(0);
        WIDTH = layout.offsetof(1);
        PITCH = layout.offsetof(2);
        BUFFER = layout.offsetof(3);
        NUM_GRAYS = layout.offsetof(4);
        PIXEL_MODE = layout.offsetof(5);
        PALETTE_MODE = layout.offsetof(6);
        PALETTE = layout.offsetof(7);
    }

    public static class Buffer
    extends StructBuffer<FT_Bitmap, Buffer>
    implements NativeResource {
        private static final FT_Bitmap ELEMENT_FACTORY = FT_Bitmap.create(-1L);

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
        protected FT_Bitmap getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="unsigned int")
        public int rows() {
            return FT_Bitmap.nrows(this.address());
        }

        @NativeType(value="unsigned int")
        public int width() {
            return FT_Bitmap.nwidth(this.address());
        }

        public int pitch() {
            return FT_Bitmap.npitch(this.address());
        }

        @Nullable
        @NativeType(value="unsigned char *")
        public ByteBuffer buffer(int capacity) {
            return FT_Bitmap.nbuffer(this.address(), capacity);
        }

        @NativeType(value="unsigned short")
        public short num_grays() {
            return FT_Bitmap.nnum_grays(this.address());
        }

        @NativeType(value="unsigned char")
        public byte pixel_mode() {
            return FT_Bitmap.npixel_mode(this.address());
        }

        @NativeType(value="unsigned char")
        public byte palette_mode() {
            return FT_Bitmap.npalette_mode(this.address());
        }

        @NativeType(value="void *")
        public long palette() {
            return FT_Bitmap.npalette(this.address());
        }
    }
}
