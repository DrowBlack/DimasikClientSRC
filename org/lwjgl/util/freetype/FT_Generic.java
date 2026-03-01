package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_Generic_Finalizer;
import org.lwjgl.util.freetype.FT_Generic_FinalizerI;

public class FT_Generic
extends Struct<FT_Generic>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int DATA;
    public static final int FINALIZER;

    protected FT_Generic(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Generic create(long address, @Nullable ByteBuffer container) {
        return new FT_Generic(address, container);
    }

    public FT_Generic(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Generic.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="void *")
    public long data() {
        return FT_Generic.ndata(this.address());
    }

    public FT_Generic_Finalizer finalizer() {
        return FT_Generic.nfinalizer(this.address());
    }

    public FT_Generic data(@NativeType(value="void *") long value) {
        FT_Generic.ndata(this.address(), value);
        return this;
    }

    public FT_Generic finalizer(@NativeType(value="FT_Generic_Finalizer") FT_Generic_FinalizerI value) {
        FT_Generic.nfinalizer(this.address(), value);
        return this;
    }

    public FT_Generic set(long data, FT_Generic_FinalizerI finalizer) {
        this.data(data);
        this.finalizer(finalizer);
        return this;
    }

    public FT_Generic set(FT_Generic src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Generic malloc() {
        return new FT_Generic(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Generic calloc() {
        return new FT_Generic(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Generic create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Generic(MemoryUtil.memAddress(container), container);
    }

    public static FT_Generic create(long address) {
        return new FT_Generic(address, null);
    }

    @Nullable
    public static FT_Generic createSafe(long address) {
        return address == 0L ? null : new FT_Generic(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Generic.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Generic.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Generic malloc(MemoryStack stack) {
        return new FT_Generic(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Generic calloc(MemoryStack stack) {
        return new FT_Generic(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long ndata(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)DATA);
    }

    public static FT_Generic_Finalizer nfinalizer(long struct) {
        return FT_Generic_Finalizer.create(MemoryUtil.memGetAddress(struct + (long)FINALIZER));
    }

    public static void ndata(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)DATA, value);
    }

    public static void nfinalizer(long struct, FT_Generic_FinalizerI value) {
        MemoryUtil.memPutAddress(struct + (long)FINALIZER, value.address());
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)FINALIZER));
    }

    static {
        Struct.Layout layout = FT_Generic.__struct(FT_Generic.__member(POINTER_SIZE), FT_Generic.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        DATA = layout.offsetof(0);
        FINALIZER = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_Generic, Buffer>
    implements NativeResource {
        private static final FT_Generic ELEMENT_FACTORY = FT_Generic.create(-1L);

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
        protected FT_Generic getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="void *")
        public long data() {
            return FT_Generic.ndata(this.address());
        }

        public FT_Generic_Finalizer finalizer() {
            return FT_Generic.nfinalizer(this.address());
        }

        public Buffer data(@NativeType(value="void *") long value) {
            FT_Generic.ndata(this.address(), value);
            return this;
        }

        public Buffer finalizer(@NativeType(value="FT_Generic_Finalizer") FT_Generic_FinalizerI value) {
            FT_Generic.nfinalizer(this.address(), value);
            return this;
        }
    }
}
