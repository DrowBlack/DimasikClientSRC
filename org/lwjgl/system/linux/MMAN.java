package org.lwjgl.system.linux;

import java.nio.ByteBuffer;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class MMAN {
    public static final long MAP_FAILED = -1L;
    public static final int PROT_EXEC = 4;
    public static final int PROT_READ = 1;
    public static final int PROT_WRITE = 2;
    public static final int PROT_NONE = 0;
    public static final int PROT_GROWSDOWN = 0x1000000;
    public static final int PROT_GROWSUP = 0x2000000;
    public static final int MAP_SHARED = 1;
    public static final int MAP_SHARED_VALIDATE = 3;
    public static final int MAP_PRIVATE = 2;
    public static final int MAP_HUGE_SHIFT = 26;
    public static final int MAP_HUGE_MASK = 63;
    public static final int MAP_32BIT = 64;
    public static final int MAP_ANONYMOUS = 32;
    public static final int MAP_ANON = 32;
    public static final int MAP_DENYWRITE = 2048;
    public static final int MAP_EXECUTABLE = 4096;
    public static final int MAP_FILE = 0;
    public static final int MAP_FIXED = 16;
    public static final int MAP_FIXED_NOREPLACE = 0x100000;
    public static final int MAP_GROWSDOWN = 256;
    public static final int MAP_HUGETLB = 262144;
    public static final int MAP_HUGE_2MB = 0x54000000;
    public static final int MAP_HUGE_1GB = 0x78000000;
    public static final int MAP_LOCKED = 8192;
    public static final int MAP_NONBLOCK = 65536;
    public static final int MAP_NORESERVE = 16384;
    public static final int MAP_POPULATE = 32768;
    public static final int MAP_STACK = 131072;
    public static final int MAP_SYNC = 524288;
    public static final int MAP_UNINITIALIZED = 0x4000000;

    protected MMAN() {
        throw new UnsupportedOperationException();
    }

    @NativeType(value="void *")
    public static native long mmap(@NativeType(value="void *") long var0, @NativeType(value="size_t") long var2, int var4, int var5, int var6, @NativeType(value="off_t") long var7);

    public static native int nmunmap(long var0, long var2);

    public static int munmap(@NativeType(value="void *") ByteBuffer addr) {
        return MMAN.nmunmap(MemoryUtil.memAddress(addr), addr.remaining());
    }

    static {
        Library.initialize();
    }
}
