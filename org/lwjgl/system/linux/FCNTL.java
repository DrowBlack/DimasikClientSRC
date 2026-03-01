package org.lwjgl.system.linux;

import java.nio.ByteBuffer;
import org.lwjgl.system.Checks;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class FCNTL {
    public static final int O_ACCMODE = 3;
    public static final int O_RDONLY = 0;
    public static final int O_WRONLY = 1;
    public static final int O_RDWR = 2;
    public static final int O_APPEND = 1024;
    public static final int O_ASYNC = 8192;
    public static final int O_CLOEXEC = 524288;
    public static final int O_CREAT = 64;
    public static final int O_DIRECT = 16384;
    public static final int O_DIRECTORY = 65536;
    public static final int O_DSYNC = 4096;
    public static final int O_EXCL = 128;
    public static final int O_LARGEFILE = 32768;
    public static final int O_NOATIME = 262144;
    public static final int O_NOCTTY = 256;
    public static final int O_NOFOLLOW = 131072;
    public static final int O_NONBLOCK = 2048;
    public static final int O_NDELAY = 2048;
    public static final int O_PATH = 0x200000;
    public static final int O_SYNC = 0x101000;
    public static final int O_TMPFILE = 0x410000;
    public static final int O_TRUNC = 512;
    public static final int S_IFMT = 61440;
    public static final int S_IFBLK = 24576;
    public static final int S_IFCHR = 8192;
    public static final int S_IFIFO = 4096;
    public static final int S_IFREG = 32768;
    public static final int S_IFDIR = 16384;
    public static final int S_IFLNK = 40960;
    public static final int S_IFSOCK = 49152;
    public static final int S_IRWXU = 448;
    public static final int S_IRUSR = 256;
    public static final int S_IWUSR = 128;
    public static final int S_IXUSR = 64;
    public static final int S_IRWXG = 56;
    public static final int S_IRGRP = 32;
    public static final int S_IWGRP = 16;
    public static final int S_IXGRP = 8;
    public static final int S_IRWXO = 7;
    public static final int S_IROTH = 4;
    public static final int S_IWOTH = 2;
    public static final int S_IXOTH = 1;
    public static final int S_ISUID = 2048;
    public static final int S_ISGID = 1024;
    public static final int S_ISVTX = 512;
    public static final int F_DUPFD = 0;
    public static final int F_GETFD = 1;
    public static final int F_SETFD = 2;
    public static final int F_GETFL = 3;
    public static final int F_SETFL = 4;
    public static final int F_GETLK = 5;
    public static final int F_SETLK = 8;
    public static final int F_SETLKW = 7;
    public static final int F_SETOWN = 8;
    public static final int F_GETOWN = 9;
    public static final int F_SETSIG = 10;
    public static final int F_GETSIG = 11;
    public static final int F_SETOWN_EX = 15;
    public static final int F_GETOWN_EX = 16;
    public static final int F_OFD_GETLK = 36;
    public static final int F_OFD_SETLK = 37;
    public static final int F_OFD_SETLKW = 38;
    public static final int F_SETLEASE = 1024;
    public static final int F_GETLEASE = 1025;
    public static final int F_NOTIFY = 1026;
    public static final int F_SETPIPE_SZ = 1031;
    public static final int F_GETPIPE_SZ = 1032;
    public static final int F_ADD_SEALS = 1033;
    public static final int F_GET_SEALS = 1034;
    public static final int F_GET_RW_HINT = 1035;
    public static final int F_SET_RW_HINT = 1036;
    public static final int F_GET_FILE_RW_HINT = 1037;
    public static final int F_SET_FILE_RW_HINT = 1038;
    public static final int F_DUPFD_CLOEXEC = 1030;
    public static final int FD_CLOEXEC = 1;
    public static final int F_RDLCK = 0;
    public static final int F_WRLCK = 1;
    public static final int F_UNLCK = 2;
    public static final int F_EXLCK = 4;
    public static final int F_SHLCK = 8;
    public static final int F_OWNER_TID = 0;
    public static final int F_OWNER_PID = 1;
    public static final int F_OWNER_PGRP = 2;
    public static final int LOCK_SH = 1;
    public static final int LOCK_EX = 2;
    public static final int LOCK_NB = 4;
    public static final int LOCK_UN = 8;
    public static final int LOCK_MAND = 32;
    public static final int LOCK_READ = 64;
    public static final int LOCK_WRITE = 128;
    public static final int LOCK_RW = 192;
    public static final int DN_ACCESS = 1;
    public static final int DN_MODIFY = 2;
    public static final int DN_CREATE = 4;
    public static final int DN_DELETE = 8;
    public static final int DN_RENAME = 16;
    public static final int DN_ATTRIB = 32;
    public static final int DN_MULTISHOT = Integer.MIN_VALUE;
    public static final int F_SEAL_SEAL = 1;
    public static final int F_SEAL_SHRINK = 2;
    public static final int F_SEAL_GROW = 4;
    public static final int F_SEAL_WRITE = 8;
    public static final int F_SEAL_FUTURE_WRITE = 16;
    public static final int RWH_WRITE_LIFE_NOT_SET = 0;
    public static final int RWH_WRITE_LIFE_NONE = 1;
    public static final int RWH_WRITE_LIFE_SHORT = 2;
    public static final int RWH_WRITE_LIFE_MEDIUM = 3;
    public static final int RWH_WRITE_LIFE_LONG = 4;
    public static final int RWH_WRITE_LIFE_EXTREME = 5;

    protected FCNTL() {
        throw new UnsupportedOperationException();
    }

    public static native int nopen(long var0, int var2, int var3);

    public static int open(@NativeType(value="char const *") ByteBuffer pathname, int flags, @NativeType(value="mode_t") int mode) {
        if (Checks.CHECKS) {
            Checks.checkNT1(pathname);
        }
        return FCNTL.nopen(MemoryUtil.memAddress(pathname), flags, mode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int open(@NativeType(value="char const *") CharSequence pathname, int flags, @NativeType(value="mode_t") int mode) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(pathname, true);
            long pathnameEncoded = stack.getPointerAddress();
            int n = FCNTL.nopen(pathnameEncoded, flags, mode);
            return n;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native int nopenat(int var0, long var1, int var3, int var4);

    public static int openat(int dirfd, @NativeType(value="char const *") ByteBuffer pathname, int flags, @NativeType(value="mode_t") int mode) {
        if (Checks.CHECKS) {
            Checks.checkNT1(pathname);
        }
        return FCNTL.nopenat(dirfd, MemoryUtil.memAddress(pathname), flags, mode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int openat(int dirfd, @NativeType(value="char const *") CharSequence pathname, int flags, @NativeType(value="mode_t") int mode) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(pathname, true);
            long pathnameEncoded = stack.getPointerAddress();
            int n = FCNTL.nopenat(dirfd, pathnameEncoded, flags, mode);
            return n;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native int ncreat(long var0, int var2);

    public static int creat(@NativeType(value="char const *") ByteBuffer pathname, @NativeType(value="mode_t") int mode) {
        if (Checks.CHECKS) {
            Checks.checkNT1(pathname);
        }
        return FCNTL.ncreat(MemoryUtil.memAddress(pathname), mode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int creat(@NativeType(value="char const *") CharSequence pathname, @NativeType(value="mode_t") int mode) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(pathname, true);
            long pathnameEncoded = stack.getPointerAddress();
            int n = FCNTL.ncreat(pathnameEncoded, mode);
            return n;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native int fcntl(int var0, int var1);

    public static native int nfcntli(int var0, int var1, int var2);

    public static int fcntli(int fd, int cmd, int arg) {
        return FCNTL.nfcntli(fd, cmd, arg);
    }

    public static native int nfcntlp(int var0, int var1, long var2);

    public static int fcntlp(int fd, int cmd, @NativeType(value="void *") long arg) {
        if (Checks.CHECKS) {
            Checks.check(arg);
        }
        return FCNTL.nfcntlp(fd, cmd, arg);
    }

    static {
        Library.initialize();
    }
}
