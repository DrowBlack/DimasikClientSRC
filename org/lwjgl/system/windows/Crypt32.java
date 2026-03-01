package org.lwjgl.system.windows;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Checks;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.SharedLibrary;
import org.lwjgl.system.windows.CRYPTPROTECT_PROMPTSTRUCT;
import org.lwjgl.system.windows.DATA_BLOB;

public class Crypt32 {
    private static final SharedLibrary CRYPT32 = Library.loadNative(Crypt32.class, "org.lwjgl", "crypt32");
    public static final int CRYPTPROTECT_UI_FORBIDDEN = 1;
    public static final int CRYPTPROTECT_LOCAL_MACHINE = 4;
    public static final int CRYPTPROTECT_AUDIT = 16;
    public static final int CRYPTPROTECT_VERIFY_PROTECTION = 64;
    public static final int CRYPTPROTECTMEMORY_SAME_PROCESS = 0;
    public static final int CRYPTPROTECTMEMORY_CROSS_PROCESS = 1;
    public static final int CRYPTPROTECTMEMORY_SAME_LOGON = 2;
    public static final int CRYPTPROTECT_PROMPT_ON_UNPROTECT = 1;
    public static final int CRYPTPROTECT_PROMPT_ON_PROTECT = 2;
    public static final int CRYPTPROTECTMEMORY_BLOCK_SIZE = 16;

    public static SharedLibrary getLibrary() {
        return CRYPT32;
    }

    protected Crypt32() {
        throw new UnsupportedOperationException();
    }

    public static native int nCryptProtectData(long var0, long var2, long var4, long var6, long var8, int var10, long var11, long var13);

    public static int nCryptProtectData(long pDataIn, long szDataDescr, long pOptionalEntropy, long pvReserved, long pPromptStruct, int dwFlags, long pDataOut) {
        long __functionAddress = Functions.CryptProtectData;
        return Crypt32.nCryptProtectData(pDataIn, szDataDescr, pOptionalEntropy, pvReserved, pPromptStruct, dwFlags, pDataOut, __functionAddress);
    }

