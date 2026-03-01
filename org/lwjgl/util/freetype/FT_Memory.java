package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_Alloc_Func;
import org.lwjgl.util.freetype.FT_Alloc_FuncI;
import org.lwjgl.util.freetype.FT_Free_Func;
import org.lwjgl.util.freetype.FT_Free_FuncI;
import org.lwjgl.util.freetype.FT_Realloc_Func;
import org.lwjgl.util.freetype.FT_Realloc_FuncI;

@NativeType(value="struct FT_MemoryRec_")
public class FT_Memory
extends Struct<FT_Memory>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int USER;
    public static final int ALLOC;
    public static final int FREE;
    public static final int REALLOC;

    protected FT_Memory(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Memory create(long address, @Nullable ByteBuffer container) {
        return new FT_Memory(address, container);
    }

    public FT_Memory(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Memory.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="void *")
    public long user() {
        return FT_Memory.nuser(this.address());
    }

    @Nullable
    public FT_Alloc_Func alloc() {
        return FT_Memory.nalloc(this.address());
    }

    @Nullable
    public FT_Free_Func free$() {
        return FT_Memory.nfree$(this.address());
    }

    @Nullable
    public FT_Realloc_Func realloc() {
        return FT_Memory.nrealloc(this.address());
    }

    public FT_Memory user(@NativeType(value="void *") long value) {
        FT_Memory.nuser(this.address(), value);
        return this;
    }

    public FT_Memory alloc(@Nullable @NativeType(value="FT_Alloc_Func") FT_Alloc_FuncI value) {
        FT_Memory.nalloc(this.address(), value);
        return this;
    }

    public FT_Memory free$(@Nullable @NativeType(value="FT_Free_Func") FT_Free_FuncI value) {
        FT_Memory.nfree$(this.address(), value);
        return this;
    }

    public FT_Memory realloc(@Nullable @NativeType(value="FT_Realloc_Func") FT_Realloc_FuncI value) {
        FT_Memory.nrealloc(this.address(), value);
        return this;
    }

    public FT_Memory set(long user, FT_Alloc_FuncI alloc, FT_Free_FuncI free$, FT_Realloc_FuncI realloc) {
        this.user(user);
        this.alloc(alloc);
        this.free$(free$);
        this.realloc(realloc);
        return this;
    }

    public FT_Memory set(FT_Memory src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Memory malloc() {
        return new FT_Memory(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Memory calloc() {
        return new FT_Memory(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Memory create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Memory(MemoryUtil.memAddress(container), container);
    }

    public static FT_Memory create(long address) {
        return new FT_Memory(address, null);
    }

    @Nullable
    public static FT_Memory createSafe(long address) {
        return address == 0L ? null : new FT_Memory(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Memory.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Memory.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Memory malloc(MemoryStack stack) {
        return new FT_Memory(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Memory calloc(MemoryStack stack) {
        return new FT_Memory(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nuser(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)USER);
    }

    @Nullable
    public static FT_Alloc_Func nalloc(long struct) {
        return FT_Alloc_Func.createSafe(MemoryUtil.memGetAddress(struct + (long)ALLOC));
    }

    @Nullable
    public static FT_Free_Func nfree$(long struct) {
        return FT_Free_Func.createSafe(MemoryUtil.memGetAddress(struct + (long)FREE));
    }

    @Nullable
    public static FT_Realloc_Func nrealloc(long struct) {
        return FT_Realloc_Func.createSafe(MemoryUtil.memGetAddress(struct + (long)REALLOC));
    }

    public static void nuser(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)USER, value);
    }

    public static void nalloc(long struct, @Nullable FT_Alloc_FuncI value) {
        MemoryUtil.memPutAddress(struct + (long)ALLOC, MemoryUtil.memAddressSafe(value));
    }

    public static void nfree$(long struct, @Nullable FT_Free_FuncI value) {
        MemoryUtil.memPutAddress(struct + (long)FREE, MemoryUtil.memAddressSafe(value));
    }

    public static void nrealloc(long struct, @Nullable FT_Realloc_FuncI value) {
        MemoryUtil.memPutAddress(struct + (long)REALLOC, MemoryUtil.memAddressSafe(value));
    }

    static {
        Struct.Layout layout = FT_Memory.__struct(FT_Memory.__member(POINTER_SIZE), FT_Memory.__member(POINTER_SIZE), FT_Memory.__member(POINTER_SIZE), FT_Memory.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        USER = layout.offsetof(0);
        ALLOC = layout.offsetof(1);
        FREE = layout.offsetof(2);
        REALLOC = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<FT_Memory, Buffer>
    implements NativeResource {
        private static final FT_Memory ELEMENT_FACTORY = FT_Memory.create(-1L);

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
        protected FT_Memory getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="void *")
        public long user() {
            return FT_Memory.nuser(this.address());
        }

        @Nullable
        public FT_Alloc_Func alloc() {
            return FT_Memory.nalloc(this.address());
        }

        @Nullable
        public FT_Free_Func free$() {
            return FT_Memory.nfree$(this.address());
        }

        @Nullable
        public FT_Realloc_Func realloc() {
            return FT_Memory.nrealloc(this.address());
        }

        public Buffer user(@NativeType(value="void *") long value) {
            FT_Memory.nuser(this.address(), value);
            return this;
        }

        public Buffer alloc(@Nullable @NativeType(value="FT_Alloc_Func") FT_Alloc_FuncI value) {
            FT_Memory.nalloc(this.address(), value);
            return this;
        }

        public Buffer free$(@Nullable @NativeType(value="FT_Free_Func") FT_Free_FuncI value) {
            FT_Memory.nfree$(this.address(), value);
            return this;
        }

        public Buffer realloc(@Nullable @NativeType(value="FT_Realloc_Func") FT_Realloc_FuncI value) {
            FT_Memory.nrealloc(this.address(), value);
            return this;
        }
    }
}
