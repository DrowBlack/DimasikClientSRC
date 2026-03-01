package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_BBox;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_SpanFunc;
import org.lwjgl.util.freetype.FT_SpanFuncI;

public class FT_Raster_Params
extends Struct<FT_Raster_Params>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int TARGET;
    public static final int SOURCE;
    public static final int FLAGS;
    public static final int GRAY_SPANS;
    public static final int BLACK_SPANS;
    public static final int BIT_TEST;
    public static final int BIT_SET;
    public static final int USER;
    public static final int CLIP_BOX;

    protected FT_Raster_Params(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Raster_Params create(long address, @Nullable ByteBuffer container) {
        return new FT_Raster_Params(address, container);
    }

    public FT_Raster_Params(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Raster_Params.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @Nullable
    @NativeType(value="FT_Bitmap const *")
    public FT_Bitmap target() {
        return FT_Raster_Params.ntarget(this.address());
    }

    @Nullable
    @NativeType(value="void const *")
    public ByteBuffer source(int capacity) {
        return FT_Raster_Params.nsource(this.address(), capacity);
    }

    public int flags() {
        return FT_Raster_Params.nflags(this.address());
    }

    @Nullable
    public FT_SpanFunc gray_spans() {
        return FT_Raster_Params.ngray_spans(this.address());
    }

    @NativeType(value="void *")
    public long user() {
        return FT_Raster_Params.nuser(this.address());
    }

    public FT_BBox clip_box() {
        return FT_Raster_Params.nclip_box(this.address());
    }

    public FT_Raster_Params target(@Nullable @NativeType(value="FT_Bitmap const *") FT_Bitmap value) {
        FT_Raster_Params.ntarget(this.address(), value);
        return this;
    }

    public FT_Raster_Params source(@Nullable @NativeType(value="void const *") ByteBuffer value) {
        FT_Raster_Params.nsource(this.address(), value);
        return this;
    }

    public FT_Raster_Params flags(int value) {
        FT_Raster_Params.nflags(this.address(), value);
        return this;
    }

    public FT_Raster_Params gray_spans(@Nullable @NativeType(value="FT_SpanFunc") FT_SpanFuncI value) {
        FT_Raster_Params.ngray_spans(this.address(), value);
        return this;
    }

    public FT_Raster_Params user(@NativeType(value="void *") long value) {
        FT_Raster_Params.nuser(this.address(), value);
        return this;
    }

    public FT_Raster_Params clip_box(FT_BBox value) {
        FT_Raster_Params.nclip_box(this.address(), value);
        return this;
    }

    public FT_Raster_Params clip_box(Consumer<FT_BBox> consumer) {
        consumer.accept(this.clip_box());
        return this;
    }

    public FT_Raster_Params set(@Nullable FT_Bitmap target, @Nullable ByteBuffer source, int flags, @Nullable FT_SpanFuncI gray_spans, long user, FT_BBox clip_box) {
        this.target(target);
        this.source(source);
        this.flags(flags);
        this.gray_spans(gray_spans);
        this.user(user);
        this.clip_box(clip_box);
        return this;
    }

    public FT_Raster_Params set(FT_Raster_Params src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Raster_Params malloc() {
        return new FT_Raster_Params(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Raster_Params calloc() {
        return new FT_Raster_Params(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Raster_Params create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Raster_Params(MemoryUtil.memAddress(container), container);
    }

    public static FT_Raster_Params create(long address) {
        return new FT_Raster_Params(address, null);
    }

    @Nullable
    public static FT_Raster_Params createSafe(long address) {
        return address == 0L ? null : new FT_Raster_Params(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Raster_Params.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Raster_Params.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Raster_Params malloc(MemoryStack stack) {
        return new FT_Raster_Params(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Raster_Params calloc(MemoryStack stack) {
        return new FT_Raster_Params(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    @Nullable
    public static FT_Bitmap ntarget(long struct) {
        return FT_Bitmap.createSafe(MemoryUtil.memGetAddress(struct + (long)TARGET));
    }

    @Nullable
    public static ByteBuffer nsource(long struct, int capacity) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)SOURCE), capacity);
    }

    public static int nflags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FLAGS);
    }

    @Nullable
    public static FT_SpanFunc ngray_spans(long struct) {
        return FT_SpanFunc.createSafe(MemoryUtil.memGetAddress(struct + (long)GRAY_SPANS));
    }

    @Nullable
    public static FT_SpanFunc nblack_spans(long struct) {
        return FT_SpanFunc.createSafe(MemoryUtil.memGetAddress(struct + (long)BLACK_SPANS));
    }

    public static long nbit_test(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)BIT_TEST);
    }

    public static long nbit_set(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)BIT_SET);
    }

    public static long nuser(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)USER);
    }

    public static FT_BBox nclip_box(long struct) {
        return FT_BBox.create(struct + (long)CLIP_BOX);
    }

    public static void ntarget(long struct, @Nullable FT_Bitmap value) {
        MemoryUtil.memPutAddress(struct + (long)TARGET, MemoryUtil.memAddressSafe(value));
    }

    public static void nsource(long struct, @Nullable ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)SOURCE, MemoryUtil.memAddressSafe(value));
    }

    public static void nflags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FLAGS, value);
    }

    public static void ngray_spans(long struct, @Nullable FT_SpanFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)GRAY_SPANS, MemoryUtil.memAddressSafe(value));
    }

    public static void nblack_spans(long struct, @Nullable FT_SpanFuncI value) {
        MemoryUtil.memPutAddress(struct + (long)BLACK_SPANS, MemoryUtil.memAddressSafe(value));
    }

    public static void nbit_test(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)BIT_TEST, value);
    }

    public static void nbit_set(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)BIT_SET, value);
    }

    public static void nuser(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)USER, value);
    }

    public static void nclip_box(long struct, FT_BBox value) {
        MemoryUtil.memCopy(value.address(), struct + (long)CLIP_BOX, FT_BBox.SIZEOF);
    }

    static {
        Struct.Layout layout = FT_Raster_Params.__struct(FT_Raster_Params.__member(POINTER_SIZE), FT_Raster_Params.__member(POINTER_SIZE), FT_Raster_Params.__member(4), FT_Raster_Params.__member(POINTER_SIZE), FT_Raster_Params.__member(POINTER_SIZE), FT_Raster_Params.__member(POINTER_SIZE), FT_Raster_Params.__member(POINTER_SIZE), FT_Raster_Params.__member(POINTER_SIZE), FT_Raster_Params.__member(FT_BBox.SIZEOF, FT_BBox.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        TARGET = layout.offsetof(0);
        SOURCE = layout.offsetof(1);
        FLAGS = layout.offsetof(2);
        GRAY_SPANS = layout.offsetof(3);
        BLACK_SPANS = layout.offsetof(4);
        BIT_TEST = layout.offsetof(5);
        BIT_SET = layout.offsetof(6);
        USER = layout.offsetof(7);
        CLIP_BOX = layout.offsetof(8);
    }

    public static class Buffer
    extends StructBuffer<FT_Raster_Params, Buffer>
    implements NativeResource {
        private static final FT_Raster_Params ELEMENT_FACTORY = FT_Raster_Params.create(-1L);

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
        protected FT_Raster_Params getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @Nullable
        @NativeType(value="FT_Bitmap const *")
        public FT_Bitmap target() {
            return FT_Raster_Params.ntarget(this.address());
        }

        @Nullable
        @NativeType(value="void const *")
        public ByteBuffer source(int capacity) {
            return FT_Raster_Params.nsource(this.address(), capacity);
        }

        public int flags() {
            return FT_Raster_Params.nflags(this.address());
        }

        @Nullable
        public FT_SpanFunc gray_spans() {
            return FT_Raster_Params.ngray_spans(this.address());
        }

        @NativeType(value="void *")
        public long user() {
            return FT_Raster_Params.nuser(this.address());
        }

        public FT_BBox clip_box() {
            return FT_Raster_Params.nclip_box(this.address());
        }

        public Buffer target(@Nullable @NativeType(value="FT_Bitmap const *") FT_Bitmap value) {
            FT_Raster_Params.ntarget(this.address(), value);
            return this;
        }

        public Buffer source(@Nullable @NativeType(value="void const *") ByteBuffer value) {
            FT_Raster_Params.nsource(this.address(), value);
            return this;
        }

        public Buffer flags(int value) {
            FT_Raster_Params.nflags(this.address(), value);
            return this;
        }

        public Buffer gray_spans(@Nullable @NativeType(value="FT_SpanFunc") FT_SpanFuncI value) {
            FT_Raster_Params.ngray_spans(this.address(), value);
            return this;
        }

        public Buffer user(@NativeType(value="void *") long value) {
            FT_Raster_Params.nuser(this.address(), value);
            return this;
        }

        public Buffer clip_box(FT_BBox value) {
            FT_Raster_Params.nclip_box(this.address(), value);
            return this;
        }

        public Buffer clip_box(Consumer<FT_BBox> consumer) {
            consumer.accept(this.clip_box());
            return this;
        }
    }
}
