package org.lwjgl.openal;

import org.lwjgl.openal.ALC;
import org.lwjgl.system.Checks;
import org.lwjgl.system.JNI;
import org.lwjgl.system.NativeType;

public class SOFTPauseDevice {
    protected SOFTPauseDevice() {
        throw new UnsupportedOperationException();
    }

    @NativeType(value="ALCvoid")
    public static void alcDevicePauseSOFT(@NativeType(value="ALCdevice *") long device) {
        long __functionAddress = ALC.getICD().alcDevicePauseSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
            Checks.check(device);
        }
        JNI.invokePV(device, __functionAddress);
    }

    @NativeType(value="ALCvoid")
    public static void alcDeviceResumeSOFT(@NativeType(value="ALCdevice *") long device) {
        long __functionAddress = ALC.getICD().alcDeviceResumeSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
            Checks.check(device);
        }
        JNI.invokePV(device, __functionAddress);
    }
}
