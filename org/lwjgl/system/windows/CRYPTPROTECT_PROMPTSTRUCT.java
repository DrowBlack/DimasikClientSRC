package org.lwjgl.system.windows;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;

public class CRYPTPROTECT_PROMPTSTRUCT
extends Struct<CRYPTPROTECT_PROMPTSTRUCT>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int CBSIZE;
    public static final int DWPROMPTFLAGS;
    public static final int HWNDAPP;
    public static final int SZPROMPT;

    protected CRYPTPROTECT_PROMPTSTRUCT(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected CRYPTPROTECT_PROMPTSTRUCT create(long address, @Nullable ByteBuffer container) {
        return new CRYPTPROTECT_PROMPTSTRUCT(address, container);
    }

    public CRYPTPROTECT_PROMPTSTRUCT(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), CRYPTPROTECT_PROMPTSTRUCT.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="DWORD")
    public int cbSize() {
        return CRYPTPROTECT_PROMPTSTRUCT.ncbSize(this.address());
    }

    @NativeType(value="DWORD")
    public int dwPromptFlags() {
        return CRYPTPROTECT_PROMPTSTRUCT.ndwPromptFlags(this.address());
    }

    @NativeType(value="HWND")
    public long hwndApp() {
        return CRYPTPROTECT_PROMPTSTRUCT.nhwndApp(this.address());
    }

    @NativeType(value="LPCWSTR")
    public ByteBuffer szPrompt() {
        return CRYPTPROTECT_PROMPTSTRUCT.nszPrompt(this.address());
    }

    @NativeType(value="LPCWSTR")
    public String szPromptString() {
        return CRYPTPROTECT_PROMPTSTRUCT.nszPromptString(this.address());
    }

    public CRYPTPROTECT_PROMPTSTRUCT cbSize(@NativeType(value="DWORD") int value) {
        CRYPTPROTECT_PROMPTSTRUCT.ncbSize(this.address(), value);
        return this;
    }

    public CRYPTPROTECT_PROMPTSTRUCT cbSize$Default() {
        return this.cbSize(SIZEOF);
    }

    public CRYPTPROTECT_PROMPTSTRUCT dwPromptFlags(@NativeType(value="DWORD") int value) {
        CRYPTPROTECT_PROMPTSTRUCT.ndwPromptFlags(this.address(), value);
        return this;
    }

    public CRYPTPROTECT_PROMPTSTRUCT hwndApp(@NativeType(value="HWND") long value) {
        CRYPTPROTECT_PROMPTSTRUCT.nhwndApp(this.address(), value);
        return this;
    }

    public CRYPTPROTECT_PROMPTSTRUCT szPrompt(@NativeType(value="LPCWSTR") ByteBuffer value) {
        CRYPTPROTECT_PROMPTSTRUCT.nszPrompt(this.address(), value);
        return this;
    }

    public CRYPTPROTECT_PROMPTSTRUCT set(int cbSize, int dwPromptFlags, long hwndApp, ByteBuffer szPrompt) {
        this.cbSize(cbSize);
        this.dwPromptFlags(dwPromptFlags);
        this.hwndApp(hwndApp);
        this.szPrompt(szPrompt);
        return this;
    }

    public CRYPTPROTECT_PROMPTSTRUCT set(CRYPTPROTECT_PROMPTSTRUCT src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static CRYPTPROTECT_PROMPTSTRUCT malloc() {
        return new CRYPTPROTECT_PROMPTSTRUCT(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static CRYPTPROTECT_PROMPTSTRUCT calloc() {
        return new CRYPTPROTECT_PROMPTSTRUCT(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static CRYPTPROTECT_PROMPTSTRUCT create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new CRYPTPROTECT_PROMPTSTRUCT(MemoryUtil.memAddress(container), container);
    }

    public static CRYPTPROTECT_PROMPTSTRUCT create(long address) {
        return new CRYPTPROTECT_PROMPTSTRUCT(address, null);
    }

    @Nullable
    public static CRYPTPROTECT_PROMPTSTRUCT createSafe(long address) {
        return address == 0L ? null : new CRYPTPROTECT_PROMPTSTRUCT(address, null);
    }

    public static CRYPTPROTECT_PROMPTSTRUCT malloc(MemoryStack stack) {
        return new CRYPTPROTECT_PROMPTSTRUCT(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static CRYPTPROTECT_PROMPTSTRUCT calloc(MemoryStack stack) {
        return new CRYPTPROTECT_PROMPTSTRUCT(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static int ncbSize(long struct) {
        return UNSAFE.getInt(null, struct + (long)CBSIZE);
    }

    public static int ndwPromptFlags(long struct) {
        return UNSAFE.getInt(null, struct + (long)DWPROMPTFLAGS);
    }

    public static long nhwndApp(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)HWNDAPP);
    }

    public static ByteBuffer nszPrompt(long struct) {
        return MemoryUtil.memByteBufferNT2(MemoryUtil.memGetAddress(struct + (long)SZPROMPT));
    }

    public static String nszPromptString(long struct) {
        return MemoryUtil.memUTF16(MemoryUtil.memGetAddress(struct + (long)SZPROMPT));
    }

    public static void ncbSize(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)CBSIZE, value);
    }

    public static void ndwPromptFlags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)DWPROMPTFLAGS, value);
    }

    public static void nhwndApp(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)HWNDAPP, Checks.check(value));
    }

    public static void nszPrompt(long struct, ByteBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkNT2(value);
        }
        MemoryUtil.memPutAddress(struct + (long)SZPROMPT, MemoryUtil.memAddress(value));
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)HWNDAPP));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)SZPROMPT));
    }

    static {
        Struct.Layout layout = CRYPTPROTECT_PROMPTSTRUCT.__struct(CRYPTPROTECT_PROMPTSTRUCT.__member(4), CRYPTPROTECT_PROMPTSTRUCT.__member(4), CRYPTPROTECT_PROMPTSTRUCT.__member(POINTER_SIZE), CRYPTPROTECT_PROMPTSTRUCT.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        CBSIZE = layout.offsetof(0);
        DWPROMPTFLAGS = layout.offsetof(1);
        HWNDAPP = layout.offsetof(2);
        SZPROMPT = layout.offsetof(3);
    }
}
