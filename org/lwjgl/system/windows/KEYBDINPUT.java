package org.lwjgl.system.windows;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class KEYBDINPUT
extends Struct<KEYBDINPUT>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int WVK;
    public static final int WSCAN;
    public static final int DWFLAGS;
    public static final int TIME;
    public static final int DWEXTRAINFO;

    protected KEYBDINPUT(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected KEYBDINPUT create(long address, @Nullable ByteBuffer container) {
        return new KEYBDINPUT(address, container);
    }

    public KEYBDINPUT(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), KEYBDINPUT.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="WORD")
    public short wVk() {
        return KEYBDINPUT.nwVk(this.address());
    }

    @NativeType(value="WORD")
    public short wScan() {
        return KEYBDINPUT.nwScan(this.address());
    }

    @NativeType(value="DWORD")
    public int dwFlags() {
        return KEYBDINPUT.ndwFlags(this.address());
    }

    @NativeType(value="DWORD")
    public int time() {
        return KEYBDINPUT.ntime(this.address());
    }

    @NativeType(value="ULONG_PTR")
    public long dwExtraInfo() {
        return KEYBDINPUT.ndwExtraInfo(this.address());
    }

    public KEYBDINPUT wVk(@NativeType(value="WORD") short value) {
        KEYBDINPUT.nwVk(this.address(), value);
        return this;
    }

    public KEYBDINPUT wScan(@NativeType(value="WORD") short value) {
        KEYBDINPUT.nwScan(this.address(), value);
        return this;
    }

    public KEYBDINPUT dwFlags(@NativeType(value="DWORD") int value) {
        KEYBDINPUT.ndwFlags(this.address(), value);
        return this;
    }

    public KEYBDINPUT time(@NativeType(value="DWORD") int value) {
        KEYBDINPUT.ntime(this.address(), value);
        return this;
    }

    public KEYBDINPUT dwExtraInfo(@NativeType(value="ULONG_PTR") long value) {
        KEYBDINPUT.ndwExtraInfo(this.address(), value);
        return this;
    }

    public KEYBDINPUT set(short wVk, short wScan, int dwFlags, int time, long dwExtraInfo) {
        this.wVk(wVk);
        this.wScan(wScan);
        this.dwFlags(dwFlags);
        this.time(time);
        this.dwExtraInfo(dwExtraInfo);
        return this;
    }

    public KEYBDINPUT set(KEYBDINPUT src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static KEYBDINPUT malloc() {
        return new KEYBDINPUT(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static KEYBDINPUT calloc() {
        return new KEYBDINPUT(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static KEYBDINPUT create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new KEYBDINPUT(MemoryUtil.memAddress(container), container);
    }

    public static KEYBDINPUT create(long address) {
        return new KEYBDINPUT(address, null);
    }

    @Nullable
    public static KEYBDINPUT createSafe(long address) {
        return address == 0L ? null : new KEYBDINPUT(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(KEYBDINPUT.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = KEYBDINPUT.__create(capacity, SIZEOF);
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
    public static KEYBDINPUT mallocStack() {
        return KEYBDINPUT.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static KEYBDINPUT callocStack() {
        return KEYBDINPUT.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static KEYBDINPUT mallocStack(MemoryStack stack) {
        return KEYBDINPUT.malloc(stack);
    }

    @Deprecated
    public static KEYBDINPUT callocStack(MemoryStack stack) {
        return KEYBDINPUT.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return KEYBDINPUT.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return KEYBDINPUT.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return KEYBDINPUT.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return KEYBDINPUT.calloc(capacity, stack);
    }

    public static KEYBDINPUT malloc(MemoryStack stack) {
        return new KEYBDINPUT(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static KEYBDINPUT calloc(MemoryStack stack) {
        return new KEYBDINPUT(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static short nwVk(long struct) {
        return UNSAFE.getShort(null, struct + (long)WVK);
    }

    public static short nwScan(long struct) {
        return UNSAFE.getShort(null, struct + (long)WSCAN);
    }

    public static int ndwFlags(long struct) {
        return UNSAFE.getInt(null, struct + (long)DWFLAGS);
    }

    public static int ntime(long struct) {
        return UNSAFE.getInt(null, struct + (long)TIME);
    }

    public static long ndwExtraInfo(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)DWEXTRAINFO);
    }

    public static void nwVk(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)WVK, value);
    }

    public static void nwScan(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)WSCAN, value);
    }

    public static void ndwFlags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)DWFLAGS, value);
    }

    public static void ntime(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)TIME, value);
    }

    public static void ndwExtraInfo(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)DWEXTRAINFO, value);
    }

    static {
        Struct.Layout layout = KEYBDINPUT.__struct(KEYBDINPUT.__member(2), KEYBDINPUT.__member(2), KEYBDINPUT.__member(4), KEYBDINPUT.__member(4), KEYBDINPUT.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        WVK = layout.offsetof(0);
        WSCAN = layout.offsetof(1);
        DWFLAGS = layout.offsetof(2);
        TIME = layout.offsetof(3);
        DWEXTRAINFO = layout.offsetof(4);
    }

    public static class Buffer
    extends StructBuffer<KEYBDINPUT, Buffer>
    implements NativeResource {
        private static final KEYBDINPUT ELEMENT_FACTORY = KEYBDINPUT.create(-1L);

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
        protected KEYBDINPUT getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="WORD")
        public short wVk() {
            return KEYBDINPUT.nwVk(this.address());
        }

        @NativeType(value="WORD")
        public short wScan() {
            return KEYBDINPUT.nwScan(this.address());
        }

        @NativeType(value="DWORD")
        public int dwFlags() {
            return KEYBDINPUT.ndwFlags(this.address());
        }

        @NativeType(value="DWORD")
        public int time() {
            return KEYBDINPUT.ntime(this.address());
        }

        @NativeType(value="ULONG_PTR")
        public long dwExtraInfo() {
            return KEYBDINPUT.ndwExtraInfo(this.address());
        }

        public Buffer wVk(@NativeType(value="WORD") short value) {
            KEYBDINPUT.nwVk(this.address(), value);
            return this;
        }

        public Buffer wScan(@NativeType(value="WORD") short value) {
            KEYBDINPUT.nwScan(this.address(), value);
            return this;
        }

        public Buffer dwFlags(@NativeType(value="DWORD") int value) {
            KEYBDINPUT.ndwFlags(this.address(), value);
            return this;
        }

        public Buffer time(@NativeType(value="DWORD") int value) {
            KEYBDINPUT.ntime(this.address(), value);
            return this;
        }

        public Buffer dwExtraInfo(@NativeType(value="ULONG_PTR") long value) {
            KEYBDINPUT.ndwExtraInfo(this.address(), value);
            return this;
        }
    }
}
