package org.lwjgl.opengl;

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

public class GPU_DEVICE
extends Struct<GPU_DEVICE>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int CB;
    public static final int DEVICENAME;
    public static final int DEVICESTRING;
    public static final int FLAGS;
    public static final int RCVIRTUALSCREEN;

    protected GPU_DEVICE(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected GPU_DEVICE create(long address, @Nullable ByteBuffer container) {
        return new GPU_DEVICE(address, container);
    }

    public GPU_DEVICE(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), GPU_DEVICE.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="DWORD")
    public int cb() {
        return GPU_DEVICE.ncb(this.address());
    }

    @NativeType(value="CHAR[32]")
    public ByteBuffer DeviceName() {
        return GPU_DEVICE.nDeviceName(this.address());
    }

    @NativeType(value="CHAR[32]")
    public String DeviceNameString() {
        return GPU_DEVICE.nDeviceNameString(this.address());
    }

    @NativeType(value="CHAR[128]")
    public ByteBuffer DeviceString() {
        return GPU_DEVICE.nDeviceString(this.address());
    }

    @NativeType(value="CHAR[128]")
    public String DeviceStringString() {
        return GPU_DEVICE.nDeviceStringString(this.address());
    }

    @NativeType(value="DWORD")
    public int Flags() {
        return GPU_DEVICE.nFlags(this.address());
    }

    public RECT rcVirtualScreen() {
        return GPU_DEVICE.nrcVirtualScreen(this.address());
    }

    public static GPU_DEVICE malloc() {
        return new GPU_DEVICE(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static GPU_DEVICE calloc() {
        return new GPU_DEVICE(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static GPU_DEVICE create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new GPU_DEVICE(MemoryUtil.memAddress(container), container);
    }

    public static GPU_DEVICE create(long address) {
        return new GPU_DEVICE(address, null);
    }

    @Nullable
    public static GPU_DEVICE createSafe(long address) {
        return address == 0L ? null : new GPU_DEVICE(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(GPU_DEVICE.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = GPU_DEVICE.__create(capacity, SIZEOF);
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
    public static GPU_DEVICE mallocStack() {
        return GPU_DEVICE.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static GPU_DEVICE callocStack() {
        return GPU_DEVICE.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static GPU_DEVICE mallocStack(MemoryStack stack) {
        return GPU_DEVICE.malloc(stack);
    }

    @Deprecated
    public static GPU_DEVICE callocStack(MemoryStack stack) {
        return GPU_DEVICE.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return GPU_DEVICE.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return GPU_DEVICE.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return GPU_DEVICE.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return GPU_DEVICE.calloc(capacity, stack);
    }

    public static GPU_DEVICE malloc(MemoryStack stack) {
        return new GPU_DEVICE(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static GPU_DEVICE calloc(MemoryStack stack) {
        return new GPU_DEVICE(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int ncb(long struct) {
        return UNSAFE.getInt(null, struct + (long)CB);
    }

    public static ByteBuffer nDeviceName(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)DEVICENAME, 32);
    }

    public static String nDeviceNameString(long struct) {
        return MemoryUtil.memASCII(struct + (long)DEVICENAME);
    }

    public static ByteBuffer nDeviceString(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)DEVICESTRING, 128);
    }

    public static String nDeviceStringString(long struct) {
        return MemoryUtil.memASCII(struct + (long)DEVICESTRING);
    }

    public static int nFlags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FLAGS);
    }

    public static RECT nrcVirtualScreen(long struct) {
        return RECT.create(struct + (long)RCVIRTUALSCREEN);
    }

    static {
        Struct.Layout layout = GPU_DEVICE.__struct(GPU_DEVICE.__member(4), GPU_DEVICE.__array(1, 32), GPU_DEVICE.__array(1, 128), GPU_DEVICE.__member(4), GPU_DEVICE.__member(RECT.SIZEOF, RECT.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        CB = layout.offsetof(0);
        DEVICENAME = layout.offsetof(1);
        DEVICESTRING = layout.offsetof(2);
        FLAGS = layout.offsetof(3);
        RCVIRTUALSCREEN = layout.offsetof(4);
    }

    public static class Buffer
    extends StructBuffer<GPU_DEVICE, Buffer>
    implements NativeResource {
        private static final GPU_DEVICE ELEMENT_FACTORY = GPU_DEVICE.create(-1L);

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
        protected GPU_DEVICE getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="DWORD")
        public int cb() {
            return GPU_DEVICE.ncb(this.address());
        }

        @NativeType(value="CHAR[32]")
        public ByteBuffer DeviceName() {
            return GPU_DEVICE.nDeviceName(this.address());
        }

        @NativeType(value="CHAR[32]")
        public String DeviceNameString() {
            return GPU_DEVICE.nDeviceNameString(this.address());
        }

        @NativeType(value="CHAR[128]")
        public ByteBuffer DeviceString() {
            return GPU_DEVICE.nDeviceString(this.address());
        }

        @NativeType(value="CHAR[128]")
        public String DeviceStringString() {
            return GPU_DEVICE.nDeviceStringString(this.address());
        }

        @NativeType(value="DWORD")
        public int Flags() {
            return GPU_DEVICE.nFlags(this.address());
        }

        public RECT rcVirtualScreen() {
            return GPU_DEVICE.nrcVirtualScreen(this.address());
        }
    }
}