    @NativeType(value="BOOL")
    public static boolean CryptProtectData(@NativeType(value="DATA_BLOB *") DATA_BLOB pDataIn, @Nullable @NativeType(value="LPCWSTR") ByteBuffer szDataDescr, @Nullable @NativeType(value="DATA_BLOB *") DATA_BLOB pOptionalEntropy, @NativeType(value="PVOID") long pvReserved, @Nullable @NativeType(value="CRYPTPROTECT_PROMPTSTRUCT *") CRYPTPROTECT_PROMPTSTRUCT pPromptStruct, @NativeType(value="DWORD") int dwFlags, @NativeType(value="DATA_BLOB *") DATA_BLOB pDataOut) {
        if (Checks.CHECKS) {
            Checks.checkNT2Safe(szDataDescr);
        }
        return Crypt32.nCryptProtectData(pDataIn.address(), MemoryUtil.memAddressSafe(szDataDescr), MemoryUtil.memAddressSafe(pOptionalEntropy), pvReserved, MemoryUtil.memAddressSafe(pPromptStruct), dwFlags, pDataOut.address()) != 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NativeType(value="BOOL")
    public static boolean CryptProtectData(@NativeType(value="DATA_BLOB *") DATA_BLOB pDataIn, @Nullable @NativeType(value="LPCWSTR") CharSequence szDataDescr, @Nullable @NativeType(value="DATA_BLOB *") DATA_BLOB pOptionalEntropy, @NativeType(value="PVOID") long pvReserved, @Nullable @NativeType(value="CRYPTPROTECT_PROMPTSTRUCT *") CRYPTPROTECT_PROMPTSTRUCT pPromptStruct, @NativeType(value="DWORD") int dwFlags, @NativeType(value="DATA_BLOB *") DATA_BLOB pDataOut) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF16Safe(szDataDescr, true);
            long szDataDescrEncoded = szDataDescr == null ? 0L : stack.getPointerAddress();
            boolean bl = Crypt32.nCryptProtectData(pDataIn.address(), szDataDescrEncoded, MemoryUtil.memAddressSafe(pOptionalEntropy), pvReserved, MemoryUtil.memAddressSafe(pPromptStruct), dwFlags, pDataOut.address()) != 0;
            return bl;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native int nCryptProtectMemory(long var0, int var2, int var3, long var4);

    public static int nCryptProtectMemory(long pDataIn, int cbDataIn, int dwFlags) {
        long __functionAddress = Functions.CryptProtectMemory;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        return Crypt32.nCryptProtectMemory(pDataIn, cbDataIn, dwFlags, __functionAddress);
    }

    @NativeType(value="BOOL")
    public static boolean CryptProtectMemory(@NativeType(value="LPVOID") ByteBuffer pDataIn, @NativeType(value="DWORD") int dwFlags) {
        return Crypt32.nCryptProtectMemory(MemoryUtil.memAddress(pDataIn), pDataIn.remaining(), dwFlags) != 0;
    }

    public static native int nCryptUnprotectData(long var0, long var2, long var4, long var6, long var8, int var10, long var11, long var13);

    public static int nCryptUnprotectData(long pDataIn, long ppszDataDescr, long pOptionalEntropy, long pvReserved, long pPromptStruct, int dwFlags, long pDataOut) {
        long __functionAddress = Functions.CryptUnprotectData;
        return Crypt32.nCryptUnprotectData(pDataIn, ppszDataDescr, pOptionalEntropy, pvReserved, pPromptStruct, dwFlags, pDataOut, __functionAddress);
    }

    @NativeType(value="BOOL")
    public static boolean CryptUnprotectData(@NativeType(value="DATA_BLOB *") DATA_BLOB pDataIn, @Nullable @NativeType(value="LPWSTR *") PointerBuffer ppszDataDescr, @Nullable @NativeType(value="DATA_BLOB *") DATA_BLOB pOptionalEntropy, @NativeType(value="PVOID") long pvReserved, @Nullable @NativeType(value="CRYPTPROTECT_PROMPTSTRUCT *") CRYPTPROTECT_PROMPTSTRUCT pPromptStruct, @NativeType(value="DWORD") int dwFlags, @NativeType(value="DATA_BLOB *") DATA_BLOB pDataOut) {
        if (Checks.CHECKS) {
            Checks.checkSafe(ppszDataDescr, 1);
        }
        return Crypt32.nCryptUnprotectData(pDataIn.address(), MemoryUtil.memAddressSafe(ppszDataDescr), MemoryUtil.memAddressSafe(pOptionalEntropy), pvReserved, MemoryUtil.memAddressSafe(pPromptStruct), dwFlags, pDataOut.address()) != 0;
    }

    public static native int nCryptUnprotectMemory(long var0, int var2, int var3, long var4);

    public static int nCryptUnprotectMemory(long pDataIn, int cbDataIn, int dwFlags) {
        long __functionAddress = Functions.CryptUnprotectMemory;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        return Crypt32.nCryptUnprotectMemory(pDataIn, cbDataIn, dwFlags, __functionAddress);
    }

    @NativeType(value="BOOL")
    public static boolean CryptUnprotectMemory(@NativeType(value="LPVOID") ByteBuffer pDataIn, @NativeType(value="DWORD") int dwFlags) {
        return Crypt32.nCryptUnprotectMemory(MemoryUtil.memAddress(pDataIn), pDataIn.remaining(), dwFlags) != 0;
    }

    static /* synthetic */ SharedLibrary access$000() {
        return CRYPT32;
    }

    public static final class Functions {
        public static final long CryptProtectData = APIUtil.apiGetFunctionAddress(Crypt32.access$000(), "CryptProtectData");
        public static final long CryptProtectMemory = APIUtil.apiGetFunctionAddressOptional(Crypt32.access$000(), "CryptProtectMemory");
        public static final long CryptUnprotectData = APIUtil.apiGetFunctionAddress(Crypt32.access$000(), "CryptUnprotectData");
        public static final long CryptUnprotectMemory = APIUtil.apiGetFunctionAddressOptional(Crypt32.access$000(), "CryptUnprotectMemory");

        private Functions() {
        }
    }
}
