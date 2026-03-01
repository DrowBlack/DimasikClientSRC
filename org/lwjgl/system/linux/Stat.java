package org.lwjgl.system.linux;

import java.nio.ByteBuffer;
import org.lwjgl.system.Checks;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class Stat {
    protected Stat() {
        throw new UnsupportedOperationException();
    }

    public static native int nstat(long var0, long var2);

    public static int stat(@NativeType(value="char const *") ByteBuffer __file, @NativeType(value="struct stat *") long __buf) {
        if (Checks.CHECKS) {
            Checks.checkNT1(__file);
            Checks.check(__buf);
        }
        return Stat.nstat(MemoryUtil.memAddress(__file), __buf);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int stat(@NativeType(value="char const *") CharSequence __file, @NativeType(value="struct stat *") long __buf) {
        if (Checks.CHECKS) {
            Checks.check(__buf);
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(__file, true);
            long __fileEncoded = stack.getPointerAddress();
            int n = Stat.nstat(__fileEncoded, __buf);
            return n;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native int nfstat(int var0, long var1);

    public static int fstat(int __fd, @NativeType(value="struct stat *") long __buf) {
        if (Checks.CHECKS) {
            Checks.check(__buf);
        }
        return Stat.nfstat(__fd, __buf);
    }

    static {
        Library.initialize();
    }
}
