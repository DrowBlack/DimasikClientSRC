package org.lwjgl.util.freetype;

import org.lwjgl.system.APIUtil;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;
import org.lwjgl.system.libffi.LibFFI;

@FunctionalInterface
@NativeType(value="FT_Realloc_Func")
public interface FT_Realloc_FuncI
extends CallbackI {
    public static final FFICIF CIF = APIUtil.apiCreateCIF(LibFFI.FFI_DEFAULT_ABI, LibFFI.ffi_type_pointer, LibFFI.ffi_type_pointer, LibFFI.ffi_type_slong, LibFFI.ffi_type_slong, LibFFI.ffi_type_pointer);

    @Override
    default public FFICIF getCallInterface() {
        return CIF;
    }

    @Override
    default public void callback(long ret, long args) {
        long __result = this.invoke(MemoryUtil.memGetAddress(MemoryUtil.memGetAddress(args)), MemoryUtil.memGetCLong(MemoryUtil.memGetAddress(args + (long)POINTER_SIZE)), MemoryUtil.memGetCLong(MemoryUtil.memGetAddress(args + (long)(2 * POINTER_SIZE))), MemoryUtil.memGetAddress(MemoryUtil.memGetAddress(args + (long)(3 * POINTER_SIZE))));
        APIUtil.apiClosureRetP(ret, __result);
    }

    @NativeType(value="void *")
    public long invoke(@NativeType(value="FT_Memory") long var1, long var3, long var5, @NativeType(value="void *") long var7);
}
