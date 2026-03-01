package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct FTC_SBitRec")
public class FTC_SBit
extends Struct<FTC_SBit> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int WIDTH;
    public static final int HEIGHT;
    public static final int LEFT;
    public static final int TOP;
    public static final int FORMAT;
    public static final int MAX_GRAYS;
    public static final int PITCH;
    public static final int XADVANCE;
    public static final int YADVANCE;
    public static final int BUFFER;

    protected FTC_SBit(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FTC_SBit create(long address, @Nullable ByteBuffer container) {
        return new FTC_SBit(address, container);
    }

    public FTC_SBit(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FTC_SBit.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Byte")
    public byte width() {
        return FTC_SBit.nwidth(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte height() {
        return FTC_SBit.nheight(this.address());
    }

    @NativeType(value="FT_Char")
    public byte left() {
        return FTC_SBit.nleft(this.address());
    }

    @NativeType(value="FT_Char")
    public byte top() {
        return FTC_SBit.ntop(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte format() {
        return FTC_SBit.nformat(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte max_grays() {
        return FTC_SBit.nmax_grays(this.address());
    }

    @NativeType(value="FT_Short")
    public short pitch() {
        return FTC_SBit.npitch(this.address());
    }

    @NativeType(value="FT_Char")
    public byte xadvance() {
        return FTC_SBit.nxadvance(this.address());
    }

    @NativeType(value="FT_Char")
    public byte yadvance() {
        return FTC_SBit.nyadvance(this.address());
    }

    @NativeType(value="FT_Byte *")
    public ByteBuffer buffer(int capacity) {
        return FTC_SBit.nbuffer(this.address(), capacity);
    }

    public static FTC_SBit create(long address) {
        return new FTC_SBit(address, null);
    }

    @Nullable
    public static FTC_SBit createSafe(long address) {
        return address == 0L ? null : new FTC_SBit(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static byte nwidth(long struct) {
        return UNSAFE.getByte(null, struct + (long)WIDTH);
    }

    public static byte nheight(long struct) {
        return UNSAFE.getByte(null, struct + (long)HEIGHT);
    }

    public static byte nleft(long struct) {
        return UNSAFE.getByte(null, struct + (long)LEFT);
    }

    public static byte ntop(long struct) {
        return UNSAFE.getByte(null, struct + (long)TOP);
    }

    public static byte nformat(long struct) {
        return UNSAFE.getByte(null, struct + (long)FORMAT);
    }

    public static byte nmax_grays(long struct) {
        return UNSAFE.getByte(null, struct + (long)MAX_GRAYS);
    }

    public static short npitch(long struct) {
        return UNSAFE.getShort(null, struct + (long)PITCH);
    }

    public static byte nxadvance(long struct) {
        return UNSAFE.getByte(null, struct + (long)XADVANCE);
    }

    public static byte nyadvance(long struct) {
        return UNSAFE.getByte(null, struct + (long)YADVANCE);
    }

    public static ByteBuffer nbuffer(long struct, int capacity) {
        return MemoryUtil.memByteBuffer(MemoryUtil.memGetAddress(struct + (long)BUFFER), capacity);
    }

    static {
        Struct.Layout layout = FTC_SBit.__struct(FTC_SBit.__member(1), FTC_SBit.__member(1), FTC_SBit.__member(1), FTC_SBit.__member(1), FTC_SBit.__member(1), FTC_SBit.__member(1), FTC_SBit.__member(2), FTC_SBit.__member(1), FTC_SBit.__member(1), FTC_SBit.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        WIDTH = layout.offsetof(0);
        HEIGHT = layout.offsetof(1);
        LEFT = layout.offsetof(2);
        TOP = layout.offsetof(3);
        FORMAT = layout.offsetof(4);
        MAX_GRAYS = layout.offsetof(5);
        PITCH = layout.offsetof(6);
        XADVANCE = layout.offsetof(7);
        YADVANCE = layout.offsetof(8);
        BUFFER = layout.offsetof(9);
    }

    public static class Buffer
    extends StructBuffer<FTC_SBit, Buffer> {
        private static final FTC_SBit ELEMENT_FACTORY = FTC_SBit.create(-1L);

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
        protected FTC_SBit getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Byte")
        public byte width() {
            return FTC_SBit.nwidth(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte height() {
            return FTC_SBit.nheight(this.address());
        }

        @NativeType(value="FT_Char")
        public byte left() {
            return FTC_SBit.nleft(this.address());
        }

        @NativeType(value="FT_Char")
        public byte top() {
            return FTC_SBit.ntop(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte format() {
            return FTC_SBit.nformat(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte max_grays() {
            return FTC_SBit.nmax_grays(this.address());
        }

        @NativeType(value="FT_Short")
        public short pitch() {
            return FTC_SBit.npitch(this.address());
        }

        @NativeType(value="FT_Char")
        public byte xadvance() {
            return FTC_SBit.nxadvance(this.address());
        }

        @NativeType(value="FT_Char")
        public byte yadvance() {
            return FTC_SBit.nyadvance(this.address());
        }

        @NativeType(value="FT_Byte *")
        public ByteBuffer buffer(int capacity) {
            return FTC_SBit.nbuffer(this.address(), capacity);
        }
    }
}
