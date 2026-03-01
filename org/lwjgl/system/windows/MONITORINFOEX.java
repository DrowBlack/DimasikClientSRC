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
import org.lwjgl.system.windows.RECT;

public class MONITORINFOEX
extends Struct<MONITORINFOEX>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int CBSIZE;
    public static final int RCMONITOR;
    public static final int RCWORK;
    public static final int DWFLAGS;
    public static final int SZDEVICE;

    protected MONITORINFOEX(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected MONITORINFOEX create(long address, @Nullable ByteBuffer container) {
        return new MONITORINFOEX(address, container);
    }

    public MONITORINFOEX(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), MONITORINFOEX.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="DWORD")
    public int cbSize() {
        return MONITORINFOEX.ncbSize(this.address());
    }

    public RECT rcMonitor() {
        return MONITORINFOEX.nrcMonitor(this.address());
    }

    public RECT rcWork() {
        return MONITORINFOEX.nrcWork(this.address());
    }

    @NativeType(value="DWORD")
    public int dwFlags() {
        return MONITORINFOEX.ndwFlags(this.address());
    }

    @NativeType(value="TCHAR[32]")
    public ByteBuffer szDevice() {
        return MONITORINFOEX.nszDevice(this.address());
    }

    @NativeType(value="TCHAR[32]")
    public String szDeviceString() {
        return MONITORINFOEX.nszDeviceString(this.address());
    }

    public MONITORINFOEX cbSize(@NativeType(value="DWORD") int value) {
        MONITORINFOEX.ncbSize(this.address(), value);
        return this;
    }

    public MONITORINFOEX set(MONITORINFOEX src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static MONITORINFOEX malloc() {
        return new MONITORINFOEX(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static MONITORINFOEX calloc() {
        return new MONITORINFOEX(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static MONITORINFOEX create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new MONITORINFOEX(MemoryUtil.memAddress(container), container);
    }

    public static MONITORINFOEX create(long address) {
        return new MONITORINFOEX(address, null);
    }

    @Nullable
    public static MONITORINFOEX createSafe(long address) {
        return address == 0L ? null : new MONITORINFOEX(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(MONITORINFOEX.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = MONITORINFOEX.__create(capacity, SIZEOF);
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
    public static MONITORINFOEX mallocStack() {
        return MONITORINFOEX.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static MONITORINFOEX callocStack() {
        return MONITORINFOEX.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static MONITORINFOEX mallocStack(MemoryStack stack) {
        return MONITORINFOEX.malloc(stack);
    }

    @Deprecated
    public static MONITORINFOEX callocStack(MemoryStack stack) {
        return MONITORINFOEX.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return MONITORINFOEX.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return MONITORINFOEX.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return MONITORINFOEX.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return MONITORINFOEX.calloc(capacity, stack);
    }

    public static MONITORINFOEX malloc(MemoryStack stack) {
        return new MONITORINFOEX(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static MONITORINFOEX calloc(MemoryStack stack) {
        return new MONITORINFOEX(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int ncbSize(long struct) {
        return UNSAFE.getInt(null, struct + (long)CBSIZE);
    }

    public static RECT nrcMonitor(long struct) {
        return RECT.create(struct + (long)RCMONITOR);
    }

    public static RECT nrcWork(long struct) {
        return RECT.create(struct + (long)RCWORK);
    }

    public static int ndwFlags(long struct) {
        return UNSAFE.getInt(null, struct + (long)DWFLAGS);
    }

    public static ByteBuffer nszDevice(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)SZDEVICE, 64);
    }

    public static String nszDeviceString(long struct) {
        return MemoryUtil.memUTF16(struct + (long)SZDEVICE);
    }

    public static void ncbSize(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)CBSIZE, value);
    }

    static {
        Struct.Layout layout = MONITORINFOEX.__struct(MONITORINFOEX.__member(4), MONITORINFOEX.__member(RECT.SIZEOF, RECT.ALIGNOF), MONITORINFOEX.__member(RECT.SIZEOF, RECT.ALIGNOF), MONITORINFOEX.__member(4), MONITORINFOEX.__array(2, 32));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        CBSIZE = layout.offsetof(0);
        RCMONITOR = layout.offsetof(1);
        RCWORK = layout.offsetof(2);
        DWFLAGS = layout.offsetof(3);
        SZDEVICE = layout.offsetof(4);
    }

    public static class Buffer
    extends StructBuffer<MONITORINFOEX, Buffer>
    implements NativeResource {
        private static final MONITORINFOEX ELEMENT_FACTORY = MONITORINFOEX.create(-1L);

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
        protected MONITORINFOEX getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="DWORD")
        public int cbSize() {
            return MONITORINFOEX.ncbSize(this.address());
        }

        public RECT rcMonitor() {
            return MONITORINFOEX.nrcMonitor(this.address());
        }

        public RECT rcWork() {
            return MONITORINFOEX.nrcWork(this.address());
        }

        @NativeType(value="DWORD")
        public int dwFlags() {
            return MONITORINFOEX.ndwFlags(this.address());
        }

        @NativeType(value="TCHAR[32]")
        public ByteBuffer szDevice() {
            return MONITORINFOEX.nszDevice(this.address());
        }

        @NativeType(value="TCHAR[32]")
        public String szDeviceString() {
            return MONITORINFOEX.nszDeviceString(this.address());
        }

        public Buffer cbSize(@NativeType(value="DWORD") int value) {
            MONITORINFOEX.ncbSize(this.address(), value);
            return this;
        }
    }
}
