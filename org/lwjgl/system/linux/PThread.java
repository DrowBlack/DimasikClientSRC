package org.lwjgl.system.linux;

import org.lwjgl.system.Library;
import org.lwjgl.system.NativeType;

public class PThread {
    protected PThread() {
        throw new UnsupportedOperationException();
    }

    @NativeType(value="pthread_t")
    public static native long pthread_self();

    static {
        Library.initialize();
    }
}
