package org.lwjgl.openal;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import org.lwjgl.openal.ALC;
import org.lwjgl.system.Checks;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class SOFTReopenDevice {
    protected SOFTReopenDevice() {
        throw new UnsupportedOperationException();
    }

    public static boolean nalcReopenDeviceSOFT(long device, long deviceName, long attribs) {
        long __functionAddress = ALC.getICD().alcReopenDeviceSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
            Checks.check(device);
        }
        return JNI.invokePPPZ(device, deviceName, attribs, __functionAddress);
    }

    @NativeType(value="ALCboolean")
    public static boolean alcReopenDeviceSOFT(@NativeType(value="ALCdevice *") long device, @Nullable @NativeType(value="ALCchar const *") ByteBuffer deviceName, @Nullable @NativeType(value="ALCint const *") IntBuffer attribs) {
        if (Checks.CHECKS) {
            Checks.checkNT1Safe(deviceName);
            Checks.checkNTSafe(attribs);
        }
        return SOFTReopenDevice.nalcReopenDeviceSOFT(device, MemoryUtil.memAddressSafe(deviceName), MemoryUtil.memAddressSafe(attribs));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NativeType(value="ALCboolean")
    public static boolean alcReopenDeviceSOFT(@NativeType(value="ALCdevice *") long device, @Nullable @NativeType(value="ALCchar const *") CharSequence deviceName, @Nullable @NativeType(value="ALCint const *") IntBuffer attribs) {
        if (Checks.CHECKS) {
            Checks.checkNTSafe(attribs);
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8Safe(deviceName, true);
            long deviceNameEncoded = deviceName == null ? 0L : stack.getPointerAddress();
            boolean bl = SOFTReopenDevice.nalcReopenDeviceSOFT(device, deviceNameEncoded, MemoryUtil.memAddressSafe(attribs));
            return bl;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    @NativeType(value="ALCboolean")
    public static boolean alcReopenDeviceSOFT(@NativeType(value="ALCdevice *") long device, @Nullable @NativeType(value="ALCchar const *") ByteBuffer deviceName, @Nullable @NativeType(value="ALCint const *") int[] attribs) {
        long __functionAddress = ALC.getICD().alcReopenDeviceSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
            Checks.check(device);
            Checks.checkNT1Safe(deviceName);
            Checks.checkNTSafe(attribs);
        }
        return JNI.invokePPPZ(device, MemoryUtil.memAddressSafe(deviceName), attribs, __functionAddress);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NativeType(value="ALCboolean")
    public static boolean alcReopenDeviceSOFT(@NativeType(value="ALCdevice *") long device, @Nullable @NativeType(value="ALCchar const *") CharSequence deviceName, @Nullable @NativeType(value="ALCint const *") int[] attribs) {
        long __functionAddress = ALC.getICD().alcReopenDeviceSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
            Checks.check(device);
            Checks.checkNTSafe(attribs);
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8Safe(deviceName, true);
            long deviceNameEncoded = deviceName == null ? 0L : stack.getPointerAddress();
            boolean bl = JNI.invokePPPZ(device, deviceNameEncoded, attribs, __functionAddress);
            return bl;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }
}
