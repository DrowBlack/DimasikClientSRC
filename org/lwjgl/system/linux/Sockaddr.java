package org.lwjgl.system.linux;

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

@NativeType(value="struct sockaddr")
public class Sockaddr
extends Struct<Sockaddr>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int SA_FAMILY;
    public static final int SA_DATA;

    protected Sockaddr(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected Sockaddr create(long address, @Nullable ByteBuffer container) {
        return new Sockaddr(address, container);
    }

    public Sockaddr(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), Sockaddr.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="sa_family_t")
    public short sa_family() {
        return Sockaddr.nsa_family(this.address());
    }

    @NativeType(value="char[14]")
    public ByteBuffer sa_data() {
        return Sockaddr.nsa_data(this.address());
    }

    @NativeType(value="char")
    public byte sa_data(int index) {
        return Sockaddr.nsa_data(this.address(), index);
    }

    public Sockaddr sa_family(@NativeType(value="sa_family_t") short value) {
        Sockaddr.nsa_family(this.address(), value);
        return this;
    }

    public Sockaddr sa_data(@NativeType(value="char[14]") ByteBuffer value) {
        Sockaddr.nsa_data(this.address(), value);
        return this;
    }

    public Sockaddr sa_data(int index, @NativeType(value="char") byte value) {
        Sockaddr.nsa_data(this.address(), index, value);
        return this;
    }

    public Sockaddr set(short sa_family, ByteBuffer sa_data) {
        this.sa_family(sa_family);
        this.sa_data(sa_data);
        return this;
    }

    public Sockaddr set(Sockaddr src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static Sockaddr malloc() {
        return new Sockaddr(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static Sockaddr calloc() {
        return new Sockaddr(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static Sockaddr create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new Sockaddr(MemoryUtil.memAddress(container), container);
    }

    public static Sockaddr create(long address) {
        return new Sockaddr(address, null);
    }

    @Nullable
    public static Sockaddr createSafe(long address) {
        return address == 0L ? null : new Sockaddr(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(Sockaddr.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = Sockaddr.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static Sockaddr malloc(MemoryStack stack) {
        return new Sockaddr(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static Sockaddr calloc(MemoryStack stack) {
        return new Sockaddr(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static short nsa_family(long struct) {
        return UNSAFE.getShort(null, struct + (long)SA_FAMILY);
    }

    public static ByteBuffer nsa_data(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)SA_DATA, 14);
    }

    public static byte nsa_data(long struct, int index) {
        return UNSAFE.getByte(null, struct + (long)SA_DATA + Checks.check(index, 14) * 1L);
    }

    public static void nsa_family(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)SA_FAMILY, value);
    }

    public static void nsa_data(long struct, ByteBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 14);
        }
        MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + (long)SA_DATA, value.remaining() * 1);
    }

    public static void nsa_data(long struct, int index, byte value) {
        UNSAFE.putByte(null, struct + (long)SA_DATA + Checks.check(index, 14) * 1L, value);
    }

    static {
        Struct.Layout layout = Sockaddr.__struct(Sockaddr.__member(2), Sockaddr.__array(1, 14));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        SA_FAMILY = layout.offsetof(0);
        SA_DATA = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<Sockaddr, Buffer>
    implements NativeResource {
        private static final Sockaddr ELEMENT_FACTORY = Sockaddr.create(-1L);

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
        protected Sockaddr getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="sa_family_t")
        public short sa_family() {
            return Sockaddr.nsa_family(this.address());
        }

        @NativeType(value="char[14]")
        public ByteBuffer sa_data() {
            return Sockaddr.nsa_data(this.address());
        }

        @NativeType(value="char")
        public byte sa_data(int index) {
            return Sockaddr.nsa_data(this.address(), index);
        }

        public Buffer sa_family(@NativeType(value="sa_family_t") short value) {
            Sockaddr.nsa_family(this.address(), value);
            return this;
        }

        public Buffer sa_data(@NativeType(value="char[14]") ByteBuffer value) {
            Sockaddr.nsa_data(this.address(), value);
            return this;
        }

        public Buffer sa_data(int index, @NativeType(value="char") byte value) {
            Sockaddr.nsa_data(this.address(), index, value);
            return this;
        }
    }
}
