package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct FTC_ScalerRec")
public class FTC_Scaler
extends Struct<FTC_Scaler> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int FACE_ID;
    public static final int WIDTH;
    public static final int HEIGHT;
    public static final int PIXEL;
    public static final int X_RES;
    public static final int Y_RES;

    protected FTC_Scaler(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FTC_Scaler create(long address, @Nullable ByteBuffer container) {
        return new FTC_Scaler(address, container);
    }

    public FTC_Scaler(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FTC_Scaler.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FTC_FaceID")
    public long face_id() {
        return FTC_Scaler.nface_id(this.address());
    }

    @NativeType(value="FT_UInt")
    public int width() {
        return FTC_Scaler.nwidth(this.address());
    }

    @NativeType(value="FT_UInt")
    public int height() {
        return FTC_Scaler.nheight(this.address());
    }

    @NativeType(value="FT_Int")
    public int pixel() {
        return FTC_Scaler.npixel(this.address());
    }

    @NativeType(value="FT_UInt")
    public int x_res() {
        return FTC_Scaler.nx_res(this.address());
    }

    @NativeType(value="FT_UInt")
    public int y_res() {
        return FTC_Scaler.ny_res(this.address());
    }

    public static FTC_Scaler create(long address) {
        return new FTC_Scaler(address, null);
    }

    @Nullable
    public static FTC_Scaler createSafe(long address) {
        return address == 0L ? null : new FTC_Scaler(address, null);
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

    public static int npixel(long struct) {
        return UNSAFE.getInt(null, struct + (long)PIXEL);
    }

    public static int nx_res(long struct) {
        return UNSAFE.getInt(null, struct + (long)X_RES);
    }

    public static int ny_res(long struct) {
        return UNSAFE.getInt(null, struct + (long)Y_RES);
    }

    static {
        Struct.Layout layout = FTC_Scaler.__struct(FTC_Scaler.__member(POINTER_SIZE), FTC_Scaler.__member(4), FTC_Scaler.__member(4), FTC_Scaler.__member(4), FTC_Scaler.__member(4), FTC_Scaler.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        FACE_ID = layout.offsetof(0);
        WIDTH = layout.offsetof(1);
        HEIGHT = layout.offsetof(2);
        PIXEL = layout.offsetof(3);
        X_RES = layout.offsetof(4);
        Y_RES = layout.offsetof(5);
    }

    public static class Buffer
    extends StructBuffer<FTC_Scaler, Buffer> {
        private static final FTC_Scaler ELEMENT_FACTORY = FTC_Scaler.create(-1L);

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
        protected FTC_Scaler getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FTC_FaceID")
        public long face_id() {
            return FTC_Scaler.nface_id(this.address());
        }

        @NativeType(value="FT_UInt")
        public int width() {
            return FTC_Scaler.nwidth(this.address());
        }

        @NativeType(value="FT_UInt")
        public int height() {
            return FTC_Scaler.nheight(this.address());
        }

        @NativeType(value="FT_Int")
        public int pixel() {
            return FTC_Scaler.npixel(this.address());
        }

        @NativeType(value="FT_UInt")
        public int x_res() {
            return FTC_Scaler.nx_res(this.address());
        }

        @NativeType(value="FT_UInt")
        public int y_res() {
            return FTC_Scaler.ny_res(this.address());
        }
    }
}
