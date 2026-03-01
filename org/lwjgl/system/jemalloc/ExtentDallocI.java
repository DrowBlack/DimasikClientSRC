package org.lwjgl.system.jemalloc;

import org.lwjgl.system.APIUtil;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;
import org.lwjgl.system.libffi.LibFFI;

@FunctionalInterface
@NativeType(value="extent_dalloc_t")
public interface ExtentDallocI
extends CallbackI {
    public static final FFICIF CIF = APIUtil.apiCreateCIF(LibFFI.FFI_DEFAULT_ABI, LibFFI.ffi_type_uint8, LibFFI.ffi_type_pointer, LibFFI.ffi_type_pointer, LibFFI.ffi_type_pointer, LibFFI.ffi_type_uint8, LibFFI.ffi_type_uint32);

    @Override
    default public FFICIF getCallInterface() {
        return CIF;
    }

    @Override
    default public void callback(long ret, long args) {
        boolean __result = this.invoke(MemoryUtil.memGetAddress(MemoryUtil.memGetAddress(args)), MemoryUtil.memGetAddress(MemoryUtil.memGetAddress(args + (long)POINTER_SIZE)), MemoryUtil.memGetAddress(MemoryUtil.memGetAddress(args + (long)(2 * POINTER_SIZE))), MemoryUtil.memGetByte(MemoryUtil.memGetAddress(args + (long)(3 * POINTER_SIZE))) != 0, MemoryUtil.memGetInt(MemoryUtil.memGetAddress(args + (long)(4 * POINTER_SIZE))));
        APIUtil.apiClosureRet(ret, __result);
    }

    @NativeType(value="bool")
    public boolean invoke(@NativeType(value="extent_hooks_t *") long var1, @NativeType(value="void *") long var3, @NativeType(value="size_t") long var5, @NativeType(value="bool") boolean var7, @NativeType(value="unsigned int") int var8);
}
