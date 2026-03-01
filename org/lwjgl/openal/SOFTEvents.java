package org.lwjgl.openal;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.SOFTEventProcI;
import org.lwjgl.system.Checks;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class SOFTEvents {
    public static final int AL_EVENT_CALLBACK_FUNCTION_SOFT = 6562;
    public static final int AL_EVENT_CALLBACK_USER_PARAM_SOFT = 6563;
    public static final int AL_EVENT_TYPE_BUFFER_COMPLETED_SOFT = 6564;
    public static final int AL_EVENT_TYPE_SOURCE_STATE_CHANGED_SOFT = 6565;
    public static final int AL_EVENT_TYPE_DISCONNECTED_SOFT = 6566;

    protected SOFTEvents() {
        throw new UnsupportedOperationException();
    }

    public static void nalEventControlSOFT(int count, long types, boolean enable) {
        long __functionAddress = AL.getICD().alEventControlSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(count, types, enable, __functionAddress);
    }

    public static void alEventControlSOFT(@NativeType(value="ALenum const *") IntBuffer types, @NativeType(value="ALboolean") boolean enable) {
        SOFTEvents.nalEventControlSOFT(types.remaining(), MemoryUtil.memAddress(types), enable);
    }

    public static void nalEventCallbackSOFT(long callback, long userParam) {
        long __functionAddress = AL.getICD().alEventCallbackSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePPV(callback, userParam, __functionAddress);
    }

    public static void alEventCallbackSOFT(@NativeType(value="ALEVENTPROCSOFT") SOFTEventProcI callback, @Nullable @NativeType(value="ALvoid *") ByteBuffer userParam) {
        SOFTEvents.nalEventCallbackSOFT(callback.address(), MemoryUtil.memAddressSafe(userParam));
    }

    @NativeType(value="ALvoid *")
    public static long alGetPointerSOFT(@NativeType(value="ALenum") int pname) {
        long __functionAddress = AL.getICD().alGetPointerSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        return JNI.invokeP(pname, __functionAddress);
    }

    public static void nalGetPointervSOFT(int pname, long values) {
        long __functionAddress = AL.getICD().alGetPointervSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(pname, values, __functionAddress);
    }

    public static void alGetPointervSOFT(@NativeType(value="ALenum") int pname, @NativeType(value="ALvoid **") PointerBuffer values) {
        if (Checks.CHECKS) {
            Checks.check(values, 1);
        }
        SOFTEvents.nalGetPointervSOFT(pname, MemoryUtil.memAddress(values));
    }

    public static void alEventControlSOFT(@NativeType(value="ALenum const *") int[] types, @NativeType(value="ALboolean") boolean enable) {
        long __functionAddress = AL.getICD().alEventControlSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(types.length, types, enable, __functionAddress);
    }
}
