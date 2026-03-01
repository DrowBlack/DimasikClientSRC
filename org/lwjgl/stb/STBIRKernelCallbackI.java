package org.lwjgl.stb;

import org.lwjgl.system.APIUtil;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;
import org.lwjgl.system.libffi.LibFFI;

@FunctionalInterface
@NativeType(value="stbir__kernel_callback *")
public interface STBIRKernelCallbackI
extends CallbackI {
    public static final FFICIF CIF = APIUtil.apiCreateCIF(LibFFI.FFI_DEFAULT_ABI, LibFFI.ffi_type_float, LibFFI.ffi_type_float, LibFFI.ffi_type_float, LibFFI.ffi_type_pointer);

    @Override
    default public FFICIF getCallInterface() {
        return CIF;
    }

    @Override
    default public void callback(long ret, long args) {
        float __result = this.invoke(MemoryUtil.memGetFloat(MemoryUtil.memGetAddress(args)), MemoryUtil.memGetFloat(MemoryUtil.memGetAddress(args + (long)POINTER_SIZE)), MemoryUtil.memGetAddress(MemoryUtil.memGetAddress(args + (long)(2 * POINTER_SIZE))));
        APIUtil.apiClosureRet(ret, __result);
    }

    public float invoke(float var1, float var2, @NativeType(value="void *") long var3);
}
