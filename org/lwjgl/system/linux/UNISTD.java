package org.lwjgl.system.linux;

import java.nio.ByteBuffer;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class UNISTD {
    public static final int _SC_OPEN_MAX = 4;
    public static final int _SC_PAGE_SIZE = 30;
    public static final int _SC_IOV_MAX = 60;

    protected UNISTD() {
        throw new UnsupportedOperationException();
    }

    public static native int close(int var0);

    public static native long sysconf(int var0);

    public static native long nread(int var0, long var1, long var3);

    @NativeType(value="ssize_t")
    public static long read(int fd, @NativeType(value="void *") ByteBuffer buf) {
        return UNISTD.nread(fd, MemoryUtil.memAddress(buf), buf.remaining());
    }

    @NativeType(value="pid_t")
    public static native int getpid();

    @NativeType(value="pid_t")
    public static native int getppid();

    @NativeType(value="pid_t")
    public static native int gettid();

    static {
        Library.initialize();
    }
}
