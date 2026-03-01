package org.lwjgl.system.linux;

import org.lwjgl.system.Library;

public class Socket {
    public static final int SHUT_RD = 0;
    public static final int SHUT_WR = 1;
    public static final int SHUT_RDWR = 2;

    protected Socket() {
        throw new UnsupportedOperationException();
    }

    public static native int socket(int var0, int var1, int var2);

    static {
        Library.initialize();
    }
}
