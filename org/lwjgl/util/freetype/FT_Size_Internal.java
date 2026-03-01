package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_Size_Metrics;

public class FT_Size_Internal
extends Struct<FT_Size_Internal> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int MODULE_DATA;
    public static final int AUTOHINT_MODE;
    public static final int AUTOHINT_METRICS;

    protected FT_Size_Internal(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Size_Internal create(long address, @Nullable ByteBuffer container) {
        return new FT_Size_Internal(address, container);
    }

    public FT_Size_Internal(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Size_Internal.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="void *")
    public long module_data() {
        return FT_Size_Internal.nmodule_data(this.address());
    }

    @NativeType(value="FT_Render_Mode")
    public int autohint_mode() {
        return FT_Size_Internal.nautohint_mode(this.address());
    }

    public FT_Size_Metrics autohint_metrics() {
        return FT_Size_Internal.nautohint_metrics(this.address());
    }

    public static FT_Size_Internal create(long address) {
        return new FT_Size_Internal(address, null);
    }

    @Nullable
    public static FT_Size_Internal createSafe(long address) {
        return address == 0L ? null : new FT_Size_Internal(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static long nmodule_data(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)MODULE_DATA);
    }

    public static int nautohint_mode(long struct) {
        return UNSAFE.getInt(null, struct + (long)AUTOHINT_MODE);
    }

    public static FT_Size_Metrics nautohint_metrics(long struct) {
        return FT_Size_Metrics.create(struct + (long)AUTOHINT_METRICS);
    }

    static {
        Struct.Layout layout = FT_Size_Internal.__struct(FT_Size_Internal.__member(POINTER_SIZE), FT_Size_Internal.__member(4), FT_Size_Internal.__member(FT_Size_Metrics.SIZEOF, FT_Size_Metrics.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        MODULE_DATA = layout.offsetof(0);
        AUTOHINT_MODE = layout.offsetof(1);
        AUTOHINT_METRICS = layout.offsetof(2);
    }

    public static class Buffer
    extends StructBuffer<FT_Size_Internal, Buffer> {
        private static final FT_Size_Internal ELEMENT_FACTORY = FT_Size_Internal.create(-1L);

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
        protected FT_Size_Internal getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="void *")
        public long module_data() {
            return FT_Size_Internal.nmodule_data(this.address());
        }

        @NativeType(value="FT_Render_Mode")
        public int autohint_mode() {
            return FT_Size_Internal.nautohint_mode(this.address());
        }

        public FT_Size_Metrics autohint_metrics() {
            return FT_Size_Internal.nautohint_metrics(this.address());
        }
    }
}
