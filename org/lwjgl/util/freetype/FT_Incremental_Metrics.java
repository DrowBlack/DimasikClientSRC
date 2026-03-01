package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct FT_Incremental_MetricsRec")
public class FT_Incremental_Metrics
extends Struct<FT_Incremental_Metrics> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int BEARING_X;
    public static final int BEARING_Y;
    public static final int ADVANCE;
    public static final int ADVANCE_V;

    protected FT_Incremental_Metrics(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Incremental_Metrics create(long address, @Nullable ByteBuffer container) {
        return new FT_Incremental_Metrics(address, container);
    }

    public FT_Incremental_Metrics(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Incremental_Metrics.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Long")
    public long bearing_x() {
        return FT_Incremental_Metrics.nbearing_x(this.address());
    }

    @NativeType(value="FT_Long")
    public long bearing_y() {
        return FT_Incremental_Metrics.nbearing_y(this.address());
    }

    @NativeType(value="FT_Long")
    public long advance() {
        return FT_Incremental_Metrics.nadvance(this.address());
    }

    @NativeType(value="FT_Long")
    public long advance_v() {
        return FT_Incremental_Metrics.nadvance_v(this.address());
    }

    public static FT_Incremental_Metrics create(long address) {
        return new FT_Incremental_Metrics(address, null);
    }

    @Nullable
    public static FT_Incremental_Metrics createSafe(long address) {
        return address == 0L ? null : new FT_Incremental_Metrics(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static long nbearing_x(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)BEARING_X);
    }

    public static long nbearing_y(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)BEARING_Y);
    }

    public static long nadvance(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)ADVANCE);
    }

    public static long nadvance_v(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)ADVANCE_V);
    }

    static {
        Struct.Layout layout = FT_Incremental_Metrics.__struct(FT_Incremental_Metrics.__member(CLONG_SIZE), FT_Incremental_Metrics.__member(CLONG_SIZE), FT_Incremental_Metrics.__member(CLONG_SIZE), FT_Incremental_Metrics.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        BEARING_X = layout.offsetof(0);
        BEARING_Y = layout.offsetof(1);
        ADVANCE = layout.offsetof(2);
        ADVANCE_V = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FT_Incremental_Metrics, Buffer> {
        private static final FT_Incremental_Metrics ELEMENT_FACTORY = FT_Incremental_Metrics.create(-1L);

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
        protected FT_Incremental_Metrics getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Long")
        public long bearing_x() {
            return FT_Incremental_Metrics.nbearing_x(this.address());
        }

        @NativeType(value="FT_Long")
        public long bearing_y() {
            return FT_Incremental_Metrics.nbearing_y(this.address());
        }

        @NativeType(value="FT_Long")
        public long advance() {
            return FT_Incremental_Metrics.nadvance(this.address());
        }

        @NativeType(value="FT_Long")
        public long advance_v() {
            return FT_Incremental_Metrics.nadvance_v(this.address());
        }
    }
}
