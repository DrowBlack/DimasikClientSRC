package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_Generic;
import org.lwjgl.util.freetype.FT_Size_Internal;
import org.lwjgl.util.freetype.FT_Size_Metrics;

@NativeType(value="struct FT_SizeRec")
public class FT_Size
extends Struct<FT_Size>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int FACE;
    public static final int GENERIC;
    public static final int METRICS;
    public static final int INTERNAL;

    protected FT_Size(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Size create(long address, @Nullable ByteBuffer container) {
        return new FT_Size(address, container);
    }

    public FT_Size(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Size.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public FT_Face face() {
        return FT_Size.nface(this.address());
    }

    public FT_Generic generic() {
        return FT_Size.ngeneric(this.address());
    }

    public FT_Size_Metrics metrics() {
        return FT_Size.nmetrics(this.address());
    }

    public FT_Size face(FT_Face value) {
        FT_Size.nface(this.address(), value);
        return this;
    }

    public FT_Size generic(FT_Generic value) {
        FT_Size.ngeneric(this.address(), value);
        return this;
    }

    public FT_Size generic(Consumer<FT_Generic> consumer) {
        consumer.accept(this.generic());
        return this;
    }

    public FT_Size metrics(FT_Size_Metrics value) {
        FT_Size.nmetrics(this.address(), value);
        return this;
    }

    public FT_Size set(FT_Face face, FT_Generic generic, FT_Size_Metrics metrics) {
        this.face(face);
        this.generic(generic);
        this.metrics(metrics);
        return this;
    }

    public FT_Size set(FT_Size src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Size malloc() {
        return new FT_Size(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Size calloc() {
        return new FT_Size(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Size create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Size(MemoryUtil.memAddress(container), container);
    }

    public static FT_Size create(long address) {
        return new FT_Size(address, null);
    }

    @Nullable
    public static FT_Size createSafe(long address) {
        return address == 0L ? null : new FT_Size(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Size.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Size.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Size malloc(MemoryStack stack) {
        return new FT_Size(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Size calloc(MemoryStack stack) {
        return new FT_Size(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static FT_Face nface(long struct) {
        return FT_Face.create(MemoryUtil.memGetAddress(struct + (long)FACE));
    }

    public static FT_Generic ngeneric(long struct) {
        return FT_Generic.create(struct + (long)GENERIC);
    }

    public static FT_Size_Metrics nmetrics(long struct) {
        return FT_Size_Metrics.create(struct + (long)METRICS);
    }

    public static FT_Size_Internal ninternal(long struct) {
        return FT_Size_Internal.create(MemoryUtil.memGetAddress(struct + (long)INTERNAL));
    }

    public static void nface(long struct, FT_Face value) {
        MemoryUtil.memPutAddress(struct + (long)FACE, value.address());
    }

    public static void ngeneric(long struct, FT_Generic value) {
        MemoryUtil.memCopy(value.address(), struct + (long)GENERIC, FT_Generic.SIZEOF);
    }

    public static void nmetrics(long struct, FT_Size_Metrics value) {
        MemoryUtil.memCopy(value.address(), struct + (long)METRICS, FT_Size_Metrics.SIZEOF);
    }

    public static void ninternal(long struct, FT_Size_Internal value) {
        MemoryUtil.memPutAddress(struct + (long)INTERNAL, value.address());
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)FACE));
        FT_Generic.validate(struct + (long)GENERIC);
    }

    static {
        Struct.Layout layout = FT_Size.__struct(FT_Size.__member(POINTER_SIZE), FT_Size.__member(FT_Generic.SIZEOF, FT_Generic.ALIGNOF), FT_Size.__member(FT_Size_Metrics.SIZEOF, FT_Size_Metrics.ALIGNOF), FT_Size.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        FACE = layout.offsetof(0);
        GENERIC = layout.offsetof(1);
        METRICS = layout.offsetof(2);
        INTERNAL = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FT_Size, Buffer>
    implements NativeResource {
        private static final FT_Size ELEMENT_FACTORY = FT_Size.create(-1L);

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
        protected FT_Size getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public FT_Face face() {
            return FT_Size.nface(this.address());
        }

        public FT_Generic generic() {
            return FT_Size.ngeneric(this.address());
        }

        public FT_Size_Metrics metrics() {
            return FT_Size.nmetrics(this.address());
        }

        public Buffer face(FT_Face value) {
            FT_Size.nface(this.address(), value);
            return this;
        }

        public Buffer generic(FT_Generic value) {
            FT_Size.ngeneric(this.address(), value);
            return this;
        }

        public Buffer generic(Consumer<FT_Generic> consumer) {
            consumer.accept(this.generic());
            return this;
        }

        public Buffer metrics(FT_Size_Metrics value) {
            FT_Size.nmetrics(this.address(), value);
            return this;
        }
    }
}
