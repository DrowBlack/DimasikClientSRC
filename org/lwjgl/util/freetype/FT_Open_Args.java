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
import org.lwjgl.util.freetype.FT_Parameter;
import org.lwjgl.util.freetype.FT_Stream;

public class FT_Open_Args
extends Struct<FT_Open_Args>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int FLAGS;
    public static final int MEMORY_BASE;
    public static final int MEMORY_SIZE;
    public static final int PATHNAME;
    public static final int STREAM;
    public static final int DRIVER;
    public static final int NUM_PARAMS;
    public static final int PARAMS;

    protected FT_Open_Args(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Open_Args create(long address, @Nullable ByteBuffer container) {
        return new FT_Open_Args(address, container);
    }

    public FT_Open_Args(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Open_Args.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_UInt")
    public int flags() {
        return FT_Open_Args.nflags(this.address());
    }

    @Nullable
    @NativeType(value="FT_Byte const *")
    public ByteBuffer memory_base() {
        return FT_Open_Args.nmemory_base(this.address());
    }

    @NativeType(value="FT_Long")
    public long memory_size() {
        return FT_Open_Args.nmemory_size(this.address());
    }

    @Nullable
    @NativeType(value="FT_String *")
    public ByteBuffer pathname() {
        return FT_Open_Args.npathname(this.address());
    }

    @Nullable
    @NativeType(value="FT_String *")
    public String pathnameString() {
        return FT_Open_Args.npathnameString(this.address());
    }

    @Nullable
    public FT_Stream stream$() {
        return FT_Open_Args.nstream$(this.address());
    }

    @NativeType(value="FT_Module")
    public long driver() {
        return FT_Open_Args.ndriver(this.address());
    }

    @NativeType(value="FT_Int")
    public int num_params() {
        return FT_Open_Args.nnum_params(this.address());
    }

    @Nullable
    @NativeType(value="FT_Parameter *")
    public FT_Parameter.Buffer params() {
        return FT_Open_Args.nparams(this.address());
    }

    public FT_Open_Args flags(@NativeType(value="FT_UInt") int value) {
        FT_Open_Args.nflags(this.address(), value);
        return this;
    }

    public FT_Open_Args memory_base(@Nullable @NativeType(value="FT_Byte const *") ByteBuffer value) {
        FT_Open_Args.nmemory_base(this.address(), value);
        return this;
    }

    public FT_Open_Args memory_size(@NativeType(value="FT_Long") long value) {
        FT_Open_Args.nmemory_size(this.address(), value);
        return this;
    }

    public FT_Open_Args pathname(@Nullable @NativeType(value="FT_String *") ByteBuffer value) {
        FT_Open_Args.npathname(this.address(), value);
        return this;
    }

    public FT_Open_Args stream$(@Nullable FT_Stream value) {
        FT_Open_Args.nstream$(this.address(), value);
        return this;
    }

    public FT_Open_Args driver(@NativeType(value="FT_Module") long value) {
        FT_Open_Args.ndriver(this.address(), value);
        return this;
    }

    public FT_Open_Args num_params(@NativeType(value="FT_Int") int value) {
        FT_Open_Args.nnum_params(this.address(), value);
        return this;
    }

    public FT_Open_Args params(@Nullable @NativeType(value="FT_Parameter *") FT_Parameter.Buffer value) {
        FT_Open_Args.nparams(this.address(), value);
        return this;
    }

    public FT_Open_Args set(int flags, @Nullable ByteBuffer memory_base, long memory_size, @Nullable ByteBuffer pathname, @Nullable FT_Stream stream$, long driver, int num_params, @Nullable FT_Parameter.Buffer params) {
        this.flags(flags);
        this.memory_base(memory_base);
        this.memory_size(memory_size);
        this.pathname(pathname);
        this.stream$(stream$);
        this.driver(driver);
        this.num_params(num_params);
        this.params(params);
        return this;
    }

    public FT_Open_Args set(FT_Open_Args src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Open_Args malloc() {
        return new FT_Open_Args(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Open_Args calloc() {
        return new FT_Open_Args(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Open_Args create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Open_Args(MemoryUtil.memAddress(container), container);
    }

    public static FT_Open_Args create(long address) {
        return new FT_Open_Args(address, null);
    }

    @Nullable
    public static FT_Open_Args createSafe(long address) {
        return address == 0L ? null : new FT_Open_Args(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Open_Args.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Open_Args.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Open_Args malloc(MemoryStack stack) {
        return new FT_Open_Args(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Open_Args calloc(MemoryStack stack) {
        return new FT_Open_Args(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nflags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FLAGS);
    }

    @Nullable
    public static ByteBuffer nmemory_base(long struct) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)MEMORY_BASE), (int)FT_Open_Args.nmemory_size(struct));
    }

    public static long nmemory_size(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)MEMORY_SIZE);
    }

    @Nullable
    public static ByteBuffer npathname(long struct) {
        return MemoryUtil.memByteBufferNT1Safe(MemoryUtil.memGetAddress(struct + (long)PATHNAME));
    }

    @Nullable
    public static String npathnameString(long struct) {
        return MemoryUtil.memUTF8Safe(MemoryUtil.memGetAddress(struct + (long)PATHNAME));
    }

    @Nullable
    public static FT_Stream nstream$(long struct) {
        return FT_Stream.createSafe(MemoryUtil.memGetAddress(struct + (long)STREAM));
    }

    public static long ndriver(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)DRIVER);
    }

    public static int nnum_params(long struct) {
        return UNSAFE.getInt(null, struct + (long)NUM_PARAMS);
    }

    @Nullable
    public static FT_Parameter.Buffer nparams(long struct) {
        return FT_Parameter.createSafe(MemoryUtil.memGetAddress(struct + (long)PARAMS), FT_Open_Args.nnum_params(struct));
    }

    public static void nflags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FLAGS, value);
    }

    public static void nmemory_base(long struct, @Nullable ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)MEMORY_BASE, MemoryUtil.memAddressSafe(value));
        FT_Open_Args.nmemory_size(struct, value == null ? 0L : (long)value.remaining());
    }

    public static void nmemory_size(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)MEMORY_SIZE, value);
    }

    public static void npathname(long struct, @Nullable ByteBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkNT1Safe(value);
        }
        MemoryUtil.memPutAddress(struct + (long)PATHNAME, MemoryUtil.memAddressSafe(value));
    }

    public static void nstream$(long struct, @Nullable FT_Stream value) {
        MemoryUtil.memPutAddress(struct + (long)STREAM, MemoryUtil.memAddressSafe(value));
    }

    public static void ndriver(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)DRIVER, value);
    }

    public static void nnum_params(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)NUM_PARAMS, value);
    }

    public static void nparams(long struct, @Nullable FT_Parameter.Buffer value) {
        MemoryUtil.memPutAddress(struct + (long)PARAMS, MemoryUtil.memAddressSafe(value));
        FT_Open_Args.nnum_params(struct, value == null ? 0 : value.remaining());
    }

    static {
        Struct.Layout layout = FT_Open_Args.__struct(FT_Open_Args.__member(4), FT_Open_Args.__member(POINTER_SIZE), FT_Open_Args.__member(CLONG_SIZE), FT_Open_Args.__member(POINTER_SIZE), FT_Open_Args.__member(POINTER_SIZE), FT_Open_Args.__member(POINTER_SIZE), FT_Open_Args.__member(4), FT_Open_Args.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        FLAGS = layout.offsetof(0);
        MEMORY_BASE = layout.offsetof(1);
        MEMORY_SIZE = layout.offsetof(2);
        PATHNAME = layout.offsetof(3);
        STREAM = layout.offsetof(4);
        DRIVER = layout.offsetof(5);
        NUM_PARAMS = layout.offsetof(6);
        PARAMS = layout.offsetof(7);
    }

    public static class Buffer
    extends StructBuffer<FT_Open_Args, Buffer>
    implements NativeResource {
        private static final FT_Open_Args ELEMENT_FACTORY = FT_Open_Args.create(-1L);

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
        protected FT_Open_Args getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_UInt")
        public int flags() {
            return FT_Open_Args.nflags(this.address());
        }

        @Nullable
        @NativeType(value="FT_Byte const *")
        public ByteBuffer memory_base() {
            return FT_Open_Args.nmemory_base(this.address());
        }

        @NativeType(value="FT_Long")
        public long memory_size() {
            return FT_Open_Args.nmemory_size(this.address());
        }

        @Nullable
        @NativeType(value="FT_String *")
        public ByteBuffer pathname() {
            return FT_Open_Args.npathname(this.address());
        }

        @Nullable
        @NativeType(value="FT_String *")
        public String pathnameString() {
            return FT_Open_Args.npathnameString(this.address());
        }

        @Nullable
        public FT_Stream stream$() {
            return FT_Open_Args.nstream$(this.address());
        }

        @NativeType(value="FT_Module")
        public long driver() {
            return FT_Open_Args.ndriver(this.address());
        }

        @NativeType(value="FT_Int")
        public int num_params() {
            return FT_Open_Args.nnum_params(this.address());
        }

        @Nullable
        @NativeType(value="FT_Parameter *")
        public FT_Parameter.Buffer params() {
            return FT_Open_Args.nparams(this.address());
        }

        public Buffer flags(@NativeType(value="FT_UInt") int value) {
            FT_Open_Args.nflags(this.address(), value);
            return this;
        }

        public Buffer memory_base(@Nullable @NativeType(value="FT_Byte const *") ByteBuffer value) {
            FT_Open_Args.nmemory_base(this.address(), value);
            return this;
        }

        public Buffer memory_size(@NativeType(value="FT_Long") long value) {
            FT_Open_Args.nmemory_size(this.address(), value);
            return this;
        }

        public Buffer pathname(@Nullable @NativeType(value="FT_String *") ByteBuffer value) {
            FT_Open_Args.npathname(this.address(), value);
            return this;
        }

        public Buffer stream$(@Nullable FT_Stream value) {
            FT_Open_Args.nstream$(this.address(), value);
            return this;
        }

        public Buffer driver(@NativeType(value="FT_Module") long value) {
            FT_Open_Args.ndriver(this.address(), value);
            return this;
        }

        public Buffer num_params(@NativeType(value="FT_Int") int value) {
            FT_Open_Args.nnum_params(this.address(), value);
            return this;
        }

        public Buffer params(@Nullable @NativeType(value="FT_Parameter *") FT_Parameter.Buffer value) {
            FT_Open_Args.nparams(this.address(), value);
            return this;
        }
    }
}
