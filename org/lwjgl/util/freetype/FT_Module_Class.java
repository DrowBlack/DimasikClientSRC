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
import org.lwjgl.util.freetype.FT_Module_Constructor;
import org.lwjgl.util.freetype.FT_Module_ConstructorI;
import org.lwjgl.util.freetype.FT_Module_Destructor;
import org.lwjgl.util.freetype.FT_Module_DestructorI;
import org.lwjgl.util.freetype.FT_Module_Requester;
import org.lwjgl.util.freetype.FT_Module_RequesterI;

public class FT_Module_Class
extends Struct<FT_Module_Class>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int MODULE_FLAGS;
    public static final int MODULE_SIZE;
    public static final int MODULE_NAME;
    public static final int MODULE_VERSION;
    public static final int MODULE_REQUIRES;
    public static final int MODULE_INTERFACE;
    public static final int MODULE_INIT;
    public static final int MODULE_DONE;
    public static final int GET_INTERFACE;

    protected FT_Module_Class(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Module_Class create(long address, @Nullable ByteBuffer container) {
        return new FT_Module_Class(address, container);
    }

    public FT_Module_Class(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Module_Class.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_ULong")
    public long module_flags() {
        return FT_Module_Class.nmodule_flags(this.address());
    }

    @NativeType(value="FT_Long")
    public long module_size() {
        return FT_Module_Class.nmodule_size(this.address());
    }

    @NativeType(value="FT_String const *")
    public ByteBuffer module_name() {
        return FT_Module_Class.nmodule_name(this.address());
    }

    @NativeType(value="FT_String const *")
    public String module_nameString() {
        return FT_Module_Class.nmodule_nameString(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long module_version() {
        return FT_Module_Class.nmodule_version(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long module_requires() {
        return FT_Module_Class.nmodule_requires(this.address());
    }

    @Nullable
    @NativeType(value="void const *")
    public ByteBuffer module_interface(int capacity) {
        return FT_Module_Class.nmodule_interface(this.address(), capacity);
    }

    @Nullable
    public FT_Module_Constructor module_init() {
        return FT_Module_Class.nmodule_init(this.address());
    }

    @Nullable
    public FT_Module_Destructor module_done() {
        return FT_Module_Class.nmodule_done(this.address());
    }

    @Nullable
    public FT_Module_Requester get_interface() {
        return FT_Module_Class.nget_interface(this.address());
    }

    public FT_Module_Class module_flags(@NativeType(value="FT_ULong") long value) {
        FT_Module_Class.nmodule_flags(this.address(), value);
        return this;
    }

    public FT_Module_Class module_size(@NativeType(value="FT_Long") long value) {
        FT_Module_Class.nmodule_size(this.address(), value);
        return this;
    }

    public FT_Module_Class module_name(@NativeType(value="FT_String const *") ByteBuffer value) {
        FT_Module_Class.nmodule_name(this.address(), value);
        return this;
    }

    public FT_Module_Class module_version(@NativeType(value="FT_Fixed") long value) {
        FT_Module_Class.nmodule_version(this.address(), value);
        return this;
    }

    public FT_Module_Class module_requires(@NativeType(value="FT_Fixed") long value) {
        FT_Module_Class.nmodule_requires(this.address(), value);
        return this;
    }

    public FT_Module_Class module_interface(@Nullable @NativeType(value="void const *") ByteBuffer value) {
        FT_Module_Class.nmodule_interface(this.address(), value);
        return this;
    }

    public FT_Module_Class module_init(@Nullable @NativeType(value="FT_Module_Constructor") FT_Module_ConstructorI value) {
        FT_Module_Class.nmodule_init(this.address(), value);
        return this;
    }

    public FT_Module_Class module_done(@Nullable @NativeType(value="FT_Module_Destructor") FT_Module_DestructorI value) {
        FT_Module_Class.nmodule_done(this.address(), value);
        return this;
    }

    public FT_Module_Class get_interface(@Nullable @NativeType(value="FT_Module_Requester") FT_Module_RequesterI value) {
        FT_Module_Class.nget_interface(this.address(), value);
        return this;
    }

    public FT_Module_Class set(long module_flags, long module_size, ByteBuffer module_name, long module_version, long module_requires, @Nullable ByteBuffer module_interface, @Nullable FT_Module_ConstructorI module_init, @Nullable FT_Module_DestructorI module_done, @Nullable FT_Module_RequesterI get_interface) {
        this.module_flags(module_flags);
        this.module_size(module_size);
        this.module_name(module_name);
        this.module_version(module_version);
        this.module_requires(module_requires);
        this.module_interface(module_interface);
        this.module_init(module_init);
        this.module_done(module_done);
        this.get_interface(get_interface);
        return this;
    }

    public FT_Module_Class set(FT_Module_Class src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static FT_Module_Class malloc() {
        return new FT_Module_Class(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Module_Class calloc() {
        return new FT_Module_Class(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Module_Class create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Module_Class(MemoryUtil.memAddress(container), container);
    }

    public static FT_Module_Class create(long address) {
        return new FT_Module_Class(address, null);
    }

    @Nullable
    public static FT_Module_Class createSafe(long address) {
        return address == 0L ? null : new FT_Module_Class(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Module_Class.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Module_Class.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Module_Class malloc(MemoryStack stack) {
        return new FT_Module_Class(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Module_Class calloc(MemoryStack stack) {
        return new FT_Module_Class(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nmodule_flags(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)MODULE_FLAGS);
    }

    public static long nmodule_size(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)MODULE_SIZE);
    }

    public static ByteBuffer nmodule_name(long struct) {
        return MemoryUtil.memByteBufferNT1(MemoryUtil.memGetAddress(struct + (long)MODULE_NAME));
    }

    public static String nmodule_nameString(long struct) {
        return MemoryUtil.memUTF8(MemoryUtil.memGetAddress(struct + (long)MODULE_NAME));
    }

    public static long nmodule_version(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)MODULE_VERSION);
    }

    public static long nmodule_requires(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)MODULE_REQUIRES);
    }

    @Nullable
    public static ByteBuffer nmodule_interface(long struct, int capacity) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)MODULE_INTERFACE), capacity);
    }

    @Nullable
    public static FT_Module_Constructor nmodule_init(long struct) {
        return FT_Module_Constructor.createSafe(MemoryUtil.memGetAddress(struct + (long)MODULE_INIT));
    }

    @Nullable
    public static FT_Module_Destructor nmodule_done(long struct) {
        return FT_Module_Destructor.createSafe(MemoryUtil.memGetAddress(struct + (long)MODULE_DONE));
    }

    @Nullable
    public static FT_Module_Requester nget_interface(long struct) {
        return FT_Module_Requester.createSafe(MemoryUtil.memGetAddress(struct + (long)GET_INTERFACE));
    }

    public static void nmodule_flags(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)MODULE_FLAGS, value);
    }

    public static void nmodule_size(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)MODULE_SIZE, value);
    }

    public static void nmodule_name(long struct, ByteBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkNT1(value);
        }
        MemoryUtil.memPutAddress(struct + (long)MODULE_NAME, MemoryUtil.memAddress(value));
    }

    public static void nmodule_version(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)MODULE_VERSION, value);
    }

    public static void nmodule_requires(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)MODULE_REQUIRES, value);
    }

    public static void nmodule_interface(long struct, @Nullable ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)MODULE_INTERFACE, MemoryUtil.memAddressSafe(value));
    }

    public static void nmodule_init(long struct, @Nullable FT_Module_ConstructorI value) {
        MemoryUtil.memPutAddress(struct + (long)MODULE_INIT, MemoryUtil.memAddressSafe(value));
    }

    public static void nmodule_done(long struct, @Nullable FT_Module_DestructorI value) {
        MemoryUtil.memPutAddress(struct + (long)MODULE_DONE, MemoryUtil.memAddressSafe(value));
    }

    public static void nget_interface(long struct, @Nullable FT_Module_RequesterI value) {
        MemoryUtil.memPutAddress(struct + (long)GET_INTERFACE, MemoryUtil.memAddressSafe(value));
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)MODULE_NAME));
    }

    static {
        Struct.Layout layout = FT_Module_Class.__struct(FT_Module_Class.__member(CLONG_SIZE), FT_Module_Class.__member(CLONG_SIZE), FT_Module_Class.__member(POINTER_SIZE), FT_Module_Class.__member(CLONG_SIZE), FT_Module_Class.__member(CLONG_SIZE), FT_Module_Class.__member(POINTER_SIZE), FT_Module_Class.__member(POINTER_SIZE), FT_Module_Class.__member(POINTER_SIZE), FT_Module_Class.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        MODULE_FLAGS = layout.offsetof(0);
        MODULE_SIZE = layout.offsetof(1);
        MODULE_NAME = layout.offsetof(2);
        MODULE_VERSION = layout.offsetof(3);
        MODULE_REQUIRES = layout.offsetof(4);
        MODULE_INTERFACE = layout.offsetof(5);
        MODULE_INIT = layout.offsetof(6);
        MODULE_DONE = layout.offsetof(7);
        GET_INTERFACE = layout.offsetof(8);
    }

    public static class Buffer
    extends StructBuffer<FT_Module_Class, Buffer>
    implements NativeResource {
        private static final FT_Module_Class ELEMENT_FACTORY = FT_Module_Class.create(-1L);

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
        protected FT_Module_Class getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_ULong")
        public long module_flags() {
            return FT_Module_Class.nmodule_flags(this.address());
        }

        @NativeType(value="FT_Long")
        public long module_size() {
            return FT_Module_Class.nmodule_size(this.address());
        }

        @NativeType(value="FT_String const *")
        public ByteBuffer module_name() {
            return FT_Module_Class.nmodule_name(this.address());
        }

        @NativeType(value="FT_String const *")
        public String module_nameString() {
            return FT_Module_Class.nmodule_nameString(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long module_version() {
            return FT_Module_Class.nmodule_version(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long module_requires() {
            return FT_Module_Class.nmodule_requires(this.address());
        }

        @Nullable
        @NativeType(value="void const *")
        public ByteBuffer module_interface(int capacity) {
            return FT_Module_Class.nmodule_interface(this.address(), capacity);
        }

        @Nullable
        public FT_Module_Constructor module_init() {
            return FT_Module_Class.nmodule_init(this.address());
        }

        @Nullable
        public FT_Module_Destructor module_done() {
            return FT_Module_Class.nmodule_done(this.address());
        }

        @Nullable
        public FT_Module_Requester get_interface() {
            return FT_Module_Class.nget_interface(this.address());
        }

        public Buffer module_flags(@NativeType(value="FT_ULong") long value) {
            FT_Module_Class.nmodule_flags(this.address(), value);
            return this;
        }

        public Buffer module_size(@NativeType(value="FT_Long") long value) {
            FT_Module_Class.nmodule_size(this.address(), value);
            return this;
        }

        public Buffer module_name(@NativeType(value="FT_String const *") ByteBuffer value) {
            FT_Module_Class.nmodule_name(this.address(), value);
            return this;
        }

        public Buffer module_version(@NativeType(value="FT_Fixed") long value) {
            FT_Module_Class.nmodule_version(this.address(), value);
            return this;
        }

        public Buffer module_requires(@NativeType(value="FT_Fixed") long value) {
            FT_Module_Class.nmodule_requires(this.address(), value);
            return this;
        }

        public Buffer module_interface(@Nullable @NativeType(value="void const *") ByteBuffer value) {
            FT_Module_Class.nmodule_interface(this.address(), value);
            return this;
        }

        public Buffer module_init(@Nullable @NativeType(value="FT_Module_Constructor") FT_Module_ConstructorI value) {
            FT_Module_Class.nmodule_init(this.address(), value);
            return this;
        }

        public Buffer module_done(@Nullable @NativeType(value="FT_Module_Destructor") FT_Module_DestructorI value) {
            FT_Module_Class.nmodule_done(this.address(), value);
            return this;
        }

        public Buffer get_interface(@Nullable @NativeType(value="FT_Module_Requester") FT_Module_RequesterI value) {
            FT_Module_Class.nget_interface(this.address(), value);
            return this;
        }
    }
}
