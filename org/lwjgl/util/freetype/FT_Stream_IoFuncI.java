package org.lwjgl.util.freetype;

import org.lwjgl.system.APIUtil;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;
import org.lwjgl.system.libffi.LibFFI;

@FunctionalInterface
@NativeType(value="FT_Stream_IoFunc")
public interface FT_Stream_IoFuncI
extends CallbackI {
    public static final FFICIF CIF = APIUtil.apiCreateCIF(LibFFI.FFI_DEFAULT_ABI, LibFFI.ffi_type_ulong, LibFFI.ffi_type_pointer, LibFFI.ffi_type_ulong, LibFFI.ffi_type_pointer, LibFFI.ffi_type_ulong);

    @Override
    default public FFICIF getCallInterface() {
        return CIF;
    }

    @Override
    default public void callback(long ret, long args) {
        long __result = this.invoke(MemoryUtil.memGetAddress(MemoryUtil.memGetAddress(args)), MemoryUtil.memGetCLong(MemoryUtil.memGetAddress(args + (long)POINTER_SIZE)), MemoryUtil.memGetAddress(MemoryUtil.memGetAddress(args + (long)(2 * POINTER_SIZE))), MemoryUtil.memGetCLong(MemoryUtil.memGetAddress(args + (long)(3 * POINTER_SIZE))));
        APIUtil.apiClosureRet(ret, __result);
    }

    @NativeType(value="unsigned long")
    public long invoke(@NativeType(value="FT_Stream") long var1, @NativeType(value="unsigned long") long var3, @NativeType(value="unsigned char *") long var5, @NativeType(value="unsigned long") long var7);
}
