package org.lwjgl.system.windows;

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
import org.lwjgl.system.windows.POINT;
import org.lwjgl.system.windows.RECT;

public class WINDOWPLACEMENT
extends Struct<WINDOWPLACEMENT>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int LENGTH;
    public static final int FLAGS;
    public static final int SHOWCMD;
    public static final int PTMINPOSITION;
    public static final int PTMAXPOSITION;
    public static final int RCNORMALPOSITION;

    protected WINDOWPLACEMENT(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected WINDOWPLACEMENT create(long address, @Nullable ByteBuffer container) {
        return new WINDOWPLACEMENT(address, container);
    }

    public WINDOWPLACEMENT(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), WINDOWPLACEMENT.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="UINT")
    public int length() {
        return WINDOWPLACEMENT.nlength(this.address());
    }

    @NativeType(value="UINT")
    public int flags() {
        return WINDOWPLACEMENT.nflags(this.address());
    }

    @NativeType(value="UINT")
    public int showCmd() {
        return WINDOWPLACEMENT.nshowCmd(this.address());
    }

    public POINT ptMinPosition() {
        return WINDOWPLACEMENT.nptMinPosition(this.address());
    }

    public POINT ptMaxPosition() {
        return WINDOWPLACEMENT.nptMaxPosition(this.address());
    }

    public RECT rcNormalPosition() {
        return WINDOWPLACEMENT.nrcNormalPosition(this.address());
    }

    public WINDOWPLACEMENT length(@NativeType(value="UINT") int value) {
        WINDOWPLACEMENT.nlength(this.address(), value);
        return this;
    }

    public WINDOWPLACEMENT flags(@NativeType(value="UINT") int value) {
        WINDOWPLACEMENT.nflags(this.address(), value);
        return this;
    }

    public WINDOWPLACEMENT showCmd(@NativeType(value="UINT") int value) {
        WINDOWPLACEMENT.nshowCmd(this.address(), value);
        return this;
    }

    public WINDOWPLACEMENT ptMinPosition(POINT value) {
        WINDOWPLACEMENT.nptMinPosition(this.address(), value);
        return this;
    }

    public WINDOWPLACEMENT ptMinPosition(Consumer<POINT> consumer) {
        consumer.accept(this.ptMinPosition());
        return this;
    }

    public WINDOWPLACEMENT ptMaxPosition(POINT value) {
        WINDOWPLACEMENT.nptMaxPosition(this.address(), value);
        return this;
    }

    public WINDOWPLACEMENT ptMaxPosition(Consumer<POINT> consumer) {
        consumer.accept(this.ptMaxPosition());
        return this;
    }

    public WINDOWPLACEMENT rcNormalPosition(RECT value) {
        WINDOWPLACEMENT.nrcNormalPosition(this.address(), value);
        return this;
    }

    public WINDOWPLACEMENT rcNormalPosition(Consumer<RECT> consumer) {
        consumer.accept(this.rcNormalPosition());
        return this;
    }

    public WINDOWPLACEMENT set(int length, int flags, int showCmd, POINT ptMinPosition, POINT ptMaxPosition, RECT rcNormalPosition) {
        this.length(length);
        this.flags(flags);
        this.showCmd(showCmd);
        this.ptMinPosition(ptMinPosition);
        this.ptMaxPosition(ptMaxPosition);
        this.rcNormalPosition(rcNormalPosition);
        return this;
    }

    public WINDOWPLACEMENT set(WINDOWPLACEMENT src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static WINDOWPLACEMENT malloc() {
        return new WINDOWPLACEMENT(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static WINDOWPLACEMENT calloc() {
        return new WINDOWPLACEMENT(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static WINDOWPLACEMENT create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new WINDOWPLACEMENT(MemoryUtil.memAddress(container), container);
    }

    public static WINDOWPLACEMENT create(long address) {
        return new WINDOWPLACEMENT(address, null);
    }

    @Nullable
    public static WINDOWPLACEMENT createSafe(long address) {
        return address == 0L ? null : new WINDOWPLACEMENT(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(WINDOWPLACEMENT.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = WINDOWPLACEMENT.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    @Deprecated
    public static WINDOWPLACEMENT mallocStack() {
        return WINDOWPLACEMENT.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static WINDOWPLACEMENT callocStack() {
        return WINDOWPLACEMENT.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static WINDOWPLACEMENT mallocStack(MemoryStack stack) {
        return WINDOWPLACEMENT.malloc(stack);
    }

    @Deprecated
    public static WINDOWPLACEMENT callocStack(MemoryStack stack) {
        return WINDOWPLACEMENT.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return WINDOWPLACEMENT.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return WINDOWPLACEMENT.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return WINDOWPLACEMENT.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return WINDOWPLACEMENT.calloc(capacity, stack);
    }

    public static WINDOWPLACEMENT malloc(MemoryStack stack) {
        return new WINDOWPLACEMENT(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static WINDOWPLACEMENT calloc(MemoryStack stack) {
        return new WINDOWPLACEMENT(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nlength(long struct) {
        return UNSAFE.getInt(null, struct + (long)LENGTH);
    }

    public static int nflags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FLAGS);
    }

    public static int nshowCmd(long struct) {
        return UNSAFE.getInt(null, struct + (long)SHOWCMD);
    }

    public static POINT nptMinPosition(long struct) {
        return POINT.create(struct + (long)PTMINPOSITION);
    }

    public static POINT nptMaxPosition(long struct) {
        return POINT.create(struct + (long)PTMAXPOSITION);
    }

    public static RECT nrcNormalPosition(long struct) {
        return RECT.create(struct + (long)RCNORMALPOSITION);
    }

    public static void nlength(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)LENGTH, value);
    }

    public static void nflags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FLAGS, value);
    }

    public static void nshowCmd(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)SHOWCMD, value);
    }

    public static void nptMinPosition(long struct, POINT value) {
        MemoryUtil.memCopy(value.address(), struct + (long)PTMINPOSITION, POINT.SIZEOF);
    }

    public static void nptMaxPosition(long struct, POINT value) {
        MemoryUtil.memCopy(value.address(), struct + (long)PTMAXPOSITION, POINT.SIZEOF);
    }

    public static void nrcNormalPosition(long struct, RECT value) {
        MemoryUtil.memCopy(value.address(), struct + (long)RCNORMALPOSITION, RECT.SIZEOF);
    }

    static {
        Struct.Layout layout = WINDOWPLACEMENT.__struct(WINDOWPLACEMENT.__member(4), WINDOWPLACEMENT.__member(4), WINDOWPLACEMENT.__member(4), WINDOWPLACEMENT.__member(POINT.SIZEOF, POINT.ALIGNOF), WINDOWPLACEMENT.__member(POINT.SIZEOF, POINT.ALIGNOF), WINDOWPLACEMENT.__member(RECT.SIZEOF, RECT.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        LENGTH = layout.offsetof(0);
        FLAGS = layout.offsetof(1);
        SHOWCMD = layout.offsetof(2);
        PTMINPOSITION = layout.offsetof(3);
        PTMAXPOSITION = layout.offsetof(4);
        RCNORMALPOSITION = layout.offsetof(5);
    }

    public static class Buffer
    extends StructBuffer<WINDOWPLACEMENT, Buffer>
    implements NativeResource {
        private static final WINDOWPLACEMENT ELEMENT_FACTORY = WINDOWPLACEMENT.create(-1L);

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
        protected WINDOWPLACEMENT getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="UINT")
        public int length() {
            return WINDOWPLACEMENT.nlength(this.address());
        }

        @NativeType(value="UINT")
        public int flags() {
            return WINDOWPLACEMENT.nflags(this.address());
        }

        @NativeType(value="UINT")
        public int showCmd() {
            return WINDOWPLACEMENT.nshowCmd(this.address());
        }

        public POINT ptMinPosition() {
            return WINDOWPLACEMENT.nptMinPosition(this.address());
        }

        public POINT ptMaxPosition() {
            return WINDOWPLACEMENT.nptMaxPosition(this.address());
        }

        public RECT rcNormalPosition() {
            return WINDOWPLACEMENT.nrcNormalPosition(this.address());
        }

        public Buffer length(@NativeType(value="UINT") int value) {
            WINDOWPLACEMENT.nlength(this.address(), value);
            return this;
        }

        public Buffer flags(@NativeType(value="UINT") int value) {
            WINDOWPLACEMENT.nflags(this.address(), value);
            return this;
        }

        public Buffer showCmd(@NativeType(value="UINT") int value) {
            WINDOWPLACEMENT.nshowCmd(this.address(), value);
            return this;
        }

        public Buffer ptMinPosition(POINT value) {
            WINDOWPLACEMENT.nptMinPosition(this.address(), value);
            return this;
        }

        public Buffer ptMinPosition(Consumer<POINT> consumer) {
            consumer.accept(this.ptMinPosition());
            return this;
        }

        public Buffer ptMaxPosition(POINT value) {
            WINDOWPLACEMENT.nptMaxPosition(this.address(), value);
            return this;
        }

        public Buffer ptMaxPosition(Consumer<POINT> consumer) {
            consumer.accept(this.ptMaxPosition());
            return this;
        }

        public Buffer rcNormalPosition(RECT value) {
            WINDOWPLACEMENT.nrcNormalPosition(this.address(), value);
            return this;
        }

        public Buffer rcNormalPosition(Consumer<RECT> consumer) {
            consumer.accept(this.rcNormalPosition());
            return this;
        }
    }
}
