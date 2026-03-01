package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct FTC_ImageTypeRec")
public class FTC_ImageType
extends Struct<FTC_ImageType> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int FACE_ID;
    public static final int WIDTH;
    public static final int HEIGHT;
    public static final int FLAGS;

    protected FTC_ImageType(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FTC_ImageType create(long address, @Nullable ByteBuffer container) {
        return new FTC_ImageType(address, container);
    }

    public FTC_ImageType(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FTC_ImageType.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FTC_FaceID")
    public long face_id() {
        return FTC_ImageType.nface_id(this.address());
    }

    @NativeType(value="FT_UInt")
    public int width() {
        return FTC_ImageType.nwidth(this.address());
    }

    @NativeType(value="FT_UInt")
    public int height() {
        return FTC_ImageType.nheight(this.address());
    }

    @NativeType(value="FT_Int32")
    public int flags() {
        return FTC_ImageType.nflags(this.address());
    }

    public static FTC_ImageType create(long address) {
        return new FTC_ImageType(address, null);
    }

    @Nullable
    public static FTC_ImageType createSafe(long address) {
        return address == 0L ? null : new FTC_ImageType(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static long nface_id(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)FACE_ID);
    }

    public static int nwidth(long struct) {
        return UNSAFE.getInt(null, struct + (long)WIDTH);
    }

    public static int nheight(long struct) {
        return UNSAFE.getInt(null, struct + (long)HEIGHT);
    }

    public static int nflags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FLAGS);
    }

    static {
        Struct.Layout layout = FTC_ImageType.__struct(FTC_ImageType.__member(POINTER_SIZE), FTC_ImageType.__member(4), FTC_ImageType.__member(4), FTC_ImageType.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        FACE_ID = layout.offsetof(0);
        WIDTH = layout.offsetof(1);
        HEIGHT = layout.offsetof(2);
        FLAGS = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FTC_ImageType, Buffer> {
        private static final FTC_ImageType ELEMENT_FACTORY = FTC_ImageType.create(-1L);

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
        protected FTC_ImageType getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FTC_FaceID")
        public long face_id() {
            return FTC_ImageType.nface_id(this.address());
        }

        @NativeType(value="FT_UInt")
        public int width() {
            return FTC_ImageType.nwidth(this.address());
        }

        @NativeType(value="FT_UInt")
        public int height() {
            return FTC_ImageType.nheight(this.address());
        }

        @NativeType(value="FT_Int32")
        public int flags() {
            return FTC_ImageType.nflags(this.address());
        }
    }
}
