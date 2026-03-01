package org.lwjgl.system.macosx;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.SharedLibraryUtil;
import org.lwjgl.system.macosx.DynamicLinkLoader;
import org.lwjgl.system.macosx.MacOSXLibrary;

public class MacOSXLibraryDL
extends MacOSXLibrary {
    public MacOSXLibraryDL(String name) {
        this(name, MacOSXLibraryDL.loadLibrary(name));
    }

    public MacOSXLibraryDL(String name, long handle) {
        super(name, handle);
    }

    private static long loadLibrary(String name) {
        long handle;
        try (MemoryStack stack = MemoryStack.stackPush();){
            handle = DynamicLinkLoader.dlopen(stack.UTF8(name), 5);
        }
        if (handle == 0L) {
            throw new UnsatisfiedLinkError("Failed to dynamically load library: " + name + "(error = " + DynamicLinkLoader.dlerror() + ")");
        }
        return handle;
    }

    @Override
    @Nullable
    public String getPath() {
        return SharedLibraryUtil.getLibraryPath(this.address());
    }

    @Override
    public long getFunctionAddress(ByteBuffer functionName) {
        return DynamicLinkLoader.dlsym(this.address(), functionName);
    }

    @Override
    public void free() {
        DynamicLinkLoader.dlclose(this.address());
    }
}
