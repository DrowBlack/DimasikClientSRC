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

public class DISPLAY_DEVICE
extends Struct<DISPLAY_DEVICE>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int CB;
    public static final int DEVICENAME;
    public static final int DEVICESTRING;
    public static final int STATEFLAGS;
    public static final int DEVICEID;
    public static final int DEVICEKEY;

    protected DISPLAY_DEVICE(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected DISPLAY_DEVICE create(long address, @Nullable ByteBuffer container) {
        return new DISPLAY_DEVICE(address, container);
    }

    public DISPLAY_DEVICE(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), DISPLAY_DEVICE.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="DWORD")
    public int cb() {
        return DISPLAY_DEVICE.ncb(this.address());
    }

    @NativeType(value="TCHAR[32]")
    public ByteBuffer DeviceName() {
        return DISPLAY_DEVICE.nDeviceName(this.address());
    }

    @NativeType(value="TCHAR[32]")
    public String DeviceNameString() {
        return DISPLAY_DEVICE.nDeviceNameString(this.address());
    }

    @NativeType(value="TCHAR[128]")
    public ByteBuffer DeviceString() {
        return DISPLAY_DEVICE.nDeviceString(this.address());
    }

    @NativeType(value="TCHAR[128]")
    public String DeviceStringString() {
        return DISPLAY_DEVICE.nDeviceStringString(this.address());
    }

    @NativeType(value="DWORD")
    public int StateFlags() {
        return DISPLAY_DEVICE.nStateFlags(this.address());
    }

    @NativeType(value="TCHAR[128]")
    public ByteBuffer DeviceID() {
        return DISPLAY_DEVICE.nDeviceID(this.address());
    }

    @NativeType(value="TCHAR[128]")
    public String DeviceIDString() {
        return DISPLAY_DEVICE.nDeviceIDString(this.address());
    }

    @NativeType(value="TCHAR[128]")
    public ByteBuffer DeviceKey() {
        return DISPLAY_DEVICE.nDeviceKey(this.address());
    }

    @NativeType(value="TCHAR[128]")
    public String DeviceKeyString() {
        return DISPLAY_DEVICE.nDeviceKeyString(this.address());
    }

    public DISPLAY_DEVICE cb(@NativeType(value="DWORD") int value) {
        DISPLAY_DEVICE.ncb(this.address(), value);
        return this;
    }

    public DISPLAY_DEVICE set(DISPLAY_DEVICE src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static DISPLAY_DEVICE malloc() {
        return new DISPLAY_DEVICE(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static DISPLAY_DEVICE calloc() {
        return new DISPLAY_DEVICE(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static DISPLAY_DEVICE create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new DISPLAY_DEVICE(MemoryUtil.memAddress(container), container);
    }

    public static DISPLAY_DEVICE create(long address) {
        return new DISPLAY_DEVICE(address, null);
    }

    @Nullable
    public static DISPLAY_DEVICE createSafe(long address) {
        return address == 0L ? null : new DISPLAY_DEVICE(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(DISPLAY_DEVICE.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = DISPLAY_DEVICE.__create(capacity, SIZEOF);
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
    public static DISPLAY_DEVICE mallocStack() {
        return DISPLAY_DEVICE.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static DISPLAY_DEVICE callocStack() {
        return DISPLAY_DEVICE.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static DISPLAY_DEVICE mallocStack(MemoryStack stack) {
        return DISPLAY_DEVICE.malloc(stack);
    }

    @Deprecated
    public static DISPLAY_DEVICE callocStack(MemoryStack stack) {
        return DISPLAY_DEVICE.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return DISPLAY_DEVICE.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return DISPLAY_DEVICE.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return DISPLAY_DEVICE.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return DISPLAY_DEVICE.calloc(capacity, stack);
    }

    public static DISPLAY_DEVICE malloc(MemoryStack stack) {
        return new DISPLAY_DEVICE(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static DISPLAY_DEVICE calloc(MemoryStack stack) {
        return new DISPLAY_DEVICE(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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
        return MemoryUtil.memByteBuffer(struct + (long)DEVICENAME, 64);
    }

    public static String nDeviceNameString(long struct) {
        return MemoryUtil.memUTF16(struct + (long)DEVICENAME);
    }

    public static ByteBuffer nDeviceString(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)DEVICESTRING, 256);
    }

    public static String nDeviceStringString(long struct) {
        return MemoryUtil.memUTF16(struct + (long)DEVICESTRING);
    }

    public static int nStateFlags(long struct) {
        return UNSAFE.getInt(null, struct + (long)STATEFLAGS);
    }

    public static ByteBuffer nDeviceID(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)DEVICEID, 256);
    }

    public static String nDeviceIDString(long struct) {
        return MemoryUtil.memUTF16(struct + (long)DEVICEID);
    }

    public static ByteBuffer nDeviceKey(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)DEVICEKEY, 256);
    }

    public static String nDeviceKeyString(long struct) {
        return MemoryUtil.memUTF16(struct + (long)DEVICEKEY);
    }

    public static void ncb(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)CB, value);
    }

    static {
        Struct.Layout layout = DISPLAY_DEVICE.__struct(DISPLAY_DEVICE.__member(4), DISPLAY_DEVICE.__array(2, 32), DISPLAY_DEVICE.__array(2, 128), DISPLAY_DEVICE.__member(4), DISPLAY_DEVICE.__array(2, 128), DISPLAY_DEVICE.__array(2, 128));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        CB = layout.offsetof(0);
        DEVICENAME = layout.offsetof(1);
        DEVICESTRING = layout.offsetof(2);
        STATEFLAGS = layout.offsetof(3);
        DEVICEID = layout.offsetof(4);
        DEVICEKEY = layout.offsetof(5);
    }

    public static class Buffer
    extends StructBuffer<DISPLAY_DEVICE, Buffer>
    implements NativeResource {
        private static final DISPLAY_DEVICE ELEMENT_FACTORY = DISPLAY_DEVICE.create(-1L);

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
        protected DISPLAY_DEVICE getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="DWORD")
        public int cb() {
            return DISPLAY_DEVICE.ncb(this.address());
        }

        @NativeType(value="TCHAR[32]")
        public ByteBuffer DeviceName() {
            return DISPLAY_DEVICE.nDeviceName(this.address());
        }

        @NativeType(value="TCHAR[32]")
        public String DeviceNameString() {
            return DISPLAY_DEVICE.nDeviceNameString(this.address());
        }

        @NativeType(value="TCHAR[128]")
        public ByteBuffer DeviceString() {
            return DISPLAY_DEVICE.nDeviceString(this.address());
        }

        @NativeType(value="TCHAR[128]")
        public String DeviceStringString() {
            return DISPLAY_DEVICE.nDeviceStringString(this.address());
        }

        @NativeType(value="DWORD")
        public int StateFlags() {
            return DISPLAY_DEVICE.nStateFlags(this.address());
        }

        @NativeType(value="TCHAR[128]")
        public ByteBuffer DeviceID() {
            return DISPLAY_DEVICE.nDeviceID(this.address());
        }

        @NativeType(value="TCHAR[128]")
        public String DeviceIDString() {
            return DISPLAY_DEVICE.nDeviceIDString(this.address());
        }

        @NativeType(value="TCHAR[128]")
        public ByteBuffer DeviceKey() {
            return DISPLAY_DEVICE.nDeviceKey(this.address());
        }

        @NativeType(value="TCHAR[128]")
        public String DeviceKeyString() {
            return DISPLAY_DEVICE.nDeviceKeyString(this.address());
        }

        public Buffer cb(@NativeType(value="DWORD") int value) {
            DISPLAY_DEVICE.ncb(this.address(), value);
            return this;
        }
    }
}
