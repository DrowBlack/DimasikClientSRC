package org.lwjgl.stb;

import org.lwjgl.system.APIUtil;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;
import org.lwjgl.system.libffi.LibFFI;

@FunctionalInterface
@NativeType(value="int (*) (void *, char *, int)")
public interface STBIReadCallbackI
extends CallbackI {
    public static final FFICIF CIF = APIUtil.apiCreateCIF(LibFFI.FFI_DEFAULT_ABI, LibFFI.ffi_type_sint32, LibFFI.ffi_type_pointer, LibFFI.ffi_type_pointer, LibFFI.ffi_type_sint32);

    @Override
    default public FFICIF getCallInterface() {
        return CIF;
    }

    @Override
    default public void callback(long ret, long args) {
        int __result = this.invoke(MemoryUtil.memGetAddress(MemoryUtil.memGetAddress(args)), MemoryUtil.memGetAddress(MemoryUtil.memGetAddress(args + (long)POINTER_SIZE)), MemoryUtil.memGetInt(MemoryUtil.memGetAddress(args + (long)(2 * POINTER_SIZE))));
        APIUtil.apiClosureRet(ret, __result);
    }

    public int invoke(@NativeType(value="void *") long var1, @NativeType(value="char *") long var3, int var5);
}
