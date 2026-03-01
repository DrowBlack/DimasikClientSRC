package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.CLongBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class FT_Var_Named_Style
extends Struct<FT_Var_Named_Style> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int COORDS;
    public static final int STRID;
    public static final int PSID;

    protected FT_Var_Named_Style(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Var_Named_Style create(long address, @Nullable ByteBuffer container) {
        return new FT_Var_Named_Style(address, container);
    }

    public FT_Var_Named_Style(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Var_Named_Style.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Fixed *")
    public CLongBuffer coords(int capacity) {
        return FT_Var_Named_Style.ncoords(this.address(), capacity);
    }

    @NativeType(value="FT_UInt")
    public int strid() {
        return FT_Var_Named_Style.nstrid(this.address());
    }

    @NativeType(value="FT_UInt")
    public int psid() {
        return FT_Var_Named_Style.npsid(this.address());
    }

    public static FT_Var_Named_Style create(long address) {
        return new FT_Var_Named_Style(address, null);
    }

    @Nullable
    public static FT_Var_Named_Style createSafe(long address) {
        return address == 0L ? null : new FT_Var_Named_Style(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static CLongBuffer ncoords(long struct, int capacity) {
        return MemoryUtil.memCLongBuffer(MemoryUtil.memGetAddress(struct + (long)COORDS), capacity);
    }

    public static int nstrid(long struct) {
        return UNSAFE.getInt(null, struct + (long)STRID);
    }

    public static int npsid(long struct) {
        return UNSAFE.getInt(null, struct + (long)PSID);
    }

    static {
        Struct.Layout layout = FT_Var_Named_Style.__struct(FT_Var_Named_Style.__member(POINTER_SIZE), FT_Var_Named_Style.__member(4), FT_Var_Named_Style.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        COORDS = layout.offsetof(0);
        STRID = layout.offsetof(1);
        PSID = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<FT_Var_Named_Style, Buffer> {
        private static final FT_Var_Named_Style ELEMENT_FACTORY = FT_Var_Named_Style.create(-1L);

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
        protected FT_Var_Named_Style getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Fixed *")
        public CLongBuffer coords(int capacity) {
            return FT_Var_Named_Style.ncoords(this.address(), capacity);
        }

        @NativeType(value="FT_UInt")
        public int strid() {
            return FT_Var_Named_Style.nstrid(this.address());
        }

        @NativeType(value="FT_UInt")
        public int psid() {
            return FT_Var_Named_Style.npsid(this.address());
        }
    }
}
