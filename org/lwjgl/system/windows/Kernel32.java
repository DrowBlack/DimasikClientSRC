package org.lwjgl.system.windows;

import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Checks;
import org.lwjgl.system.JNI;
import org.lwjgl.system.Library;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.SharedLibrary;

public class Kernel32 {
    private static final SharedLibrary KERNEL32 = Library.loadNative(Kernel32.class, "org.lwjgl", "kernel32");

    public static SharedLibrary getLibrary() {
        return KERNEL32;
    }

    protected Kernel32() {
        throw new UnsupportedOperationException();
    }

    @NativeType(value="HANDLE")
    public static long GetCurrentProcess() {
        long __functionAddress = Functions.GetCurrentProcess;
        return JNI.callP(__functionAddress);
    }

    @NativeType(value="DWORD")
    public static int GetCurrentProcessId() {
        long __functionAddress = Functions.GetCurrentProcessId;
        return JNI.callI(__functionAddress);
    }

    @NativeType(value="DWORD")
    public static int GetProcessId(@NativeType(value="HANDLE") long Process) {
        long __functionAddress = Functions.GetProcessId;
        if (Checks.CHECKS) {
            Checks.check(Process);
        }
        return JNI.callPI(Process, __functionAddress);
    }

    @NativeType(value="HANDLE")
    public static long GetCurrentThread() {
        long __functionAddress = Functions.GetCurrentThread;
        return JNI.callP(__functionAddress);
    }

    @NativeType(value="DWORD")
    public static int GetCurrentThreadId() {
        long __functionAddress = Functions.GetCurrentThreadId;
        return JNI.callI(__functionAddress);
    }

    @NativeType(value="DWORD")
    public static int GetThreadId(@NativeType(value="HANDLE") long Thread) {
        long __functionAddress = Functions.GetThreadId;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
            Checks.check(Thread);
        }
        return JNI.callPI(Thread, __functionAddress);
    }

    @NativeType(value="DWORD")
    public static int GetProcessIdOfThread(@NativeType(value="HANDLE") long Thread) {
        long __functionAddress = Functions.GetProcessIdOfThread;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
            Checks.check(Thread);
        }
        return JNI.callPI(Thread, __functionAddress);
    }

    @NativeType(value="DWORD")
    public static int GetCurrentProcessorNumber() {
        long __functionAddress = Functions.GetCurrentProcessorNumber;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        return JNI.callI(__functionAddress);
    }

    static /* synthetic */ SharedLibrary access$000() {
        return KERNEL32;
    }

    public static final class Functions {
        public static final long GetCurrentProcess = APIUtil.apiGetFunctionAddress(Kernel32.access$000(), "GetCurrentProcess");
        public static final long GetCurrentProcessId = APIUtil.apiGetFunctionAddress(Kernel32.access$000(), "GetCurrentProcessId");
        public static final long GetProcessId = APIUtil.apiGetFunctionAddress(Kernel32.access$000(), "GetProcessId");
        public static final long GetCurrentThread = APIUtil.apiGetFunctionAddress(Kernel32.access$000(), "GetCurrentThread");
        public static final long GetCurrentThreadId = APIUtil.apiGetFunctionAddress(Kernel32.access$000(), "GetCurrentThreadId");
        public static final long GetThreadId = APIUtil.apiGetFunctionAddressOptional(Kernel32.access$000(), "GetThreadId");
        public static final long GetProcessIdOfThread = APIUtil.apiGetFunctionAddressOptional(Kernel32.access$000(), "GetProcessIdOfThread");
        public static final long GetCurrentProcessorNumber = APIUtil.apiGetFunctionAddressOptional(Kernel32.access$000(), "GetCurrentProcessorNumber");

        private Functions() {
        }
    }
}
