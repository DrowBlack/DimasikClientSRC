package org.lwjgl.system.linux;

import org.lwjgl.system.Library;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.linux.IOVec;

public class UIO {
    public static final int UIO_FASTIOV = 8;
    public static final int UIO_MAXIOV = 1024;
    public static final int RWF_HIPRI = 1;
    public static final int RWF_DSYNC = 2;
    public static final int RWF_SYNC = 4;
    public static final int RWF_NOWAIT = 8;
    public static final int RWF_APPEND = 16;

    protected UIO() {
        throw new UnsupportedOperationException();
    }

    public static native long nreadv(int var0, long var1, int var3);

    @NativeType(value="ssize_t")
    public static long readv(int __fd, @NativeType(value="struct iovec const *") IOVec __iovec, int __count) {
        return UIO.nreadv(__fd, __iovec.address(), __count);
    }

    public static native long nwritev(int var0, long var1, int var3);

    @NativeType(value="ssize_t")
    public static long writev(int __fd, @NativeType(value="struct iovec const *") IOVec __iovec, int __count) {
        return UIO.nwritev(__fd, __iovec.address(), __count);
    }

    public static native long npreadv(int var0, long var1, int var3, long var4);

    @NativeType(value="ssize_t")
    public static long preadv(int __fd, @NativeType(value="struct iovec const *") IOVec __iovec, int __count, @NativeType(value="off_t") long __offset) {
        return UIO.npreadv(__fd, __iovec.address(), __count, __offset);
    }

    public static native long npwritev(int var0, long var1, int var3, long var4);

    @NativeType(value="ssize_t")
    public static long pwritev(int __fd, @NativeType(value="struct iovec const *") IOVec __iovec, int __count, @NativeType(value="off_t") long __offset) {
        return UIO.npwritev(__fd, __iovec.address(), __count, __offset);
    }

    public static native long nprocess_vm_readv(int var0, long var1, long var3, long var5, long var7, long var9);

    @NativeType(value="ssize_t")
    public static long process_vm_readv(@NativeType(value="pid_t") int __pid, @NativeType(value="struct iovec const *") IOVec __lvec, @NativeType(value="unsigned long int") long __liovcnt, @NativeType(value="struct iovec const *") IOVec __rvec, @NativeType(value="unsigned long int") long __riovcnt, @NativeType(value="unsigned long int") long __flags) {
        return UIO.nprocess_vm_readv(__pid, __lvec.address(), __liovcnt, __rvec.address(), __riovcnt, __flags);
    }

    public static native long nprocess_vm_writev(int var0, long var1, long var3, long var5, long var7, long var9);

    @NativeType(value="ssize_t")
    public static long process_vm_writev(@NativeType(value="pid_t") int __pid, @NativeType(value="struct iovec const *") IOVec __lvec, @NativeType(value="unsigned long int") long __liovcnt, @NativeType(value="struct iovec const *") IOVec __rvec, @NativeType(value="unsigned long int") long __riovcnt, @NativeType(value="unsigned long int") long __flags) {
        return UIO.nprocess_vm_writev(__pid, __lvec.address(), __liovcnt, __rvec.address(), __riovcnt, __flags);
    }

    static {
        Library.initialize();
    }
}
