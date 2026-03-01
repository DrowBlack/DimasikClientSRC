package org.lwjgl.openal;

import org.lwjgl.PointerBuffer;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.SOFTCallbackBufferTypeI;
import org.lwjgl.system.Checks;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class SOFTCallbackBuffer {
    public static final int AL_BUFFER_CALLBACK_FUNCTION_SOFT = 6560;
    public static final int AL_BUFFER_CALLBACK_USER_PARAM_SOFT = 6561;

    protected SOFTCallbackBuffer() {
        throw new UnsupportedOperationException();
    }

    public static void nalBufferCallbackSOFT(int buffer, int format, int freq, long callback, long userptr) {
        long __functionAddress = AL.getICD().alBufferCallbackSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
            Checks.check(userptr);
        }
        JNI.invokePPV(buffer, format, freq, callback, userptr, __functionAddress);
    }

    @NativeType(value="ALvoid")
    public static void alBufferCallbackSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALenum") int format, @NativeType(value="ALsizei") int freq, @NativeType(value="ALBUFFERCALLBACKTYPESOFT") SOFTCallbackBufferTypeI callback, @NativeType(value="ALvoid *") long userptr) {
        SOFTCallbackBuffer.nalBufferCallbackSOFT(buffer, format, freq, callback.address(), userptr);
    }

    public static void nalGetBufferPtrSOFT(int buffer, int param, long ptr) {
        long __functionAddress = AL.getICD().alGetBufferPtrSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, param, ptr, __functionAddress);
    }

    @NativeType(value="ALvoid")
    public static void alGetBufferPtrSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALenum") int param, @NativeType(value="ALvoid **") PointerBuffer ptr) {
        if (Checks.CHECKS) {
            Checks.check(ptr, 1);
        }
        SOFTCallbackBuffer.nalGetBufferPtrSOFT(buffer, param, MemoryUtil.memAddress(ptr));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NativeType(value="ALvoid")
    public static long alGetBufferPtrSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALenum") int param) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            PointerBuffer ptr = stack.callocPointer(1);
            SOFTCallbackBuffer.nalGetBufferPtrSOFT(buffer, param, MemoryUtil.memAddress(ptr));
            long l = ptr.get(0);
            return l;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static void nalGetBuffer3PtrSOFT(int buffer, int param, long ptr0, long ptr1, long ptr2) {
        long __functionAddress = AL.getICD().alGetBuffer3PtrSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePPPV(buffer, param, ptr0, ptr1, ptr2, __functionAddress);
    }

    @NativeType(value="ALvoid")
    public static void alGetBuffer3PtrSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALenum") int param, @NativeType(value="ALvoid **") PointerBuffer ptr0, @NativeType(value="ALvoid **") PointerBuffer ptr1, @NativeType(value="ALvoid **") PointerBuffer ptr2) {
        if (Checks.CHECKS) {
            Checks.check(ptr0, 1);
            Checks.check(ptr1, 1);
            Checks.check(ptr2, 1);
        }
        SOFTCallbackBuffer.nalGetBuffer3PtrSOFT(buffer, param, MemoryUtil.memAddress(ptr0), MemoryUtil.memAddress(ptr1), MemoryUtil.memAddress(ptr2));
    }

    public static void nalGetBufferPtrvSOFT(int buffer, int param, long ptr) {
        long __functionAddress = AL.getICD().alGetBufferPtrvSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, param, ptr, __functionAddress);
    }

    @NativeType(value="ALvoid")
    public static void alGetBufferPtrvSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALenum") int param, @NativeType(value="ALvoid **") PointerBuffer ptr) {
        if (Checks.CHECKS) {
            Checks.check(ptr, 1);
        }
        SOFTCallbackBuffer.nalGetBufferPtrvSOFT(buffer, param, MemoryUtil.memAddress(ptr));
    }
}
