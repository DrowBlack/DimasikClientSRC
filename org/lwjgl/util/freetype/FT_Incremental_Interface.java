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
import org.lwjgl.util.freetype.FT_Incremental_Funcs;

@NativeType(value="struct FT_Incremental_InterfaceRec")
public class FT_Incremental_Interface
extends Struct<FT_Incremental_Interface>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int FUNCS;
    public static final int OBJECT;

    protected FT_Incremental_Interface(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Incremental_Interface create(long address, @Nullable ByteBuffer container) {
        return new FT_Incremental_Interface(address, container);
    }

    public FT_Incremental_Interface(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Incremental_Interface.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Incremental_FuncsRec const *")
    public FT_Incremental_Funcs funcs() {
        return FT_Incremental_Interface.nfuncs(this.address());
    }

    @NativeType(value="FT_Incremental")
    public long object() {
        return FT_Incremental_Interface.nobject(this.address());
    }

    public FT_Incremental_Interface funcs(@NativeType(value="FT_Incremental_FuncsRec const *") FT_Incremental_Funcs value) {
        FT_Incremental_Interface.nfuncs(this.address(), value);
        return this;
    }

    public FT_Incremental_Interface object(@NativeType(value="FT_Incremental") long value) {
        FT_Incremental_Interface.nobject(this.address(), value);
        return this;
    }

    public FT_Incremental_Interface set(FT_Incremental_Funcs funcs, long object) {
        this.funcs(funcs);
        this.object(object);
        return this;
    }

    public FT_Incremental_Interface set(FT_Incremental_Interface src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Incremental_Interface malloc() {
        return new FT_Incremental_Interface(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Incremental_Interface calloc() {
        return new FT_Incremental_Interface(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Incremental_Interface create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Incremental_Interface(MemoryUtil.memAddress(container), container);
    }

    public static FT_Incremental_Interface create(long address) {
        return new FT_Incremental_Interface(address, null);
    }

    @Nullable
    public static FT_Incremental_Interface createSafe(long address) {
        return address == 0L ? null : new FT_Incremental_Interface(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Incremental_Interface.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Incremental_Interface.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Incremental_Interface malloc(MemoryStack stack) {
        return new FT_Incremental_Interface(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Incremental_Interface calloc(MemoryStack stack) {
        return new FT_Incremental_Interface(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static FT_Incremental_Funcs nfuncs(long struct) {
        return FT_Incremental_Funcs.create(MemoryUtil.memGetAddress(struct + (long)FUNCS));
    }

    public static long nobject(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)OBJECT);
    }

    public static void nfuncs(long struct, FT_Incremental_Funcs value) {
        MemoryUtil.memPutAddress(struct + (long)FUNCS, value.address());
    }

    public static void nobject(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)OBJECT, value);
    }

    public static void validate(long struct) {
        long funcs = MemoryUtil.memGetAddress(struct + (long)FUNCS);
        Checks.check(funcs);
        FT_Incremental_Funcs.validate(funcs);
    }

    static {
        Struct.Layout layout = FT_Incremental_Interface.__struct(FT_Incremental_Interface.__member(POINTER_SIZE), FT_Incremental_Interface.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        FUNCS = layout.offsetof(0);
        OBJECT = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_Incremental_Interface, Buffer>
    implements NativeResource {
        private static final FT_Incremental_Interface ELEMENT_FACTORY = FT_Incremental_Interface.create(-1L);

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
        protected FT_Incremental_Interface getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Incremental_FuncsRec const *")
        public FT_Incremental_Funcs funcs() {
            return FT_Incremental_Interface.nfuncs(this.address());
        }

        @NativeType(value="FT_Incremental")
        public long object() {
            return FT_Incremental_Interface.nobject(this.address());
        }

        public Buffer funcs(@NativeType(value="FT_Incremental_FuncsRec const *") FT_Incremental_Funcs value) {
            FT_Incremental_Interface.nfuncs(this.address(), value);
            return this;
        }

        public Buffer object(@NativeType(value="FT_Incremental") long value) {
            FT_Incremental_Interface.nobject(this.address(), value);
            return this;
        }
    }
}
